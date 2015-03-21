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
public class EnumMapping {

    // mapped properties    
    public static final Enum place = new Enum("EnumPlace", "placeId");
    public static final Enum category = new Enum("EnumCategory", "categoryId");
    public static final Enum cycle = new Enum("EnumCycle", "cycleId");
    public static final Enum itemSubject = new Enum("EnumItemSubject", "itemSubjectId");

    // Intersystem mapping enums
    public static List<Enum> getMappedEnums(String resourceName) {

        switch (resourceName) {
            case "CPAction":
                return new ArrayList<Enum>() {
                    {
                        add(place);
                        add(category);
                        add(cycle);
                    }
                };
            case "Item":
                return new ArrayList<Enum>() {
                    {
                        add(itemSubject);
                    }
                };
        }
        return new ArrayList();

    }

}
