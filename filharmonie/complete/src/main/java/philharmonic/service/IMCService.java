/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package philharmonic.service;


import org.springframework.stereotype.Service;
import philharmonic.dao.IDao;
import philharmonic.model.MappedEntity;
import java.io.IOException;
import philharmonic.model.Message;
import philharmonic.model.Enum;

import java.util.List;
import org.apache.log4j.Logger;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import philharmonic.model.*;
import philharmonic.utilities.*;
import static philharmonic.resources.StringConstants.*;
import static philharmonic.resources.LoggingConstants.*;
import static philharmonic.resources.ErrorMessages.*;
import static philharmonic.resources.mapping.EnumMapping.getMappedEnums;

/**
 *
 * @author Kookie
 */

@Service
public class IMCService {
    
    @Autowired
    IDao dao;

    private ObjectMapper mapper;
    private ErrorHolder errorHolder;

    @Autowired
    private JsonUtil jsonUtil;

    @Autowired
    private MessageSender sender;

    @Autowired
    private MappedEntityIdResolver resolver;

    @Autowired
    private MessagesParser parser;

    private static final Logger logger = Logger.getLogger(IMCService.class);


    
    public IMCService() {
        mapper = new ObjectMapper();
        errorHolder = new ErrorHolder();
    }  
    
        /*
     * Returns MappedEntity for given id in given component in mapped table
     * if there are more entities with the same id, returns the first one
     * if there are no entities, returns null
     */
     
    public MappedEntity getMappedEntity(int id, String resourceTableName, String componentName) {
        if(id == 0)
            return null;
        if(resourceTableName == null || componentName == null)
            return null;
        if(resourceTableName.isEmpty() || componentName.isEmpty())
            return null;
        return dao.get(id, resourceTableName, componentName);        
    }
   
   public ResponseEntity<String> processPOSTRequest(String JSON, String sourceName, String resource) {
        try {
            String validationError = validate(JSON);
            if (!validationError.equals("")) {
                ResponseEntity<String> ret = new ResponseEntity(validationError, HttpStatus.BAD_REQUEST);
                logger.info(returning + ret.getStatusCode() + "\n" + ret.getBody());
                logger.info(finish);
                return ret;
            }
            int idToSet = 0;
            try {
                idToSet = mapper.readTree(JSON).findValue(idName).asInt();
            } catch (Exception e) {
                ResponseEntity<String> ret = new ResponseEntity(errorInvalidId(), HttpStatus.BAD_REQUEST);
                logger.info(returning + ret.getStatusCode() + "\n" + ret.getBody());
                logger.info(finish);
                return ret;
            }
            if (idToSet == 0) {
                ResponseEntity<String> ret = new ResponseEntity<>(errorId0(), HttpStatus.BAD_REQUEST);
                logger.info(returning + ret.getStatusCode() + "\n" + ret.getBody());
                logger.info(finish);
                return ret;
            }
            if (entityExists(idToSet, resource, sourceName)) {
                ResponseEntity<String> ret = new ResponseEntity<>(errorEntityExists(idToSet), HttpStatus.CONFLICT);
                logger.info(returning + ret.getStatusCode() + "\n" + ret.getBody());
                logger.info(finish);
                return ret;
            }

            List<Message> messages = parseMessages(resource, namePOSTAction);
            // new resource that will be saved
            MappedResource resourceToSave = new MappedResource();
            resolver.setId(resourceToSave, idToSet, sourceName);

            sendPOSTMessages(messages, JSON, sourceName, resource, resourceToSave);
            saveMappedResource(resourceToSave, resource);
            return returnAfterPOST();
        } catch (Exception e) {
            logger.error(exceptionThrown, e);
            ResponseEntity<String> ret = new ResponseEntity(errorWhileProcessing(), HttpStatus.CONFLICT);
            logger.info(returning + ret.getStatusCode() + "\n" + ret.getBody());
            logger.info(finish);
            return ret;
        }
    }

    public ResponseEntity<String> processPUTRequest(String JSON, String sourceName, String resource) {
        try {
            String validationError = validate(JSON);
            if (!validationError.equals("")) {
                ResponseEntity<String> ret = new ResponseEntity(validationError, HttpStatus.BAD_REQUEST);
                logger.info(returning + ret.getStatusCode() + "\n" + ret.getBody());
                logger.info(finish);
                return ret;
            }
            int id = 0;
            try {
                id = mapper.readTree(JSON).findValue(idName).asInt();
            } catch (Exception e) {
                ResponseEntity<String> ret = new ResponseEntity<>(errorInvalidId(), HttpStatus.BAD_REQUEST);
                logger.info(returning + ret.getStatusCode() + "\n" + ret.getBody());
                logger.info(finish);
                return ret;
            }
            // Hack -  because they dont want to resolve new/existing actions in component wrappers
            if (!entityExists(id, resource, sourceName)) {
                logger.info(redirectingToPOST + JSON);
                return processPOSTRequest(JSON, sourceName, resource);
            }
            List<Message> messages = parseMessages(resource, namePUTAction);
            sendPUTMessages(messages, JSON, resource, sourceName);
            return returnAfterPUT();
        } catch (Exception e) {
            logger.error(exceptionThrown, e);
            ResponseEntity<String> ret = new ResponseEntity(errorWhileProcessing(), HttpStatus.CONFLICT);
            logger.info(returning + ret.getStatusCode() + "\n" + ret.getBody());
            logger.info(finish);
            return ret;
        }
    }
    
    
    
    public ResponseEntity<String> processDELETERequest(String id, String JSON, String sourceName, String resource) {
        try {
            if(id == null || id.isEmpty()) {
                ResponseEntity<String> ret = new ResponseEntity<>(errorInvalidId(), HttpStatus.BAD_REQUEST);
                logger.info(returning + ret.getStatusCode() + "\n" + ret.getBody());
                logger.info(finish);
                return ret;
            }
            int idInt = 0;
            try {
                idInt = Integer.parseInt(id);
            } catch (Exception e) {
                ResponseEntity<String> ret = new ResponseEntity<>(errorId0(), HttpStatus.BAD_REQUEST);
                logger.info(returning + ret.getStatusCode() + "\n" + ret.getBody());
                logger.info(finish);
                return ret;
            }
            if (idInt == 0) {
                ResponseEntity<String> ret = new ResponseEntity<>(errorId0(), HttpStatus.BAD_REQUEST);
                logger.info(returning + ret.getStatusCode() + "\n" + ret.getBody());
                logger.info(finish);
                return ret;
            }
            if(JSON != null && !JSON.isEmpty()) {
                String validationError = validate(JSON);
                if (!validationError.equals("")) {
                    ResponseEntity<String> ret = new ResponseEntity(validationError, HttpStatus.BAD_REQUEST);
                    logger.info(returning + ret.getStatusCode() + "\n" + ret.getBody());
                    logger.info(finish);
                    return ret;
                }
            }
            
            if (!entityExists(idInt, resource, sourceName)) {
                ResponseEntity<String> ret = new ResponseEntity<>(errorEntityDoesNotExist(idInt), HttpStatus.CONFLICT);
                logger.info(returning + ret.getStatusCode() + "\n" + ret.getBody());
                logger.info(finish);
                return ret;
            }
            List<Message> messages = parseMessages(resource, nameDELETEAction);
            sendDELETEMessages(messages, idInt, JSON, resource, sourceName);

            deleteEntity(idInt, resource, sourceName);
            return returnAfterDELETE();

        } catch (Exception e) {
            logger.info(exceptionThrown, e);
            ResponseEntity<String> ret = new ResponseEntity(errorWhileProcessing(), HttpStatus.CONFLICT);
            logger.info(returning + ret.getStatusCode() + "\n" + ret.getBody());
            logger.info(finish);
            return ret;
        }
    }
    
    private void sendPOSTMessages(List<Message> messages, String JSON, String sourceName, String resource, MappedResource resourceToSave) throws JSONException, IOException {
        for (Message message : messages) {
            assureConsistentEnums(JSON, resource, sourceName, message.getTargetComponentName());
            // shifting enum ids
            String jsonToSend = shiftEnumIdsInJSON(JSON, resource, sourceName, message.getTargetComponentName());
            // resource id will be 0
            jsonToSend = nullResourceIdInJSON(jsonToSend, idName);
            // adds ids of this entity in other systems if specified in config xml file
            jsonToSend = addNeededIds(resourceToSave, message, jsonToSend);
            // some error while doing stuff with ids
            if (jsonToSend == null) {
                continue;
            }
            ResponseEntity<String> response = new ResponseEntity<>(HttpStatus.I_AM_A_TEAPOT);
            try {
                logger.info(sendingMessage + message.getTargetComponentName() + "/"
                        + message.getResourceName() + "/" + message.getAction() + "\n"
                        + jsonToSend);
                response = sendMessage(message, jsonToSend);
                logger.info(messageResponse + response.getStatusCode() + "\n"
                        + response.getBody());
            } catch (ResourceAccessException e) {
                logger.info(resourceAccessException + message.getTargetComponentName(), e);
                errorHolder.addError(resourceAccessException + message.getTargetComponentName() + ": " + e.getLocalizedMessage());
            } catch (HttpClientErrorException e) {
                logger.info(httpClientErrorException + message.getTargetComponentName(), e);
                errorHolder.addError(httpClientErrorException + message.getTargetComponentName() + ": " + e.getLocalizedMessage());
            } catch (Exception e) {
                logger.info(anotherTargetException + message.getTargetComponentName(), e);
                errorHolder.addError(anotherTargetException + message.getTargetComponentName() + ": " + e.getLocalizedMessage());
            }
            // response ok
            if (response.getStatusCode().equals(HttpStatus.OK)
                    && shouldBeMapped(message.getTargetComponentName())) {
                String responseJson = response.getBody();
                if (responseJson != null) {
                    int returnedId = 0;
                    try {
                        returnedId = mapper.readTree(responseJson).findValue(idName).asInt();
                    } catch (Exception e) {
                        logger.error(responseBodyError + message.getTargetComponentName(), e);
                        errorHolder.addError(responseBodyError + message.getTargetComponentName() + e.getLocalizedMessage());
                    }
                    resolver.setId(resourceToSave, returnedId, message.getTargetComponentName());
                }
            } else if (response.getStatusCode().equals(HttpStatus.CONFLICT)) {
                logger.info(httpClientErrorException + message.getTargetComponentName());
                errorHolder.addError(httpClientErrorException + message.getTargetComponentName());
            }
        }
    }

    private ResponseEntity<String> returnAfterPOST() throws JSONException {
        if (errorHolder.getErrors().isEmpty()) {
            ResponseEntity<String> ret = new ResponseEntity<>(HttpStatus.CREATED);
            logger.info(returning + ret.getStatusCode() + "\n" + ret.getBody());
            logger.info(finish);
            return ret;
        } else {
            ResponseEntity<String> ret = new ResponseEntity(errorTargetReturnedError(errorHolder), HttpStatus.CONFLICT);
            errorHolder.clear();
            logger.info(returning + ret.getStatusCode() + "\n" + ret.getBody());
            logger.info(finish);
            return ret;
        }
    }

    private void sendPUTMessages(List<Message> messages, String JSON, String resource, String sourceName) throws JSONException, IOException {
        for (Message message : messages) {
            assureConsistentEnums(JSON, resource, sourceName, message.getTargetComponentName());
            String jsonToSend = shiftResourceIdsInJSON(JSON, resource, sourceName, message.getTargetComponentName());
            jsonToSend = shiftEnumIdsInJSON(jsonToSend, resource, sourceName, message.getTargetComponentName());
            if (jsonToSend == null) {
                continue;
            }
            ResponseEntity<String> response = new ResponseEntity<>(HttpStatus.I_AM_A_TEAPOT);
            try {
                logger.info(sendingMessage + message.getTargetComponentName() + "/"
                        + message.getResourceName() + "/" + message.getAction() + "\n"
                        + jsonToSend);
                response = sendMessage(message, jsonToSend);
                logger.info(messageResponse + response.getStatusCode() + "\n"
                        + response.getBody());
            } catch (ResourceAccessException e) {
                logger.info(resourceAccessException + message.getTargetComponentName(), e);
                errorHolder.addError(resourceAccessException + message.getTargetComponentName() + ": " + e.getLocalizedMessage());
            } catch (HttpClientErrorException e) {
                logger.info(httpClientErrorException + message.getTargetComponentName(), e);
                errorHolder.addError(httpClientErrorException + message.getTargetComponentName() + ": " + e.getLocalizedMessage());
            } catch (Exception e) {
                logger.info(anotherTargetException + message.getTargetComponentName(), e);
                errorHolder.addError(anotherTargetException + message.getTargetComponentName() + ": " + e.getLocalizedMessage());
            }
            // response ok
            if (!response.getStatusCode().equals(HttpStatus.OK)) {
                logger.info(httpClientErrorException + message.getTargetComponentName());
                errorHolder.addError(httpClientErrorException + message.getTargetComponentName());
            }
        }
    }

    private ResponseEntity<String> returnAfterPUT() throws JSONException {
        if (errorHolder.getErrors().isEmpty()) {
            ResponseEntity<String> ret = new ResponseEntity<>(HttpStatus.OK);
            logger.info(returning + ret.getStatusCode() + "\n" + ret.getBody());
            logger.info(finish);
            return ret;
        } else {
            ResponseEntity<String> ret = new ResponseEntity(errorTargetReturnedError(errorHolder), HttpStatus.CONFLICT);
            errorHolder.clear();
            logger.info(returning + ret.getStatusCode() + "\n" + ret.getBody());
            logger.info(finish);
            return ret;
        }
    }

    private void sendDELETEMessages(List<Message> messages, int sourceId, String JSON, String resource, String sourceName) throws JSONException, IOException {
        boolean hasBody = (JSON != null && !JSON.isEmpty());
        for (Message message : messages) {
            String jsonToSend = "";
            if(hasBody) {
                assureConsistentEnums(JSON, resource, sourceName, message.getTargetComponentName());
                jsonToSend = shiftEnumIdsInJSON(JSON, resource, sourceName, message.getTargetComponentName());
            }
            
            ResponseEntity<String> response = new ResponseEntity<>(HttpStatus.I_AM_A_TEAPOT);
            try {
                int targetId = sourceId;
                if(shouldBeMapped(message.getTargetComponentName())) {
                    targetId = getResourceIdInTarget(sourceId, resource, sourceName, message.getTargetComponentName());
                }
                logger.info(sendingMessage + message.getTargetComponentName() + "/"
                        + message.getResourceName() + "/" + targetId + message.getAction() + " \n" + jsonToSend);
                response = sendMessage(message, targetId, jsonToSend);
                logger.info(messageResponse + response.getStatusCode() + "\n"
                        + response.getBody());
            } catch (ResourceAccessException e) {
                logger.info(resourceAccessException + message.getTargetComponentName(), e);
                errorHolder.addError(resourceAccessException + message.getTargetComponentName() + ": " + e.getLocalizedMessage());
            } catch (HttpClientErrorException e) {
                logger.info(httpClientErrorException + message.getTargetComponentName(), e);
                errorHolder.addError(httpClientErrorException + message.getTargetComponentName() + ": " + e.getLocalizedMessage());
            } catch (Exception e) {
                logger.info(anotherTargetException + message.getTargetComponentName(), e);
                errorHolder.addError(anotherTargetException + message.getTargetComponentName() + ": " + e.getLocalizedMessage());
            }

            if (!response.getStatusCode().equals(HttpStatus.OK)) {
                logger.info(httpClientErrorException + message.getTargetComponentName());
                errorHolder.addError(httpClientErrorException + message.getTargetComponentName());
            }
        }
    }

    private ResponseEntity<String> returnAfterDELETE() throws JSONException {
        if (errorHolder.getErrors().isEmpty()) {
            ResponseEntity<String> ret = new ResponseEntity<>(HttpStatus.OK);
            logger.info(returning + ret.getStatusCode() + "\n" + ret.getBody());
            logger.info(finish);
            return ret;
        } else {
            ResponseEntity<String> ret = new ResponseEntity(errorTargetReturnedError(errorHolder), HttpStatus.CONFLICT);
            errorHolder.clear();
            logger.info(returning + ret.getStatusCode() + "\n" + ret.getBody());
            logger.info(finish);
            return ret;
        }
    }

    // Private methods for communication with other layers
    private String shiftResourceIdsInJSON(String originalJSON, String resource, String sourceComponentName, String targetComponentName) throws JSONException, IOException {
            return jsonUtil.shiftResourceIdsInJSON(originalJSON, resource, sourceComponentName, targetComponentName);
    }

    private int getResourceIdInTarget(int sourceId, String resource, String sourceComponentName, String targetComponentName) {
        return jsonUtil.getResourceIdInTarget(sourceId, resource, sourceComponentName, targetComponentName);
    }

    private String addResourceIdToJSON(String originalJSON, String componentName, int idValue) {
        try {
            return jsonUtil.addResourceIdToJSON(originalJSON, componentName, idValue);
        } catch (Exception e) {
            logger.error(errorWhileAddingResourceId, e);
            errorHolder.addError(errorWhileAddingResourceId(e));
        }
        return null;
    }

    private String shiftEnumIdsInJSON(String originalJSON, String resourceName, String sourceComponentName, String targetComponentName) throws JSONException, IOException {
            if (shouldBeMapped(targetComponentName)) {
                return jsonUtil.shiftEnumIdsInJSON(originalJSON, resourceName, sourceComponentName, targetComponentName);
            } else {
                return originalJSON;
            }
    }

    private void assureConsistentEnums(String originalJSON,
            String resourceName,
            String sourceComponentName, String targetComponentName) {
        try {
            if (!shouldBeMapped(targetComponentName)) {
                return;
            }
            List<Enum> enums = getMappedEnums(resourceName);
            for (Enum enume : enums) {
                int enumeSourceId = jsonUtil.getEnumId(originalJSON, enume.getIdName());
                if (enumeSourceId == 0) {
                    continue;
                }
                boolean exists = entityExists(enumeSourceId, enume.getTableName(), sourceComponentName);
                boolean isMapped = isMapped(enumeSourceId, enume.getTableName(), sourceComponentName, targetComponentName);
                if (exists && isMapped) {
                    continue;
                }
                if (!exists) {
                    MappedEntity enumToSave = new MappedEntity();
                    resolver.setId(enumToSave, enumeSourceId, sourceComponentName);
                    saveMappedResource(enumToSave, enume.getTableName());
                }
                // Vyzadej si textovou reprezentaci od source
                Message getRepresentation = new Message(nameGETAction, enume.getTableName(), sourceComponentName, null);
                logger.info(sendingMessage + getRepresentation.getTargetComponentName() + "/"
                        + getRepresentation.getResourceName() + "/" + enumeSourceId + " [" + getRepresentation.getAction() + "]");
                ResponseEntity<String> response = sendMessage(getRepresentation, enumeSourceId);
                String enumTextRepresentation;
                if(response != null && response.hasBody()) {
                    enumTextRepresentation = response.getBody();
                }
                else {
                    enumTextRepresentation = "{\"id\":\"0\"}";
                }
                
                logger.info(messageResponse + enumTextRepresentation);

                // Posli ji do targetu
                Message postRepresentation = new Message(namePOSTAction, enume.getTableName(), targetComponentName, null);
                logger.info(sendingMessage + postRepresentation.getTargetComponentName() + "/"
                        + postRepresentation.getResourceName() + " \n" + postRepresentation.getAction()
                        + " \n" + enumTextRepresentation);
                String idInTarget = sendMessage(postRepresentation, enumTextRepresentation).getBody();
                logger.info(messageResponse + idInTarget);
                int returnedId = mapper.readTree(idInTarget).findValue(idName).asInt();
                // Updatni id targetu v databazi
                updateEntity(enume.getTableName(), targetComponentName, returnedId, sourceComponentName, enumeSourceId);
            }
        } catch (Exception e) {
            logger.error(errorWhileMappingEnums, e);
            errorHolder.addError(errorWhileMappingEnums(e));
        }

    }

    private boolean isMapped(int id, String enumName, String sourceComponentName, String targetComponentName) {
        MappedEntity e = getMappedEntity(id, enumName, sourceComponentName);
        if (e == null) {
            return false;
        }
        return resolver.getIdValue(e, targetComponentName) != 0;
    }

    private String nullResourceIdInJSON(String originalJSON, String resourceIdName) throws JSONException {
        return jsonUtil.nullResourceIdInJSON(originalJSON, resourceIdName);
    }

    private List<Message> parseMessages(String resourceName, String actionName) {
        return parser.getRequiredMessagesFor(resourceName, actionName);
    }

    private boolean entityExists(int id, String resource, String componentName) {
        return getMappedEntity(id, resource, componentName) != null;
    }

    private String validate(String JSON) {
        try {
            final JsonParser validationParser = new ObjectMapper().getJsonFactory().createJsonParser(JSON);
            while (validationParser.nextToken() != null) {
            }
            return "";
        } catch (Exception e) {
            return errorInvalidJson();
        }
    }

    private ResponseEntity<String> sendMessage(Message message, String body) {
        return sender.sendMessage(message, body);
    }

    private ResponseEntity<String> sendMessage(Message message, int id) {
        return sender.sendMessage(message, id, "");
    }
    
    private ResponseEntity<String> sendMessage(Message message, int id, String body) {
        return sender.sendMessage(message, id, body);
    }

    /*
     For all component names included in message.neededIds finds appropriate value in resource
     and adds the value to string Json
     */
    private String addNeededIds(MappedResource resource, Message message, String Json) {
        if (message.getNeededIds() == null) {
            return Json;
        }
        for (String component : message.getNeededIds()) {
            int id = resolver.getIdValue(resource, component);
            Json = addResourceIdToJSON(Json, component, id);
        }
        return Json;
    }

    public boolean shouldBeMapped(String componentName) {
        for (Component c : getMappedComponents()) {
            if (c.getComponentName().equals(componentName)) {
                return true;
            }
        }
        return false;
    }
    
    private void saveMappedResource(MappedEntity entity, String resourceName) {
        dao.create(entity, resourceName);
    }
    
    
    
   private void deleteEntity(int id, String resourceTableName, String componentName) {
        dao.delete(id, resourceTableName, componentName);
        
    }
   
   private void updateEntity(String resourceTableName, String setColumn, int setId, String whereColumn, int whereId) {
        dao.update(resourceTableName, setColumn, setId, whereColumn, whereId);        
    }
    
    
}
