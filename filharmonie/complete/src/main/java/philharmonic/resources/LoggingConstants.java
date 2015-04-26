/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package philharmonic.resources;

/**
 *
 * @author Kookie
 */
public class LoggingConstants {  
    public static final String sendingMessage = "Sending message \n";
    public static final String messageResponse = "Message response was \n";
    public static final String validating = "Validating \n";
    public static final String returning = "Returning: \n";
    public static final String invokingCPActionPOST = "CPAction POST was called \n";
    public static final String invokingCPActionPUT = "CPAction PUT was called \n";
    public static final String invokingExternalActionPOST = "ExternalAction POST was called \n";
    public static final String invokingExternalActionPUT = "ExternalAction PUT was called \n";
    public static final String invokingItemPUT = "Item PUT was called \n";
    public static final String invokingItemPOST = "Item POST was called \n";
    public static final String invokingItemDELETE = "Item DELETE was called \n";
    public static final String redirectingToPOST = "Entity does not exist => Redirecting to POST \n";
    public static final String finish = "\n################################################################## \n\n\n\n";
    
    
    public static final String errorWhileDeletingResourceId = "There was an error while trying to remove resource id. ";
    public static final String errorWhileShiftingResourceIds = "There was an error while trying to shift resource ids. ";
    public static final String errorWhileShiftingEnumIds = "There was an error while trying to shift enum ids. ";
    public static final String errorWhileAddingResourceId = "There was an error while trying to add resource id. ";
    public static final String errorWhileMappingEnums = "There was an error while mapping enums. ";
    
    public static final String exceptionThrown = "Following error occurred: \n";
    
    public static final String resourceAccessException = "Resource access exception (target component address was not found). Target component name: \n";
    public static final String httpClientErrorException = "Http client exception (conflict or error in target component). Target component name: \n";
    public static final String anotherTargetException = "Something wrong happened in target system (see the log). Target component name: \n";
    public static final String responseBodyError = "Response body from target component was not in specified format. Target component name: \n";
}
