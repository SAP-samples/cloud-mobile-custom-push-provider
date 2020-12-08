package com.sap.push.xiaomi;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;


import com.sap.cloud.mobile.foundation.authentication.OAuth2ClientCredentialGrantProcessor;
import com.sap.cloud.mobile.foundation.authentication.OAuth2Configuration;
import com.sap.cloud.mobile.foundation.authentication.OAuth2Interceptor;
import com.sap.cloud.mobile.foundation.authentication.OAuth2TokenInMemoryStore;
import com.sap.cloud.mobile.foundation.common.ClientProvider;
import com.sap.cloud.mobile.foundation.common.SettingsParameters;
import com.sap.cloud.mobile.foundation.common.SettingsProvider;
import com.sap.cloud.mobile.foundation.networking.AppHeadersInterceptor;
import com.sap.cloud.mobile.foundation.networking.WebkitCookieJar;
import com.sap.cloud.mobile.foundation.authentication.OAuth2WebViewProcessor;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.sap.push.xiaomi.TimeIntervalDialog.TimeIntervalInterface;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 运行前请修改几个地方：DemoApplication.java 中的 APP_ID,
 * APP_KEY，AndroidManifest.xml 中的 packagename，和权限 permission.MIPUSH_RECEIVE 的前缀为你的 packagename。
 * 另外，还需要把连接SAP的相关参数做修改： appID，serverUrl, deviceID, CLIENT_ID, 
 */
public class MainActivity extends Activity {

    public static List<String> logList = new CopyOnWriteArrayList<String>();

    private final String appID = "testCustomPushXiaomi929";
    private String serverUrl = "https://mobile-tenant3-integration-ins-" + appID + ".cfapps.sap.hana.ondemand.com";
    private String deviceID = "customdevice";
	private String appVersion = "1.1";
	private String CLIENT_ID = "test_client_id_1";
	private String TOKEN_URL = serverUrl + "/oauth2/api/v1/token";
	private String AUTH_URL = serverUrl + "/oauth2/api/v1/authorize";
	private String REDIRECT_URL = "https://some.url1";
    private final String regUrlToSAP = serverUrl + "/mobileservices/push/v1/runtime/applications/" + appID + "/os/custom/devices/customdevicetest";
	
    private TextView mLogView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DemoApplication.setMainActivity(this);
        mLogView = (TextView) findViewById(R.id.log);
        // 设置别名
        findViewById(R.id.set_alias).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                final EditText editText = new EditText(MainActivity.this);
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(R.string.set_alias)
                        .setView(editText)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String alias = editText.getText().toString();
                                MiPushClient.setAlias(MainActivity.this, alias, null);
                            }

                        })
                        .setNegativeButton(R.string.cancel, null)
                        .show();
            }
        });
        // 撤销别名
        findViewById(R.id.unset_alias).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                final EditText editText = new EditText(MainActivity.this);
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(R.string.unset_alias)
                        .setView(editText)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String alias = editText.getText().toString();
                                MiPushClient.unsetAlias(MainActivity.this, alias, null);
                            }

                        })
                        .setNegativeButton(R.string.cancel, null)
                        .show();

            }
        });
        // 设置帐号
        findViewById(R.id.set_account).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                final EditText editText = new EditText(MainActivity.this);
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(R.string.set_account)
                        .setView(editText)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String account = editText.getText().toString();
                                MiPushClient.setUserAccount(MainActivity.this, account, null);
                            }

                        })
                        .setNegativeButton(R.string.cancel, null)
                        .show();

            }
        });
        // 撤销帐号
        findViewById(R.id.unset_account).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                final EditText editText = new EditText(MainActivity.this);
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(R.string.unset_account)
                        .setView(editText)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String account = editText.getText().toString();
                                MiPushClient.unsetUserAccount(MainActivity.this, account, null);
                            }

                        })
                        .setNegativeButton(R.string.cancel, null)
                        .show();
            }
        });
        // 设置标签
        findViewById(R.id.subscribe_topic).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                final EditText editText = new EditText(MainActivity.this);
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(R.string.subscribe_topic)
                        .setView(editText)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String topic = editText.getText().toString();
                                MiPushClient.subscribe(MainActivity.this, topic, null);
                            }

                        })
                        .setNegativeButton(R.string.cancel, null)
                        .show();
            }
        });
        // 撤销标签
        findViewById(R.id.unsubscribe_topic).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                final EditText editText = new EditText(MainActivity.this);
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(R.string.unsubscribe_topic)
                        .setView(editText)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String topic = editText.getText().toString();
                                MiPushClient.unsubscribe(MainActivity.this, topic, null);
                            }

                        })
                        .setNegativeButton(R.string.cancel, null)
                        .show();
            }
        });
        // 设置接收消息时间
        findViewById(R.id.set_accept_time).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                new TimeIntervalDialog(MainActivity.this, new TimeIntervalInterface() {

                    @Override
                    public void apply(int startHour, int startMin, int endHour,
                                      int endMin) {
                        MiPushClient.setAcceptTime(MainActivity.this, startHour, startMin, endHour, endMin, null);
                    }

                    @Override
                    public void cancel() {
                        //ignore
                    }

                })
                        .show();
            }
        });
        // 暂停推送
        findViewById(R.id.pause_push).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                MiPushClient.pausePush(MainActivity.this, null);
            }
        });

        // 恢复推送
        findViewById(R.id.resume_push).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                MiPushClient.registerPush(MainActivity.this, DemoApplication.getAppId(), DemoApplication.getAppKey()); //register again
                MiPushClient.resumePush(MainActivity.this, null);
            }
        });
        // Register to SAP
        findViewById(R.id.register_sap).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mLogView.setText("RegisterToSAPWithOAuth hhe");
                registerToSAPWithOauth();
            }
        });
        // Register device to pointed App
        findViewById(R.id.register_device).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                mLogView.setText("RegisterToSAPWithOAuth hhe");
                registerToCustomPush();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
      //  refreshLogInfo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DemoApplication.setMainActivity(null);
    }

    public void refreshLogInfo() {
        String AllLog = "";
        for (String log : logList) {
            AllLog = AllLog + log + "\n\n";
        }
        mLogView.setText(AllLog);
    }

    public void registerToSAPWithOauth(){
        MainActivity.logList.add(0," " + "registerToSAP");
        try {
            SettingsParameters settingsParameters = new SettingsParameters(serverUrl, appID, deviceID, appVersion);
            SettingsProvider.set(settingsParameters);
        } catch (Exception e) {
            e.printStackTrace();
        }



        AppHeadersInterceptor appHeadersInterceptor = new AppHeadersInterceptor(SettingsProvider.get());
        OAuth2Configuration auth2Configuration = new OAuth2Configuration.Builder()
                .clientId(CLIENT_ID)
                .responseType("code")
                .tokenUrl(TOKEN_URL)
                .authUrl(AUTH_URL)
				.redirectUrl(REDIRECT_URL)
                .build();
        OAuth2TokenInMemoryStore tokenStore = new OAuth2TokenInMemoryStore();

        OkHttpClient okHttpClient= new OkHttpClient.Builder()
                .addInterceptor(new AppHeadersInterceptor(appID))
                .addInterceptor(new OAuth2Interceptor(new OAuth2WebViewProcessor(auth2Configuration),tokenStore))
                .cookieJar(new WebkitCookieJar())
                .build();
        Request request = new Request.Builder()
                .url(serverUrl + "/" + appID)
                .get()
                .addHeader("Accept", "application/json")
                .build();
        ClientProvider.set(okHttpClient);
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                String log = e.getMessage();
                MainActivity.logList.add(0," " + log);
                Log.e("onboard failed" , e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.isSuccessful()){
                    Log.e("onboard success  " ,"ok");
                }
            }
        });
    }

    public void registerToCustomPush(){
        String log = "registerToCustomPush " + regUrlToSAP;
        MainActivity.logList.add(0," " + log);

        String pushToken = MiPushClient.getRegId(DemoApplication.getAppContext());
        JSONObject jsonObject;
        RequestBody requestBody = null;
        try {
            MediaType mediaType = MediaType.parse("application/json");
            jsonObject = new JSONObject();
            jsonObject.put("deviceModel","android");
            jsonObject.put("pushToken",pushToken);
            jsonObject.put("pushGroup","GoldMembers");
            jsonObject.put("formFactor","phone");
            jsonObject.put("timeZone","GMT+8");
            requestBody = RequestBody.create(jsonObject.toString(),mediaType);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("xiaomi pushtoken is ", pushToken);
        Request req = new Request.Builder()
                .url(regUrlToSAP)
                .post(requestBody).build();
        ClientProvider.get().newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("register device to Custom Push failed",e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.isSuccessful()){
                    Log.e("register device to Custom Push success",response.message());
                }
            }
        });
    }

}
