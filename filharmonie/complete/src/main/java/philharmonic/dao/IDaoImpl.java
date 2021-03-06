/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package philharmonic.dao;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import philharmonic.dao.mapper.EntityRowMapper;
import philharmonic.model.Component;
import philharmonic.model.MappedEntity;
import static philharmonic.resources.StringConstants.*;
import philharmonic.utilities.MappedEntityIdResolver;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 *
 * @author Kookie
 */
@Repository
public class IDaoImpl implements IDao {

    @Autowired
    private JdbcTemplate jt;

    @Autowired
    private MappedEntityIdResolver resolver;

    public IDaoImpl() {
        resolver = new MappedEntityIdResolver();
    }

    @Override
    public void create(MappedEntity entity, String tableName) {
        
        String idNames = buildIdNames(entity);
        String idValues = buildIdValues(entity);
        
        
        jt.update("INSERT INTO " + tableName + "(" + idNames + ")"
                + " VALUES(" + idValues + ")");
    }
    
    private boolean valueIs0(MappedEntity entity, Component c) {
        return resolver.getIdValue(entity, c.getComponentName()) == 0;
    }
    
    private String buildIdNames(MappedEntity entity) {
        String ret = "";        
        for (Component c : getMappedComponents()) {
            if(!valueIs0(entity, c)) {
                ret += c.getIdName() + ", ";                
            }
        }
        if(ret.substring(ret.length()-2).equals(", "))
            ret = ret.substring(0, ret.length()-2);
        return ret;
    }
    
    private String buildIdValues(MappedEntity entity) {
        String ret = "";        
        for (Component c : getMappedComponents()) {
            if(!valueIs0(entity, c)) {
                ret += resolver.getIdValue(entity, c.getComponentName()) + ", ";
            }            
        }
        if(ret.substring(ret.length()-2).equals(", "))
            ret = ret.substring(0, ret.length()-2);
        return ret;
    }

    @Override
    public MappedEntity get(int id, String tableName, String componentName) {
        String sqlQuery = createSelectQueryFor(id, tableName, componentName);
        List<MappedEntity> rows = jt.query(sqlQuery, new EntityRowMapper());
        if (!rows.isEmpty()) {
            return (MappedEntity) rows.get(0);
        }
        return null;
    }
    
    @Override
    public void delete(int id, String tableName, String componentName) {
        String sqlQuery = createDeleteQueryFor(id, tableName, componentName);
        jt.execute(sqlQuery);
    }
    
    @Override
    public void update(String resourceTableName, String setColumn, int setId, String whereColumn, int whereId) {
        String sqlQuery = createUpdateQueryFor(resourceTableName, setColumn, setId, whereColumn, whereId);
        jt.execute(sqlQuery);
    }

    private String createSelectQueryFor(int id, String tableName, String componentName) {
        String idName = resolver.getIdName(componentName);
        return "SELECT * FROM " + tableName + " WHERE " + idName + " = " + id;
    }

    private String createDeleteQueryFor(int id, String tableName, String componentName) {
        String idName = resolver.getIdName(componentName);
        return "DELETE FROM " + tableName + " WHERE " + idName + " = " + id;
    }
    
    private String createUpdateQueryFor(String tableName, String setColumn, int setId, String whereColumn, int whereId) {
        String setIdName = resolver.getIdName(setColumn);
        String whereIdName = resolver.getIdName(whereColumn);
        return "UPDATE " + tableName +
                " SET " + setIdName + " = " + setId + 
                " WHERE " + whereIdName + " = " + whereId;
    }

}
