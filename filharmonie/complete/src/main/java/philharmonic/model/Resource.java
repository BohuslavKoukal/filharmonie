/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package philharmonic.model;

/**
 *
 * @author Kookie
 */
public class Resource {
    private String name;
    private String idName;

    public Resource(String name, String idName) {
        this.name = name;
        this.idName = idName;
    }

    public String getName() {
        return name;
    }

    public void setTableName(String name) {
        this.name = name;
    }
    
    public String getIdName() {
        return idName;
    }

    public void setIdName(String idName) {
        this.idName = idName;
    }
    
    
}
