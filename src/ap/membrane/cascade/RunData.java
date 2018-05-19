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
class RunData {
    static final int RunTypeSingle = 1;
    static final int RunTypeData = 2;
    static final int RunTypeOptimize = 3;
    
    private double mInFlow;
    private double mMinRetFlow;
    private int mMaxSurface;
    private boolean mComp;
    private int mRunType;
    private String mLocale;
    private int mConf;
    private String mS1Type;
    private String mS2Type;
    private String mS3Type;
    private int mS1Pressure;
    private int mS2Pressure;
    private int mS3Pressure;
    private int mS1Surface;
    private int mS2Surface;
    private int mS3Surface;
    
    RunData(double pInFlow, double pMinRetFlow, int pMaxSurface, boolean pComp, int pRunType, String pLocale, int pConf, String pS1Type, int pS1Pressure, int pS1Surface, String pS2Type, int pS2Pressure, int pS2Surface, String pS3Type, int pS3Pressure, int pS3Surface){
        mInFlow = pInFlow;
        mMinRetFlow = pMinRetFlow;
        mMaxSurface = pMaxSurface;
        mComp = pComp;
        mRunType = pRunType;
        mLocale = pLocale;
        mConf = pConf;
        mS1Type = pS1Type;
        mS1Pressure = pS1Pressure;
        mS1Surface = pS1Surface;
        mS2Type = pS2Type;
        mS2Pressure = pS2Pressure;
        mS2Surface = pS2Surface;
        mS3Type = pS3Type;
        mS3Pressure = pS3Pressure;
        mS3Surface = pS3Surface;
    }
    
    double xInFlow(){
        return mInFlow;
    }
    
    double xMinRetFlow(){
        return mMinRetFlow;
    }
    
    int xMaxSurface(){
        return mMaxSurface;
    }
    
    boolean xComp(){
        return mComp;
    }
    
    int xRunType(){
        return mRunType;
    }
    
    String xLocale(){
        return mLocale;
    }
    
    int xConf(){
        return mConf;
    }
    
    String xS1Type(){
        return mS1Type;
    }
    
    String xS2Type(){
        return mS2Type;
    }
    
    String xS3Type(){
        return mS3Type;
    }
    
    int xS1Pressure(){
        return mS1Pressure;
    }
    
    int xS2Pressure(){
        return mS2Pressure;
    }
    
    int xS3Pressure(){
        return mS3Pressure;
    }
    
    int xS1Surface(){
        return mS1Surface;
    }
    
    int xS2Surface(){
        return mS2Surface;
    }
    
    int xS3Surface(){
        return mS3Surface;
    }
}
