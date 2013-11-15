package collaborativefiltering;

/**
 * Created with IntelliJ IDEA.
 * User: xiangji
 * Date: 10/14/13
 * Time: 12:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class Result implements Comparable<Result>{
    private User user;
    private Condition condition;
    private double score;
    private double support;

    public Result(User user, Condition condition, double score, double support) {
        this.user = user;
        this.condition = condition;
        this.score = score;
        this.support = support;
    }

    public double getSupport() {
        return this.support;
    }

    public void setSupport(int support) {
        this.support = support;
    }


    @Override
    public String toString() {
        return "user:{" + user.getId() + ", " + user.getUserName() + "}; condition:{" + condition.getConditionId() + ", " +
                condition.getConditionName() + "}; score:{" + score + "}; support:{" + support + "}";
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

    @Override
    /*public int compareTo(Result that) {
        if (this.score  < that.score)  return -1;
        else if (this.score  > that.score)  return +1;
        else if (this.support < that.support)
            return -1;
        else if (this.support > that.support)
            return +1;
        else
            return 0;
    }*/

    /*public int compareTo(Result that) {
       if (this.support < that.support)
            return -1;
        else if (this.support > that.support)
            return +1;
        else
            return 0;
    }*/

    public int compareTo(Result that) {
        if (this.support*this.score < that.support*that.score)
            return -1;
        else if (this.support*this.score > that.support*that.score)
            return +1;
        else
            return 0;
    }
}
