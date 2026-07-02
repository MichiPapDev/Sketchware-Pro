package mod.pap.github;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ProgressMonitor;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GitHub {

    // variables de github oauth app
    private static final String Tag = "GitHub";
    private static final String ClienteID = "Ov23liqlSFus3ywe3RmL";

    //token y variable de almacenamiento
    private static final String PrefName = "Github_Prefs";
    private static final String KeyToken = "accesToken";

    //EndPoint para github oauth
    private static final String DeviceCodeUrl = "https://github.com/login/device/code";
    private static final String AccessTokenUrl = "https://github.com/login/oauth/access_token";

    //variables de Deviceflow para obtener el token
    private String DeviceCode;
    private int interval = 5;
    private Handler pollingHandler = new Handler();
    private Boolean isPolling = false;
    private DeviceFlowCallBack deviceCallback;



    // variables de conexion http
    private Context context;
    private OkHttpClient client;
    private Handler MainHandler;

    public GitHub(Context context){
        this.context = context.getApplicationContext();
        this.MainHandler = new Handler(Looper.getMainLooper());
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

    }

    //Funcion de autentificacion con oauth de deviceflow
    public void authenticate(DeviceFlowCallBack callBack){
        this.deviceCallback = callBack;
        requestDeviceCode();

    }
    public boolean isAuthenticated() {
        return getToken() != null;

    }
    public String getToken(){
        SharedPreferences prefs = context.getSharedPreferences(PrefName, Context.MODE_PRIVATE);
        return prefs.getString(KeyToken, null);

    }
    public void logout() {
        SharedPreferences prefs = context.getSharedPreferences(PrefName, Context.MODE_PRIVATE);
        prefs.edit().remove(KeyToken).apply();

    }
    public void cancelAuthentication() {
        isPolling = false;
        pollingHandler.removeCallbacksAndMessages(null);

    }

    // obtenemos el device code
    public void requestDeviceCode(){
        try {
            JSONObject body = new JSONObject();
            body.put("client_id", ClienteID);
            body.put("scope", "repo");

            Request request = new Request.Builder()
                    .url(DeviceCodeUrl)
                    .header("Accept", "Application/json")
                    .post(RequestBody.create(body.toString(), MediaType.parse("Application/json")))
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e){


                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        try {
                            JSONObject json = new JSONObject(response.body().string());
                            DeviceCode = json.getString("device_code");
                            String userCode = json.getString("user_code");
                            String verificationUri = json.getString("verification_uri");
                            interval = json.optInt("interval", 5);

                            // iniciar polling


                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }

                    } else {

                    }

                }
            });

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    //comenzamos el polling por cada token
    private void startPolling() {
        if (isPolling) return;
        isPolling = true;
        pollingHandler.postDelayed(() -> {
            if (isPolling) pollForToken();
        }, Math.min(interval, 60) * 1000L);

    }
    private void pollForToken() {
        try {
            JSONObject body = new JSONObject();
            body.put("client_id", ClienteID);
            body.put("device_code", DeviceCode);
            body.put("grant_type", "urn:ietf:params:oauth:grant-type:device_code");

            Request request = new Request.Builder()
                    .url(AccessTokenUrl)
                    .header("accept", "application/json")
                    .post(RequestBody.create(body.toString(), MediaType.parse("application/json")))
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    isPolling = false;


                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful()){
                        try {
                            JSONObject json = new JSONObject(response.body().string());
                            if (json.has("access_token")) {
                                String token = json.getString("access_token");
                                isPolling = false;

                                //guargar token
                                SharedPreferences prefs = context.getSharedPreferences(PrefName, Context.MODE_PRIVATE);
                                prefs.edit().putString(KeyToken, token).apply();

                            } else if (json.has("error")) {
                                String error = json.getString("error");
                                if ("authorization_pending".equals(error)){
                                    startPolling();
                                } else if ("slow_down".equals(error)) {
                                    interval += 5;
                                    startPolling();

                                } else {
                                    isPolling = false;

                                }

                            }

                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        isPolling = false;

                    }

                }
            });

        } catch (Exception e) {

        }

    }


    public void cloneRepo(String repoUrl, String pathDestination, String user, String mail, String token) throws Exception {

        File destDir = new File(pathDestination);

        if (destDir.exists()){

        }

        CloneCommand cloneCmd = Git.cloneRepository()
                .setURI(repoUrl)
                .setDirectory(destDir)
                .setCloneAllBranches(true);

        if (user != null && !user.isEmpty() && token != null && !token.isEmpty()) {
            cloneCmd.setCredentialsProvider(
                    new UsernamePasswordCredentialsProvider(user, token)
            );

        }

        cloneCmd.setProgressMonitor(new ProgressMonitor() {
            @Override
            public void start(int totalTasks) {
                Log.d("clone", "iniciando clonacion");

            }

            @Override
            public void beginTask(String title, int totalWork) {

            }

            @Override
            public void update(int completed) {

            }

            @Override
            public void endTask() {

                Log.d("clone", "termino la clonacion");

            }

            @Override
            public boolean isCancelled() {
                return false;
            }
        });

        Log.d("clone", "Clonando");
        Git git = cloneCmd.call();

        git.close();

        Log.d("clone", "clonacion finalizado");


    }

    private void dirDelete(File dir){
        if (dir.isDirectory()){
            File[] children = dir.listFiles();

            if (children != null){
                for (File child : children){
                    dirDelete(child);
                }
            }
        }
        dir.delete();
    }

    public void DeviceFlow () {

    }

    public interface DeviceFlowCallBack {
        // manejo de eventos
        void onDeviceCodeReceived(String userCode, String verificationUri);
        void onSuccess(String Success);
        void onError(String error);
    }

}
