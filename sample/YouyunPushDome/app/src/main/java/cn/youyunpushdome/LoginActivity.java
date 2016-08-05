package cn.youyunpushdome;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.ioyouyun.wchat.WeimiInstance;
import com.ioyouyun.wchat.data.AuthResultData;
import com.ioyouyun.wchat.message.WChatException;
import com.weimi.push.WeimiPush;

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
                    String clientIdDefault = "1-20271-4fdc5c9315f5dcfb8645ba5c7c9d3194-andriod";
                    String clientSecretDefault = "ce13b34d3e68be8f337829b8ed20d712";
                    AuthResultData authResultData = WeimiInstance.getInstance().registerApp(getApplicationContext(), udid,
                            clientIdDefault, clientSecretDefault, 60);

                    if (authResultData.success) {
                        /**
                         * 前台接收PUSH消息
                         */
                        WeimiInstance.getInstance().frontReceiveMsg();
                        /**
                         * 注册push用户信息,startTime和endTime为null时默认以为设置的时间,以前没设置过则默认0~24
                         */
                        WeimiInstance.getInstance().shortPushCreate(null, null, null, 60);
                        /**
                         * 启动PUSH服务
                         */
                        WeimiPush.connect(LoginActivity.this, WeimiPush.pushServerIp, true);

                        Intent intent = new Intent(LoginActivity.this, PushActivity.class);
                        startActivity(intent);
                        finish();
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
