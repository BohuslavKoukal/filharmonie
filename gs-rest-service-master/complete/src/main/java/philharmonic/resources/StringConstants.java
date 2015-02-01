/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package philharmonic.resources;

/**
 *
 * @author Kookie
 */
public class StringConstants {
    // These resource strings must match their equivalents in Messages.xml
    
    // Component names    
    public static final String nameMiddleComponent = "IMC";
    public static final String nameOrchestrWrapper = "orchestr";
    public static final String nameRudolfWrapper = "rudolf";
    public static final String nameTicketingWrapper = "ticketing";
    public static final String nameWebWrapper = "web";
    public static final String nameMailerComponent = "mailer";
    
    // Component addressing
    public static final String addressMiddleComponent = "/" + nameMiddleComponent;
    public static final String addressOrchestrWrapper = "/" + nameOrchestrWrapper;
    public static final String addressRudolfWrapper = "/" + nameRudolfWrapper;
    public static final String addressTicketingWrapper = "/" + nameTicketingWrapper;
    public static final String addressWebWrapper = "/" + nameWebWrapper;
    public static final String addressMailerComponent = "/" + nameMailerComponent;
    
    // Resource names    
    public static final String resourceNameCPAction = "CPAction";
    public static final String resourceNameExternalACtion = "ExternalAction";
    public static final String resourceNameItem = "Item";
    public static final String resourceNameTickets = "Tickets";
    
    // Data sets addressing  
    public static final String resourceAddressCPAction = "/" + resourceNameCPAction;
    public static final String resourceAddressExternalACtion = "/" + resourceNameExternalACtion;
    public static final String resourceAddressItem = "/" + resourceNameItem;
    public static final String resourceAddressTickets = "/" + resourceNameTickets;
    
    // Variables constants
    public static final String idValue = "id";
    
    // View names
    public static final String viewOrchestr = "orchestr";
    
    // Server address
    public static final String serverAddress = "http://localhost:8080";
    
    // REST actions names
    public static final String namePUTAction = "PUT";
    public static final String namePOSTAction = "POST";
    
    // Random return values
    public static String getRandomIdJSON() {
        return "{\"" + idValue + "\": \"" + getRandomId() +" \"}";
    }    
    
    private static int getRandomId() {
        return (int)(Math.random() * 1000);
    }
    
}
