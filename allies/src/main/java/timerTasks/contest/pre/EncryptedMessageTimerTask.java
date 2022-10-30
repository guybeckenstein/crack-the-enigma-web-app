package timerTasks.contest.pre;

import controllersAllies.ContestController;
import javafx.application.Platform;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.TimerTask;

import static http.Base.BASE_URL;

public class EncryptedMessageTimerTask extends TimerTask {
    private final ContestController contestController;

    public EncryptedMessageTimerTask(ContestController contestController) {
        this.contestController = contestController;
    }

    @Override
    public void run() {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();

        // URL
        String RESOURCE = "/uboat/set-message";
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(BASE_URL + RESOURCE)).newBuilder();
        urlBuilder.addQueryParameter("username", contestController.getContestUsername());
        String finalUrl = urlBuilder.build().toString();

        // Request + body + response
        try {
            Request request = new Request.Builder()
                    .url(finalUrl)
                    .method("GET", null)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    System.out.println("ERROR: " + e.getLocalizedMessage());
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    try (ResponseBody body = response.body()) {
                        final String encryptionMessage = java.net.URLDecoder.decode(Objects.requireNonNull(body).string(), StandardCharsets.UTF_8.name());
                        Platform.runLater(() -> contestController.updateEncryptionMessage(encryptionMessage));
                        sendEncryptionMessageToAgents(encryptionMessage);
                    }
                }
            });
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    private void sendEncryptionMessageToAgents(String encryptionMessage) {

        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();

        // URL
        String RESOURCE = "/allies/set-message";
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(BASE_URL + RESOURCE)).newBuilder();
        urlBuilder.addQueryParameter("team", contestController.getUsername());
        urlBuilder.addQueryParameter("message", encryptionMessage);
        String finalUrl = urlBuilder.build().toString();

        // Request + body + response
        try {
            RequestBody body = new FormBody.Builder() // Create request body
                    .build();
            Request request = new Request.Builder()
                    .url(finalUrl)
                    .method("PUT", body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    System.out.println("Unable to send encrypted message from Allies' username " + contestController.getUsername() + " to Agents.");
                    System.out.println(e.getLocalizedMessage());
                }

                @SuppressWarnings({"unused", "EmptyTryBlock"})
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) {
                    try (ResponseBody body = response.body()) {
                        // Successfully added Allies' encryption message
                    }
                }
            });
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
}
