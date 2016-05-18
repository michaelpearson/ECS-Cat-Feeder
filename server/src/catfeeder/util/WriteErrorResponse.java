package catfeeder.util;

import org.json.simple.JSONObject;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class WriteErrorResponse {
    public static void Write(HttpServletResponse resp, String message) throws IOException {
        JSONObject responseData = new JSONObject();
        responseData.put("success", false);
        responseData.put("message", message);
        WriteJsonResponse.writeResponse(resp, responseData);
    }
}
