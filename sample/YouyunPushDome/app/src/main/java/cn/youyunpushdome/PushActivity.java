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

import com.weimi.push.WeimiPush;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import matrix.sdk.WeimiInstance;
import matrix.sdk.message.HistoryMessage;
import matrix.sdk.util.HttpCallback;

/**
 * Created by 卫彪 on 2016/5/11.
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
        uid = WeimiInstance.getInstance().getUID();
        PushUtil.context = getApplicationContext();
        editStart = (EditText) findViewById(R.id.edit_start);
        editEnd = (EditText) findViewById(R.id.edit_end);
        textGetInfo = (TextView) findViewById(R.id.text_get);
        textSetTime = (TextView) findViewById(R.id.text_set);
        vibrateBtn = (ToggleButton)findViewById(R.id.btn_vibrate);
        vibrateBtn.setChecked(SharedPreferenceUtil.getInstance().getVibration());
        vibrateBtn.setOnCheckedChangeListener(onCheckedChangeListener);
        soundBtn = (ToggleButton)findViewById(R.id.btn_sound);
        soundBtn.setChecked(SharedPreferenceUtil.getInstance().getSound());
        soundBtn.setOnCheckedChangeListener(onCheckedChangeListener);
        setTitle(uid);
    }

    CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(buttonView == vibrateBtn){
                SharedPreferenceUtil.getInstance().setVibration(isChecked);
            } else if(buttonView == soundBtn){
                SharedPreferenceUtil.getInstance().setSound(isChecked);
            }
        }
    };

    public void handleStartPush(View v) {
        startPush();
    }

    public void handleExit(View v){
       System.exit(0);
    }

    public void handleGetPushInfo(View v) {
        textGetInfo.setText("");
        getInfo();
    }

    public void handleSetPushInfo(View v) {
        set();
    }

    private void startPush() {
        boolean startpush = WeimiPush.connect(
                PushActivity.this.getApplicationContext(), WeimiPush.pushServerIp, false);
        if (startpush) {
            Toast.makeText(PushActivity.this, getResources().getString(R.string.push_start_success), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(PushActivity.this, getResources().getString(R.string.push_start_faild), Toast.LENGTH_SHORT).show();
        }
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
        WeimiInstance.getInstance().shortPushCreate(start, end, new HttpCallback() {

            @Override
            public void onResponseHistory(
                    List<HistoryMessage> historyMessage) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onResponse(String result) {
                if (result != null) {
                    JSONObject jsonObject;
                    try {
                        jsonObject = new JSONObject(result);
                        String code = jsonObject.optString("code", null);
                        if (code != null && code.equals("200")) {
                            Log.v(TAG, "result:" + result);
                            Message message = handler.obtainMessage();
                            message.what = 2;
                            message.obj = "set success";
                            handler.sendMessage(message);
                        }
                    } catch (JSONException e) {
                        Message message = handler.obtainMessage();
                        message.what = 2;
                        message.obj = "error";
                        handler.sendMessage(message);
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onError(Exception e) {
                Log.v(TAG, "e:" + e.getMessage());
                Message message = handler.obtainMessage();
                message.what = 2;
                message.obj = "error";
                handler.sendMessage(message);
            }

            @Override
            public void onResponse(byte[] arg0) {
                // TODO Auto-generated method stub

            }
        }, 60);
    }

    private void getInfo() {
        WeimiInstance.getInstance().shortPushShowUser(new HttpCallback() {

            @Override
            public void onResponseHistory(List<HistoryMessage> arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onResponse(String result) {
                if (result != null) {
                    JSONObject jsonObject;
                    try {
                        jsonObject = new JSONObject(result);
                        String code = jsonObject.optString("code", null);
                        if (code != null && code.equals("200")) {
                            String msg = jsonObject.optJSONObject("msg").toString();
                            Log.v(TAG, "msg:" + msg);
                            if (!TextUtils.isEmpty(msg)) {
                                Message message = handler.obtainMessage();
                                message.what = 1;
                                message.obj = msg;
                                handler.sendMessage(message);
                            }
                        }
                    } catch (JSONException e) {
                        Message message = handler.obtainMessage();
                        message.what = 1;
                        message.obj = "error";
                        handler.sendMessage(message);
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onError(Exception e) {
                Message message = handler.obtainMessage();
                message.what = 1;
                message.obj = "error";
                handler.sendMessage(message);
                Log.v(TAG, "e:" + e.getMessage());

            }

            @Override
            public void onResponse(byte[] arg0) {
                // TODO Auto-generated method stub

            }
        }, 120);
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
