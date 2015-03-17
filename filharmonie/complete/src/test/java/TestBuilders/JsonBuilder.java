/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package TestBuilders;

import org.json.JSONException;
import org.json.JSONObject;
import static philharmonic.resources.mapping.EnumMapping.*;

/**
 *
 * @author Kookie
 */
public class JsonBuilder {

    public int id;
    public int placeId;
    public int categoryId;
    public int cycleId;
    public int itemSubjectId;
    
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

    public String build(String resource) {
        switch(resource) {
            case("CPAction"):
                return buildCPAction();
            case("Item"):
                return buildItem();
        }
        return "";
        
    }
    
    private String buildCPAction() {
        try {
            JSONObject jo = new JSONObject();
            jo.put(CPAction.getIdName(), id);
            jo.put(place.getPropertyName(), placeId);
            jo.put(category.getPropertyName(), categoryId);
            jo.put(cycle.getPropertyName(), cycleId);
            return jo.toString();
        } catch (Exception e) {
            return "";
        }
    }
    
    private String buildItem() {
        try {
            JSONObject jo = new JSONObject();
            jo.put(Item.getIdName(), id);
            jo.put(itemSubject.getPropertyName(), itemSubjectId);
            return jo.toString();
        } catch (Exception e) {
            return "";
        }
    }
    
    public static String invalidate(String json) {
        return json + "jochochou(xx...){}";
    }
    
    public String withNoId(String resource) throws JSONException {
        JSONObject jo = new JSONObject(build(resource));
        jo.remove("id");
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
    
    public JsonBuilder withItemSubjectId(int itemSubjectId) {
        this.itemSubjectId = itemSubjectId;
        return this;
    }
    
}
