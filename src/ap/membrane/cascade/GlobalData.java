/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ap.membrane.cascade;

import java.util.List;

/**
 *
 * @author Afke
 */
class GlobalData {
//    static DataBase gDatabase;
    static List<Configuration> gConfigurations;
    static List<MembraneData> gMembranes;
    static List<Component> gComponents;
    static List<String> gMembTypes;
    static List<Integer> gPressures;
    static RunData gRunData;
//    static OutFile gOutput;
    
    private GlobalData(){
//  Empty private constructor so no instances can be created        
    }
    
    static MembraneData xMembrane(String pType, int pPressure){
        MembraneData lData;
        int lTeller;
        
        lData = null;
        for (lTeller = 0; lTeller < gMembranes.size(); lTeller++){
            lData = gMembranes.get(lTeller);
            if (lData.xType().equals(pType) && lData.xPressure() == pPressure){
                break;
            }
        }
        if (lTeller < gMembranes.size()){
            return lData;
        } else {
            return null;
        }
    }
}
