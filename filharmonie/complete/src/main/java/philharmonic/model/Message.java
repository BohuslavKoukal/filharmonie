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

    public String getAction() {
        return action;
    }

    public String getResourceName() {
        return resourceName;
    }

    public String getTargetComponentName() {
        return targetComponentName;
    }

    public List<String> getNeededIds() {
        return neededIds;
    }
    
    @Override
    public int hashCode() {
        return new StringBuilder().append(action).append(resourceName).append(targetComponentName)
                .toString().hashCode();
    }

    
    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        Message m = (Message) o;
        return(this.hashCode() == m.hashCode());
    }
}
