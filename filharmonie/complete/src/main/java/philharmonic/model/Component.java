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
public class Component {
    String componentName;
    String idName;

    public Component() {
    }

    public Component(String componentName, String idName) {
        this.componentName = componentName;
        this.idName = idName;
    }

    public String getComponentName() {
        return componentName;
    }

    public String getIdName() {
        return idName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    public void setIdName(String idName) {
        this.idName = idName;
    }
    
    
}
