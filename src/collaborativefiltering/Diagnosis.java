package collaborativefiltering;

/**
 * Created with IntelliJ IDEA.
 * User: xiangji
 * Date: 10/3/13
 * Time: 3:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class Diagnosis implements Comparable<Diagnosis> {
    private int userId;
    private Condition condition;
    private boolean isPrimaryCondition;
    private Date firstSymptomDate;
    private Date DiagnosisDate;

    public Diagnosis() {
    }

    public Diagnosis(int userId, Condition condition, boolean primaryCondition, Date firstSymptomDate, Date diagnosisDate) {
        this.userId = userId;
        this.condition = condition;
        isPrimaryCondition = primaryCondition;
        this.firstSymptomDate = firstSymptomDate;
        DiagnosisDate = diagnosisDate;
    }

    public Condition getCondition() {
        return condition;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public boolean isPrimaryCondition() {
        return isPrimaryCondition;
    }

    public void setPrimaryCondition(boolean primaryCondition) {
        isPrimaryCondition = primaryCondition;
    }

    public Date getFirstSymptomDate() {
        return firstSymptomDate;
    }

    public void setFirstSymptomDate(Date firstSymptomDate) {
        this.firstSymptomDate = firstSymptomDate;
    }

    public Date getDiagnosisDate() {
        return DiagnosisDate;
    }

    public void setDiagnosisDate(Date diagnosisDate) {
        DiagnosisDate = diagnosisDate;
    }

    @Override

    public int compareTo(Diagnosis that) {
        //first compare the diagnosis dates
        if (this.DiagnosisDate.isBefore(that.DiagnosisDate))  return -1;
        else if (this.DiagnosisDate.isAfter(that.DiagnosisDate))  return +1;
        //if diagnosis dates are equal, compare symptom dates
        else if (this.firstSymptomDate.isBefore(that.firstSymptomDate)) return -1;
        else if (this.firstSymptomDate.isAfter(that.firstSymptomDate)) return +1;
        //if both diagnosis dates and symptom dates are equal, return equal
        else
            return 0;
    }

    @Override
    public String toString() {
        return "Diagnosis{" +
                "userId=" + userId +
                ", conditionid=" + condition.getConditionId() +
                ", conditionname=" + condition.getConditionName() +
                ", isPrimaryCondition=" + isPrimaryCondition +
                ", firstSymptomDate=" + firstSymptomDate +
                ", DiagnosisDate=" + DiagnosisDate +
                '}';
    }
}
