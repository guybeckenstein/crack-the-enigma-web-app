package timerTasks.dashboard;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import controllersAllies.DashboardController;
import jar.clients.battlefield.Battlefield;
import javafx.application.Platform;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Objects;
import java.util.TimerTask;

import static http.Base.BASE_URL;

public class ContestsTimerTask extends TimerTask {
    private final Gson deserializer;
    private final Type type;
    private final DashboardController dashboardController;

    public ContestsTimerTask(DashboardController dashboardController) {
        super();
        deserializer = new Gson();
        type = new TypeToken<Collection<Battlefield>>(){}.getType();
        this.dashboardController = dashboardController;
    }

    @Override
    public void run() {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();

        // URL
        String RESOURCE = "/uboat/upload-file";
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(BASE_URL + RESOURCE)).newBuilder();
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
                    System.out.println("Failed to get available contests.");
                    System.out.println(e.getLocalizedMessage());
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    try (ResponseBody body = response.body()) {
                        String battlefieldsAsJson = Objects.requireNonNull(body).string();

                        Collection<Battlefield> allCurrent = deserializer.fromJson(battlefieldsAsJson.trim(), type);
                        Platform.runLater(() -> dashboardController.updateBattlefieldContestDataTableView(allCurrent));
                    }
                }
            });
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
}
