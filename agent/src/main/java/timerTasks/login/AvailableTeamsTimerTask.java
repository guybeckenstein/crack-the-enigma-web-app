package timerTasks.login;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import controllersAgent.LoginController;
import javafx.application.Platform;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

import static http.Base.BASE_URL;

public class AvailableTeamsTimerTask extends TimerTask {
    private final Gson deserializer;
    private final Type type;
    private final LoginController loginController;

    public AvailableTeamsTimerTask(LoginController loginController) {
        super();
        deserializer = new Gson();
        type = new TypeToken<List<String>>(){}.getType();
        this.loginController = loginController;
    }

    @Override
    public void run() {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();

        // URL
        String RESOURCE = "/username";
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(BASE_URL + RESOURCE)).newBuilder();
        urlBuilder.addQueryParameter("type", "Allies");
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
                    System.out.println("Failed to get available teams for login screen.");
                    System.out.println(e.getLocalizedMessage());
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String namesAsJson = Objects.requireNonNull(response.body()).string();

                    List<String> allCurrent = deserializer.fromJson(namesAsJson.trim(), type);
                    removeReadyAlliesFromList(allCurrent);
                }
            });
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    private void removeReadyAlliesFromList(List<String> allCurrent) {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();

        // URL
        String RESOURCE = "/agent/remove-ready-allies";
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
                    System.out.println("Failed to remove unavailable Allies' teams for login screen.");
                    System.out.println(e.getLocalizedMessage());
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String namesAsJson = Objects.requireNonNull(response.body()).string();

                    List<String> readyList = deserializer.fromJson(namesAsJson.trim(), type);
                    allCurrent.removeAll(readyList);
                    Platform.runLater(() -> loginController.updateExistingAlliesTeams(allCurrent));
                }
            });
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
}
