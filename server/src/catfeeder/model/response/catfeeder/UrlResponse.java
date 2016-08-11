package catfeeder.model.response.catfeeder;

import catfeeder.model.response.GeneralResponse;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.net.NetworkInterface;
import java.net.SocketException;

@XmlAccessorType(XmlAccessType.NONE)
public class UrlResponse extends GeneralResponse {

    public static final int PORT;
    public static final String HOST;

    static {
        String serverPort = System.getenv("server_port");
        String serverHost = System.getenv("server_host");
        if(serverHost == null) {
            try {
                serverHost = NetworkInterface.getNetworkInterfaces().nextElement().getInetAddresses().nextElement().getHostAddress();
            } catch (SocketException ignore) {
                serverHost = "catfeeder.heroku.com";
            }
        }
        if(serverPort == null) {
            serverPort = "6969";
        }
        PORT = Integer.valueOf(serverPort);
        HOST = serverHost;
    }

    @XmlElement
    private String host;
    @XmlElement
    private int port;

    public UrlResponse() {}

    public UrlResponse(String host, int port) {
        this.host = host;
        this.port = port;
        this.success = true;
    }

}
