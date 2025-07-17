package com.running.you_run.user.service;

import okhttp3.OkHttpClient;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.util.Map;

@Service
public class PushNotificationService {

    private final OkHttpClient client = new OkHttpClient();

    public void sendPushNotification(String pushToken, String title, String body) throws IOException {
        String json = new ObjectMapper().writeValueAsString(Map.of(
                "to", pushToken,
                "sound", "default",
                "title", title,
                "body", body
        ));

        RequestBody requestBody = RequestBody.create(
                json,
                MediaType.get("application/json")
        );

        Request request = new Request.Builder()
                .url("https://exp.host/--/api/v2/push/send")
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            System.out.println(response.body().string());
        }
    }
}
