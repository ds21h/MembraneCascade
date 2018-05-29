/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ap.membrane.cascade;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 *
 * @author Afke
 */
public class MembraneCascade {

    private static MembraneCascade mCascade;

    private static String cSep = ";";
    private Locale mLocale = null;

    private int mStatus;
    private final int cStatusOK = 0;
    private final int cStatusInitOK = 10;
    private final int cStatusInitError = 90;
    private final int cStatusNoDB = 99;

    private File mDir;
    private OutFile mRunReport;

    private Membrane mStage1;
    private Membrane mStage2;
    private Membrane mStage3;
    private Membrane mStage1Max;
    private Membrane mStage2Max;
    private Membrane mStage3Max;
    private Configuration mMaxConf;
    private Flow mFlowIn;
    private Flow mFlowRecycleInit;

    private MembraneCascade() {
        sInit();
        if (mStatus == cStatusInitOK) {
            switch (GlobalData.gRunData.xRunType()) {
                case RunData.RunTypeSingle:
                    System.out.println("Start Single combination");
                    mRunReport.xWriteln("Single combination");
                    System.out.println("Configuration " + GlobalData.gRunData.xConf() + ", "
                            + "S1: " + GlobalData.gRunData.xS1Surface() + " * " + GlobalData.gRunData.xS1Type() + ", " + GlobalData.gRunData.xS1Pressure() + " bar, "
                            + "S2: " + GlobalData.gRunData.xS2Surface() + " * " + GlobalData.gRunData.xS2Type() + ", " + GlobalData.gRunData.xS2Pressure() + " bar, "
                            + "S3: " + GlobalData.gRunData.xS3Surface() + " * " + GlobalData.gRunData.xS3Type() + ", " + GlobalData.gRunData.xS3Pressure() + " bar");
                    sRunSingle();
                    System.out.println("End Single combination");
                    break;
                case RunData.RunTypeData:
                    System.out.println("Start Data collection");
                    mRunReport.xWriteln("Data collection");
                    sRunModel(false);
                    System.out.println("End Data collection");
                    break;
                default:
                    System.out.println("Start Optimization");
                    mRunReport.xWriteln("Optimization");
                    sRunModel(true);
                    System.out.println("End Optimization");
                    break;
            }
        }
        sCloseDown();
    }

    private void sRunModel(boolean pOptimize) {
        int lCountType1;
        int lCountType2;
        int lCountType3;
        int lCountPressure1;
        int lCountPressure2;
        int lCountPressure3;
        int lCountSurface1;
        int lCountSurface2;
        int lCountSurface3;
        int lCountConfiguration;
        boolean lFeasible;
        int lNumberFeasible;
        int lNumberInfeasible;
        double lObjective;
        double lObjectiveT;
        Flow lExitL;
        Flow lExitM;
        Flow lExitH;
        OutFile lOutput = null;
        List<Configuration> lConfigurations;
        Configuration lConf;
        int lCount;
        int lCountInt;

        lNumberFeasible = 0;
        lNumberInfeasible = 0;
        lObjective = 0.0d;
        lConfigurations = GlobalData.gConfigurations;
        for (lCountConfiguration = 0; lCountConfiguration < lConfigurations.size(); lCountConfiguration++) {
            lConf = lConfigurations.get(lCountConfiguration);
            if (lConf.xActive()) {
                if (!pOptimize) {
                    lOutput = new OutFile(mDir, "Data Configuration " + lConf.xConfId());
                    lOutput.xWrite("S1Type;S1Press;S1Surf;S1In Vol/Conc;;;;;;;S1Perm Vol/Conc;;;;;;;S1Ret Vol/Conc;;;;;;;S2Type;S2Press;S2Surf;S2In Vol/Conc;;;;;;;S2Perm Vol/Conc;;;;;;;S2Ret Vol/Conc;;;;;;;S3Type;S3Press;S3Surf;S3In Vol/Conc;;;;;;;S3Perm Vol/Conc;;;;;;;S3Ret Vol/Conc;;;;;;;");
                    lOutput.xNewLine();
                }
                System.out.println("Configuration " + lConf.xConfId());
                lCount = 0;
                lCountInt = 0;
                for (lCountType1 = 0; lCountType1 < GlobalData.gMembTypes.size(); lCountType1++) {
                    for (lCountPressure1 = 0; lCountPressure1 < GlobalData.gPressures.size(); lCountPressure1++) {
                        for (lCountSurface1 = 1; lCountSurface1 <= GlobalData.gRunData.xMaxSurface(); lCountSurface1++) {
                            for (lCountType2 = 0; lCountType2 < GlobalData.gMembTypes.size(); lCountType2++) {
                                for (lCountPressure2 = 0; lCountPressure2 < GlobalData.gPressures.size(); lCountPressure2++) {
                                    for (lCountSurface2 = 1; lCountSurface2 <= GlobalData.gRunData.xMaxSurface(); lCountSurface2++) {
                                        for (lCountType3 = 0; lCountType3 < GlobalData.gMembTypes.size(); lCountType3++) {
                                            for (lCountPressure3 = 0; lCountPressure3 < GlobalData.gPressures.size(); lCountPressure3++) {
                                                for (lCountSurface3 = 1; lCountSurface3 <= GlobalData.gRunData.xMaxSurface(); lCountSurface3++) {
                                                    mStage1.xInitMembrane(GlobalData.gMembTypes.get(lCountType1), GlobalData.gPressures.get(lCountPressure1).intValue(), lCountSurface1);
                                                    mStage2.xInitMembrane(GlobalData.gMembTypes.get(lCountType2), GlobalData.gPressures.get(lCountPressure2).intValue(), lCountSurface2);
                                                    mStage3.xInitMembrane(GlobalData.gMembTypes.get(lCountType3), GlobalData.gPressures.get(lCountPressure3).intValue(), lCountSurface3);
                                                    lFeasible = sCalculateFlows(lConf);
                                                    if (lFeasible) {
                                                        lNumberFeasible++;
                                                        lExitL = sCreateExit(lConf, "EL");
                                                        lExitM = sCreateExit(lConf, "EM");
                                                        lExitH = sCreateExit(lConf, "EH");
                                                        lObjectiveT = sCalculateObjective(lExitL, lExitM, lExitH);
                                                        if (!pOptimize) {
                                                            sPrintCombination(lOutput);
                                                        }
                                                        if (lObjectiveT > lObjective) {
                                                            lObjective = lObjectiveT;
                                                            mStage1Max.xCopyFrom(mStage1);
                                                            mStage2Max.xCopyFrom(mStage2);
                                                            mStage3Max.xCopyFrom(mStage3);
                                                            mMaxConf = lConf;
                                                        }
                                                    } else {
                                                        lNumberInfeasible++;
                                                    }
                                                    if (lCountInt < 250000){
                                                        lCountInt++;
                                                    } else {
                                                        System.out.println("Combinations processed: " + lCount);
                                                        lCountInt = 1;
                                                    }
                                                    lCount++;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if (!pOptimize) {
                    lOutput.xClose();
                }
            }
        }
        if (pOptimize) {
            mRunReport.xWriteln("Feasible: " + lNumberFeasible + ", Infeasible: " + lNumberInfeasible);
            mRunReport.xWriteln("Max. Objective function: " + String.valueOf(lObjective));
            sPrintSolution(mMaxConf, mStage1Max, mStage2Max, mStage3Max);
        }
    }

    Flow sCreateExit(Configuration pConf, String pStage) {
        Flow lResult;
        Flow lComponent;
        String[] lFeed;
        int lCount;

        lResult = new Flow();
        lFeed = pConf.xFeed(pStage);
        for (lCount = 0; lCount < lFeed.length; lCount++) {
            switch (lFeed[lCount]) {
                case "In":
                    lComponent = mFlowIn;
                    break;
                case "P1":
                    lComponent = mStage1.xPermFlow();
                    break;
                case "R1":
                    lComponent = mStage1.xRetFlow();
                    break;
                case "P2":
                    lComponent = mStage2.xPermFlow();
                    break;
                case "R2":
                    lComponent = mStage2.xRetFlow();
                    break;
                case "P3":
                    lComponent = mStage3.xPermFlow();
                    break;
                case "R3":
                    lComponent = mStage3.xRetFlow();
                    break;
                default:
                    lComponent = new Flow();
                    break;
            }
            lResult.xAdd(lComponent);
        }
        return lResult;
    }

    private void sPrintCombination(OutFile pOutput) {
        sPrintMembrane(pOutput, mStage1, false);
        sPrintMembrane(pOutput, mStage2, true);
        sPrintMembrane(pOutput, mStage3, true);
        pOutput.xNewLine();
    }

    private void sPrintMembrane(OutFile pOutput, Membrane pMemb, boolean pSeparation) {
        String lSep;
        String lData;

        lData = pMemb.xType() + cSep + pMemb.xPressure() + cSep + pMemb.xSurface();
        if (pSeparation) {
            lSep = cSep;
        } else {
            lSep = "";
        }
        pOutput.xWrite(lSep + lData);
        sPrintFlow(pOutput, pMemb.xInFlow());
        sPrintFlow(pOutput, pMemb.xPermFlow());
        sPrintFlow(pOutput, pMemb.xRetFlow());
    }

    private void sPrintFlow(OutFile pOutput, Flow pFlow) {
        int lCount;

        pOutput.xWrite(cSep + String.format(mLocale, "%f6", pFlow.xVolume()));
        for (lCount = 0; lCount < pFlow.xConcentration().length; lCount++) {
            pOutput.xWrite(cSep + String.format(mLocale, "%f6", pFlow.xConcentration()[lCount]));
        }
    }

    private void sRunSingle() {
        boolean lFeasible;
        double lObjective;
        Flow lExitL;
        Flow lExitM;
        Flow lExitH;
        List<Configuration> lConfigurations;
        int lCount;
        Configuration lConf = null;

        lConfigurations = GlobalData.gConfigurations;
        for (lCount = 0; lCount < lConfigurations.size(); lCount++) {
            lConf = lConfigurations.get(lCount);
            if (lConf.xConfId() == GlobalData.gRunData.xConf()) {
                break;
            }
        }
        if (lCount < lConfigurations.size()) {
            if (lConf.xActive()) {
                mStage1.xInitMembrane(GlobalData.gRunData.xS1Type(), GlobalData.gRunData.xS1Pressure(), GlobalData.gRunData.xS1Surface());
                mStage2.xInitMembrane(GlobalData.gRunData.xS2Type(), GlobalData.gRunData.xS2Pressure(), GlobalData.gRunData.xS2Surface());
                mStage3.xInitMembrane(GlobalData.gRunData.xS3Type(), GlobalData.gRunData.xS3Pressure(), GlobalData.gRunData.xS3Surface());
                lFeasible = sCalculateFlows(lConf);
                if (lFeasible) {
                    lExitL = sCreateExit(lConf, "EL");
                    lExitM = sCreateExit(lConf, "EM");
                    lExitH = sCreateExit(lConf, "EH");
                    lObjective = sCalculateObjective(lExitL, lExitM, lExitH);
                    mRunReport.xWriteln("Objective function: " + String.valueOf(lObjective));
                    sPrintSolution(lConf, mStage1, mStage2, mStage3);
                }
            }
        }
    }

    private void sPrintSolution(Configuration pConf, Membrane pStage1, Membrane pStage2, Membrane pStage3) {
        mRunReport.xWriteln("Conf: " + pConf.xConfId() + " S1: " + pStage1.xSurface() + " * " + pStage1.xType() + ", " + pStage1.xPressure() + " bar, " + "S2: " + pStage2.xSurface() + " * " + pStage2.xType() + ", " + pStage2.xPressure() + " bar, " + "S3: " + pStage3.xSurface() + " * " + pStage3.xType() + ", " + pStage3.xPressure() + " bar");
        mRunReport.xWriteln("Stage 1:");
        sPrintFlow("Input", pStage1.xInFlow());
        sPrintFlow("Permeate", pStage1.xPermFlow());
        sPrintFlow("Retentate", pStage1.xRetFlow());
        mRunReport.xWriteln("Stage 2:");
        sPrintFlow("Input", pStage2.xInFlow());
        sPrintFlow("Permeate", pStage2.xPermFlow());
        sPrintFlow("Retentate", pStage2.xRetFlow());
        mRunReport.xWriteln("Stage 3:");
        sPrintFlow("Input", pStage3.xInFlow());
        sPrintFlow("Permeate", pStage3.xPermFlow());
        sPrintFlow("Retentate", pStage3.xRetFlow());
    }

    private boolean sCalculateFlows(Configuration pConf) {
        boolean lRecycleOK;
        boolean lFeasible;
        boolean lFirst;
        Flow lFlowIn1;
        Flow lFlowIn2;
        Flow lFlowIn3;
        Flow lFlowTest;
        List<RecycleItem> lRecycle;
        RecycleItem lItem;
        int lCount;
        int lDev;
        int lDevMax;

        lRecycle = new ArrayList<>();
        lRecycleOK = false;
        lFeasible = true;
        lFirst = true;
        do {
            lFlowIn1 = sCreateInput(pConf, "S1", lRecycle, lFirst);
            mStage1.xSetInFlow(lFlowIn1);
            mStage1.xCalculate();
            if (mStage1.xPermFlow().xVolume() == 0 || mStage1.xRetFlow().xVolume() == 0) {
                lFeasible = false;
            } else {
                lFlowIn2 = sCreateInput(pConf, "S2", lRecycle, lFirst);
                mStage2.xSetInFlow(lFlowIn2);
                mStage2.xCalculate();
                if (mStage2.xPermFlow().xVolume() == 0 || mStage2.xRetFlow().xVolume() == 0) {
                    lFeasible = false;
                } else {
                    lFlowIn3 = sCreateInput(pConf, "S3", lRecycle, lFirst);
                    mStage3.xSetInFlow(lFlowIn3);
                    mStage3.xCalculate();
                    if (mStage3.xPermFlow().xVolume() == 0 || mStage3.xRetFlow().xVolume() == 0) {
                        lFeasible = false;
                    } else {
                        lDevMax = 0;
                        for (lCount = 0; lCount < lRecycle.size(); lCount++) {
                            lItem = lRecycle.get(lCount);
                            switch (lItem.xSource()) {
                                case "P1":
                                    lFlowTest = mStage1.xPermFlow();
                                    break;
                                case "R1":
                                    lFlowTest = mStage1.xRetFlow();
                                    break;
                                case "P2":
                                    lFlowTest = mStage2.xPermFlow();
                                    break;
                                case "R2":
                                    lFlowTest = mStage2.xRetFlow();
                                    break;
                                case "P3":
                                    lFlowTest = mStage3.xPermFlow();
                                    break;
                                case "R3":
                                    lFlowTest = mStage3.xRetFlow();
                                    break;
                                default:
                                    lFlowTest = new Flow();
                            }
                            lDev = lFlowTest.xCompare(lItem.xFlow());
                            if (lDev > lDevMax) {
                                lDevMax = lDev;
                            }
                        }
                        if (lDevMax > 10) {
                            lRecycleOK = false;
                        } else {
                            lRecycleOK = true;
                        }
                    }
                }
            }
            if (!lFeasible) {
                lRecycleOK = true;
            }
            lFirst = false;
        } while (!lRecycleOK);
        return lFeasible;
    }

    Flow sCreateInput(Configuration pConf, String pStage, List<RecycleItem> pRecycle, boolean pFirst) {
        Flow lResult;
        Flow lComponent;
        String[] lFeed;
        int lCount;
        int lLevelStage;

        lResult = new Flow();
        lFeed = pConf.xFeed(pStage);
        if (pStage.length() < 2) {
            lLevelStage = 99;
        } else {
            try {
                lLevelStage = Integer.parseInt(pStage.substring(1));
            } catch (NumberFormatException pExc) {
                lLevelStage = 99;
            }
        }
        for (lCount = 0; lCount < lFeed.length; lCount++) {
            switch (lFeed[lCount]) {
                case "In":
                    lComponent = mFlowIn;
                    break;
                case "P1":
                    lComponent = sSetComponent(pStage, lLevelStage, lFeed[lCount], 1, pFirst, pRecycle, mStage1.xPermFlow());
                    break;
                case "R1":
                    lComponent = sSetComponent(pStage, lLevelStage, lFeed[lCount], 1, pFirst, pRecycle, mStage1.xRetFlow());
                    break;
                case "P2":
                    lComponent = sSetComponent(pStage, lLevelStage, lFeed[lCount], 2, pFirst, pRecycle, mStage2.xPermFlow());
                    break;
                case "R2":
                    lComponent = sSetComponent(pStage, lLevelStage, lFeed[lCount], 2, pFirst, pRecycle, mStage2.xRetFlow());
                    break;
                case "P3":
                    lComponent = sSetComponent(pStage, lLevelStage, lFeed[lCount], 3, pFirst, pRecycle, mStage3.xPermFlow());
                    break;
                case "R3":
                    lComponent = sSetComponent(pStage, lLevelStage, lFeed[lCount], 3, pFirst, pRecycle, mStage3.xRetFlow());
                    break;
                default:
                    lComponent = new Flow();
                    break;
            }
            lResult.xAdd(lComponent);
        }
        return lResult;
    }

    Flow sSetComponent(String pStage, int pLevelStage, String pSource, int pLevelSource, boolean pFirst, List<RecycleItem> pRecycle, Flow pBasicFlow) {
        Flow lComponent;
        RecycleItem lRecycle = null;
        int lCount;

        if (pLevelStage <= pLevelSource) {
            if (pFirst) {
                lRecycle = new RecycleItem(mFlowRecycleInit, pStage, pSource);
                pRecycle.add(lRecycle);
                lComponent = lRecycle.xFlow();
            } else {
                for (lCount = 0; lCount < pRecycle.size(); lCount++) {
                    lRecycle = pRecycle.get(lCount);
                    if (lRecycle.xIsItem(pStage, pSource)) {
                        break;
                    }
                }
                if (lCount < pRecycle.size()) {
                    lRecycle.xFlow().xMakeFlow(pBasicFlow);
                    lComponent = lRecycle.xFlow();
                } else {
                    lComponent = new Flow();
                }
            }
        } else {
            lComponent = pBasicFlow;
        }
        return lComponent;
    }

    void sPrintFlow(String pDescr, Flow pFlow) {
        mRunReport.xWriteln(pDescr + ": Volume: " + pFlow.xVolume() + " Concentration: " + pFlow.xConcentration()[0] + " / " + pFlow.xConcentration()[1] + " / " + pFlow.xConcentration()[2] + " / " + pFlow.xConcentration()[3] + " / " + pFlow.xConcentration()[4] + " / " + pFlow.xConcentration()[5]);
    }

    double sCalculateObjective(Flow pExitL, Flow pExitM, Flow pExitH) {
        double lResultL;
        double lResultH;
        double lTotalL;
        double lTotalH;

        lTotalL = pExitL.xConcentration()[0] + pExitL.xConcentration()[1] + pExitL.xConcentration()[2] + pExitL.xConcentration()[3] + pExitL.xConcentration()[4] + pExitL.xConcentration()[5];
        lTotalH = pExitH.xConcentration()[0] + pExitH.xConcentration()[1] + pExitH.xConcentration()[2] + pExitH.xConcentration()[3] + pExitH.xConcentration()[4] + pExitH.xConcentration()[5];
//        lResultL = pExitL.xVolume() * pExitL.xConcentration()[0] + pExitL.xVolume() * pExitL.xConcentration()[1] + pExitL.xVolume() * pExitL.xConcentration()[2];
//        lResultH = pExitH.xVolume() * pExitH.xConcentration()[3] + pExitH.xVolume() * pExitH.xConcentration()[4] + pExitH.xVolume() * pExitH.xConcentration()[5];
        lResultL = pExitL.xConcentration()[0] / lTotalL + pExitL.xConcentration()[1] / lTotalL + pExitL.xConcentration()[2] / lTotalL;
        lResultH = pExitH.xConcentration()[3] / lTotalH + pExitH.xConcentration()[4] / lTotalH + pExitH.xConcentration()[5] / lTotalH;
        return lResultL + lResultH;
    }

    private void sInit() {
        DataBase lDatabase;
        LocalDateTime lDate;

        sSetOutputDir();
        mRunReport = new OutFile(mDir, "Runreport");
        lDate = LocalDateTime.now();
        mRunReport.xWriteln("Start run " + lDate.format(DateTimeFormatter.ISO_DATE_TIME));
        lDatabase = new DataBase();
        if (lDatabase.xStatus() == DataBase.cOK) {
            GlobalData.gConfigurations = lDatabase.xConfigurations();
            GlobalData.gMembranes = lDatabase.xMembranes();
            GlobalData.gComponents = lDatabase.xComponents();
            GlobalData.gMembTypes = lDatabase.xMembTypes();
            GlobalData.gPressures = lDatabase.xPressures();
            GlobalData.gRunData = lDatabase.xRunData();
            if (lDatabase.xStatus() == DataBase.cOK) {
                if (!GlobalData.gRunData.xLocale().equals("")) {
                    mLocale = Locale.forLanguageTag(GlobalData.gRunData.xLocale());
                }
                mStage1 = new Membrane();
                mStage2 = new Membrane();
                mStage3 = new Membrane();
                mStage1Max = new Membrane();
                mStage2Max = new Membrane();
                mStage3Max = new Membrane();
                sInitFlowIn();
                mStatus = cStatusInitOK;
            } else {
                mStatus = cStatusInitError;
            }
        } else {
            mStatus = cStatusNoDB;
        }
        lDatabase.xClose();
    }

    private void sCloseDown() {
        LocalDateTime lDate;

        lDate = LocalDateTime.now();
        mRunReport.xWriteln("End run " + lDate.format(DateTimeFormatter.ISO_DATE_TIME));
        mRunReport.xClose();
    }

    private void sSetOutputDir() {
        int lCount;
        LocalDate lDate;
        String lDirName;
        boolean lExists;

        lCount = 0;
        lDate = LocalDate.now();
        lExists = true;
        while (lExists) {
            lDirName = "MembraneCascade_Output_" + lDate.format(DateTimeFormatter.ISO_DATE) + "_" + String.format("%03d", lCount);
            mDir = new File(lDirName);
            lExists = mDir.exists();
            lCount++;
        }
        mDir.mkdir();
    }

    private void sInitFlowIn() {
        double[] lConcentration;
        int lCount;

        lConcentration = new double[GlobalData.gComponents.size()];
        for (lCount = 0; lCount < GlobalData.gComponents.size(); lCount++) {
            lConcentration[lCount] = GlobalData.gComponents.get(lCount).xConcentration();
        }
        mFlowIn = new Flow();
        mFlowIn.xMakeFlow(GlobalData.gRunData.xInFlow(), lConcentration);
        mFlowRecycleInit = new Flow();
        mFlowRecycleInit.xMakeFlow(GlobalData.gRunData.xInFlow() * GlobalData.gRunData.xMaxSurface(), lConcentration);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        mCascade = new MembraneCascade();
    }
}
