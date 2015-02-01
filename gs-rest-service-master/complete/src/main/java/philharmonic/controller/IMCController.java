/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package philharmonic.controller;

import java.io.IOException;
import philharmonic.model.Message;
import philharmonic.service.IMCService;
import java.util.List;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import philharmonic.model.Entity;
import static philharmonic.resources.StringConstants.*;
import org.json.JSONObject;
import org.springframework.http.HttpMethod;
/**
 *
 * @author Kookie
 */
@Controller
@RequestMapping(addressMiddleComponent)
public class IMCController {

    private RestTemplate rt;
    private ObjectMapper mapper;
    
    @Autowired
    private IMCService service;
    
    public IMCController() {
        rt = new RestTemplate();
        mapper = new ObjectMapper();
    }

    
    @RequestMapping(value = resourceAddressCPAction, method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> postCPAction(@RequestBody String actionJSON) throws IOException {
        List<Message> messages = parseMessages(resourceNameCPAction, namePOSTAction);
        Entity entityToSave = new Entity();
        HttpEntity bodyToSend = new HttpEntity(actionJSON);
        setId(entityToSave, mapper.readTree(actionJSON).findValue(idValue).asInt(), nameOrchestrWrapper);
        for (Message message : messages) {
            ResponseEntity<String> response = sendMessage(message, bodyToSend);   
            if(response != null && response.getStatusCode().equals(HttpStatus.OK)) {
                String responseJson = response.getBody();
                if(responseJson != null) {
                    int returnedId = mapper.readTree(responseJson).findValue(idValue).asInt();
                    setId(entityToSave, returnedId, message.getTargetComponentName());
                }                
            }
            else
                handleException();
        }
        service.saveEntity(entityToSave);
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    
    @RequestMapping(value = resourceAddressCPAction, method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<String> putCPAction(@RequestBody String actionJSON) throws IOException, JSONException {
        List<Message> messages = parseMessages(resourceNameCPAction, namePUTAction);
        // We suppose this message to come only from Orchestr as analysis says
        // There could be some more robust way to determine the source, but for now this is good enough
        int changedEntitySourceId = mapper.readTree(actionJSON).findValue(idValue).asInt();
        Entity changedEntity = getEntity(changedEntitySourceId, nameOrchestrWrapper);
        if(changedEntity == null)
            handleException();
        else {
            for (Message message : messages) {
                String jsonToSend = shiftIdsInJSON(actionJSON, message.getTargetComponentName(), changedEntity);
                HttpEntity bodyToSend = new HttpEntity(jsonToSend); 
                HttpStatus responseStatus = sendMessage(message, bodyToSend).getStatusCode();
                if(!responseStatus.equals(HttpStatus.OK))
                    handleException();
            }
        }
        
        return new ResponseEntity<String>(HttpStatus.OK);
    }
    

    

    

    private void setId(Entity entity, int Id, String idType) {
        if (idType.equals(nameRudolfWrapper)) {
            entity.setIdRudolf(Id);
        } else if (idType.equals(nameOrchestrWrapper)) {
            entity.setIdOrchestr(Id);
        } else if (idType.equals(nameWebWrapper)) {
            entity.setIdWeb(Id);
        } else if (idType.equals(nameTicketingWrapper)) {
            entity.setIdTicketing(Id);
        }
    }

    // podle attributu action v message rozlisit posilani post/put
    private ResponseEntity<String> sendMessage(Message message, HttpEntity body) {
        String URI = serverAddress + "/" + message.getTargetComponentName()+ "/" + message.getResourceName();
        if(message.getAction().equals(namePOSTAction)) {
            return rt.exchange(URI, HttpMethod.POST, body, String.class);
        }
        if(message.getAction().equals(namePUTAction)) {
            return rt.exchange(URI, HttpMethod.PUT, body, String.class);
        }
        return null;
    }

    private List<Message> parseMessages(String resourceName, String actionName) {
        return service.getMessagesFor(resourceName, actionName);
    }
    
    private Entity getEntity(int id, String componentName) {
        return service.getEntity(id, componentName);
    }
    
    /*
     * This method takes JSON string, reads appropriate ID from entity according to name of target component
     * and returns new JSON with changed ID
     */
    private String shiftIdsInJSON(String originalJSON, String componentName, Entity entity) throws JSONException {
        int newId = 0;
        if (componentName.equals(nameRudolfWrapper)) {
            newId = entity.getIdRudolf();
        } else if (componentName.equals(nameOrchestrWrapper)) {
            newId = entity.getIdOrchestr();
        } else if (componentName.equals(nameWebWrapper)) {
            newId = entity.getIdWeb();
        } else if (componentName.equals(nameTicketingWrapper)) {
            newId = entity.getIdTicketing();
        }
        JSONObject jo = new JSONObject(originalJSON);
        jo.remove(idValue);
        jo.put(idValue, newId);
        return jo.toString();
    }
    
    private void handleException() {
        
    }
    
}
