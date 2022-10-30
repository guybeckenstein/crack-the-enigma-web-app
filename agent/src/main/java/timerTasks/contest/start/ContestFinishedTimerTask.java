package timerTasks.contest.start;

import controllersAgent.ContestController;
import javafx.application.Platform;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;
import java.util.TimerTask;

import static http.Base.BASE_URL;

public class ContestFinishedTimerTask extends TimerTask {
    private final ContestController contestController;

    public ContestFinishedTimerTask(ContestController contestController) {
        this.contestController = contestController;
    }

    @Override
    public void run() {
        // Get winning Allies
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();

        // URL
        String RESOURCE = "/agent/winning-team";
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
                    System.out.println("ERROR: Failing to get task size and Enigma engine from server!");
                    System.out.println(e.getLocalizedMessage());
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    try (ResponseBody body = response.body()) {
                        String winningAllies = Objects.requireNonNull(body).string();
                        if (!winningAllies.trim().isEmpty()) {
                            Platform.runLater(() -> contestController.getDmTask().setStopDM(true));
                        }
                    }
                }
            });
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
}
