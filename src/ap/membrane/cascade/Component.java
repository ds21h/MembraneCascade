/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ap.membrane.cascade;

/**
 *
 * @author Afke
 */
class Component {
    private int mId;
    private String mName;
    private double mMolWeight;
    private double mConcentration;
    
    Component(int pId, String pName, double pMolWeight, double pConcentration){
        mId = pId;
        mName = pName;
        mMolWeight = pMolWeight;
        mConcentration = pConcentration;
    }
    
    double xConcentration(){
        return mConcentration / mMolWeight;
    }
    
    double xConcentrationMass(){
        return mConcentration;
    }
    
    double xMolWeight(){
        return mMolWeight;
    }
}
