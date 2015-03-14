/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package philharmonic.model;

/**
 *
 * @author Kookie
 */
public class Enum {
    private String propertyName;
    private String tableName;

    public Enum(String propertyName, String tableName) {
        this.propertyName = propertyName;
        this.tableName = tableName;
    }

    public Enum() {
    }
    
    
    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
    
    
}
