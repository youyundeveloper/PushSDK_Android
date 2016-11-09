package cn.youyunpushdome;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

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
    }

    private void initSDK(){
        String udid = generateOpenUDID();
        String clientIdTest = "1-20142-2e563db99a8ca41df48973b0c43ea50a-andriod";
        String clientSecretTest = "ace518dab1fde58eacb126df6521d34c";

        // frist step 初始化SDK
        YouyunInstance.getInstance().initSDK(this, clientIdTest, clientSecretTest, udid, isOnline, new YouYunHttpCallback(){

            @Override
            public void onResponse(String s) {
                Log.d("Bill", "response:" + s);
                if(!TextUtils.isEmpty(s)){
                    try {
                        JSONObject object = new JSONObject(s);
                        int status = object.getInt("apistatus");
                        String result = object.getString("result");
                        if(1 == status){
                            // second step 启动PUSH
                            boolean startResult = YouyunInstance.getInstance().startPush(LoginActivity.this, isOnline, true);
                            if(startResult){
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
}
