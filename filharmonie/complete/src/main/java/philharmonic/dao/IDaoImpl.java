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
    public void create(MappedEntity entity) {

        String idNames = "";
        for (Component c : getMappedComponents()) {
            idNames += c.getIdName();
            // if this is not last element
            if (!c.getComponentName().equals(
                    getMappedComponents().get(getMappedComponents().size() - 1).getComponentName())) {
                idNames += ", ";
            }

        }

        String idValues = "";
        for (Component c : getMappedComponents()) {
            idValues += resolver.getIdValue(entity, c.getComponentName());
            // if this is not last element
            if (!c.getComponentName().equals(
                    getMappedComponents().get(getMappedComponents().size() - 1).getComponentName())) {
                idValues += ", ";
            }
        }
        jt.update("INSERT INTO CPAction (" + idNames + ")"
                + " VALUES(" + idValues + ")");
    }

    @Override
    public MappedEntity get(int id, String componentName, String tableName) {
        String sqlQuery = createSelectQueryFor(id, componentName, tableName);
        List<MappedEntity> rows = jt.query(sqlQuery, new EntityRowMapper());
        if (!rows.isEmpty()) {
            return (MappedEntity) rows.get(0);
        }
        return null;
    }

    private String createSelectQueryFor(int id, String componentName, String tableName) {
        String idName = resolver.getIdName(componentName);
        return "SELECT * FROM " + tableName + " WHERE " + idName + " = " + id;
    }

}
