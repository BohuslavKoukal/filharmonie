/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package philharmonic.resources;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;
import philharmonic.model.Component;
import philharmonic.utilities.MessagesParser;

/**
 *
 * @author Kookie
 */
public class StringConstants {
    private static Logger logger = Logger.getLogger(StringConstants.class);
          
    // Components   
    public static final String middleComponentName = "IMC";
    public static final String middleComponentIdName = "id";
    
    public static final String orchestrComponentName = "orchestr";
    public static final String orchestrComponentIdName = "idOrchestr";
    
    public static final String rudolfComponentName = "rudolf";
    public static final String rudolfComponentIdName = "idRudolf";
    
    public static final String ticketingComponentName = "ticketing";
    public static final String ticketingComponentIdName = "idTicketing";
    
    // Component addressing
    public static final String addressMiddleComponent = "/" + middleComponentName;
    public static final String addressOrchestrComponent = "/" + orchestrComponentName;
    public static final String addressRudolfComponent = "/" + rudolfComponentName;
    public static final String addressTicketingComponent = "/" + ticketingComponentName;
    
    // Intersystem mapping components
    public static final List<Component> getMappedComponents() {
        return new ArrayList<Component>() {
            {
                add(new Component(orchestrComponentName, orchestrComponentIdName));
                add(new Component(rudolfComponentName, rudolfComponentIdName));
                add(new Component(ticketingComponentName, ticketingComponentIdName));
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
    
}
