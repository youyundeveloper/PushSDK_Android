package cn.youyunpushdome;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.ioyouyun.wchat.WeimiInstance;
import com.ioyouyun.wchat.data.AuthResultData;
import com.ioyouyun.wchat.message.WChatException;

import java.math.BigInteger;
import java.security.SecureRandom;

import yun.mi.push.test.R;


/**
 * Created by 卫彪 on 2016/5/11.
 */
public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    private void login() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    String udid = generateOpenUDID();
                    String clientIdDefault = "1-20091-46309b3229d9a4de30ff954c7a2c6b5b-android";
                    String clientSecretDefault = "5ae8f6fe14d7101ae1af71c749025558";
                    AuthResultData authResultData = WeimiInstance.getInstance().registerApp(getApplicationContext(), udid,
                                    clientIdDefault, clientSecretDefault, 30);

                    if (authResultData.success) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                WeimiInstance.getInstance().frontReceiveMsg();
                                String uid = WeimiInstance.getInstance().getUID();
                                if(TextUtils.isEmpty(uid)){
                                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.nouid), Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                Toast.makeText(LoginActivity.this, getResources().getString(R.string.login_success), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this, PushActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                    }

                } catch (WChatException e) {
                    e.printStackTrace();
                }

            }

        }).start();

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
