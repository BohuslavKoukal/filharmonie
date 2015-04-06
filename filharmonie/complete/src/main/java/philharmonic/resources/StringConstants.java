/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package philharmonic.resources;

import java.util.ArrayList;
import java.util.List;
import philharmonic.model.Component;
import static philharmonic.resources.mapping.EnumMapping.category;
import static philharmonic.resources.mapping.EnumMapping.cycle;
import static philharmonic.resources.mapping.EnumMapping.itemSubject;
import static philharmonic.resources.mapping.EnumMapping.place;

/**
 *
 * @author Kookie
 */
public class StringConstants {
          
    // Components   
    public static final String middleComponentName = "IMC";
    public static final String middleComponentIdName = "id";
    
    public static final String orchestrComponentName = "orchestr";
    public static final String orchestrComponentIdName = "idOrchestr";
    
    public static final String rudolfComponentName = "rudolf";
    public static final String rudolfComponentIdName = "idRudolf";
    
    public static final String ticketingComponentName = "ticketing";
    public static final String ticketingComponentIdName = "idTicketing";
    
    public static final String teploComponentName = "teplo";
    public static final String teploComponentIdName = "idTeplo";
    
    public static final String mailerComponentName = "mailer";
    
    // Component addressing
    public static final String addressMiddleComponent = "/" + middleComponentName;
    public static final String addressOrchestrComponent = "/" + orchestrComponentName;
    public static final String addressRudolfComponent = "/" + rudolfComponentName;
    public static final String addressTicketingComponent = "/" + ticketingComponentName;
    public static final String addressTeploComponent = "/" + teploComponentName;
    public static final String addressMailerComponent = "/" + mailerComponentName;
    
    // Intersystem mapping components
    public static final List<Component> getMappedComponents() {
        return new ArrayList<Component>() {
            {
                add(new Component(orchestrComponentName, orchestrComponentIdName));
                add(new Component(rudolfComponentName, rudolfComponentIdName));
                add(new Component(ticketingComponentName, ticketingComponentIdName));
                add(new Component(teploComponentName, teploComponentIdName));
            }
        };
    }
    
    
    // Resource names    
    public static final String CPAction = "CPAction";
    public static final String Item = "Item";
    
    public static final String idName = "id";
    
    // Resource addressing  
    public static final String resourceAddressCPAction = "/" + CPAction;
    public static final String resourceAddressItem = "/" + Item;
    
    
    
    // REST actions names
    public static final String namePUTAction = "PUT";
    public static final String namePOSTAction = "POST";
    public static final String nameDELETEAction = "DELETE";
    public static final String nameGETAction = "GET";
}
