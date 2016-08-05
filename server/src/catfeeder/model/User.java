package catfeeder.model;

import catfeeder.Passwords;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "users")
public class User {
    @DatabaseField(id = true)
    private String email;
    @DatabaseField
    private String password;
    @DatabaseField
    private String name;
    @DatabaseField(foreign = true, canBeNull = false)
    private CatFeeder catFeeder;

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


    public boolean checkPassword(String password) {
        return Passwords.checkPassword(password, this.password);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CatFeeder getCatFeeder() {
        return catFeeder;
    }

    public void setCatFeeder(CatFeeder catFeeder) {
        this.catFeeder = catFeeder;
    }
}
