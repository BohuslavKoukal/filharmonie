/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package philharmonic.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import philharmonic.model.Entity;

/**
 *
 * @author Kookie
 */
public class EntityRowMapper implements RowMapper {

    public Object mapRow(ResultSet rs, int i) throws SQLException {
        EntityResultSetExtractor extractor = new EntityResultSetExtractor();
        return extractor.extractData(rs);
    }
    
}
