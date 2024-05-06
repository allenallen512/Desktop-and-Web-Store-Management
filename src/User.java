public class User {

    public int userID;

    public String userName;

    public String userEmail;

    public User() {
        this.userID = 999;
        this.userName = "No Name";
        this.userEmail = "no@email.com";
    }

    public User(int id, String name, String email) {
        this.userID = id;
        this.userName = name;
        this.userEmail = email;
    }
}
