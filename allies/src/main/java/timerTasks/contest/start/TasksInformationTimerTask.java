package timerTasks.contest.start;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import controllersAllies.ContestController;
import javafx.application.Platform;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;
import java.util.TimerTask;

import static http.Base.BASE_URL;

public class TasksInformationTimerTask extends TimerTask {
    private final Gson deserializer;
    private final Type type;
    private final ContestController contestController;
    public TasksInformationTimerTask(ContestController contestController) {
        super();
        deserializer = new Gson();
        type = new TypeToken<List<Long>>(){}.getType();
        this.contestController = contestController;
    }

    @Override
    public void run() {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();

        // URL
        String RESOURCE = "/allies/tasks-information";
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
                    System.out.println("Failed to update tasks' progress.");
                    System.out.println(e.getLocalizedMessage());
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    try (ResponseBody body = response.body()) {
                        String namesAsJson = Objects.requireNonNull(body).string();

                        List<Long> info = deserializer.fromJson(namesAsJson.trim(), type);
                        long availableTasks = info.get(0);
                        long tasksGenerated = info.get(1);
                        long tasksFinished = info.get(2);

                        Platform.runLater(() -> contestController.updateValues(availableTasks, tasksGenerated, tasksFinished));
                    }
                }
            });
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
}
