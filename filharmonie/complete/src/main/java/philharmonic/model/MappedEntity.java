/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package philharmonic.model;


/**
 *
 * @author Kookie
 */

public class MappedEntity {
    public int id;
    public int idOrchestr;
    public int idRudolf;
    public int idTicketing;
    
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 17 * hash + this.id;
        hash = 17 * hash + this.idOrchestr;
        hash = 17 * hash + this.idRudolf;
        hash = 17 * hash + this.idTicketing;
        return hash;
    }
    
    @Override
    public boolean equals(Object o) {
        try {
            MappedEntity me = (MappedEntity) o;
            return hashCode() == me.hashCode();
        }
        catch (Exception e) {
            return false;
        }
    }
    
    
}
