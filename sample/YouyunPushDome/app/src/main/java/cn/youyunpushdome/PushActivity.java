package cn.youyunpushdome;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.weimi.push.YouYunHttpCallback;
import com.weimi.push.YouyunInstance;

import yun.mi.push.test.R;


/**
 * Created by 卫彪 on 2016/5/11.
 * modify on 2016/11/8
 */
public class PushActivity extends AppCompatActivity {

    private String TAG = "Bill";
    private EditText editStart;
    private EditText editEnd;
    private TextView textGetInfo;
    private TextView textSetTime;
    private ToggleButton vibrateBtn;
    private ToggleButton soundBtn;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push);
        uid = getIntent().getStringExtra("uid");
        PushUtil.context = getApplicationContext();
        editStart = (EditText) findViewById(R.id.edit_start);
        editEnd = (EditText) findViewById(R.id.edit_end);
        textGetInfo = (TextView) findViewById(R.id.text_get);
        textSetTime = (TextView) findViewById(R.id.text_set);
        vibrateBtn = (ToggleButton) findViewById(R.id.btn_vibrate);
        vibrateBtn.setChecked(SharedPreferenceUtil.getInstance().getVibration());
        vibrateBtn.setOnCheckedChangeListener(onCheckedChangeListener);
        soundBtn = (ToggleButton) findViewById(R.id.btn_sound);
        soundBtn.setChecked(SharedPreferenceUtil.getInstance().getSound());
        soundBtn.setOnCheckedChangeListener(onCheckedChangeListener);
        setTitle(uid);
    }

    CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (buttonView == vibrateBtn) {
                SharedPreferenceUtil.getInstance().setVibration(isChecked);
            } else if (buttonView == soundBtn) {
                SharedPreferenceUtil.getInstance().setSound(isChecked);
            }
        }
    };

    @Override
    protected void onStop() {
        super.onStop();

        // 调用此方法后，PushActivity所持有的短连请求会中断，也可以根据之前短连中设置的tag来中断某一个短连请求
        // 注：此方法最终调用volley中的中断短连的方法
        YouyunInstance.getInstance().cancleRequest(this);
    }

    public void handleExit(View v) {
        System.exit(0);
    }

    /**
     * 取消用户注册信息
     * @param v
     */
    public void handleCancle(View v) {
        YouyunInstance.getInstance().pushCancle(new YouYunHttpCallback() {
            @Override
            public void onResponse(String s) {
                Log.d(TAG, "pushCancle:" + s);
            }

            @Override
            public void onError(Exception e) {

            }
        }, 60, "push_cancle");
    }

    /**
     * 获取用户注册信息
     * @param v
     */
    public void handleGetPushInfo(View v) {
        textGetInfo.setText("");
        getInfo();
    }

    /**
     * 设置勿扰时段
     * @param v
     */
    public void handleSetPushInfo(View v) {
        set();
    }


    private void set() {
        String start = editStart.getText().toString().trim();
        String end = editEnd.getText().toString().trim();
        if (TextUtils.isEmpty(start) || TextUtils.isEmpty(end)) {
            return;
        }
        int startTime = Integer.parseInt(start);
        int endTime = Integer.parseInt(end);
        if (startTime < 0 || startTime > 24 || endTime < 0 || endTime > 24) {
            Toast.makeText(PushActivity.this, getResources().getString(R.string.timeprompt), Toast.LENGTH_SHORT).show();
            return;
        }

        YouyunInstance.getInstance().pushCreate(this, start, end, new YouYunHttpCallback() {
            @Override
            public void onResponse(String s) {
                Log.v(TAG, "result:" + s);
                Message message = handler.obtainMessage();
                message.what = 2;
                message.obj = s;
                handler.sendMessage(message);
            }

            @Override
            public void onError(Exception e) {
                Message message = handler.obtainMessage();
                message.what = 2;
                message.obj = "error";
                handler.sendMessage(message);
                e.printStackTrace();
            }
        }, 60, "create_push");

    }

    private void getInfo() {
        YouyunInstance.getInstance().pushShowUsers(new YouYunHttpCallback() {
            @Override
            public void onResponse(String s) {
                Log.v(TAG, "result:" + s);
                Message message = handler.obtainMessage();
                message.what = 1;
                message.obj = s;
                handler.sendMessage(message);
            }

            @Override
            public void onError(Exception e) {
                Message message = handler.obtainMessage();
                message.what = 1;
                message.obj = "error";
                handler.sendMessage(message);
            }
        }, 60, "get_push");
    }

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    String str = (String) msg.obj;
                    textGetInfo.setText(str);
                    break;
                case 2:
                    String message = (String) msg.obj;
                    textSetTime.setText(message);
                    getInfo();
                    break;
                default:
                    break;
            }
        }

        ;
    };
}
