package POJO;

import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.Nullable;

public class User {
    private String email;
    private String password;
    private String name;

    // конструктор со всеми параметрами
    public User(String email, String password, @Nullable String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    // конструктор без параметров
    public User() {
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static User generateRandomUser() {

        String email = RandomStringUtils.randomAlphanumeric(5) + "@yandex.ru";
        String password = RandomStringUtils.randomAlphanumeric(6);
        String name = RandomStringUtils.randomAlphanumeric(6);

        return new User(email, password, name);
    }
}
