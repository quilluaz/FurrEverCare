package cit.edu.furrevercare.entity;

public class User {
    private String userID;
    private String name;
    private String email;
    private String password;
    private String phone;

    public User() {
    }

    public User(String userID, String name, String email, String password, String phone) {
        this.userID = userID;
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
    }

    public String getUserID() {
        return userID;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getPhone(){
        return phone;
    }
    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPhone(String phone){
        this.phone = phone;
    }
}
