/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package philharmonic.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import philharmonic.model.Entity;

/**
 *
 * @author Kookie
 */
public class EntityResultSetExtractor implements ResultSetExtractor {

    public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
    Entity entity = new Entity();
    entity.setId((int)rs.getLong("id"));
    entity.setIdOrchestr((int)rs.getLong("idOrchestr"));
    entity.setIdRudolf((int)rs.getLong("idRudolf"));
    entity.setIdTicketing((int)rs.getLong("idTicketing"));
    entity.setIdWeb((int)rs.getLong("idWeb"));
    return entity;
    }
    
}
