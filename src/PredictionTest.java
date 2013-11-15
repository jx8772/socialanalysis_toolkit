import collaborativefiltering.*;
import collaborativefiltering.Date;
import collaborativefiltering.utility.Number;
import collaborativefiltering.utility.StdOut;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: xiangji
 * Date: 10/3/13
 * Time: 3:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class PredictionTest {
    private static String database;
    private static String hostname;
    private static int port;
    private static String username;
    private static String password;
    private static String userBasicTable;
    private static String userHistoryTable;
    private static String headFile;
    private static String rankListFile;
    private static String analysisFile;

    private static Connection mySQLConnection = null;
    private static UsersHistory userHistory = null;
    private static HashMap<Integer, User> userBasic = null;

    private static final int HEADSIZE = 6;
    private static final int TOPN = 20;

    public static void main(String[] args) throws Exception {
        init(args[0]);
        //userHistory.checkDates();
        //userHistory.dataAnalysis(analysisFile);

        userHistory.preProcess();

        getAllUserPrediction();
    }

    /*public static void main(String[] args) throws Exception {
        init(args[0]);
        //userHistory.checkDates();
        userHistory.preProcess();
        //userHistory.checkDates();

        Diagnosis d1 = new Diagnosis(0, new Condition("c1",1), true, new Date(2,4,2013), new Date(3,5,2013));
        Diagnosis d2 = new Diagnosis(0, new Condition("c5",5), true, new Date(1,4,2011), new Date(4,5,2012));
        Diagnosis d3 = new Diagnosis(0, new Condition("c6",6), true, new Date(1,4,2012), new Date(4,5,2012));
        ArrayList<Diagnosis> ad = new ArrayList<Diagnosis>();
        ad.add(d1);
        ad.add(d2);
        ad.add(d3);
        getNewUserPrediction(ad);
    }*/

    /*static static void main(String[] args) throws Exception {
        init(args[0]);
        userHistory.preProcess();
        //getAllUserPrediction();
        Diagnosis d1 = new Diagnosis(50, new Condition("c1",1), true, new Date(2,4,2013), new Date(3,5,2013));
        Diagnosis d2 = new Diagnosis(50, new Condition("c3",3), true, new Date(1,4,2011), new Date(4,5,2012));
        ArrayList<Diagnosis> ad = new ArrayList<Diagnosis>();
        ad.add(d1);
        ad.add(d2);
        getNewUserPrediction(ad);
    }*/

    public static void init(String config_path) throws Exception {
        Properties prop = new Properties();

        //prop.load(new FileInputStream("config/properties"));
        prop.load(new FileInputStream(config_path));

        hostname = prop.getProperty("db.host");
        username = prop.getProperty("db.username");
        password = prop.getProperty("db.password");
        database = prop.getProperty("db.database");
        userBasicTable = prop.getProperty("db.userbasictable");
        userHistoryTable = prop.getProperty("db.userhistorytable");
        port = Integer.parseInt(prop.getProperty("db.port"));
        userHistory = new UsersHistory();
        userBasic = new HashMap<Integer, User>();
        headFile =  prop.getProperty("headfile");
        rankListFile =  prop.getProperty("ranklistfile");
        analysisFile = prop.getProperty("analysisfile");

        initMySQLDB();
        initPatientsLikeMeData();
    }

    public static void initMySQLDB() {
        Connection conn = null;

        try {
            Class.forName("com.mysql.jdbc.Driver");
            String connectString = "jdbc:mysql://" + hostname + ":" + port + "/" + database + "?user=" + username + "&password=" + password;
            conn = DriverManager.getConnection(connectString);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            mySQLConnection = conn;
        }
    }

    public static void initPatientsLikeMeData () {
        Statement stmtUserBasic = null;
        Statement stmtUserHistory = null;
        ResultSet rsUserBasic = null;
        ResultSet rsUserHistory = null;

        try {
            stmtUserBasic = mySQLConnection.createStatement();
            stmtUserHistory =  mySQLConnection.createStatement();
            String selectUserBasic = "SELECT * FROM " + userBasicTable + " ORDER BY ID";
            String selectUserHistory = "SELECT * FROM " + userHistoryTable + " ORDER BY ID, CONDITION_ID ASC LIMIT 0,2000";
            rsUserBasic = stmtUserBasic.executeQuery(selectUserBasic);
            rsUserHistory = stmtUserHistory.executeQuery(selectUserHistory);

            while(rsUserBasic.next()) {
                User u = new User();
                u.setId(rsUserBasic.getInt("ID"));
                u.setPatientsLikeMeId(rsUserBasic.getInt("PATIENT_ID"));
                u.setUserName(rsUserBasic.getString("USERNAME"));
                u.setGender(rsUserBasic.getString("GENDER"));
                u.setAge(rsUserBasic.getInt("Age"));
                u.setLocation(rsUserBasic.getString("Location"));
                userBasic.put(u.getId(), u);
            }

            int previousId = 0;
            ArrayList<Diagnosis> diagnosisList = new ArrayList<Diagnosis>();
            while(rsUserHistory.next()) {
                if(rsUserHistory.getInt("ID") != previousId && previousId > 0) {
                    userHistory.addUserHistory(userBasic.get(previousId), (ArrayList<Diagnosis>)diagnosisList.clone());
                    diagnosisList.clear();
                }
                Diagnosis diagnosis = new Diagnosis();
                diagnosis.setUserId(rsUserHistory.getInt("ID"));
                diagnosis.setCondition(new Condition(rsUserHistory.getString("HAS_CONDITION"), rsUserHistory.getInt("CONDITION_ID")));
                diagnosis.setPrimaryCondition(rsUserHistory.getBoolean("IS_PRIMARY_CONDITION"));
                diagnosis.setFirstSymptomDate(collaborativefiltering.Date.parsePatientsLikeMeDate(rsUserHistory.getString("FIRST_SYMPTOM")));
                diagnosis.setDiagnosisDate(Date.parsePatientsLikeMeDate(rsUserHistory.getString("DIAGNOSIS")));

                diagnosisList.add(diagnosis);
                previousId = rsUserHistory.getInt("ID");
            }
            if(!diagnosisList.isEmpty())
                userHistory.addUserHistory(userBasic.get(previousId), (ArrayList<Diagnosis>) diagnosisList.clone());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void getAllUserPrediction () {
        //in order to be considered, patients need to have at least threshold # of conditions
        int threshold = HEADSIZE + 1;



        UsersHistory selectedUH = userHistory.generateSelectedUsersHistory(threshold);

        PredictionResults pr = null;
        try {
            pr = selectedUH.getPrediction(HEADSIZE, headFile, rankListFile);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        getEvaluation(pr, selectedUH);

    }

    public static void getNewUserPrediction (ArrayList<Diagnosis> ad) {
        //create a dummy user
        User user = new User();

        PredictionResults pr = userHistory.getPrediction(user, ad);

        ArrayList<Result> rankList = userHistory.getPredictionRankedList(user, TOPN, pr);
        for (Result r: rankList) {
            StdOut.println(r.toString());
        }

    }

    public static void getEvaluation (PredictionResults pr, UsersHistory uh) {
        double accuracySum = 0;
        double accuracyAvg = 0;
        double coverageSum = 0;
        double coverageAvg = 0;

        Set<User> users = pr.getUsers();

        for (User u : users) {
            HashSet<Condition> nonHeadConditions = uh.getNonHeadConditions(u, HEADSIZE);
            ArrayList<Result> rankList = uh.getPredictionRankedList(u, TOPN, pr);
            accuracySum += getAccuracy(rankList, uh, u, nonHeadConditions);
            coverageSum += getCoverage(rankList, nonHeadConditions);
        }
        accuracyAvg = accuracySum / users.size();
        coverageAvg = coverageSum / users.size();

        StdOut.println("accuracySum: " + accuracySum + "||" + "coverageSum: " + coverageSum);
        StdOut.println("accuracyAvg: " + accuracyAvg + "||" + "coverageAvg: " + coverageAvg);

    }

    //what percentage of conditions in ranklist appear in the nonHeadConditions
    private static double getCoverage(ArrayList<Result> rankList, Set<Condition> nonHeadConditions) {
        double occurance = 0;
        for (Result r : rankList) {
            if(nonHeadConditions.contains(r.getCondition()))
                occurance++;
        }
        return Number.getNDecimals(occurance/nonHeadConditions.size(),2);
    }

    private static double getAccuracy(ArrayList<Result> rankList, UsersHistory uh, User u, Set<Condition> nonHeadConditions) {
        double decayLikelihood = getDecayLikelihood(rankList, uh, u, nonHeadConditions);
        double decayMaxLikelihood = getDecayMaxLikelihood(rankList, nonHeadConditions);
        return Number.getNDecimals(decayLikelihood/decayMaxLikelihood,2);
    }

    private static double getDecayLikelihood(ArrayList<Result> rankList, UsersHistory uh, User u, Set<Condition> nonHeadConditions) {

        double decaylikelihood = 0;

        for(int k = 0; k < rankList.size(); k++) {
            Result r = rankList.get(k);
            if(nonHeadConditions.contains(r.getCondition()))
                decaylikelihood += getDecayLikelihood(k);
        }
        return decaylikelihood;
    }

    private static double getDecayMaxLikelihood(ArrayList<Result> rankList, Set<Condition> nonHeadConditions) {
        int rankListSize = rankList.size();
        int nonHeadConditionsSize = nonHeadConditions.size();
        int listSize = (rankListSize < nonHeadConditionsSize) ? rankListSize : nonHeadConditionsSize;
        double decaylikelihood = 0;

        for(int k = 0; k < listSize; k++) {
            decaylikelihood += getDecayLikelihood(k);
        }
        return decaylikelihood;
    }

    private static double getDecayLikelihood(double rank) {
        int a = 5;
        return Number.getNDecimals(Math.pow(2, -rank/5),2);
    }
}
