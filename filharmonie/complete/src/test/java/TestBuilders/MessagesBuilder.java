/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TestBuilders;

import java.util.ArrayList;
import java.util.List;
import philharmonic.model.Message;
import static philharmonic.resources.StringConstants.*;


/**
 *
 * @author Kookie
 */
public class MessagesBuilder {
    String action;
    String resourceName;
    String targetComponentName;
    List<String> neededIds;

    public Message build() {
        return new Message(action, resourceName, targetComponentName, neededIds);
    }
    
    public List<Message> buildSampleMessages(String action) {
        List<Message> messages = new ArrayList<Message>();
        // will be send two messages - to ticketing and to web
        messages.add(new MessagesBuilder()
                .withAction(action)
                .withNeededIds(null)
                .withResourceName(resourceNameCPAction)
                .withTargetComponentName(rudolfComponentName)
                .build());
        List<String> neededIds = new ArrayList<String>();
        neededIds.add(rudolfComponentName);
        messages.add(new MessagesBuilder()
                .withAction(action)
                .withNeededIds(neededIds)
                .withResourceName(resourceNameCPAction)
                .withTargetComponentName(orchestrComponentName)
                .build());
        return messages;
    }
    
    public MessagesBuilder withAction(String action) {
        this.action = action;
        return this;
    }
    
    public MessagesBuilder withResourceName(String resourceName) {
        this.resourceName = resourceName;
        return this;
    }
    
    public MessagesBuilder withTargetComponentName(String targetComponentName) {
        this.targetComponentName = targetComponentName;
        return this;
    }
    
public MessagesBuilder withNeededIds(List<String> neededIds) {
        this.neededIds = neededIds;
        return this;
    }
    
}
