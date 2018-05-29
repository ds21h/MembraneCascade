/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ap.membrane.cascade;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Afke
 */
class DataBase {

    private int mStatus;
    private String mDescription = "";
    static final int cNotSet = -1;
    static final int cOK = 0;
    static final int cSQLite_not_found = 100;
    static final int cSQL_error = 200;

    private Connection mConn;

    DataBase() {
        mStatus = cNotSet;
        mDescription = "Not initialised";

        try {
            Class.forName("org.sqlite.JDBC");
//            mConn = DriverManager.getConnection("jdbc:sqlite:d:/Source/Netbeans/MembraneCascade/MembraneCascade.db");
            mConn = DriverManager.getConnection("jdbc:sqlite:MembraneCascade.db");
            mStatus = cOK;
            mDescription = "";
        } catch (ClassNotFoundException ex) {
            mStatus = cSQLite_not_found;
            mDescription = "SQLite library not found";
        } catch (SQLException ex) {
            mStatus = cSQL_error;
            mDescription = ex.getMessage();
        }
    }

    int xStatus() {
        return mStatus;
    }

    String xDescription() {
        return mDescription;
    }

    void xClose() {
        try {
            mConn.close();
        } catch (SQLException ex) {
            mDescription = ex.getMessage();
        }
    }

    List<Configuration> xConfigurations() {
        Statement lStm;
        ResultSet lRes;
        List<Configuration> lConfigurations;
        Configuration lConf = null;
        int lConfId;
        int lVorigConfId;
        String lInput;
        String lSource;
        String lSql = "SELECT ConfId, Input, Source "
                + "FROM Configuration "
                + "ORDER BY ConfId, Input;";

        lConfigurations = new ArrayList<>();
        if (mStatus == cOK) {
            lVorigConfId = -1;
            try {
                lStm = mConn.createStatement();
                lRes = lStm.executeQuery(lSql);
                while (lRes.next()) {
                    lConfId = lRes.getInt("ConfId");
                    lInput = lRes.getString("Input");
                    lSource = lRes.getString("Source");
                    if (lConfId != lVorigConfId) {
                        lVorigConfId = lConfId;
                        if (lConf != null) {
                            lConfigurations.add(lConf);
                        }
                        lConf = new Configuration(lConfId);
                    }
                    lConf.xAddEntry(lInput, lSource);
                }
                if (lConf != null) {
                    lConfigurations.add(lConf);
                }
                lRes.close();
                lStm.close();
            } catch (SQLException ex) {
                mStatus = cSQL_error;
                mDescription = ex.getMessage();
            }
        }
        return lConfigurations;
    }

    List<MembraneData> xMembranes() {
        Statement lStm;
        ResultSet lRes;
        List<MembraneData> lMembranes;
        MembraneData lData;
        String lType;
        int lPressure;
        double lFlow;
        double lFrac1;
        double lFrac2;
        double lFrac3;
        double lFrac4;
        double lFrac5;
        double lFrac6;
        String lSql = "SELECT Type, Pressure, PermeateFlow, Fraction1, Fraction2, Fraction3, Fraction4, Fraction5, Fraction6 "
                + "FROM Membrane "
                + "ORDER BY Type, Pressure;";

        lMembranes = new ArrayList<>();
        if (mStatus == cOK) {
            try {
                lStm = mConn.createStatement();
                lRes = lStm.executeQuery(lSql);
                while (lRes.next()) {
                    lType = lRes.getString("Type");
                    lPressure = lRes.getInt("Pressure");
                    lFlow = lRes.getDouble("PermeateFlow");
                    lFrac1 = lRes.getDouble("Fraction1");
                    lFrac2 = lRes.getDouble("Fraction2");
                    lFrac3 = lRes.getDouble("Fraction3");
                    lFrac4 = lRes.getDouble("Fraction4");
                    lFrac5 = lRes.getDouble("Fraction5");
                    lFrac6 = lRes.getDouble("Fraction6");
                    lData = new MembraneData(lType, lPressure, lFlow, lFrac1, lFrac2, lFrac3, lFrac4, lFrac5, lFrac6);
                    lMembranes.add(lData);
                }
                lRes.close();
                lStm.close();
            } catch (SQLException ex) {
                mStatus = cSQL_error;
                mDescription = ex.getMessage();
            }
        }
        return lMembranes;
    }

    List<Component> xComponents() {
        List<Component> lComponents;
        Component lComponent;
        int lCompId;
        String lName;
        double lMolWeight;
        double lConcentration;
        Statement lStm;
        ResultSet lRes;
        String lSql = "SELECT CompId, Name, MolWeight, Concentration "
                + "FROM Component "
                + "ORDER BY CompId;";

        lComponents = new ArrayList<>();
        if (mStatus == cOK) {
            mDescription = "";
            try {
                lStm = mConn.createStatement();
                lRes = lStm.executeQuery(lSql);
                while (lRes.next()) {
                    lCompId = lRes.getInt("CompId");
                    lName = lRes.getString("Name");
                    lMolWeight = lRes.getDouble("MolWeight");
                    lConcentration = lRes.getDouble("Concentration");
                    lComponent = new Component(lCompId, lName, lMolWeight, lConcentration);
                    lComponents.add(lComponent);
                }
                lRes.close();
                lStm.close();
            } catch (SQLException ex) {
                mStatus = cSQL_error;
                mDescription = ex.getMessage();
            }
        }
        return lComponents;
    }

    List<String> xMembTypes() {
        List<String> lTypes;
        String lType;
        Statement lStm;
        ResultSet lRes;
        String lSql = "SELECT DISTINCT Type "
                + "FROM Membrane "
                + "ORDER BY Type;";

        lTypes = new ArrayList<>();
        if (mStatus == cOK) {
            mDescription = "";
            try {
                lStm = mConn.createStatement();
                lRes = lStm.executeQuery(lSql);
                while (lRes.next()) {
                    lType = lRes.getString("Type");
                    lTypes.add(lType);
                }
                lRes.close();
                lStm.close();
            } catch (SQLException ex) {
                mStatus = cSQL_error;
                mDescription = ex.getMessage();
            }
        }
        return lTypes;
    }

    List<Integer> xPressures() {
        List<Integer> lPressures;
        Integer lPressure;
        Statement lStm;
        ResultSet lRes;
        String lSql = "SELECT DISTINCT Pressure "
                + "FROM Membrane "
                + "ORDER BY Pressure;";

        lPressures = new ArrayList<>();
        if (mStatus == cOK) {
            mDescription = "";
            try {
                lStm = mConn.createStatement();
                lRes = lStm.executeQuery(lSql);
                while (lRes.next()) {
                    lPressure = lRes.getInt("Pressure");
                    lPressures.add(lPressure);
                }
                lRes.close();
                lStm.close();
            } catch (SQLException ex) {
                mStatus = cSQL_error;
                mDescription = ex.getMessage();
            }
        }
        return lPressures;
    }

    RunData xRunData() {
        RunData lData = null;
        double lInFlow;
        double lMinRetFlow;
        int lMaxSurface;
        int lRunType;
        int lConf;
        String lLocale;
        String lS1Type;
        String lS2Type;
        String lS3Type;
        int lS1Pressure;
        int lS2Pressure;
        int lS3Pressure;
        int lS1Surface;
        int lS2Surface;
        int lS3Surface;
        Statement lStm;
        ResultSet lRes;
        String lSql = "SELECT InFlow, MinRetentateFlow, MaxSurface, RunType, Locale, Configuration, S1Type, S1Pressure, S1Surface, S2Type, S2Pressure, S2Surface, S3Type, S3Pressure, S3Surface "
                + "FROM Run;";

        if (mStatus == cOK) {
            mDescription = "";
            try {
                lStm = mConn.createStatement();
                lRes = lStm.executeQuery(lSql);
                if (lRes.next()) {
                    lInFlow = lRes.getDouble("InFlow");
                    lMinRetFlow = lRes.getDouble("MinRetentateFlow");
                    lMaxSurface = lRes.getInt("MaxSurface");
                    lLocale = lRes.getString("Locale");
                    lRunType = lRes.getInt("RunType");
                    lConf = lRes.getInt("Configuration");
                    lS1Type = lRes.getString("S1Type");
                    lS1Pressure = lRes.getInt("S1Pressure");
                    lS1Surface = lRes.getInt("S1Surface");
                    lS2Type = lRes.getString("S2Type");
                    lS2Pressure = lRes.getInt("S2Pressure");
                    lS2Surface = lRes.getInt("S2Surface");
                    lS3Type = lRes.getString("S3Type");
                    lS3Pressure = lRes.getInt("S3Pressure");
                    lS3Surface = lRes.getInt("S3Surface");
                    lData = new RunData(lInFlow, lMinRetFlow, lMaxSurface, lRunType, lLocale, lConf, lS1Type, lS1Pressure, lS1Surface, lS2Type, lS2Pressure, lS2Surface, lS3Type, lS3Pressure, lS3Surface);
                }
                lRes.close();
                lStm.close();
            } catch (SQLException ex) {
                mStatus = cSQL_error;
                mDescription = ex.getMessage();
            }
        }
        return lData;
    }
}
