/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ap.membrane.cascade;

import static java.lang.Math.abs;

/**
 *
 * @author Afke
 */
class Flow {

    private double mVolume;
    private double[] mConcentration;

    Flow() {
        int lTeller;

        mConcentration = new double[GlobalData.gComponents.size()];
        mVolume = 0.0d;
        for (lTeller = 0; lTeller < mConcentration.length; lTeller++) {
            mConcentration[lTeller] = 0.0d;
        }
    }

    void xMakeFlow(Flow pFlow) {
        xMakeFlow(pFlow.xVolume(), pFlow.xConcentration());
    }

    void xMakeFlow(double pVolume, double[] pConcentration) {
        int lTeller;

        if (pVolume > 0.0d) {
            mVolume = pVolume;
        } else {
            mVolume = 0.0d;
        }
        for (lTeller = 0; lTeller < mConcentration.length; lTeller++) {
            if (lTeller < pConcentration.length) {
                if (pConcentration[lTeller] < 0.0d) {
                    mConcentration[lTeller] = 0.0d;
                } else {
                    mConcentration[lTeller] = pConcentration[lTeller];
                }
            } else {
                mConcentration[lTeller] = 0.0d;
            }
        }
    }

    void xSubtract(Flow pFlow) {
        double lVolume;
        double[] lConc;
        int lTeller;

        lVolume = mVolume - pFlow.xVolume();
        if (lVolume < 0.0d) {
            mVolume = 0.0d;
        } else {
            lConc = new double[GlobalData.gComponents.size()];
            for (lTeller = 0; lTeller < lConc.length; lTeller++) {
                lConc[lTeller] = ((mVolume * mConcentration[lTeller]) - (pFlow.xVolume() * pFlow.xConcentration()[lTeller])) / lVolume;
            }
            mVolume = lVolume;
            mConcentration = lConc;
        }
    }

    void xAdd(Flow pFlow) {
        double lVolume;
        double[] lConc;
        int lTeller;

        lVolume = mVolume + pFlow.xVolume();
        if (!GlobalData.gRunData.xComp()) {
            lConc = new double[GlobalData.gComponents.size()];
            for (lTeller = 0; lTeller < lConc.length; lTeller++) {
                lConc[lTeller] = ((mVolume * mConcentration[lTeller]) + (pFlow.xVolume() * pFlow.xConcentration()[lTeller])) / lVolume;
            }
            mConcentration = lConc;
        }
        mVolume = lVolume;
    }

    int xCompare(Flow pFlow) {
        int lResult;
        int lResultT;
        double lVerschil;
        int lTeller;

        if (mVolume == 0) {
            if (pFlow.xVolume() == 0) {
                lResult = 0;
            } else {
                lResult = 999;
            }
        } else {
            lVerschil = abs(mVolume - pFlow.xVolume());
            lResult = (int) ((lVerschil / mVolume) * 1000);
            if (!GlobalData.gRunData.xComp()) {
                for (lTeller = 0; lTeller < mConcentration.length; lTeller++) {
                    lVerschil = abs(mConcentration[lTeller] - pFlow.xConcentration()[lTeller]);
                    lResultT = (int) ((lVerschil / mConcentration[lTeller]) * 1000);
                    if (lResultT > lResult) {
                        lResult = lResultT;
                    }
                }
            }
        }
        return lResult;
    }

    double xVolume() {
        return mVolume;
    }

    double[] xConcentration() {
        return mConcentration;
    }
}
