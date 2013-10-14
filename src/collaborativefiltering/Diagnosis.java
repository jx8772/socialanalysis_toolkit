package collaborativefiltering;

/**
 * Created with IntelliJ IDEA.
 * User: xiangji
 * Date: 10/3/13
 * Time: 3:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class Diagnosis {
    private int userId;
    private Condition condition;
    private boolean isPrimaryCondition;
    private Date firstSymptomDate;
    private Date DiagnosisDate;

    public Diagnosis() {
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
}
