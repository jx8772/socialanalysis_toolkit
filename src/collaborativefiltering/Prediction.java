package collaborativefiltering;

import java.io.FileInputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: xiangji
 * Date: 10/3/13
 * Time: 3:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class Prediction {
    private static String database;
    private static String hostname;
    private static int port;
    private static String username;
    private static String password;
    private static String userBasicTable;
    private static String userHistoryTable;

    private static Connection mySQLConnection = null;
    private static UsersHistory userHistory = null;
    private static HashMap<Integer, User> userBasic = null;

    private static final int HEADSIZE = 3;

    public static void main(String[] args) throws Exception {
        init(args[0]);
        getPrediction();
    }

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
        userBasic = new HashMap<Integer, User >();

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
            String selectUserBasic = "SELECT * FROM " + userBasicTable;
            String selectUserHistory = "SELECT * FROM " + userHistoryTable;
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
                diagnosis.setFirstSymptomDate(Date.parsePatientsLikeMeDate(rsUserHistory.getString("FIRST_SYMPTOM")));
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

    public static void getPrediction () {
        //in order to be considered, patients need to have at least threshold # of conditions
        int threshold = HEADSIZE + 1;

        UsersHistory selectedUH = userHistory.cloneSelectedUsersHistory(threshold);
        selectedUH.getPrediction(HEADSIZE);
    }

    public static void getEvaluation (PredictionResults pr) {

    }
}
