package cn.youyunpushdome;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;

import com.weimi.push.YouYunHttpCallback;
import com.weimi.push.YouyunInstance;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.security.SecureRandom;

import yun.mi.push.test.R;

/**
 * Created by 卫彪 on 2016/5/11.
 * modify on 2016/11/8
 */
public class LoginActivity extends AppCompatActivity {

    /**
     * 集成游云PUSH需要四步即可,调用请参考下面initSDK()方法
     * 1、调用 YouyunInstance.getInstance().initSDK() 初始化SDK 成功返回 {"apistatus":1,"result":"xxxxx"}，result为用户id
     * 2、第1步成功回调中调用YouyunInstance.getInstance().startPush()启动服务
     * 3、调用YouyunInstance.getInstance().registerEquipment()注册设备信息
     * 4、在AndroidManifest中的广播中配置appid
     *   <receiver
     *      android:name="com.weimi.push.WeimiPushReceiver"
     *      android:exported="false">
     *          <intent-filter>
     *              <action android:name="me.weimi.push.action.20142" />
     *              <action android:name="me.weimi.push.action.20525" />
     *          </intent-filter>
     *      </receiver>
     *
     *
     *  通过以上四步就可以收到push推送了，在任何android手机都可以接收push了
     *  上面功能需要两个push推送基础包youyun-push-android-1.1.1.jar和youyun-protobuf-java-2.4.1.jar和http网络包volley.jar
     *
     *  如果想在小米手机上使用小米push则需要加入MiPush_SDK_Client_3_0_3.jar
     *  如果想在华为手机上使用华为push则需要加入HwPush_SDK_V2705.jar
     *  否则所有手机都使用游云推送功能
     *
     */




    private boolean isOnline = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initSDK();
            }
        });

        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.rg_platform);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_online) {
                    isOnline = true;
                } else if (checkedId == R.id.rb_test) {
                    isOnline = false;
                }
            }
        });

    }

    private void initSDK() {
        String udid = generateOpenUDID();
        String clientId;
        String clientSecret;

        if (isOnline) {
            clientId = "1-20525-4ab3a7c3ddb665945d0074f51e979ef0-andriod";
            clientSecret = "6f3efde9fb49a76ff6bfb257f74f4d5b";
        } else {
            clientId = "1-20142-2e563db99a8ca41df48973b0c43ea50a-andriod";
            clientSecret = "ace518dab1fde58eacb126df6521d34c";
        }
        // frist step 初始化SDK
        YouyunInstance.getInstance().initSDK(LoginActivity.this.getApplicationContext(), clientId, clientSecret, udid, isOnline, new YouYunHttpCallback() {

            @Override
            public void onResponse(String s) {
                Log.d("Bill", "response:" + s);
                if (!TextUtils.isEmpty(s)) {
                    try {
                        JSONObject object = new JSONObject(s);
                        int status = object.getInt("apistatus");
                        String result = object.getString("result");
                        if (1 == status) {
                            // second step 启动PUSH
                            boolean startResult = YouyunInstance.getInstance().startPush(LoginActivity.this, isOnline, true);
                            if (startResult) {
                                // third step 注册用户信息,首次创建用户，必须调用次方法
                                YouyunInstance.getInstance().registerEquipment(LoginActivity.this, null, 120, "register");

                                Intent intent = new Intent(LoginActivity.this, PushActivity.class);
                                intent.putExtra("uid", result);
                                startActivity(intent);
                                finish();
                            }
                        } else {
                            Log.d("Bill", "result:" + result);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onError(Exception e) {
                Log.d("Bill", "error:" + e.getMessage());
            }
        }, 60);

    }

    /**
     * 根据设备生成一个唯一标识
     *
     * @return
     */
    private String generateOpenUDID() {
        // Try to get the ANDROID_ID
        String OpenUDID = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        if (OpenUDID == null || OpenUDID.equals("9774d56d682e549c")
                | OpenUDID.length() < 15) {
            // if ANDROID_ID is null, or it's equals to the GalaxyTab generic
            // ANDROID_ID or bad, generates a new one
            final SecureRandom random = new SecureRandom();
            OpenUDID = new BigInteger(64, random).toString(16);
        }
        return OpenUDID;
    }

    /**
     * 获取手机IMEI号
     *
     * 在华为开放平台推送华为需要用imei推送
     */
    public static String getIMEI(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
        String imei = telephonyManager.getDeviceId();
        return imei;
    }

}
