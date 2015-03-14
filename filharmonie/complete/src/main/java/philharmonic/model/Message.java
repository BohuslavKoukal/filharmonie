/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package philharmonic.model;

import java.util.List;

/**
 *
 * @author Kookie
 */
public class Message {
    String action;
    String resourceName;
    String targetComponentName;
    List<String> neededIds;


    public Message(String action,
            String resourceName,
            String targetComponentName,
            List<String> neededIds) 
    {
        this.action = action;
        this.resourceName = resourceName;
        this.targetComponentName = targetComponentName;
        this.neededIds = neededIds;
        
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

    public List<String> getNeededIds() {
        return neededIds;
    }

    public void setNeededIds(List<String> neededIds) {
        this.neededIds = neededIds;
    }

    
    
    
    
    
}
