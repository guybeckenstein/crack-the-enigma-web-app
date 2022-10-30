package timerTasks.contest.start;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import controllersUBoat.ContestController;
import jar.common.rawData.Candidate;
import javafx.application.Platform;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.Queue;
import java.util.TimerTask;

import static http.Base.BASE_URL;

public class FinalCandidatesTimerTask extends TimerTask {
    private final Gson deserializer;
    private final Type type;
    private final ContestController contestController;

    public FinalCandidatesTimerTask(ContestController contestController) {
        super();
        deserializer = new Gson();
        type = new TypeToken<Queue<Candidate>>(){}.getType();
        this.contestController = contestController;
    }

    @Override
    public void run() {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();

        // URL
        String RESOURCE = "/uboat/get-candidates";
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(BASE_URL + RESOURCE)).newBuilder();
        urlBuilder.addQueryParameter("username", contestController.getUsername());
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
                    System.out.println(e.getLocalizedMessage());
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    try (ResponseBody body = response.body()) {
                        String battlefieldsAsJson = Objects.requireNonNull(body).string();

                        Queue<Candidate> currentCandidates = deserializer.fromJson(battlefieldsAsJson, type);
                        Platform.runLater(() -> contestController.updateFinalCandidatesTableView(currentCandidates));
                    }
                }
            });
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
}
