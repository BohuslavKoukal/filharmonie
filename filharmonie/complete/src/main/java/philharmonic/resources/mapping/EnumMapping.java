/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package philharmonic.resources.mapping;

import java.util.ArrayList;
import java.util.List;
import philharmonic.model.Enum;

/**
 *
 * @author Kookie
 */
public class CPActionEnumMapping {
    // mapped properties
    public static final Enum CPAction = new Enum("id", "CPAction");
    
    public static final Enum place = new Enum("placeId", "EnumPlace");
    public static final Enum category = new Enum("categoryId", "EnumCategory");
    public static final Enum cycle = new Enum("cycleId", "EnumCycle");

    // Intersystem mapping enums
    public static List<Enum> getMappedEnums() {
        return new ArrayList<Enum>() {
            {
                add(place);
                add(category);
                add(cycle);
            }
        };
    }
}
