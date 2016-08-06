package catfeeder.model.response.user;

import catfeeder.model.User;
import catfeeder.model.response.GeneralResponse;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "")
public class UserResponse extends GeneralResponse {
    @XmlElement
    private String email;
    @XmlElement(defaultValue = "")
    private String name;

    public UserResponse() {}

    public UserResponse(User u) {
        this.email = u.getEmail();
        this.name = u.getName();
        this.success = true;
    }
}
