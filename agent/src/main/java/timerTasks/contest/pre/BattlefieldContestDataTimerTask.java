package timerTasks.contest.pre;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import controllersAgent.ContestController;
import jar.common.rawData.agents.AlliesRawData;
import javafx.application.Platform;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.TimerTask;

import static http.Base.BASE_URL;

public class BattlefieldContestDataTimerTask extends TimerTask {
    private final Gson deserializer;
    private final Type type;
    private final ContestController contestController;

    public BattlefieldContestDataTimerTask(ContestController contestController) {
        super();
        deserializer = new Gson();
        type = new TypeToken<AlliesRawData>(){}.getType();
        this.contestController = contestController;
    }

    @Override
    public void run() {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();

        // URL
        String RESOURCE = "/agent/get-my-allies";
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
                    System.out.println("ERROR: " + e.getLocalizedMessage());
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String namesAsJson = Objects.requireNonNull(response.body()).string();

                    AlliesRawData alliesTeamData = deserializer.fromJson(namesAsJson.trim(), type);
                    Platform.runLater(() -> contestController.updateBattlefieldContestDataTableView(alliesTeamData));
                }
            });
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
}
