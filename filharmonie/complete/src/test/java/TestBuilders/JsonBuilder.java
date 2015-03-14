/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package TestBuilders;

import org.json.JSONException;
import org.json.JSONObject;
import static philharmonic.resources.mapping.CPActionEnumMapping.*;

/**
 *
 * @author Kookie
 */
public class JsonBuilder {

    public int id;
    public int placeId;
    public int categoryId;
    public int cycleId;
    
    public JsonBuilder(int allIds) {
        id = allIds;
        placeId = allIds;
        categoryId = allIds;
        cycleId = allIds;
    }
    
    public JsonBuilder() {
        id = 0;
        placeId = 0;
        categoryId = 0;
        cycleId = 0;
    }

    public String build() {
        try {
            JSONObject jo = new JSONObject();
            jo.put(CPAction.getPropertyName(), id);
            jo.put(place.getPropertyName(), placeId);
            jo.put(category.getPropertyName(), categoryId);
            jo.put(cycle.getPropertyName(), cycleId);
            return jo.toString();
        } catch (Exception e) {
            return "";
        }
    }
    
    public static String invalidate(String json) {
        return json + "jochochou(xx...){}";
    }
    
    public String withNoId() throws JSONException {
        JSONObject jo = new JSONObject(build());
        jo.remove(CPAction.getPropertyName());
        return jo.toString();
    }
    
    public JsonBuilder withId(int id) {
        this.id = id;
        return this;
    }
    
    public JsonBuilder withPlaceId(int placeId) {
        this.placeId = placeId;
        return this;
    }
    
    public JsonBuilder withCategoryId(int categoryId) {
        this.categoryId = categoryId;
        return this;
    }
    
    public JsonBuilder withCycleId(int cycleId) {
        this.cycleId = cycleId;
        return this;
    }
}
