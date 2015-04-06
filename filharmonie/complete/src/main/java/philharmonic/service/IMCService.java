/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package philharmonic.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import philharmonic.dao.IDao;
import philharmonic.model.MappedEntity;

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
    
    
    public void saveMappedResource(MappedEntity entity, String resourceName) {
        if(entity == null) {
            throw new IllegalArgumentException("MappedResource to save in db was null.");
        }
        if(resourceName == null)
            throw new IllegalArgumentException("Name of resource(table) to save in db was null.");
        if(resourceName.isEmpty())
            throw new IllegalArgumentException("Name of resource(table) to save in db was empty.");
        dao.create(entity, resourceName);
    }
    
    
    /*
     * Returns MappedEntity for given id in given component in mapped table
     * if there are more entities with the same id, returns the first one
     * if there are no entities, returns null
     */
     
    public MappedEntity getMappedEntity(int id, String resourceTableName, String componentName) {
        if(id == 0)
            return null;
        if(resourceTableName == null || componentName == null)
            return null;
        if(resourceTableName.isEmpty() || componentName.isEmpty())
            return null;
        return dao.get(id, resourceTableName, componentName);        
    }
    
   public void deleteEntity(int id, String resourceTableName, String componentName) {
        if(id == 0)
            return;
        if(resourceTableName == null || componentName == null)
            return;
        if(resourceTableName.isEmpty() || componentName.isEmpty())
            return;
        dao.delete(id, resourceTableName, componentName);
        
    }
   
   public void updateEntity(String resourceTableName, String setColumn, int setId, String whereColumn, int whereId) {
        if(whereId == 0)
            return;
        if(resourceTableName == null || setColumn == null || whereColumn == null)
            return;
        if(resourceTableName.isEmpty() || setColumn.isEmpty() || whereColumn.isEmpty())
            return;
        dao.update(resourceTableName, setColumn, setId, whereColumn, whereId);        
    }
    
    
}
