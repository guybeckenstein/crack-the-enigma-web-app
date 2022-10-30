package timerTasks.contest.pre;

import controllersAllies.ContestController;
import javafx.application.Platform;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;
import java.util.TimerTask;

import static http.Base.BASE_URL;

public class StartContestTimerTask extends TimerTask {
    private final ContestController contestController;

    public StartContestTimerTask(ContestController contestController) {
        this.contestController = contestController;
    }

    @Override
    public void run() {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();

        // URL
        String RESOURCE = "/uboat/contest";
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
                        if (Objects.requireNonNull(body).string().equals("true")) {
                            Platform.runLater(contestController::startContestMode);
                            notifyAgentsContestHasStarted();
                        }
                    }
                }
            });
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    private void notifyAgentsContestHasStarted() {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();

        // URL
        String RESOURCE = "/allies/is-contest-started";
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(BASE_URL + RESOURCE)).newBuilder();
        urlBuilder.addQueryParameter("team", contestController.getUsername());
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
                    System.out.println("Unable to know if contest has started for Allies' username " + contestController.getUsername());
                    System.out.println(e.getLocalizedMessage());
                }

                @SuppressWarnings({"unused", "EmptyTryBlock"})
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) {
                    try (ResponseBody body = response.body()) {
                        // Added Allies' username to server's ready set
                    }
                }
            });
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
}
