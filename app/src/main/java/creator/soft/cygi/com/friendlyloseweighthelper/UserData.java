package creator.soft.cygi.com.friendlyloseweighthelper;

/**
 * Created by CygiMasterProgrammer on 2016-02-01.
 */
public class UserData {
    private Long userId;
    private String name;
    private String password="";
    private String weightGoal;

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setWeightGoal(String weightGoal) {
        this.weightGoal = weightGoal;
    }

    public String getWeightGoal() {
        return weightGoal;
    }

    @Override
    public String toString() {
        return name;
    }
}
