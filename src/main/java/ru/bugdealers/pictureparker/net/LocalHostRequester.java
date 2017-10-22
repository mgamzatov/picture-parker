package ru.bugdealers.pictureparker.net;

import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class LocalHostRequester {
    private OkHttpClient okHttpClient;
    private Logger logger = LoggerFactory.getLogger(LocalHostRequester.class);
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");


    @Autowired
    public LocalHostRequester(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
    }

    public long getPictureIdByImage(String pathToFile) throws IOException {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("path", pathToFile);

        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://localhost:5000/").newBuilder();
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            urlBuilder.addQueryParameter(entry.getKey(), entry.getValue());
        }

        Request.Builder builder = new Request.Builder()
                .url(urlBuilder.build().toString());

        Request request = builder.build();

        Response response;
        long result = -1;
        try {
            response = okHttpClient.newCall(request).execute();
            logger.info(response.body().string());
            result = Long.parseLong(response.body().string());
        } catch (IOException e) {
            logger.error("Error: {}", e);
        }
        return result;
    }
}
