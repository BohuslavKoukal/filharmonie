/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package philharmonic.dao;

import java.util.List;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import philharmonic.dao.mapper.EntityRowMapper;
import philharmonic.model.Entity;
import static philharmonic.resources.StringConstants.*;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 *
 * @author Kookie
 */
@Repository
public class PostgresDao implements IDao {

    @Autowired
    private DataSource dataSource;

    public PostgresDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public PostgresDao() {
    }

    public void create(Entity entity) {
        JdbcTemplate insert = new JdbcTemplate(dataSource);
        insert.update("INSERT INTO ENTITY (IDWEB, IDORCHESTR, IDRUDOLF, IDTICKETING)"
                + " VALUES("
                + entity.getIdWeb() + ","
                + entity.getIdOrchestr() + ","
                + entity.getIdRudolf() + ","
                + entity.getIdTicketing() + ")");
    }

    public Entity get(int id) {
        throw new NotImplementedException();
    }
    
    public Entity get(int id, String componentName) {
        JdbcTemplate select = new JdbcTemplate(dataSource);
        String sqlQuery = createSelectQueryFor(id, componentName);
        List<Entity> rows = select.query(sqlQuery, new EntityRowMapper());
        if(!rows.isEmpty())
            return (Entity) rows.get(0);
        return null;
    }
    
    
    
    
    
    
    private String createSelectQueryFor(int id, String componentName) {
        String idName = "";
        if(componentName.equals(nameOrchestrWrapper)) {
            idName = "IDORCHESTR";
        }
        if(componentName.equals(nameRudolfWrapper)) {
            idName = "IDRUDOLF";
        }
        if(componentName.equals(nameWebWrapper)) {
            idName = "IDWEB";
        }
        if(componentName.equals(nameTicketingWrapper)) {
            idName = "IDTICKETING";
        }
        return "SELECT * FROM ENTITY WHERE " + idName + " = " + id;
    }

}
