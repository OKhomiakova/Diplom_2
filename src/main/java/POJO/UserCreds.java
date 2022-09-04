package POJO;

public class UserCreds {
    private String email;
    private String password;

    // конструктор со всеми параметрами
    public UserCreds(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // конструктор без параметров
    public UserCreds() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public static UserCreds from(User user) {
        return new UserCreds(user.getEmail(), user.getPassword());
    }

}

