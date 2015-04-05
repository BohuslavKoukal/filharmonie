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
    private String idName;
    private String tableName;

    public Enum(String tableName, String idName) {
        this.tableName = tableName;
        this.idName = idName;
        
    }

    public Enum() {
    }
    
    
    public String getIdName() {
        return idName;
    }

    public void setIdName(String idName) {
        this.idName = idName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
    
    
}
