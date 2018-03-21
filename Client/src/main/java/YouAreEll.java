import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.io.IOException;

public class YouAreEll {
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private final String baseUrl = "http://zipcode.rocks:8085";
    private OkHttpClient client;
    private ObjectMapper mapper;
    private String fromId;

    YouAreEll() {
        client = new OkHttpClient();
        mapper = new ObjectMapper();
        fromId = "ghost";
    }

    public static void main(String[] args) throws IOException {
        YouAreEll urlhandler = new YouAreEll();
        ObjectMapper mapper = new ObjectMapper();
    }

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    public String post_ids(String payload) throws IOException {
        //return payload;
        return MakeURLCall("/ids", "POST", payload);
    }

    public String put_ids(User user) throws IOException {
        String payload = mapper.writeValueAsString(user);
        return MakeURLCall("/ids", "PUT", payload);
    }

    public String get_ids(String... path) throws IOException {
        String mainurl = "/ids";
        if (path.length > 0) {
            mainurl = mainurl + path[0];
        }
        return MakeURLCall(mainurl, "GET", "");
    }

    public String post_messages(Message msg) throws IOException {
        String payload = mapper.writeValueAsString(msg);
        return MakeURLCall("/ids/" + msg.getFromid() + "/messages", "POST", payload);
    }

    public String get_messages(String... path) throws IOException {
        String mainurl = "/messages";
        if (path.length > 0)
            mainurl = "/ids/" + path[0] + mainurl;

        if (path.length > 1)
            mainurl = mainurl + "/" + path[1];

        return MakeURLCall(mainurl, "GET", "");
    }

    public String MakeURLCall(String mainurl, String method, String jpayload) throws IOException {
        String finalURL = baseUrl + mainurl;

        Request request = null;

        if (method.equalsIgnoreCase("get")) {
            request = new Request.Builder()
                    .url(finalURL)
                    .build();
        }


        if (method.equalsIgnoreCase("POST")) {
            RequestBody body = RequestBody.create(JSON, jpayload);
            request = new Request.Builder()
                    .url(finalURL)
                    .post(body)
                    .build();
        }

        if (method.equalsIgnoreCase("PUT")) {
            return "PUT currently under maintenance. sorry!";
        }

        if (request != null) {
            try (Response response = client.newCall(request).execute()) {
                return response.body().string();
            }
        }

        return "error: bad method " + method;
    }
}
