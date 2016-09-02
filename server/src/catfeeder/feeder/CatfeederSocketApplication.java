package catfeeder.feeder;

import org.glassfish.grizzly.websockets.DataFrame;
import org.glassfish.grizzly.websockets.WebSocket;
import org.glassfish.grizzly.websockets.WebSocketApplication;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class CatfeederSocketApplication extends WebSocketApplication {
    private static CatfeederSocketApplication singleton;

    private static Map<WebSocket, CatFeederConnection> catFeeders = new HashMap<>();

    private CatfeederSocketApplication() {}

    public static CatfeederSocketApplication getInstance() {
        if(singleton == null) {
            singleton = new CatfeederSocketApplication();
        }
        return singleton;
    }

    public static CatFeederConnection getCatfeederConnection(long catfeederId) {
        for(Map.Entry<WebSocket, CatFeederConnection> entry : catFeeders.entrySet()) {
            if(entry.getValue().getFeederHardwareId() == catfeederId && entry.getKey().isConnected()) {
                return entry.getValue();
            }
        }
        return null;
    }

    @Override
    public void onMessage(WebSocket socket, String message) {
        try {
            JSONObject data = (JSONObject)new JSONParser().parse(message);
            if(catFeeders.keySet().contains(socket)) {
                catFeeders.get(socket).onMessage(data);
            } else {
                long feederId = (long)data.get("deviceId");
                catFeeders.values().stream().filter(c -> c.getFeederHardwareId() == feederId).forEach(c -> c.getWebSocket().close());
                catFeeders.put(socket, new CatFeederConnection(socket, (int)feederId));
            }
        } catch (ParseException | SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClose(WebSocket socket, DataFrame frame) {
        catFeeders.remove(socket);
    }
}
