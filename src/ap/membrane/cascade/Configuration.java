/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ap.membrane.cascade;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Afke
 */
class Configuration {

    private int mConfId;
    private boolean mActive;
    private List<ConfigurationEntry> mEntries;

    Configuration(int pConfId) {
        mConfId = pConfId;
        mActive = false;
        mEntries = new ArrayList<>();
    }

    void xAddEntry(String pInput, String pSource) {
        ConfigurationEntry lEntry;

        if (pInput.equals("Active")) {
            if (pSource.equals("True")) {
                mActive = true;
            } else {
                mActive = false;
            }
        } else {
            lEntry = new ConfigurationEntry(pInput, pSource);
            mEntries.add(lEntry);
        }
    }

    boolean xActive() {
        return mActive;
    }

    int xConfId() {
        return mConfId;
    }

    String[] xFeed(String pInput) {
        List<String> lFeed;
        ConfigurationEntry lEntry;
        int lCount;

        lFeed = new ArrayList<>();
        if (mActive) {
            for (lCount = 0; lCount < mEntries.size(); lCount++) {
                lEntry = mEntries.get(lCount);
                if (lEntry.xInput().equals(pInput)) {
                    lFeed.add(lEntry.xSource());
                }
            }
        }

        /*        switch(pInput){
            case "S1":
                lFeed.add("In");
                lFeed.add("R2");
                lFeed.add("P3");
                break;
            case "S2":
                lFeed.add("P1");
                break;
            case "S3":
                lFeed.add("R1");
                break;
            case "EH":
                lFeed.add("R3");
                break;
            case "EM":
                break;
            case "EL":
                lFeed.add("P2");
                break;
        } */
        return lFeed.toArray(new String[lFeed.size()]);
    }
}
