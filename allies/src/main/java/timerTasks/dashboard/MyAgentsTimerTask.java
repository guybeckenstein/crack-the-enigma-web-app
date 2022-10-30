package timerTasks.dashboard;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import controllersAllies.DashboardController;
import jar.clients.agent.Agent;
import javafx.application.Platform;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

import static http.Base.BASE_URL;

public class MyAgentsTimerTask extends TimerTask {
    private final Gson deserializer;
    private final Type type;
    private final DashboardController dashboardController;

    public MyAgentsTimerTask(DashboardController dashboardController) {
        super();
        deserializer = new Gson();
        type = new TypeToken<List<Agent>>(){}.getType();
        this.dashboardController = dashboardController;
    }

    @Override
    public void run() {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();

        // URL
        String RESOURCE = "/allies/add-agent";
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(BASE_URL + RESOURCE)).newBuilder();
        urlBuilder.addQueryParameter("team", dashboardController.getUsername());
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
                    System.out.println("Failed to get " + dashboardController.getUsername() + "'s Agents on dashboard screen.");
                    System.out.println(e.getLocalizedMessage());
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    try (ResponseBody body = response.body()) {
                        String namesAsJson = Objects.requireNonNull(body).string();

                        List<Agent> allCurrent = deserializer.fromJson(namesAsJson.trim(), type);
                        Platform.runLater(() -> dashboardController.updateAgentsTableView(allCurrent));
                    }
                }
            });
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
}