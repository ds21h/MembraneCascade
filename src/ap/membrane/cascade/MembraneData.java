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
class MembraneData {
    private String mType;
    private int mPressure;
    private double mPermeateFlow;
    private double[] mMassFraction;
     
    MembraneData(String pType, int pPressure, double pPermeateFlow, double pFrac1, double pFrac2, double pFrac3, double pFrac4, double pFrac5, double pFrac6){
        mMassFraction = new double[6];
        mType = pType;
        mPressure = pPressure;
        mPermeateFlow = pPermeateFlow;
        mMassFraction[0] = pFrac1;
        mMassFraction[1] = pFrac2;
        mMassFraction[2] = pFrac3;
        mMassFraction[3] = pFrac4;
        mMassFraction[4] = pFrac5;
        mMassFraction[5] = pFrac6;
    }
    
    String xType(){
        return mType;
    }
    
    int xPressure(){
        return mPressure;
    }
    
    double xPermeateFlow(){
        return mPermeateFlow;
    }
    
    double[] xMassFraction(){
        return mMassFraction;
    }
}
