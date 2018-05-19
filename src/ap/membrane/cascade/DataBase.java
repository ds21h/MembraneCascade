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
        int lComp;
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
        String lSql = "SELECT InFlow, MinRetentateFlow, MaxSurface, Compatibility, RunType, Locale, Configuration, S1Type, S1Pressure, S1Surface, S2Type, S2Pressure, S2Surface, S3Type, S3Pressure, S3Surface "
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
                    lComp = lRes.getInt("Compatibility");
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
                    lData = new RunData(lInFlow, lMinRetFlow, lMaxSurface, (lComp != 0), lRunType, lLocale, lConf, lS1Type, lS1Pressure, lS1Surface, lS2Type, lS2Pressure, lS2Surface, lS3Type, lS3Pressure, lS3Surface);
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
    /*    public Instelling xInstelling() {
        double lLengte;
        double lBreedte;
        int lLichtUitUur;
        int lLichtUitMin;
        int lUitTijd;
        int lSensorGrens;
        int lSensorDrempel;
        int lMaxSensor;
        int lPeriodeDonker;
        int lPeriodeMinuut;
        int lPeriodeSec;
        Statement lStm;
        ResultSet lRes;
        Instelling lInst;
        String lSql = "SELECT Lengte, Breedte, LichtUitUur, LichtUitMin, UitTijd, SensorGrens, SensorDrempel, MaxSensor, PeriodeDonker, PeriodeMinuut, PeriodeSec "
                + "FROM Instelling "
                + "WHERE ID = 'Licht';";

        mDescription = "";
        lInst = new Instelling();
        if (mStatus == cOK) {
            try {
                lStm = mConn.createStatement();
                lRes = lStm.executeQuery(lSql);
                if (lRes.next()) {
                    lLengte = lRes.getDouble("Lengte");
                    lBreedte = lRes.getDouble("Breedte");
                    lLichtUitUur = lRes.getInt("LichtUitUur");
                    lLichtUitMin = lRes.getInt("LichtUitMin");
                    lUitTijd = lRes.getInt("UitTijd");
                    lSensorGrens = lRes.getInt("SensorGrens");
                    lSensorDrempel = lRes.getInt("SensorDrempel");
                    lMaxSensor = lRes.getInt("MaxSensor");
                    lPeriodeDonker = lRes.getInt("PeriodeDonker");
                    lPeriodeMinuut = lRes.getInt("PeriodeMinuut");
                    lPeriodeSec = lRes.getInt("PeriodeSec");
                    lInst = new Instelling(lLengte, lBreedte, lLichtUitUur, lLichtUitMin, lUitTijd, lSensorGrens, lSensorDrempel, lMaxSensor, lPeriodeDonker, lPeriodeMinuut, lPeriodeSec);
                }
                lRes.close();
                lStm.close();
            } catch (SQLException ex) {
                mStatus = cSQL_error;
                mDescription = ex.getMessage();
                xSchrijfLog("xInstelling: SQL error " + mDescription);
            }
        }
        return lInst;
    }

    public void xWijzigInstelling(Instelling pInstelling) {
        Statement lStm;
        String lSql;

        if (mStatus == cOK) {
            lSql = "UPDATE Instelling "
                    + "SET "
                    + "Lengte = '" + String.valueOf(pInstelling.xLengte()) + "', "
                    + "Breedte = '" + String.valueOf(pInstelling.xBreedte()) + "', "
                    + "LichtUitUur = '" + String.valueOf(pInstelling.xLichtUitUur()) + "', "
                    + "LichtUitMin = '" + String.valueOf(pInstelling.xLichtUitMin()) + "', "
                    + "UitTijd = '" + String.valueOf(pInstelling.xUitTijd()) + "', "
                    + "SensorGrens = '" + String.valueOf(pInstelling.xSensorGrens()) + "', "
                    + "SensorDrempel = '" + String.valueOf(pInstelling.xSensorDrempel()) + "', "
                    + "MaxSensor = '" + String.valueOf(pInstelling.xMaxSensor()) + "', "
                    + "PeriodeDonker = '" + String.valueOf(pInstelling.xPeriodeDonker()) + "', "
                    + "PeriodeMinuut = '" + String.valueOf(pInstelling.xPeriodeMinuut()) + "', "
                    + "PeriodeSec = '" + String.valueOf(pInstelling.xPeriodeSec()) + "' "
                + "WHERE ID = 'Licht';";
            try {
                lStm = mConn.createStatement();
                lStm.executeUpdate(lSql);
                lStm.close();
            } catch (SQLException ex) {
                mStatus = cSQL_error;
                mDescription = ex.getMessage();
                xSchrijfLog("xWijzigInstelling: SQL error " + mDescription);
            }
        }
    }

    public List<Schakelaar> xSchakelaars() {
        List<Schakelaar> lSchakelaars;
        Schakelaar lSchakelaar;
        int lVolgNummer;
        String lNaam;
        boolean lAktief;
        String lType;
        String lGroep;
        String lPunt;
        int lPauze;
        String lIP;
        Statement lStm;
        ResultSet lRes;
        String lSql = "SELECT VolgNummer, Naam, Aktief, Type, Groep, Punt, Pauze, IP "
                + "FROM Schakelaar "
                + "ORDER BY VolgNummer, Naam;";

        lSchakelaars = new ArrayList<>();
        mDescription = "";
        if (mStatus == cOK) {
            try {
                lStm = mConn.createStatement();
                lRes = lStm.executeQuery(lSql);
                while (lRes.next()) {
                    lVolgNummer = lRes.getInt("VolgNummer");
                    lNaam = lRes.getString("Naam");
                    lAktief = lRes.getBoolean("Aktief");
                    lType = lRes.getString("Type");
                    lGroep = lRes.getString("Groep");
                    if (lGroep == null){
                        lGroep = "";
                    }
                    lPunt = lRes.getString("Punt");
                    if (lPunt == null){
                        lPunt = "";
                    }
                    lPauze = lRes.getInt("Pauze");
                    lIP = lRes.getString("IP");
                    if (lIP == null){
                        lIP = "";
                    }
                    lSchakelaar = new Schakelaar(lVolgNummer, lNaam, lAktief, lType, lGroep, lPunt, lPauze, lIP);
                    lSchakelaars.add(lSchakelaar);
                }
                lRes.close();
                lStm.close();
            } catch (SQLException ex) {
                mStatus = cSQL_error;
                mDescription = ex.getMessage();
                xSchrijfLog("xSchakelaars: SQL error " + mDescription);
            }
        }
        return lSchakelaars;
    }

    public Schakelaar xSchakelaar(String pNaam) {
        Schakelaar lSchakelaar;
        int lVolgNummer;
        String lNaam;
        boolean lAktief;
        String lType;
        String lGroep;
        String lPunt;
        int lPauze;
        String lIP;
        Statement lStm;
        ResultSet lRes;
        String lSql = "SELECT VolgNummer, Naam, Aktief, Type, Groep, Punt, Pauze, IP "
                + "FROM Schakelaar "
                + "WHERE Naam = '" + pNaam + "';";

        mDescription = "";
        lSchakelaar = new Schakelaar();
        if (mStatus == cOK) {
            try {
                lStm = mConn.createStatement();
                lRes = lStm.executeQuery(lSql);
                if (lRes.next()) {
                    lVolgNummer = lRes.getInt("VolgNummer");
                    lNaam = lRes.getString("Naam");
                    lAktief = lRes.getBoolean("Aktief");
                    lType = lRes.getString("Type");
                    lGroep = lRes.getString("Groep");
                    if (lGroep == null){
                        lGroep = "";
                    }
                    lPunt = lRes.getString("Punt");
                    if (lPunt == null){
                        lPunt = "";
                    }
                    lPauze = lRes.getInt("Pauze");
                    lIP = lRes.getString("IP");
                    if (lIP == null){
                        lIP = "";
                    }
                    lSchakelaar = new Schakelaar(lVolgNummer, lNaam, lAktief, lType, lGroep, lPunt, lPauze, lIP);
                }
                lRes.close();
                lStm.close();
            } catch (SQLException ex) {
                mStatus = cSQL_error;
                mDescription = ex.getMessage();
                xSchrijfLog("xSchakelaar: SQL error " + mDescription);
            }
        }
        return lSchakelaar;
    }

    public void xWijzigSchakelaar(Schakelaar pSchakelaar) {
        Statement lStm;
        String lSql;
        int lAktief;
        String lGroep;
        String lPunt;
        String lIP;

        if (pSchakelaar.xGroep().equals("")){
            lGroep = "null";
        } else {
            lGroep = "'" + pSchakelaar.xGroep() + "'";
        }
        if (pSchakelaar.xPunt().equals("")){
            lPunt = "null";
        } else {
            lPunt = "'" + pSchakelaar.xPunt() + "'";
        }
        if (pSchakelaar.xIP().equals("")){
            lIP = "null";
        } else {
            lIP = "'" + pSchakelaar.xIP() + "'";
        }
        if (pSchakelaar.xAktief()) {
            lAktief = 1;
        } else {
            lAktief = 0;
        }
        if (mStatus == cOK) {
            lSql = "UPDATE Schakelaar "
                    + "SET "
                    + "VolgNummer = '" + pSchakelaar.xVolgNummer() + "', "
                    + "Aktief = '" + lAktief + "', "
                    + "Type = '" + pSchakelaar.xType() + "', "
                    + "Groep = " + lGroep + ", "
                    + "Punt = " + lPunt + ", "
                    + "Pauze = '" + pSchakelaar.xPauze() + "', "
                    + "IP = " + lIP + " "
                    + "WHERE Naam = '" + pSchakelaar.xNaam() + "';";
            try {
                lStm = mConn.createStatement();
                lStm.executeUpdate(lSql);
                lStm.close();
            } catch (SQLException ex) {
                mStatus = cSQL_error;
                mDescription = ex.getMessage();
                xSchrijfLog("xWijzigSchakelaar: SQL error " + mDescription);
            }
        }
    }

    public void xVerwijderSchakelaar(String pSchakelaarId) {
        Statement lStm;
        String lSql;

        if (mStatus == cOK) {
            lSql = "DELETE from Schakelaar "
                    + "WHERE Naam = '" + pSchakelaarId + "';";
            try {
                lStm = mConn.createStatement();
                lStm.executeUpdate(lSql);
                lStm.close();
            } catch (SQLException ex) {
                mStatus = cSQL_error;
                mDescription = ex.getMessage();
                xSchrijfLog("xVerwijderSchakelaar: SQL error " + mDescription);
            }
        }
    }

    public void xNieuweSchakelaar(Schakelaar pSchakelaar) {
        Statement lStm;
        String lSql;
        int lAktief;
        String lGroep;
        String lPunt;
        String lIP;

        if (pSchakelaar.xGroep().equals("")){
            lGroep = "null";
        } else {
            lGroep = "'" + pSchakelaar.xGroep() + "'";
        }
        if (pSchakelaar.xPunt().equals("")){
            lPunt = "null";
        } else {
            lPunt = "'" + pSchakelaar.xPunt() + "'";
        }
        if (pSchakelaar.xIP().equals("")){
            lIP = "null";
        } else {
            lIP = "'" + pSchakelaar.xIP() + "'";
        }

        if (pSchakelaar.xAktief()) {
            lAktief = 1;
        } else {
            lAktief = 0;
        }
        if (mStatus == cOK) {
            lSql = "INSERT INTO Schakelaar (VolgNummer, Naam, Aktief, Type, Groep, Punt, Pauze, IP) "
                    + "VALUES ('" + pSchakelaar.xVolgNummer() + "', "
                    + "'" + pSchakelaar.xNaam() + "', "
                    + "'" + lAktief  + "', "
                    + "'" + pSchakelaar.xType()  + "', "
                    + lGroep + ", "
                    + lPunt + ", " 
                    + "'" + pSchakelaar.xPauze()  + "', "
                    + lIP + ");";
            try {
                lStm = mConn.createStatement();
                lStm.executeUpdate(lSql);
                lStm.close();
            } catch (SQLException ex) {
                mStatus = cSQL_error;
                mDescription = ex.getMessage();
                xSchrijfLog("xNieuweSchakelaar: SQL error " + mDescription);
            }
        }
    }

    public Huidig xHuidig() {
        String lZonsOndergang;
        String lStartLichtAan;
        String lLichtUit;
        String lBijwerken;
        int lFase;
        int lLichtMeting;
        Statement lStm;
        ResultSet lRes;
        Huidig lHuidig;
        String lSql = "SELECT ZonsOndergang, StartLichtAan, LichtUit, Bijwerken, Fase, LichtMeting "
                + "FROM Huidig "
                + "WHERE ID = 'Licht';";

        mDescription = "";
        lHuidig = new Huidig();
        if (mStatus == cOK) {
            try {
                lStm = mConn.createStatement();
                lRes = lStm.executeQuery(lSql);
                if (lRes.next()) {
                    lZonsOndergang = lRes.getString("ZonsOndergang");
                    lStartLichtAan = lRes.getString("StartLichtAan");
                    lLichtUit = lRes.getString("LichtUit");
                    lBijwerken = lRes.getString("Bijwerken");
                    lFase = lRes.getInt("Fase");
                    lLichtMeting = lRes.getInt("LichtMeting");
                    lHuidig = new Huidig(lZonsOndergang, lStartLichtAan, lLichtUit, lBijwerken, lFase, lLichtMeting);
                }
                lRes.close();
                lStm.close();
            } catch (SQLException ex) {
                mStatus = cSQL_error;
                mDescription = ex.getMessage();
                xSchrijfLog("xHuidig: SQL error " + mDescription);
            }
        }
        return lHuidig;
    }

    public void xHuidig(Huidig pHuidig) {
        Statement lStm;
        String lSql;
        DateTimeFormatter lFormat;

        lFormat = DateTimeFormatter.ISO_ZONED_DATE_TIME;
        if (mStatus == cOK) {
            lSql = "UPDATE Huidig "
                    + "SET "
                    + "ZonsOndergang = '" + pHuidig.xZonsOndergang().format(lFormat) + "', "
                    + "StartLichtAan = '" + pHuidig.xStartLichtAan().format(lFormat) + "', "
                    + "LichtUit = '" + pHuidig.xLichtUit().format(lFormat) + "', "
                    + "Bijwerken = '" + pHuidig.xBijwerken().format(lFormat) + "', "
                    + "Fase = '" + pHuidig.xFase() + "', "
                    + "LichtMeting = '" + pHuidig.xLichtMeting() + "' "
                    + "WHERE ID = 'Licht';";
            try {
                lStm = mConn.createStatement();
                lStm.executeUpdate(lSql);
                lStm.close();
            } catch (SQLException ex) {
                mStatus = cSQL_error;
                mDescription = ex.getMessage();
                xSchrijfLog("xHuidig_Update: SQL error " + mDescription);
            }
        }
    }

    public void xNieuweAktie(Aktie pAktie) {
        Statement lStm;
        String lSql;
        DateTimeFormatter lFormat;
        String lGemaakt;
        String lUitvoeren;

        if (mStatus == cOK) {
        lFormat = DateTimeFormatter.ISO_ZONED_DATE_TIME;
        if (pAktie.xGemaakt() == null){
            lGemaakt = "";
        } else {
            lGemaakt = pAktie.xGemaakt().format(lFormat);
        }
        if (pAktie.xUitvoeren() == null){
            lUitvoeren = "";
        } else {
            lUitvoeren = pAktie.xUitvoeren().format(lFormat);
        }

            lSql = "INSERT INTO Aktie (Gemaakt, Uitvoeren, Type, Par, Klaar) "
                    + "VALUES ('" + lGemaakt + "', '" + lUitvoeren + "', '" + pAktie.xType() + "', '" + pAktie.xPar() + "', 0);";
            try {
                lStm = mConn.createStatement();
                lStm.executeUpdate(lSql);
                lStm.close();
            } catch (SQLException ex) {
                mStatus = cSQL_error;
                mDescription = ex.getMessage();
                xSchrijfLog("xNieuweAktie: SQL error " + mDescription);
            }
        }
    }

    public List<Aktie> xAkties() {
        List<Aktie> lAkties;
        Aktie lAktie;
        int lID;
        String lType;
        String lGemaakt;
        String lUitvoeren;
        String lPar;
        boolean lKlaar;
        Statement lStm;
        ResultSet lRes;
        String lSql = "SELECT ID, Gemaakt, Uitvoeren, Type, Par, Klaar "
                + "FROM Aktie "
                + "WHERE Klaar = 0 "
                + "Order by Uitvoeren, Gemaakt;";

        lAkties = new ArrayList<>();
        mDescription = "";
        if (mStatus == cOK) {
            try {
                lStm = mConn.createStatement();
                lRes = lStm.executeQuery(lSql);
                while (lRes.next()) {
                    lID = lRes.getInt("ID");
                    lGemaakt = lRes.getString("Gemaakt");
                    if (lGemaakt != null && lGemaakt.equals("")){
                        lGemaakt = null;
                    }
                    lUitvoeren = lRes.getString("Uitvoeren");
                    if (lUitvoeren != null && lUitvoeren.equals("")){
                        lUitvoeren = null;
                    }
                    lType = lRes.getString("Type");
                    lPar = lRes.getString("Par");
                    lKlaar = lRes.getBoolean("Klaar");
                    lAktie = new Aktie(lID, lGemaakt, lUitvoeren, lType, lPar, lKlaar);
                    lAkties.add(lAktie);
                }
                lRes.close();
                lStm.close();
            } catch (SQLException ex) {
                mStatus = cSQL_error;
                mDescription = ex.getMessage();
                xSchrijfLog("xAkties: SQL error " + mDescription);
            }
        }

        return lAkties;
    }

    public void xAktieUitgevoerd(Aktie pAktie) {
        Statement lStm;
        String lSql;
        if (mStatus == cOK) {
            lSql = "UPDATE Aktie "
                    + "SET "
                    + "Klaar = 1 "
                    + "WHERE ID = " + pAktie.xID() + ";";
            try {
                lStm = mConn.createStatement();
                lStm.executeUpdate(lSql);
                lStm.close();
            } catch (SQLException ex) {
                mStatus = cSQL_error;
                mDescription = ex.getMessage();
                xSchrijfLog("xAktieUitgevoerd: SQL error " + mDescription);
            }
        }
    }

    public void xAktieSchakelaarUitgevoerd(Schakelaar pSchakelaar) {
        Statement lStm;
        String lSql;
        if (mStatus == cOK) {
            lSql = "UPDATE Aktie "
                    + "SET "
                    + "Klaar = 1 "
                    + "WHERE Par = '" + pSchakelaar.xNaam()+ "';";
            try {
                lStm = mConn.createStatement();
                lStm.executeUpdate(lSql);
                lStm.close();
            } catch (SQLException ex) {
                mStatus = cSQL_error;
                mDescription = ex.getMessage();
                xSchrijfLog("xAktieUitgevoerd: SQL error " + mDescription);
            }
        }
    }

    public final void xSchrijfLog(String pMelding) {
        LocalDateTime lNu;
        Statement lStm;
        String lSql;

        lNu = LocalDateTime.now();
        if (mStatus == cOK) {
            lSql = "INSERT INTO Log (Tijdstip, Inhoud) "
                    + "VALUES ('" + lNu.toString() + "', '" + pMelding + "');";
            try {
                lStm = mConn.createStatement();
                lStm.executeUpdate(lSql);
                lStm.close();
            } catch (SQLException ex) {
                mStatus = cSQL_error;
                mDescription = ex.getMessage();
                System.out.println(lNu.toString() + " " + mDescription);
            }
        } else {
            System.out.println(lNu.toString() + " Status niet OK: " + mStatus);
        }
        System.out.println(lNu.toString() + " " + pMelding);
    }
     */
}
