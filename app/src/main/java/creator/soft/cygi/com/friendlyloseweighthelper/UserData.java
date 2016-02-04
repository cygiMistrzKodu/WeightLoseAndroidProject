package creator.soft.cygi.com.friendlyloseweighthelper;

import android.util.Log;

/**
 * Created by CygiMasterProgrammer on 2016-02-01.
 */
public class UserData {
    private Long userId;
    private String name;
    private String password;

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
}
