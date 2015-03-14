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
import static philharmonic.resources.mapping.CPActionEnumMapping.*;
import static philharmonic.resources.LoggingConstants.*;



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

    private Logger logger;

    @Autowired
    private IMCService service;

    public IMCController() {
        rt = new RestTemplate();
        mapper = new ObjectMapper();
        errorHolder = new ArrayList<>();
        logger = Logger.getLogger(IMCController.class);
    }

    @RequestMapping(value = resourceAddressCPAction, method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> postCPAction(@RequestBody String actionJSON) {
        logger.debug(invokingCPActionPOST + actionJSON);
        try {
            validate(actionJSON);
            String sourceName = orchestrComponentName;
            List<Message> messages = parseMessages(resourceNameCPAction, namePOSTAction);
            // new resource that will be saved
            MappedResource resourceToSave = new MappedResource();
            int idToSet = mapper.readTree(actionJSON).findValue(CPAction.getPropertyName()).asInt();
            if (idToSet == 0) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            if (entityExists(actionJSON, sourceName, CPAction.getTableName())) {
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }
            resolver.setId(resourceToSave, idToSet, sourceName);

            for (Message message : messages) {
                // shifting enum ids
                String jsonToSend = shiftEnumIdsInJSON(actionJSON, sourceName, message.getTargetComponentName());
                // resource id will be 0
                jsonToSend = nullResourceIdInJSON(jsonToSend);
                // adds ids of this entity in other systems if specified in config xml file
                jsonToSend = addNeededIds(resourceToSave, message, jsonToSend);
                ResponseEntity<String> response = new ResponseEntity<>(HttpStatus.I_AM_A_TEAPOT);
                try {                    
                    response = sendMessage(message, jsonToSend);
                }
                catch(Exception e) {
                    logger.info(exceptionThrown, e);
                    e.printStackTrace();
                }
                logger.debug(messageResponse + response);
                // response ok
                if (response != null && response.getStatusCode().equals(HttpStatus.OK)) {
                    String responseJson = response.getBody();
                    if (responseJson != null) {
                        int returnedId = mapper.readTree(responseJson).findValue(CPAction.getPropertyName()).asInt();
                        resolver.setId(resourceToSave, returnedId, message.getTargetComponentName());
                    }
                } else {
                    handleError(response);
                }
            }
            saveMappedResource(resourceToSave);
            if (errorHolder.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.CREATED);
            } else {
                errorHolder.clear();
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }
        } catch (Exception e) {
            logger.info(exceptionThrown, e);
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }

    @RequestMapping(value = resourceAddressCPAction, method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<String> putCPAction(@RequestBody String actionJSON) throws IOException, JSONException {
        logger.debug(invokingCPActionPUT + actionJSON);
        String sourceName = orchestrComponentName;
        try {
            validate(actionJSON);
            // Hack -  because they dont want to resolve new/existing actions in component wrappers
            if (!entityExists(actionJSON, sourceName, CPAction.getTableName())) {
                return postCPAction(actionJSON);
            }
            List<Message> messages = parseMessages(resourceNameCPAction, namePUTAction);
            for (Message message : messages) {
                String jsonToSend = shiftResourceIdsInJSON(actionJSON, sourceName, message.getTargetComponentName());
                jsonToSend = shiftEnumIdsInJSON(jsonToSend, sourceName, message.getTargetComponentName());
                ResponseEntity<String> response = new ResponseEntity<>(HttpStatus.I_AM_A_TEAPOT);
                try {                    
                    response = sendMessage(message, jsonToSend);
                }
                catch(Exception e) {
                    logger.info(exceptionThrown, e);
                    e.printStackTrace();
                }
                logger.debug(messageResponse + response);
                if (!response.getStatusCode().equals(HttpStatus.OK)) {
                    handleError(response);
                }
            }
            if (errorHolder.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                errorHolder.clear();
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }

    // Private methods


    private String shiftResourceIdsInJSON(String originalJSON, String sourceComponentName, String targetComponentName) throws JSONException, IOException {
        return jsonUtil.shiftResourceIdsInJSON(originalJSON, sourceComponentName, targetComponentName);
    }

    private String addResourceIdToJSON(String originalJSON, String componentName, int idValue) throws JSONException, IOException {
        return jsonUtil.addResourceIdToJSON(originalJSON, componentName, idValue);
    }

    private String shiftEnumIdsInJSON(String originalJSON, String sourceComponentName, String targetComponentName) throws JSONException, IOException {
        return jsonUtil.shiftEnumIdsInJSON(originalJSON, sourceComponentName, targetComponentName);
    }

    private String nullResourceIdInJSON(String originalJSON) throws JSONException {
        return jsonUtil.nullResourceIdInJSON(originalJSON);
    }

    private List<Message> parseMessages(String resourceName, String actionName) {
        return parser.getRequiredMessagesFor(resourceName, actionName);
    }

    private boolean entityExists(String JSON, String componentName, String tableName) throws IOException {
        int sourceId = mapper.readTree(JSON).findValue(CPAction.getPropertyName()).asInt();
        return getMappedEntity(sourceId, componentName, tableName) != null;
    }

    private MappedEntity getMappedEntity(int id, String componentName, String tableName) {
        MappedEntity ret = service.getMappedEntity(id, componentName, tableName);
        return ret;
    }

    private void saveMappedResource(MappedResource resource) {
        service.saveMappedResource(resource);
    }

    private void handleError(ResponseEntity<String> response) {
        errorHolder.add(response);
    }

    private void validate(String JSON) throws Exception {
        logger.debug(validating + JSON);
        final JsonParser validationParser = new ObjectMapper().getJsonFactory().createJsonParser(JSON);
        while (validationParser.nextToken() != null) {
        }
    }
    
    private ResponseEntity<String> sendMessage(Message message, String body) {
        logger.debug(sendingMessage + " Message: " + message + " Body: " + body);
        return sender.sendMessage(message, body);
    }

    /*
     For all component names included in message.neededIds finds appropriate value in resource
     and adds the value to string Json
     */
    private String addNeededIds(MappedResource resource, Message message, String Json) throws JSONException, IOException {
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
