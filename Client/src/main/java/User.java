/**
 * filename:
 * project: YouAreEll
 * author: https://github.com/vvmk
 * date: 3/20/18
 */
public class User {
    private String userid;
    private String name;
    private String github;

    public User() {}

    public User(String userid, String name, String github) {
        this.userid = userid;
        this.name = name;
        this.github = github;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGithub() {
        return github;
    }

    public void setGithub(String github) {
        this.github = github;
    }

    @Override
    public String toString() {
        return String.format("  userid: %s\n    name: %s\ngithub: %s", userid, name, github);
    }
}
