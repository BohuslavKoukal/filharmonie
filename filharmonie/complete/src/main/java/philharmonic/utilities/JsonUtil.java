/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package philharmonic.utilities;

import java.io.IOException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import philharmonic.model.MappedEntity;
import philharmonic.model.Enum;
import static philharmonic.resources.mapping.EnumMapping.*;
import static philharmonic.resources.StringConstants.*;
import philharmonic.service.IMCService;

/**
 *
 * @author Kookie
 */
public class JsonUtil {
    
    private ObjectMapper mapper;
    
    @Autowired
    private MappedEntityIdResolver resolver;
    
    @Autowired
    private IMCService service;

    public JsonUtil() {
        mapper = new ObjectMapper();
    }
    
    
    /*
     * Take originalJSON, get its entity from DB
     * remove original ID from JSON and put there ID of targetComponent instead
     * return changed JSON
     */
    public String shiftResourceIdsInJSON(String originalJSON, String resourceName, String sourceComponentName, String targetComponentName) throws JSONException, IOException {
        int sourceId = mapper.readTree(originalJSON).findValue(idName).asInt();
        MappedEntity entity = service.getMappedEntity(sourceId, resourceName, sourceComponentName);
        JSONObject jo = new JSONObject(originalJSON);
        jo.remove(idName);
        if (entity == null) {
            jo.put(idName, 0);            
        }
        else {
            int targetId = resolver.getIdValue(entity, targetComponentName);
            jo.put(idName, targetId);
        }
        return jo.toString();
    }

    
    /*
     * For each mapped enum defined in MappingConstants
     * remove original enum ID from JSON and put there ID of enum in targetComponent instead
     * return changed JSON
     */
    public String shiftEnumIdsInJSON(String originalJSON, String resourceName, String sourceComponentName, String targetComponentName)
            throws JSONException, IOException
    {
        JSONObject jo = new JSONObject(originalJSON);
        for (Enum property : getMappedEnums(resourceName)) {
            JsonNode stringId = mapper.readTree(originalJSON).findValue(property.getPropertyName());
            if(stringId == null) {
                jo.put(property.getPropertyName(), 0);
                continue;
            }
            int sourceId = stringId.asInt();
            jo.remove(property.getPropertyName());
            MappedEntity entity = service.getMappedEntity(sourceId, property.getTableName(), sourceComponentName);
            if(entity == null) {
                jo.put(property.getPropertyName(), 0);
            }
            else {
                int targetId = resolver.getIdValue(entity, targetComponentName);            
                jo.put(property.getPropertyName(), targetId);
            }            
        }
        return jo.toString();
    }
    
    /*
     * Remove original ID from JSON and put there ID 0 instead
     * return changed JSON
     */
    public String nullResourceIdInJSON(String originalJSON, String resourceIdName)
            throws JSONException
    {
        JSONObject jo = new JSONObject(originalJSON);
        jo.remove(resourceIdName);
        jo.put(resourceIdName, 0);
        return jo.toString();
    }
    
    public String addResourceIdToJSON(String originalJSON, String componentName, int idValue)
            throws JSONException
    {
        JSONObject jo = new JSONObject(originalJSON);
        jo.put(resolver.getIdName(componentName), idValue);
        return jo.toString();
    }
}
