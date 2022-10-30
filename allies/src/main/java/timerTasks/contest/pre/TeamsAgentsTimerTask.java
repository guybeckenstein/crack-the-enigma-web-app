package timerTasks.contest.pre;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import controllersAllies.ContestController;
import jar.clients.agent.Agent;
import jar.common.rawData.agents.AgentsRawData;
import javafx.application.Platform;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.TimerTask;

import static http.Base.BASE_URL;

public class TeamsAgentsTimerTask extends TimerTask {
    private final Gson deserializer;
    private final Type type;
    private final ContestController contestController;

    public TeamsAgentsTimerTask(ContestController contestController) {
        super();
        deserializer = new Gson();
        type = new TypeToken<List<Agent>>(){}.getType();
        this.contestController = contestController;
    }

    @Override
    public void run() {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();

        // URL
        String RESOURCE = "/allies/add-agent";
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
                    System.out.println("Failed to get " + contestController.getUsername() + "'s Agents.");
                    System.out.println(e.getLocalizedMessage());
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    try (ResponseBody body = response.body()) {
                        String namesAsJson = Objects.requireNonNull(body).string();

                        List<Agent> agents = deserializer.fromJson(namesAsJson.trim(), type);
                        List<AgentsRawData> agentsRawData = new ArrayList<>();
                        agents.forEach((agent) -> agentsRawData.add(new AgentsRawData(agent.getUsername(), 0, 0, 0)));

                        Platform.runLater(() -> contestController.updateTeamsAgentsTableView(agentsRawData));
                    }
                }
            });
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
}