package catfeeder.model.response.user;

import catfeeder.model.User;
import catfeeder.model.response.GeneralResponse;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "")
public class UserResponse extends GeneralResponse {
    @XmlElement
    private User user;

    public UserResponse() {}

    public UserResponse(User u) {
        this.user = u;
        this.success = true;
    }
}
