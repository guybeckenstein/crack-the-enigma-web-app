package timerTasks.contest.pre;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import controllersUBoat.ContestController;
import jar.common.rawData.battlefieldContest.AlliesData;
import javafx.application.Platform;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Objects;
import java.util.TimerTask;

import static http.Base.BASE_URL;

public class ActiveTeamsTimerTask extends TimerTask {
    private final Gson deserializer;
    private final Type type;
    private final ContestController contestController;

    public ActiveTeamsTimerTask(ContestController contestController) {
        super();
        deserializer = new Gson();
        type = new TypeToken<Collection<AlliesData>>(){}.getType();
        this.contestController = contestController;
    }

    @Override
    public void run() {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();

        // URL
        String RESOURCE = "/uboat/get-allies";
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
                    System.out.println("Failed to get " + contestController.getUsername() + "'s active teams in Battlefield.");
                    System.out.println(e.getLocalizedMessage());
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    try (ResponseBody body = response.body()) {
                        String battlefieldsAsJson = Objects.requireNonNull(body).string();

                        Collection<AlliesData> allCurrent = deserializer.fromJson(battlefieldsAsJson, type);
                        Platform.runLater(() -> contestController.updateActiveTeamsTableView(allCurrent));
                        updateBattlefieldManagerAgentsAmount();
                    }
                }
            });
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("all")
    private void updateBattlefieldManagerAgentsAmount() {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();

        // URL
        String RESOURCE = "/allies/update-battlefield-manager";
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
                    System.out.println("ERROR: " + e.getLocalizedMessage());
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) {
                    try (ResponseBody body = response.body()) {
                        // Nothing...
                        // Updated agents' TableColumn values
                    }
                }
            });
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
}
