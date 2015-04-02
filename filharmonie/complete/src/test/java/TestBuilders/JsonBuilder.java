/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package TestBuilders;

import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Kookie
 */
public class JsonBuilder {

    public int id;
    
    public JsonBuilder(int allIds) {
        id = allIds;
    }
    
    public JsonBuilder() {
        id = 0;
    }
    
    public static String invalidate(String json) {
        return json + "jochochou(xx...){}";
    }

}
