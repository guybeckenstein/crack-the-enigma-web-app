package timerTasks.contest.start.decryptionManager;

import com.google.gson.Gson;
import decryptionManager.DecryptionManagerTask;
import jar.common.rawData.agents.AgentsRawData;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;
import java.util.TimerTask;

import static http.Base.BASE_URL;

public class SendAgentRawDataTimerTask extends TimerTask {
    private final Gson serializer;
    private final DecryptionManagerTask dmTask;

    public SendAgentRawDataTimerTask(DecryptionManagerTask dmTask) {
        serializer = new Gson();
        this.dmTask = dmTask;
    }

    @Override
    public void run() {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();

        // URL
        String RESOURCE = "/agent/final-candidates";
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(BASE_URL + RESOURCE)).newBuilder();
        urlBuilder.addQueryParameter("team", dmTask.getAlliesUsername());
        AgentsRawData agentsRawData = new AgentsRawData(dmTask.getAgentUsername(), (int) dmTask.getIterations(),
                dmTask.getTasksWithdrawalSize(), dmTask.getContestController().getCandidatesCreatedSize());
        urlBuilder.addQueryParameter("agentRawData", serializer.toJson(agentsRawData));
        String finalUrl = urlBuilder.build().toString();

        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create("", mediaType);
        // Request + body + response
        try {
            Request request = new Request.Builder()
                    .url(finalUrl)
                    .method("POST", body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    System.out.println("Failed to send Agent's information to server (to Allies client).");
                    System.out.println(e.getLocalizedMessage());
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) {
                    response.body();
                }
            });
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
}
