package timerTasks.contest.start.decryptionManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import decryptionManager.DecryptionManagerTask;
import jar.dto.ConfigurationDTO;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.Queue;
import java.util.TimerTask;

import static http.Base.BASE_URL;

public class GetBlockingQueueTimerTask extends TimerTask {
    private final Gson deserializer;
    private final Type type;
    private final DecryptionManagerTask dmTask;
    private final Object currentTasksLock;

    public GetBlockingQueueTimerTask(DecryptionManagerTask dmTask, Object currentTasksLock) {
        super();
        deserializer = new Gson();
        type = new TypeToken<Queue<ConfigurationDTO>>(){}.getType();
        this.dmTask = dmTask;
        this.currentTasksLock = currentTasksLock;
    }

    @Override
    public void run() {
        int tasksInThreadPoolSize;
        synchronized (currentTasksLock) {
            tasksInThreadPoolSize = dmTask.getCurrentTasksInThreadPool().size();
        }
        if (tasksInThreadPoolSize == 0) {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();

            // URL
            String RESOURCE = "/agent/get-tasks";
            HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(BASE_URL + RESOURCE)).newBuilder();
            urlBuilder.addQueryParameter("team", dmTask.getAlliesUsername());
            urlBuilder.addQueryParameter("withdraw", String.valueOf(dmTask.getTasksWithdrawalSize()));
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
                        System.out.println("ERROR: Failing to pull blocking queue tasks!");
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        try (ResponseBody body = response.body()) {
                            String tasksAsJson = Objects.requireNonNull(body).string();
                            if (!tasksAsJson.equals("the-end")) {
                                Queue<ConfigurationDTO> currentTasks = deserializer.fromJson(tasksAsJson, type);
                                synchronized (currentTasksLock) {
                                    dmTask.getCurrentTasksInThreadPool().addAll(currentTasks);
                                }

                                dmTask.updateContestScreen(currentTasks.size());
                            } else {
                                dmTask.setStopDM(true);
                            }
                        }
                    }
                });
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }
}
