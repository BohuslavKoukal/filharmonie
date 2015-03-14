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

  void create(MappedEntity entity);
  
  MappedEntity get(int id, String componentName, String tableName);

}
