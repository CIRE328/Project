package pojo;

public class User {
    private Integer id;
    private String username;
    private String password;
    private String email;
    private String phone;
    private String role;

    public User(Integer id, String username, String password, String email, String phone, String role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.role = role;
    }



}
