/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package philharmonic.dao;

import java.util.List;
import javax.sql.DataSource;
import philharmonic.model.Entity;

/**
 *
 * @author Kookie
 */
public interface IDao {

  void create(Entity entity);

  Entity get(int id);
  
  Entity get(int id, String componentName);


}
