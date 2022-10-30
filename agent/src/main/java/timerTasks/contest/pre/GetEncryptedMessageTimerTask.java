package timerTasks.contest.pre;

import controllersAgent.ContestController;
import javafx.application.Platform;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;
import java.util.TimerTask;

import static http.Base.BASE_URL;

public class GetEncryptedMessageTimerTask extends TimerTask {
    private final ContestController contestController;
    public GetEncryptedMessageTimerTask(ContestController contestController) {
        this.contestController = contestController;
    }
    @Override
    public void run() {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();

        // URL
        String RESOURCE = "/allies/set-message";
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(BASE_URL + RESOURCE)).newBuilder();
        urlBuilder.addQueryParameter("team", contestController.getAlliesUsername());
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
                    System.out.println("Failed to get contest encrypted message.");
                    System.out.println(e.getLocalizedMessage());
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    try (ResponseBody body = response.body()) {
                        String encryptionMessage = Objects.requireNonNull(body).string();
                        Platform.runLater(() -> contestController.updateEncryptedMessageTextField(encryptionMessage));
                    }
                }
            });
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
}
