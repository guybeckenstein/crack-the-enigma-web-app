package timerTasks.contest.pre;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import controllersAllies.ContestController;
import jar.clients.battlefield.Battlefield;
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
        type = new TypeToken<Battlefield>(){}.getType();
        this.contestController = contestController;
    }

    @Override
    public void run() {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();

        // URL
        String RESOURCE = "/allies/get-battlefield-data";
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
                        String battlefieldAsJson = Objects.requireNonNull(body).string();

                        Battlefield battlefield = deserializer.fromJson(battlefieldAsJson.trim(), type);
                        Platform.runLater(() -> contestController.updateBattlefieldContestDataTableView(battlefield));
                    }
                }
            });
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
}
