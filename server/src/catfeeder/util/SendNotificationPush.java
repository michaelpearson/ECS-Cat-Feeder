package catfeeder.util;

import catfeeder.model.NotificationRegistrations;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SendNotificationPush {

    private static final java.net.URL GMS_URL;

    static {
        try {
            GMS_URL = new URL("http://android.googleapis.com/gcm/send");
        } catch (MalformedURLException e) {
            throw new RuntimeException();
        }
    }

    public static void sendNotificationPush(NotificationRegistrations registration) throws IOException {
        HttpURLConnection connection = (HttpURLConnection)GMS_URL.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", String.format("key= %s", System.getenv("GCM_API_KEY")));
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        JSONObject payload = new JSONObject();
        JSONArray registrationIds = new JSONArray();
        registrationIds.add(registration.getRegistrationId());
        payload.put("registration_ids", registrationIds);
        byte[] payloadBytes = payload.toJSONString().getBytes();
        connection.setRequestProperty("Content-Length", String.format("%d", payloadBytes.length));
        connection.setUseCaches(false);
        connection.getOutputStream().write(payloadBytes);
        connection.getResponseCode();
    }
}
