package catfeeder.model;

import catfeeder.util.Passwords;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@DatabaseTable(tableName = "users")
public class User {
    @DatabaseField(id = true)
    private String email;
    @DatabaseField
    private String password;
    @DatabaseField
    private String name;
    @ForeignCollectionField
    private ForeignCollection<FeederUserConnection> feedersConnections;


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


    public List<CatFeeder> getFeeders() {
        return feedersConnections.stream().map(FeederUserConnection::getFeeder).collect(Collectors.toList());
    }

    public boolean doesUserOwnCatfeeder(CatFeeder feeder) {
        return getFeeders().stream().anyMatch(f -> f.getHardwareId() == feeder.getHardwareId());
    }

    public boolean isSame(User user) {
        return user != null && user.getEmail().equals(getEmail());
    }
}
