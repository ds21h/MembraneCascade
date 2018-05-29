/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ap.membrane.cascade;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author Afke
 */
class OutFile {
    private static String cExtension = ".txt";
    
    private FileWriter mWriter;
    private BufferedWriter mBuffer;
    private int mStatus;
    static int StatusOK = 0;
    static int StatusNOK = 9;

    OutFile(File pDir, String pName) {
        File lFile;
        
        lFile = new File(pDir, pName + cExtension);
        try {
            mWriter = new FileWriter(lFile);
            mBuffer = new BufferedWriter(mWriter);
            mStatus = StatusOK;
        } catch (IOException pExc) {
            mStatus = StatusNOK;
        }
    }

    void xWrite(String pOutput) {
        if (mStatus == StatusOK) {
            try {
                mBuffer.write(pOutput);
            } catch (IOException pExc) {
                mStatus = StatusNOK;
            }
        }
    }

    void xWriteln(String pOutput) {
        if (mStatus == StatusOK) {
            try {
                mBuffer.write(pOutput);
                mBuffer.newLine();
            } catch (IOException pExc) {
                mStatus = StatusNOK;
            }
        }
    }
    
    void xNewLine(){
        if (mStatus == StatusOK) {
            try {
                mBuffer.newLine();
            } catch (IOException pExc) {
                mStatus = StatusNOK;
            }
        }
    }

    void xClose() {
        try {
            if (mBuffer != null) {
                mBuffer.close();
            }
            if (mWriter != null) {
                mWriter.close();
            }
        } catch (IOException pExc) {
        } finally {
            mStatus = StatusNOK;
        }
    }
}
