package collaborativefiltering;

import collaborativefiltering.Condition;
import collaborativefiltering.User;

/**
 * Created with IntelliJ IDEA.
 * User: xiangji
 * Date: 10/14/13
 * Time: 12:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class Result {
    private User user;
    private Condition condition;
    private double score;

    public Result(User user, Condition condition, double score) {
        this.user = user;
        this.condition = condition;
        this.score = score;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Condition getCondition() {
        return condition;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}
