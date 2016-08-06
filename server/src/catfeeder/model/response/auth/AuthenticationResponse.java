package catfeeder.model.response.auth;

import catfeeder.model.response.GeneralResponse;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "")
public class AuthenticationResponse extends GeneralResponse {

    @XmlElement
    private String token;

    public AuthenticationResponse() {}

    public AuthenticationResponse(boolean success, String token) {
        this.success = success;
        this.token = token;
    }
}
