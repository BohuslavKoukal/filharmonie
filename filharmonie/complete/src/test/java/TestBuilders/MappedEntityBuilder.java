/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package TestBuilders;

import philharmonic.model.MappedEntity;

/**
 *
 * @author Kookie
 */
public class MappedEntityBuilder {
    public int id;
    public int idOrchestr;
    public int idRudolf;

    public MappedEntity build() {
        MappedEntity me = new MappedEntity();
        me.id = id; me.idOrchestr = idOrchestr; me.idRudolf = idRudolf;
        return me;
    }
    
    public MappedEntityBuilder withId(int id) {
        this.id = id;
        return this;
    }
    
    public MappedEntityBuilder withIdOrchestr(int idOrchestr) {
        this.idOrchestr = idOrchestr;
        return this;
    }
    
    public MappedEntityBuilder withIdRudolf(int idRudolf) {
        this.idRudolf = idRudolf;
        return this;
    }
}
