package collaborativefiltering;

/**
 * Created with IntelliJ IDEA.
 * User: xiangji
 * Date: 10/4/13
 * Time: 10:26 AM
 * To change this template use File | Settings | File Templates.
 */
public class Condition {
    String ConditionName;
    int ConditionId;

    public Condition(String conditionName, int conditionId) {
        ConditionName = conditionName;
        ConditionId = conditionId;
    }

    public Condition() {

    }

    public String getConditionName() {
        return ConditionName;
    }

    public void setConditionName(String conditionName) {
        ConditionName = conditionName;
    }

    public int getConditionId() {
        return ConditionId;
    }

    public void setConditionId(int conditionId) {
        ConditionId = conditionId;
    }

    @Override
    public boolean equals(Object that) {
        return (this.ConditionId == ((Condition)that).ConditionId);
    }

    /**
     * Return a hash code.
     * @return a hash code for this date
     */
    @Override
    public int hashCode() {
        int hash = 17;
        hash = 31*hash + ConditionId;
        return hash;
    }
}
