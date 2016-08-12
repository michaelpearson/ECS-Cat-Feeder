package catfeeder.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@DatabaseTable(tableName = "tag")
@XmlAccessorType(XmlAccessType.NONE)
public class Tag {
    @XmlElement
    @DatabaseField(generatedId = true)
    private int id;

    @XmlElement
    @DatabaseField
    private long tagUID;

    @DatabaseField(foreign = true)
    private User user;

    @XmlElement
    @DatabaseField
    private String tagName;

    public int getId() {
        return id;
    }

    public long getTagUID() {
        return tagUID;
    }

    /**
     * Note that @param tagUID can only be a 32bit unsigned integer. Java requires a long to enforce unsigned.
     * @param tagUID The tag UID
     */
    public void setTagUID(long tagUID) {
        this.tagUID = tagUID & 0xFFFFFFFFL;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
