/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package philharmonic.utilities;

import philharmonic.model.MappedEntity;
import static philharmonic.resources.StringConstants.*;

/**
 *
 * @author Kookie
 */

public class MappedEntityIdResolver {
    
    public int getIdValue(MappedEntity entity, String componentName) {
        switch (componentName) {
            case middleComponentName:
                return entity.id;
            case orchestrComponentName:
                return entity.idOrchestr;
            case rudolfComponentName:
                return entity.idRudolf;
            case ticketingComponentName:
                return entity.idTicketing;
            case teploComponentName:
                return entity.idTeplo;
            case webComponentName:
                return entity.idWeb;
        }
        return 0;
    }
    
    public void setId(MappedEntity entity, int id, String componentName) {
        switch (componentName) {
            case middleComponentName:
                entity.id = id;
                break;
            case orchestrComponentName:
                entity.idOrchestr = id;
                break;
            case rudolfComponentName:
                entity.idRudolf = id;
                break;
            case ticketingComponentName:
                entity.idTicketing = id;
                break;
            case teploComponentName:
                entity.idTeplo = id;
                break;
            case webComponentName:
                entity.idWeb = id;
                break;
        }
    }
    
    public String getIdName(String componentName) {
        switch (componentName) {
            case middleComponentName:
                return middleComponentIdName;
            case orchestrComponentName:
                return orchestrComponentIdName;
            case rudolfComponentName:
                return rudolfComponentIdName;
            case ticketingComponentName:
                return ticketingComponentIdName;
            case teploComponentName:
                return teploComponentIdName;
            case webComponentName:
                return webComponentIdName;
        }
        return "";
    }
    
}
