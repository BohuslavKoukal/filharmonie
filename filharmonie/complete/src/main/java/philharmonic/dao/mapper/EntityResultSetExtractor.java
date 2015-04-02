/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package philharmonic.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import philharmonic.model.Component;
import philharmonic.model.MappedEntity;
import static philharmonic.resources.StringConstants.*;
import philharmonic.utilities.MappedEntityIdResolver;

/**
 *
 * @author Kookie
 */

@org.springframework.stereotype.Component
public class EntityResultSetExtractor implements ResultSetExtractor {
    
    @Autowired
    MappedEntityIdResolver resolver;
    
    public EntityResultSetExtractor() {
        resolver = new MappedEntityIdResolver();
    }
    
    @Override
    public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
        MappedEntity entity = new MappedEntity();
        entity.id = (int) rs.getLong("id");

            for (Component component : getMappedComponents()) {
                
                try {
                    int idToSet = (int)rs.getLong(component.getIdName());
                    resolver.setId(entity, idToSet, component.getComponentName());
                }
                catch (Exception e) {
                    // setting id for all components needs to be changed in enums
                }
                
            }

        

        
        
//        entity.setIdOrchestr((int) rs.getLong("idOrchestr"));
//        entity.setIdRudolf((int) rs.getLong("idRudolf"));
//        entity.setIdTicketing((int) rs.getLong("idTicketing"));
//        entity.setIdWeb((int) rs.getLong("idWeb"));
        
        return entity;
    }
    
}
