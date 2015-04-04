/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package philharmonic.controller;

import java.io.IOException;
import java.util.ArrayList;
import philharmonic.model.Message;
import philharmonic.service.IMCService;
import java.util.List;
import org.apache.log4j.Logger;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import philharmonic.model.*;
import philharmonic.utilities.*;
import static philharmonic.resources.StringConstants.*;
import static philharmonic.resources.LoggingConstants.*;
import static philharmonic.resources.ErrorMessages.*;

/**
 *
 * @author Kookie
 */
@Controller
@RequestMapping(addressMiddleComponent)
public class IMCController {

    private RestTemplate rt;
    private ObjectMapper mapper;
    private List errorHolder;

    @Autowired
    JsonUtil jsonUtil;

    @Autowired
    MessageSender sender;

    @Autowired
    private MappedEntityIdResolver resolver;

    @Autowired
    MessagesParser parser;

    private static final Logger logger = Logger.getLogger(IMCController.class);

    @Autowired
    private IMCService service;

    public IMCController() {
        rt = new RestTemplate();
        mapper = new ObjectMapper();
        errorHolder = new ArrayList<>();
    }

    /**
     * **********************************************************************
     */
    @RequestMapping(value = resourceAddressCPAction, method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> postCPAction(@RequestBody String actionJSON) {
        logger.info(invokingCPActionPOST + actionJSON);
        try {
            validate(actionJSON);
            String sourceName = orchestrComponentName;
            String resource = CPAction;
            List<Message> messages = parseMessages(resource, namePOSTAction);
            // new resource that will be saved
            MappedResource resourceToSave = new MappedResource();
            int idToSet = mapper.readTree(actionJSON).findValue(idName).asInt();
            if (idToSet == 0) {
                return new ResponseEntity<>(errorId0(), HttpStatus.BAD_REQUEST);
            }
            if (entityExists(idToSet, resource, sourceName)) {
                return new ResponseEntity<>(errorEntityExists(idToSet), HttpStatus.CONFLICT);
            }
            resolver.setId(resourceToSave, idToSet, sourceName);

            sendPOSTMessages(messages, actionJSON, sourceName, resource, resourceToSave);
            saveMappedResource(resourceToSave, resource);
            return returnAfterPOST();

        } catch (Exception e) {
            logger.error(exceptionThrown, e);
            return new ResponseEntity(errorWhileProcessing(), HttpStatus.BAD_REQUEST);

        }

    }

    @RequestMapping(value = resourceAddressCPAction, method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<String> putCPAction(@RequestBody String actionJSON) throws IOException, JSONException {
        logger.info(invokingCPActionPUT + actionJSON);
        String sourceName = orchestrComponentName;
        String resource = CPAction;
        try {
            validate(actionJSON);
            int id = 0;
            try{
                id = mapper.readTree(actionJSON).findValue(idName).asInt();
            }
            catch (Exception e) {
                return new ResponseEntity<>(errorInvalidId(), HttpStatus.BAD_REQUEST);
            }
            // Hack -  because they dont want to resolve new/existing actions in component wrappers
            if (!entityExists(id, resource, sourceName)) {
                return postCPAction(actionJSON);
            }
            List<Message> messages = parseMessages(resource, namePUTAction);
            sendPUTMessages(messages, actionJSON, resource, sourceName);
            return returnAfterPUT();
        } catch (Exception e) {
            logger.error(exceptionThrown, e);
            return new ResponseEntity(errorWhileProcessing(), HttpStatus.BAD_REQUEST);
        }

    }

    /**
     * *************************************************************************************************
     */
    @RequestMapping(value = resourceAddressItem, method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> postItem(@RequestBody String itemJSON) {
        logger.info(invokingItemPOST + itemJSON);
        String sourceName = rudolfComponentName;
        String resource = Item;
//        try {
            String validationError = validate(itemJSON);
            if (!validationError.equals("")) {
                return new ResponseEntity(validationError, HttpStatus.BAD_REQUEST);
            }
            int idToSet = 0;
            try{
                idToSet = mapper.readTree(itemJSON).findValue(idName).asInt();
            }
            catch (Exception e) {
                return new ResponseEntity<>(errorInvalidId(), HttpStatus.BAD_REQUEST);
            }            
            if (idToSet == 0) {
                return new ResponseEntity<>(errorId0(), HttpStatus.BAD_REQUEST);
            }
            if (entityExists(idToSet, resource, sourceName)) {
                return new ResponseEntity<>(errorEntityExists(idToSet), HttpStatus.CONFLICT);
            }

            List<Message> messages = parseMessages(resource, namePOSTAction);
            MappedResource resourceToSave = new MappedResource();

            resolver.setId(resourceToSave, idToSet, sourceName);

            sendPOSTMessages(messages, itemJSON, sourceName, resource, resourceToSave);
            saveMappedResource(resourceToSave, resource);
            return returnAfterPOST();
//        } catch (IOException | JSONException e) {
//            logger.error(exceptionThrown, e);
//            return new ResponseEntity(errorWhileProcessing(), HttpStatus.BAD_REQUEST);
//        }
    }

    @RequestMapping(value = resourceAddressItem, method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<String> putItem(@RequestBody String itemJSON) {
        logger.info(invokingItemPUT + itemJSON);
        String sourceName = rudolfComponentName;
        String resource = Item;
        try {
            validate(itemJSON);
            int id = 0;
            try{
                id = mapper.readTree(itemJSON).findValue(idName).asInt();
            }
            catch (Exception e) {
                return new ResponseEntity<>(errorInvalidId(), HttpStatus.BAD_REQUEST);
            }
            // Hack -  because they dont want to resolve new/existing actions in component wrappers
            if (!entityExists(id, resource, sourceName)) {
                return postItem(itemJSON);
            }
            List<Message> messages = parseMessages(resource, namePUTAction);
            sendPUTMessages(messages, itemJSON, resource, sourceName);
            return returnAfterPUT();
        } catch (Exception e) {
            logger.error(exceptionThrown, e);
            return new ResponseEntity(errorWhileProcessing(), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = resourceAddressItem, method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<String> deleteItem(@RequestBody int id) {
        logger.info(invokingItemDELETE + " with id " + id);
        String sourceName = rudolfComponentName;
        String resource = Item;
        try {
            if (id == 0) {
                return new ResponseEntity<>(errorId0(), HttpStatus.BAD_REQUEST);
            }
            // Hack -  because they dont want to resolve new/existing actions in component wrappers
            if (!entityExists(id, resource, sourceName)) {
                return new ResponseEntity<>(errorEntityDoesNotExist(id), HttpStatus.CONFLICT);
            }
            List<Message> messages = parseMessages(resource, nameDELETEAction);
            sendDELETEMessages(messages, id, resource, sourceName);

            deleteEntity(id, resource, sourceName);
            return returnAfterDELETE();

        } catch (Exception e) {
            logger.info(exceptionThrown, e);
            return new ResponseEntity(errorWhileProcessing(), HttpStatus.BAD_REQUEST);
        }
    }

    // Private methods for shared actions among all controller methods
    private void sendPOSTMessages(List<Message> messages, String JSON, String sourceName, String resource, MappedResource resourceToSave) 
    {
        for (Message message : messages) {            
            // shifting enum ids
            String jsonToSend = shiftEnumIdsInJSON(JSON, resource, sourceName, message.getTargetComponentName());
            // resource id will be 0
            jsonToSend = nullResourceIdInJSON(jsonToSend, idName);
            // adds ids of this entity in other systems if specified in config xml file
            jsonToSend = addNeededIds(resourceToSave, message, jsonToSend);
            // some error while doing stuff with ids
            if(jsonToSend == null) {
                continue;
            }
            ResponseEntity<String> response = new ResponseEntity<>(HttpStatus.I_AM_A_TEAPOT);
            try {
                logger.info(sendingMessage + message.getTargetComponentName() + "/"
                        + message.getResourceName() + "/" + message.getAction() + "\n"
                        + jsonToSend);
                // Vyzkouset, co prijde pri nebezici komponente,
                // pri konfliktu nebo chybe v komponente (podle test planu) vracet konflikt s prislusnymi chybami
                response = sendMessage(message, jsonToSend);
                logger.info(messageResponse + response.getStatusCode() + "\n"
                        + response.getBody());
            } catch (Exception e) {
                logger.info(exceptionThrown, e);
                errorHolder.add(exceptionThrown + e.getLocalizedMessage());
            }
            // response ok
            if (response.getStatusCode().equals(HttpStatus.OK)) {
                String responseJson = response.getBody();
                if (responseJson != null) {
                    int returnedId = 0;
                    try {
                        returnedId = mapper.readTree(responseJson).findValue(idName).asInt();
                    }
                    catch(Exception e) {
                        logger.error(exceptionThrown, e);
                        errorHolder.add(exceptionThrown + e.getLocalizedMessage());
                    }
                    resolver.setId(resourceToSave, returnedId, message.getTargetComponentName());
                }
            } else {
                handleError(response);
            }
        }
    }

    private ResponseEntity<String> returnAfterPOST() {
        if (errorHolder.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.CREATED);
        } else {
            ResponseEntity<String> ret = new ResponseEntity(errorTargetReturnedError(errorHolder.toString()), HttpStatus.CONFLICT);
            errorHolder.clear();
            logger.info(returning + ret.getStatusCode() + "\n" + ret.getBody());
            return ret;
        }
    }

    private void sendPUTMessages(List<Message> messages, String JSON, String resource, String sourceName) throws JSONException, IOException {
        for (Message message : messages) {
            String jsonToSend = shiftResourceIdsInJSON(JSON, resource, sourceName, message.getTargetComponentName());
            jsonToSend = shiftEnumIdsInJSON(jsonToSend, resource, sourceName, message.getTargetComponentName());
            ResponseEntity<String> response = new ResponseEntity<>(HttpStatus.I_AM_A_TEAPOT);
            try {
                logger.info(sendingMessage + message.getTargetComponentName() + "/"
                        + message.getResourceName() + "/" + message.getAction() + "\n"
                        + jsonToSend);
                response = sendMessage(message, jsonToSend);
                logger.info(messageResponse + response.getStatusCode() + "\n"
                        + response.getBody());
            } catch (Exception e) {
                logger.error(exceptionThrown, e);
                e.printStackTrace();
            }
            if (!response.getStatusCode().equals(HttpStatus.OK)) {
                handleError(response);
            }
        }
    }

    private ResponseEntity<String> returnAfterPUT() {
        if (errorHolder.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            ResponseEntity<String> ret = new ResponseEntity(errorTargetReturnedError(errorHolder.toString()), HttpStatus.CONFLICT);
            errorHolder.clear();
            logger.info(returning + ret.getStatusCode() + "\n" + ret.getBody());
            return ret;
        }
    }

    private void sendDELETEMessages(List<Message> messages, int id, String resource, String sourceName) throws JSONException, IOException {
        for (Message message : messages) {
            ResponseEntity<String> response = new ResponseEntity<>(HttpStatus.I_AM_A_TEAPOT);
            try {
                // quick hack because of java bug with delete                    
                response = sendMessage(message, id);
            } catch (Exception e) {
                logger.error(exceptionThrown, e);
                e.printStackTrace();
            }
            logger.info(messageResponse + response);
            if (!response.getStatusCode().equals(HttpStatus.OK)) {
                handleError(response);
            }
        }
    }

    private ResponseEntity<String> returnAfterDELETE() {
        if (errorHolder.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            ResponseEntity<String> ret = new ResponseEntity(errorTargetReturnedError(errorHolder.toString()), HttpStatus.CONFLICT);
            errorHolder.clear();
            logger.info(returning + ret.getStatusCode() + "\n" + ret.getBody());
            return ret;
        }
    }

    // Private methods for communication with other layers
    private String shiftResourceIdsInJSON(String originalJSON, String resource, String sourceComponentName, String targetComponentName)
    {
        try {
            return jsonUtil.shiftResourceIdsInJSON(originalJSON, resource, sourceComponentName, targetComponentName);
        }
        catch(Exception e) {
            logger.error(errorWhileShiftingResourceIds, e);
            errorHolder.add(errorWhileShiftingResourceIds(e));
        }
        return null;
    }

    private String addResourceIdToJSON(String originalJSON, String componentName, int idValue)
    {
        try {
            return jsonUtil.addResourceIdToJSON(originalJSON, componentName, idValue);
        }
        catch(Exception e) {
            logger.error(errorWhileAddingResourceId, e);
            errorHolder.add(errorWhileAddingResourceId(e));
        }
        return null;
    }

    private String shiftEnumIdsInJSON(String originalJSON, String resourceName, String sourceComponentName, String targetComponentName) 
    {
        try {
        return jsonUtil.shiftEnumIdsInJSON(originalJSON, resourceName, sourceComponentName, targetComponentName);
        }
        catch(Exception e) {
            logger.error(errorWhileShiftingEnumIds, e);
            errorHolder.add(errorWhileShiftingEnumIds(e));
        }
        return null;
    }

    private String nullResourceIdInJSON(String originalJSON, String resourceIdName) 
    {
        try {
            return jsonUtil.nullResourceIdInJSON(originalJSON, resourceIdName);
        }
        catch(Exception e) {
            logger.error(errorWhileDeletingResourceId, e);
            errorHolder.add(errorWhileDeletingResourceId(e));
        }
        return null;
    }

    private List<Message> parseMessages(String resourceName, String actionName) {
        return parser.getRequiredMessagesFor(resourceName, actionName);
    }

    private boolean entityExists(int id, String resource, String componentName) {
        return getMappedEntity(id, resource, componentName) != null;
    }

    private void deleteEntity(int entityId, String resourceName, String componentName) throws IOException {
        service.deleteEntity(entityId, resourceName, componentName);
    }

    private MappedEntity getMappedEntity(int id, String resourceName, String componentName) {
        MappedEntity ret = service.getMappedEntity(id, resourceName, componentName);
        return ret;
    }

    private void saveMappedResource(MappedResource resource, String resourceName) {
        service.saveMappedResource(resource, resourceName);
    }

    private void handleError(ResponseEntity<String> response) {
        errorHolder.add(response);
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
        return sender.sendMessage(message, id);
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

}
