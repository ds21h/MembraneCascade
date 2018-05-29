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
class Membrane {

    static final int cStatusNotSet = 0;
    static final int cStatusInFlow = 10;
    static final int cStatusNoInFlow = 15;
    static final int cStatusReadyToCalculate = 20;
    static final int cStatusCalculatated = 30;
    static final int cStatusNoData = 99;

    private int mStatus;
    private String mType;
    private int mPressure;
    private int mSurface;
    private MembraneData mMembraneData;
    private Flow mInFlow;
    private Flow mPermeateFlow;
    private Flow mRetentateFlow;

    Membrane() {
        mStatus = cStatusNotSet;
        mType = "";
        mPressure = 0;
        mSurface = 0;
        mMembraneData = null;
        mInFlow = new Flow();
        mPermeateFlow = new Flow();
        mRetentateFlow = new Flow();
    }

    String xType() {
        return mType;
    }

    int xPressure() {
        return mPressure;
    }

    int xSurface() {
        return mSurface;
    }

    Flow xInFlow() {
        return mInFlow;
    }

    Flow xPermFlow() {
        return mPermeateFlow;
    }

    Flow xRetFlow() {
        return mRetentateFlow;
    }

    void xInitMembrane(String pType, int pPressure, int pSurface) {
        mType = pType;
        mPressure = pPressure;
        mSurface = pSurface;
        mMembraneData = GlobalData.xMembrane(pType, pPressure);
        if (mMembraneData == null) {
            mStatus = cStatusNoData;
        } else {
            switch (mStatus) {
                case cStatusNotSet:
                    mStatus = cStatusNoInFlow;
                    break;
                case cStatusInFlow:
                    mStatus = cStatusReadyToCalculate;
                    break;
                case cStatusNoInFlow:
                    break;
                case cStatusReadyToCalculate:
                    break;
                case cStatusCalculatated:
                    mStatus = cStatusReadyToCalculate;
                    break;
                case cStatusNoData:
                    break;
            }
        }
    }

    void xSetInFlow(Flow pInFlow) {
        mInFlow.xMakeFlow(pInFlow);
        switch (mStatus) {
            case cStatusNotSet:
                mStatus = cStatusInFlow;
                break;
            case cStatusInFlow:
                break;
            case cStatusNoInFlow:
                mStatus = cStatusReadyToCalculate;
                break;
            case cStatusReadyToCalculate:
                break;
            case cStatusCalculatated:
                mStatus = cStatusReadyToCalculate;
                break;
            case cStatusNoData:
                break;
        }
    }

    void xCopyFrom(Membrane pMemb) {
        mType = pMemb.xType();
        mPressure = pMemb.xPressure();
        mSurface = pMemb.xSurface();
        mMembraneData = GlobalData.xMembrane(mType, mPressure);
        if (mMembraneData == null) {
            mStatus = cStatusNoData;
        } else {
            mInFlow.xMakeFlow(pMemb.xInFlow());
            mPermeateFlow.xMakeFlow(pMemb.xPermFlow());
            mRetentateFlow.xMakeFlow(pMemb.xRetFlow());
        }
    }

    void xCalculate() {
        double[] lPermConcentration;
        int lTeller;

        if (mStatus == cStatusReadyToCalculate) {
            if (mInFlow.xVolume() < (mMembraneData.xPermeateFlow() + GlobalData.gRunData.xMinRetFlow()) * mSurface) {
                mPermeateFlow.xMakeFlow(0.0d, mInFlow.xConcentration());
                mRetentateFlow.xMakeFlow(0.0d, mInFlow.xConcentration());
            } else {
                lPermConcentration = new double[GlobalData.gComponents.size()];
                for (lTeller = 0; lTeller < lPermConcentration.length; lTeller++) {
                    lPermConcentration[lTeller] = mInFlow.xConcentration()[lTeller] * mMembraneData.xMassFraction()[lTeller];
                }
                mPermeateFlow.xMakeFlow(mMembraneData.xPermeateFlow() * mSurface, lPermConcentration);
                mRetentateFlow.xMakeFlow(mInFlow);
                mRetentateFlow.xSubtract(mPermeateFlow);
            }
        }
    }
}
