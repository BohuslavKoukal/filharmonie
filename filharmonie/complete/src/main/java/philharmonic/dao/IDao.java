/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package philharmonic.dao;

import org.springframework.stereotype.Repository;
import philharmonic.model.MappedEntity;

/**
 *
 * @author Kookie
 */
@Repository
public interface IDao {

    void create(MappedEntity entity, String tableName);

    MappedEntity get(int id, String tableName, String componentName);

    void delete(int id, String tableName, String componentName);
    
    void update(String resourceTableName, String setColumn, int setId, String whereColumn, int whereId);
}
