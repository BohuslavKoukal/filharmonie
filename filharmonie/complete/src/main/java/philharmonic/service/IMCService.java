/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package philharmonic.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import philharmonic.dao.IDao;
import philharmonic.model.MappedEntity;
import philharmonic.model.MappedResource;

/**
 *
 * @author Kookie
 */

@Service
public class IMCService {
    
    @Autowired
    IDao dao;


    public IMCService(IDao dao) {
        this.dao = dao;
    }
    
    public IMCService() {
    }
    
    
    public void saveMappedResource(MappedResource resource) {
        if(resource == null) {
            throw new IllegalArgumentException("MappedResource to save in db was null.");
        }
            
        dao.create(resource);
    }
    
    
    /*
     * Returns MappedEntity for given id in given component in mapped table
     * if there are more entities with the same id, returns the first one
     * if there are no entities, returns null
     */
     
    public MappedEntity getMappedEntity(int id, String componentName, String tableName) {
        if(id == 0 || componentName == null || tableName == null)
            return null;
        if(componentName.isEmpty() || tableName.isEmpty())
            return null;
        return dao.get(id, componentName, tableName);
        
    }
    
   
    
    
}
