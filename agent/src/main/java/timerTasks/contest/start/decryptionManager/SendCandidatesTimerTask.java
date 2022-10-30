package timerTasks.contest.start.decryptionManager;

import com.google.gson.Gson;
import decryptionManager.DecryptionManagerTask;
import jar.common.rawData.Candidate;
import jar.common.rawData.battlefieldContest.TeamCandidates;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Objects;
import java.util.TimerTask;

import static http.Base.BASE_URL;

public class SendCandidatesTimerTask extends TimerTask {
    private final Gson serializer;
    private final DecryptionManagerTask dmTask;
    private final LinkedList<Candidate> uBoatCandidatesQueue;
    private final LinkedList<TeamCandidates> alliesCandidatesQueue;
    public SendCandidatesTimerTask(DecryptionManagerTask dmTask,
                                   LinkedList<Candidate> uBoatCandidatesQueue, LinkedList<TeamCandidates> alliesCandidatesQueue) {
        serializer = new Gson();
        this.dmTask = dmTask;
        this.uBoatCandidatesQueue = uBoatCandidatesQueue;
        this.alliesCandidatesQueue = alliesCandidatesQueue;
    }

    @Override
    public void run() {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();

        // URL
        String RESOURCE = "/agent/final-candidates";
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(BASE_URL + RESOURCE)).newBuilder();
        urlBuilder.addQueryParameter("team", dmTask.getAlliesUsername());
        urlBuilder.addQueryParameter("uboatCandidates", serializer.toJson(uBoatCandidatesQueue));
        urlBuilder.addQueryParameter("alliesCandidates", serializer.toJson(alliesCandidatesQueue));
        String finalUrl = urlBuilder.build().toString();

        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create("", mediaType);
        // Request + body + response
        try {
            Request request = new Request.Builder()
                    .url(finalUrl)
                    .method("PUT", body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    System.out.println("Failed to send candidates to server (eventually to UBoat and Allies clients).");
                    System.out.println(e.getLocalizedMessage());
                }

                @SuppressWarnings({"unused", "EmptyTryBlock"})
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) {
                    try (ResponseBody body = response.body()) {
                        // DO NOTHING
                    }
                }
            });
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
}
