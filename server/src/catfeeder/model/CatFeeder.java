package catfeeder.model;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.Date;

/**
 CREATE TABLE IF NOT EXISTS PUBLIC.cat_feeder
 (
 id INT AUTO_INCREMENT PRIMARY KEY NOT NULL,
 description TEXT,
 name VARCHAR(255),
 ip_address VARCHAR(255)
 );
 */
@DatabaseTable(tableName = "Cat_Feeder")
public class CatFeeder {
    @DatabaseField(id = true)
    private long hardware_id;
    @DatabaseField
    private String name;
    @DatabaseField
    private Date lastConnectionAt;

    public long getHardware_id() {
        return hardware_id;
    }

    public void setHardware_id(long hardware_id) {
        this.hardware_id = hardware_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getLastConnectionAt() {
        return lastConnectionAt;
    }

    public void setLastConnectionAt(Date lastConnectionAt) {
        this.lastConnectionAt = lastConnectionAt;
    }
}
