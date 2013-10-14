package collaborativefiltering;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: xiangji
 * Date: 10/14/13
 * Time: 11:59 AM
 * To change this template use File | Settings | File Templates.
 */
public class UsersHistory {
    private HashMap<User, ArrayList<Diagnosis>> usersHistory;

    public UsersHistory() {
        usersHistory = new HashMap<User, ArrayList<Diagnosis>>();
    }

    public void addUserHistory (User user, ArrayList<Diagnosis> diagnosises) {
        usersHistory.put(user, diagnosises);
    }

    public UsersHistory cloneSelectedUsersHistory(int diagnosisNumThreshold) {
        UsersHistory selectedUsersHistory = new UsersHistory();

        //search each patient, if his number of diagnosis is >= threshold, add the patients and his diagnosis to the new hashmap
        Iterator it = usersHistory.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            if(((ArrayList<Diagnosis>)pairs.getValue()).size() >= diagnosisNumThreshold) {
                selectedUsersHistory.addUserHistory((User)pairs.getKey(), (ArrayList<Diagnosis>)pairs.getValue());
            }
            it.remove(); // avoids a ConcurrentModificationException
        }

        return selectedUsersHistory;
    }

    public Set<Condition> selectConditions (int patientId, int numCondition, String order) {
        Set<Condition> subsetCondition = new HashSet<Condition>();

        if (order.equals("increase")) {

        }

        return subsetCondition;
    }

    private HashSet<Condition> getConditionUnion() {
        HashSet<Condition> conditionUnion = new HashSet<Condition>();

        //search each patient's conditions, add each condition to the conditionunion set
        HashMap<User, ArrayList<Diagnosis>> usersHistoryCloned = (HashMap<User, ArrayList<Diagnosis>>)usersHistory.clone();
        Iterator it = usersHistoryCloned.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            ArrayList<Diagnosis> ds = (ArrayList<Diagnosis>)pairs.getValue();

            for (Diagnosis s : ds) {
                conditionUnion.add(s.getCondition());
            }
            it.remove(); // avoids a ConcurrentModificationException
        }
        return conditionUnion;
    }

    public HashMap<User, ArrayList<Diagnosis>> getUsersHistory() {
        return usersHistory;
    }

    public Set<User> getUsersWithCondition (Condition c) {
        Set<User> users = new HashSet<User>();

        return users;
    }

    public double getWeightScore(User u1, User u2) {
        double weightScore = 0;

        return weightScore;
    }

    public double getPredictionScore (User u, Condition c) {
        double predictionScore = 0;

        return predictionScore;
    }

    public PredictionResults getPrediction (int headSize) {
        PredictionResults pr = new PredictionResults();
        Set<Condition> cu = getConditionUnion();
        Set<User> users = getUsers();


        return pr;
    }

    private HashSet<User> getUsers() {
        HashSet<User> users = new HashSet<User>();

        HashMap<User, ArrayList<Diagnosis>> usersHistoryCloned = (HashMap<User, ArrayList<Diagnosis>>)usersHistory.clone();
        Iterator it = usersHistory.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            users.add(((User)pairs.getKey()));
            it.remove(); // avoids a ConcurrentModificationException
        }

        return users;
    }
}
