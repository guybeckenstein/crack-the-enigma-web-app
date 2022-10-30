package timerTasks.contest.start;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import controllersAllies.ContestController;
import jar.common.rawData.agents.AgentsRawData;
import jar.common.rawData.battlefieldContest.TeamCandidates;
import javafx.application.Platform;
import javafx.util.Pair;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.TimerTask;

import static http.Base.BASE_URL;

public class PullInformationTimerTask extends TimerTask {
    private final Gson deserializer;
    private final Type type;
    private final ContestController contestController;
    public PullInformationTimerTask(ContestController contestController) {
        deserializer = new Gson();
        type = new TypeToken<Pair<LinkedList<TeamCandidates>, List<AgentsRawData>>>(){}.getType();
        this.contestController = contestController;
    }
    @Override
    public void run() {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();

        // URL
        String RESOURCE = "/allies/final-candidates";
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(BASE_URL + RESOURCE)).newBuilder();
        urlBuilder.addQueryParameter("team", contestController.getUsername());
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
                    System.out.println("Failed to retrieve TableViews information for " + contestController.getUsername() + " from his agents.");
                    System.out.println(e.getLocalizedMessage());
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    try (ResponseBody body = response.body()) {
                        Pair<LinkedList<TeamCandidates>, List<AgentsRawData>> result = deserializer.fromJson(Objects.requireNonNull(body).string(), type);
                        Platform.runLater(() -> contestController.updateContestTableViews(result));
                    }
                }
            });
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
}
