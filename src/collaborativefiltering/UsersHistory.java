package collaborativefiltering;

import collaborativefiltering.utility.MaxPQ;
import collaborativefiltering.utility.Quick;
import collaborativefiltering.utility.Number;
import collaborativefiltering.utility.StdOut;
import collaborativefiltering.utility.IOOperation;

import java.io.BufferedWriter;
import java.io.IOException;
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

    private double conditionUnionSize = 0;

    public UsersHistory() {
        usersHistory = new HashMap<User, ArrayList<Diagnosis>>();
    }

    public void addUserHistory (User user, ArrayList<Diagnosis> diagnosises) {
        usersHistory.put(user, diagnosises);
    }

    private double getConditionSupport(Condition c) {
        double num = 0;
        double cz = 0;
        if (conditionUnionSize != 0)
            cz = conditionUnionSize;
        else  {
            cz = getConditionUnion().size();
            conditionUnionSize = cz;
        }

        Iterator it = usersHistory.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            ArrayList<Diagnosis> ds = (ArrayList<Diagnosis>)pairs.getValue();

            for (Diagnosis s : ds) {
                if(c.equals(s.getCondition()))
                    num++;
            }
        }



        return Number.getNDecimals(num/cz,5);
    }

    public UsersHistory generateSelectedUsersHistory(int diagnosisNumThreshold) {
        UsersHistory selectedUsersHistory = new UsersHistory();

        HashMap<User, ArrayList<Diagnosis>> usersHistoryCloned = (HashMap<User, ArrayList<Diagnosis>>)usersHistory.clone();

        //search each patient, if his number of diagnosis is >= threshold, add the patients and his diagnosis to the new hashmap
        Iterator it = usersHistoryCloned.entrySet().iterator();
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

    //get the conditions of one user, if the condition is contained in another set of conditions
    private HashSet<Condition> getConditionContained(User u, Set<Condition> cset) {
        HashSet<Condition> conditionContained = new HashSet<Condition>();

        //get the Diagonsis ArrayList of one user
        ArrayList<Diagnosis> ds = usersHistory.get(u);

        for (Diagnosis d : ds) {
            Condition c = d.getCondition();
            if(cset.contains(c))
                conditionContained.add(c);
        }
        return conditionContained;
    }

    public double getWeightScore(User self, User other, Set<Condition> head) {
        double weightScore = 0;

        HashSet<Condition> conditionContained = getConditionContained(other, head);
        weightScore = Number.getNDecimals((double)conditionContained.size()/(double)head.size(),2);

        return weightScore;
    }

    public double getPredictionScore (User u, Condition c, Set<Condition> head) {
        double predictionScore = 0;
        Set<User> neighbors = getNeighbors(u, c);

        for(User neighbor : neighbors) {
            predictionScore += getWeightScore(u, neighbor, head);
        }

        if(neighbors.size() == 0)
            return 0;
        else {
            predictionScore = predictionScore / neighbors.size();
            return predictionScore;
        }
    }

    //get the users who also have condition c
    private HashSet<User> getNeighbors(User u, Condition c) {
        HashSet<User> neighbors = new HashSet<User>();

        HashMap<User, ArrayList<Diagnosis>> usersHistoryCloned = (HashMap<User, ArrayList<Diagnosis>>)usersHistory.clone();
        Iterator it = usersHistoryCloned.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            ArrayList<Diagnosis> ds = (ArrayList<Diagnosis>)pairs.getValue();
            User neighboru = (User)pairs.getKey();

            for (Diagnosis s : ds) {
                if(s.getCondition().equals(c) && !neighboru.equals(u))
                    neighbors.add(neighboru);
            }
            it.remove(); // avoids a ConcurrentModificationException
        }
        return neighbors;
    }

    public PredictionResults getPrediction (int headSize, String hf, String rlf) throws IOException {
        PredictionResults pr = new PredictionResults();
        Set<Condition> cu = getConditionUnion();
        Set<User> users = getUsers();

        BufferedWriter rankListFileWriter = IOOperation.getBufferedWriter(rlf);
        BufferedWriter headFileWriter = IOOperation.getBufferedWriter(hf);

        double count = 0;

        //make predictions for each user
        for(User user : users) {
            Set<Condition> head = getHeadConditions(user, headSize);

            Set<Condition> nonHead = getNonHeadConditions(user, headSize);
            Set<Condition> target = new HashSet<Condition>(cu);
            target.removeAll(head);
            MaxPQ<Result> heap = new MaxPQ<Result>();

            //make predictions of each target condition for user
            for(Condition c : target) {
                Double score = getPredictionScore(user, c, new HashSet<Condition>(head));
                Result r = new Result(user, c, score, getConditionSupport(c));
                heap.insert(r);
                pr.addResult(user, r);
            }

            // store the ranklist results into file
            while(!heap.isEmpty()) {
                Result r = heap.delMax();
                if(nonHead.contains(r.getCondition()))
                    rankListFileWriter.write(r.toString() + "InTail" + "\n");
                else
                    rankListFileWriter.write(r.toString() + "\n");
            }
            // store the headConditions into file
            for(Condition c : head) {
                headFileWriter.write("head;" + user.toString() + ";" + c.toString() + "\n");
            }
            for(Condition c : nonHead) {
                headFileWriter.write("tail;" + user.toString() + ";" + c.toString() + "\n");
            }
            for(Condition c : target) {
                headFileWriter.write("target;" + user.toString() + ";" + c.toString() + "\n");
            }
            StdOut.println("finished: " + Number.getNDecimals(++count/users.size(),2));
        }

        if(rankListFileWriter != null) {
            rankListFileWriter.close();
            rankListFileWriter = null;
        }
        if(headFileWriter != null) {
            headFileWriter.close();
            headFileWriter = null;
        }
        return pr;
    }

    //make prediction for new user
    public PredictionResults getPrediction (User user, ArrayList<Diagnosis> ad) {

        PredictionResults pr = new PredictionResults();
        Set<Condition> cu = getConditionUnion();

        Set<Condition> head = new HashSet<Condition>();

        for(Condition c : cu) {
            StdOut.println(c.getConditionId());
        }

        //use all of new users' reported conditions as head conditions
        for (Diagnosis d : ad) {
            head.add(d.getCondition());
        }

        Set<Condition> target = new HashSet<Condition>(cu);
        //remove all head conditions from the targeted conditions
        target.removeAll(head);

        //make predictions of each target condition for user
        for(Condition c : target) {
            StdOut.println(c.getConditionId());
            Double score = getPredictionScore(user, c, new HashSet<Condition>(head));
            Result r = new Result(user, c, score, getConditionSupport(c));
            pr.addResult(user, r);
        }
        return pr;
    }

    //ArrayList<Result>[0] is the condition with largest probability, ArrayList<Result>[1] is the second largest, etc
    public ArrayList<Result> getPredictionRankedList(User u, int topN, PredictionResults pr) {
        ArrayList<Result> rankedList = new ArrayList<Result>();
        MaxPQ<Result> resultPQ = pr.getResult(u);

        for(int i = 1; i <= topN; i++) {
            if(!resultPQ.isEmpty())
                rankedList.add(resultPQ.delMax());
            else
                break;
        }

        return rankedList;
    }

    //
    public HashSet<Condition> getHeadConditions(User u, int headSize) throws IOException {
        HashSet<Condition> conditions = new HashSet<Condition>();

        //convert the Diagonsis ArrayList into Diagnosis array
        Diagnosis[] da = (usersHistory.get(u)).toArray(new Diagnosis[0]);
        //sort the array
        Arrays.sort(da);
        for(int i = 0; i < headSize; i++) {
            //get the ith earliest condition
            Condition c = da[i].getCondition();

            conditions.add(c);
        }

        return conditions;
    }

    // get the conditions, which are not in headset
    public HashSet<Condition> getNonHeadConditions(User u, int headSize) {
        HashSet<Condition> conditions = new HashSet<Condition>();

        //convert the Diagonsis ArrayList into Diagnosis array
        Diagnosis[] da = (usersHistory.get(u)).toArray(new Diagnosis[0]);
        //sort the array
        Arrays.sort(da);
        for(int i = headSize; i < da.length; i++) {
            //get the ith earliest condition
            Condition c = da[i].getCondition();
            conditions.add(c);
        }
        return conditions;
    }

    private HashSet<User> getUsers() {
        HashSet<User> users = new HashSet<User>();

        HashMap<User, ArrayList<Diagnosis>> usersHistoryCloned = (HashMap<User, ArrayList<Diagnosis>>)usersHistory.clone();
        Iterator it = usersHistoryCloned.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            users.add(((User)pairs.getKey()));
            it.remove(); // avoids a ConcurrentModificationException
        }

        return users;
    }

    private ArrayList<Diagnosis> getDiagnoses() {
        ArrayList<Diagnosis> combined = new ArrayList<Diagnosis>();

        HashMap<User, ArrayList<Diagnosis>> usersHistoryCloned = (HashMap<User, ArrayList<Diagnosis>>)usersHistory.clone();
        Iterator it = usersHistoryCloned.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            ArrayList<Diagnosis> ds = (ArrayList<Diagnosis>)pairs.getValue();
            combined.addAll(ds);
            it.remove(); // avoids a ConcurrentModificationException
        }

        return combined;
    }

    public User getUser(int id) {
        HashMap<User, ArrayList<Diagnosis>> usersHistoryCloned = (HashMap<User, ArrayList<Diagnosis>>)usersHistory.clone();
        Iterator it = usersHistoryCloned.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            User u  = ((User)pairs.getKey());
            if(u.getId() == id)
                return u;
            it.remove(); // avoids a ConcurrentModificationException
        }
        return new User();
    }

    public void preProcess() {
        deleteInvalidDiagnosis();
        fillMissingValues();
    }

    private void deleteInvalidDiagnosis() {
        //if both symptom date and diagnosis date are empty, the diagnosis will be deleted

        //the usersHistory itself will be used, since it needs to be modified
        Iterator it = usersHistory.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            ArrayList<Diagnosis> ds = (ArrayList<Diagnosis>)pairs.getValue();

            //if the user is null, use the userid in diagnosis as the user's id
            User user = (User)pairs.getKey();
            if(user == null)  {
                user = new User();
                user.setId(ds.get(0).getUserId());
            }

            //StdOut.println(ds.size());

            for (int i = ds.size()-1; i >= 0; i--) {
                Diagnosis d = ds.get(i);
                if(!d.getDiagnosisDate().isPatientLikeMeDateValid() && !d.getFirstSymptomDate().isPatientLikeMeDateValid()) {
                    ds.remove(i);
                }
            }

        }
    }

    private void fillMissingValues() {
        //HashMap<User, ArrayList<Diagnosis>> usersHistoryCloned = (HashMap<User, ArrayList<Diagnosis>>)usersHistory.clone();

        // get the gap of all users
        int global_gap = getAverageSymptomDiagnosisGap(getDiagnoses());
        HashSet<User> users = getUsers();
        for(User u : users) {
            ArrayList<Diagnosis> ds = usersHistory.get(u);
            int gap;

            //if the user's own average gap is not 0, use this gap
            if(getAverageSymptomDiagnosisGap(ds) != 0)
                gap = getAverageSymptomDiagnosisGap(ds);
            //if the user's own average gap is 0, use the average gap of all users' history
            else
                gap = global_gap;

            for(Diagnosis d : ds) {
                //if dianogsis is null or symptom date is null, use the valid date and gap to fill the other invalid date
                if(d.getFirstSymptomDate().isPatientLikeMeDateValid() && !d.getDiagnosisDate().isPatientLikeMeDateValid()) {
                    d.setDiagnosisDate(new Date(d.getFirstSymptomDate(), gap));
                } else if (!d.getFirstSymptomDate().isPatientLikeMeDateValid() && d.getDiagnosisDate().isPatientLikeMeDateValid()) {
                    d.setFirstSymptomDate(new Date(d.getDiagnosisDate(), -gap));
                }
            }

        }
    }


    private int getAverageSymptomDiagnosisGap(ArrayList<Diagnosis> ds) {
        int gap = 0;
        int countValid = 0;

        for (int i = 0; i < ds.size(); i++) {
            Diagnosis d = ds.get(i);
            if(d.getDiagnosisDate().isPatientLikeMeDateValid() && d.getFirstSymptomDate().isPatientLikeMeDateValid()) {
                gap += d.getFirstSymptomDate().getDifference(d.getDiagnosisDate());
                countValid++;
            }
        }

        if(countValid == 0)
            return 0;
        else
            return gap/countValid;
    }

    //used to check the number of invalid dates
    public void checkDates () {
        int numSymptomDateMissing = 0;
        int numDiagnosisDateMissing = 0;
        int numBothMissing = 0;
        int counter = 0;

        //the usersHistory itself will be used, since it needs to be modified
        Iterator it = usersHistory.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            ArrayList<Diagnosis> ds = (ArrayList<Diagnosis>)pairs.getValue();

            for (int i = 0; i < ds.size(); i++) {
                Diagnosis d = ds.get(i);
                //StdOut.println(d.getUserId() + "||" + d.getCondition() + "||" + d.getFirstSymptomDate().toString() + "||" + d.getDiagnosisDate().toString());
                if(d.getDiagnosisDate().isPatientLikeMeDateValid() && !d.getFirstSymptomDate().isPatientLikeMeDateValid()) {
                    numSymptomDateMissing++;
                }
                if(!d.getDiagnosisDate().isPatientLikeMeDateValid() && d.getFirstSymptomDate().isPatientLikeMeDateValid()) {
                    numDiagnosisDateMissing++;
                }
                if(!d.getDiagnosisDate().isPatientLikeMeDateValid() && !d.getFirstSymptomDate().isPatientLikeMeDateValid()) {
                    //StdOut.println("both missing, userid: " + d.getUserId() + ", conditionid: " + d.getCondition().getConditionId());
                    numSymptomDateMissing++;
                    numDiagnosisDateMissing++;
                    numBothMissing++;
                }
                counter++;
            }

        }
        StdOut.println(counter + " diagnoses are examined");
        StdOut.println("numSymptomDateMissing: " + numSymptomDateMissing);
        StdOut.println("numDiagnosisDateMissing: " + numDiagnosisDateMissing);
        StdOut.println("numBothMissing: " + numBothMissing);
    }

    public void dataAnalysis(String analysisFile) throws IOException {
        BufferedWriter analysisFileWriter = IOOperation.getBufferedWriter(analysisFile);

        getDemographicDist(analysisFileWriter);
        getDiagnosisDist(analysisFileWriter);

        if(analysisFileWriter != null) {
            analysisFileWriter.close();
            analysisFileWriter = null;
        }
    }

    public void getDemographicDist(BufferedWriter analysisFileWriter) throws IOException {
        final int[] AGES = { 10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110, 120};

        int male = 0;
        int female = 0;
        int unknown = 0;

        int[] agesDist = new int[AGES.length];
        for (int i = 0; i < agesDist.length; i++)
            agesDist[i] = 0;

        HashMap<User, ArrayList<Diagnosis>> usersHistoryCloned = (HashMap<User, ArrayList<Diagnosis>>)usersHistory.clone();
        Iterator it = usersHistoryCloned.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            User u  = ((User)pairs.getKey());
            if(u != null) {
                String gender = u.getGender();
                if(gender != null) {
                    if (u.getGender().equals("Male"))
                        male++;
                    else if (u.getGender().equals("Female"))
                        female++;
                    else
                        unknown++;
                } else {
                    unknown++;
                }

                int age = u.getAge();
                if (age != 0) {
                    for (int i = 0; i < AGES.length; i++) {
                        if(age < AGES[i])  {
                            agesDist[i]++;
                            break;
                        }
                    }
                }
            }

            it.remove(); // avoids a ConcurrentModificationException
        }
        analysisFileWriter.write("gender distribution" + "\n");
        analysisFileWriter.write("male" + "\n" + "female" + "\n" + "unknown" + "\n");
        analysisFileWriter.write(male + "\n" + female + "\n" + unknown + "\n");
        analysisFileWriter.write("\n");

        analysisFileWriter.write("age distribution" + "\n");
        for (int i = 0; i < agesDist.length; i++) {
            analysisFileWriter.write(agesDist[i] + "\n");
        }
        analysisFileWriter.write("\n");
    }

    public void getDiagnosisDist(BufferedWriter analysisFileWriter) throws IOException {
        Set<User> users = getUsers();
        Set<Condition> cu = getConditionUnion();
        int freqDistSize = 22;
        int conditionSize = 1300;
        int[] conditionFreqDist = new int[freqDistSize];
        int[] conditionDist = new int[conditionSize];
        for (int i = 0; i < freqDistSize; i++)
            conditionFreqDist[i] = 0;
        for (int i = 0; i < cu.size(); i++)
            conditionDist[i] = 0;

        for (User u : users) {
            int conditionFreq = usersHistory.get(u).size();
            if(conditionFreq >= freqDistSize-1)
                conditionFreqDist[freqDistSize-1]++;
            else
                conditionFreqDist[conditionFreq]++;

            ArrayList<Diagnosis> ad = usersHistory.get(u);
            for (Diagnosis d : ad) {
                conditionDist[d.getCondition().getConditionId()]++;
            }
        }

        analysisFileWriter.write("user frequnecy distribution" + "\n");
        for (int i = 0; i < conditionFreqDist.length; i++) {
            analysisFileWriter.write(conditionFreqDist[i] + "\n");
        }
        analysisFileWriter.write("\n");

        analysisFileWriter.write("condition frequnecy distribution" + "\n");
        for (int i = 0; i < conditionDist.length; i++) {
            analysisFileWriter.write(conditionDist[i] + "\n");
        }
        analysisFileWriter.write("\n");

        analysisFileWriter.write("top n conditions" + "\n");

    }

    /*void getTopNCondition(int[] conditionDist, int N, BufferedWriter analysisFileWriter) throws IOException {
        for(int i = 0; i < N; i-- ) {

        }

    }*/
}
