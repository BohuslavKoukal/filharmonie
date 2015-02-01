/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package philharmonic.service;

import java.util.List;
import philharmonic.utilities.MessagesParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import philharmonic.dao.PostgresDao;
import philharmonic.model.Entity;
import philharmonic.model.Message;

/**
 *
 * @author Kookie
 */

@Service
public class IMCService {
    MessagesParser parser = new MessagesParser();
    PostgresDao dao;

    @Autowired
    public IMCService(PostgresDao dao) {
        this.dao = dao;
    }
    
    @Transactional
    public void saveEntity(Entity e) {
        dao.create(e);
    }
    
    // Returns Entity for given id in given component
    // if there are more entities with the same id, returns the first one
    // if there are no entities, returns null
    public Entity getEntity(int id, String componentName) {
        return dao.get(id, componentName);
    }
    
    public List<Message> getMessagesFor(String resourceName, String actionName) {
        return parser.getRequiredMessagesFor(resourceName, actionName);
    }
    
}
