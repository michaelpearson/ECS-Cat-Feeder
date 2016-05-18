package catfeeder.util;

import org.json.simple.JSONObject;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class WriteJsonResponse {
    public static void writeResponse(HttpServletResponse response, JSONObject responseData) throws IOException {
        response.setHeader("content-type", "application/json");
        response.getWriter().write(responseData.toJSONString());
    }
}
