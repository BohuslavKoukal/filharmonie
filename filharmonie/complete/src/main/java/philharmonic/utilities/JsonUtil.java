/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package philharmonic.utilities;

import java.io.IOException;
import java.util.List;
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
    
    public int getResourceIdInTarget(int sourceId, String resourceName, String sourceComponentName, String targetComponentName) {
        MappedEntity entity = service.getMappedEntity(sourceId, resourceName, sourceComponentName);
        if (entity == null) {
            return 0;            
        }
        else {
            return resolver.getIdValue(entity, targetComponentName);
        }
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
        for (Enum enume : getMappedEnums(resourceName)) {
            JsonNode stringId = mapper.readTree(originalJSON).findValue(enume.getIdName());
            if(stringId == null) {
                continue;
            }
            int sourceId = stringId.asInt();
            jo.remove(enume.getIdName());
            // vezmi si z databaze ten enum
            MappedEntity entity = service.getMappedEntity(sourceId, enume.getTableName(), sourceComponentName);
            // enum v databazi neexistuje
            // zeptej se zdroje na jeho textovou reprezentaci, posli ji cili, namapuj si ji a pak pokracuj
            if(entity == null) {
                jo.put(enume.getIdName(), 0);
            }
            else {
                int targetId = resolver.getIdValue(entity, targetComponentName);            
                jo.put(enume.getIdName(), targetId);
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
    
    
    public int getEnumId(String JSON, String enumIdName) throws IOException {
        JsonNode stringId = mapper.readTree(JSON).findValue(enumIdName);
        if(stringId == null) return 0;
        else
            return stringId.asInt();
    }
}
