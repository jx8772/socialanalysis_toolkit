package collaborativefiltering;

import collaborativefiltering.utility.MaxPQ;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: xiangji
 * Date: 10/14/13
 * Time: 12:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class PredictionResults {

    private HashMap<User, MaxPQ<Result>> results;

    public PredictionResults() {
        results = new HashMap<User, MaxPQ<Result>>();
    }

    //add the result of user u to the existing results
    public void addResult(User u, Result r) {
        if(results.get(u) == null)
            results.put(u, new MaxPQ<Result>());
        results.get(u).insert(r);
    }

    public MaxPQ<Result> getResult(User u) {
        return results.get(u);
    }

    public HashSet<User> getUsers () {
        HashSet<User> users = new HashSet<User>();

        HashMap<User, MaxPQ<Result>> resultsCloned = (HashMap<User, MaxPQ<Result>>)results.clone();
        Iterator it = resultsCloned.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            users.add(((User)pairs.getKey()));
            it.remove(); // avoids a ConcurrentModificationException
        }

        return users;
    }


}


