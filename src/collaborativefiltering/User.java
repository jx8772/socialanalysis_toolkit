package collaborativefiltering;

/**
 * Created with IntelliJ IDEA.
 * User: xiangji
 * Date: 10/3/13
 * Time: 3:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class User {
    private int id;
    private int patientsLikeMeId;
    private String userName;
    private String gender;
    private int age;
    private String location;

    public User() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPatientsLikeMeId() {
        return patientsLikeMeId;
    }

    public void setPatientsLikeMeId(int patientsLikeMeId) {
        this.patientsLikeMeId = patientsLikeMeId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public boolean equals(Object that) {
        return (this.id == ((User)that).id);
    }

    /**
     * Return a hash code.
     * @return a hash code for this date
     */
    @Override
    public int hashCode() {
        int hash = 17;
        hash = 31*hash + id;
        return hash;
    }
}
