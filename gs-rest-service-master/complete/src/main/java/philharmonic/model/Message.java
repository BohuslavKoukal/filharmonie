/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package philharmonic.model;

/**
 *
 * @author Kookie
 */
public class Message {
    String action;
    String resourceName;
    String targetComponentName;

    public Message(String action, String resourceName, String targetComponentName) {
        this.action = action;
        this.resourceName = resourceName;
        this.targetComponentName = targetComponentName;
    }

    public Message() {
    }

    
    
    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getTargetComponentName() {
        return targetComponentName;
    }

    public void setTargetComponentName(String targetComponentName) {
        this.targetComponentName = targetComponentName;
    }

    
    
    
    
    
}
