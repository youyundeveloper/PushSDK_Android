package cn.youyunpushdome;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.weimi.push.ClickReceiver;
import com.weimi.push.data.PayLoadMessage;
import com.weimi.push.util.Log;
import com.xiaomi.mipush.sdk.MiPushMessage;

import java.util.Map;

import yun.mi.push.test.R;

/**
 * Created by 卫彪 on 2016/9/23.
 */
public class PushMsgReceiver extends ClickReceiver {

    @Override
    public NotificationCompat.Builder setBuilder(NotificationCompat.Builder builder) {
        return builder.setSmallIcon(R.drawable.notification_sensitive_small_icon).setColor(Color.parseColor("#ff0000"));
    }

    /**
     * 集成游云PUSH的接收消息会回调
     *
     * @param context
     * @param payLoadMessage 消息数据
     */
    @Override
    protected void onYouYunReceiveMessage(Context context, PayLoadMessage payLoadMessage) {
        super.onYouYunReceiveMessage(context, payLoadMessage);
        PushUtil.context = context;
        if (payLoadMessage.sound != null) {
            if (SharedPreferenceUtil.getInstance().getVibration()) {
                SoundVibrateUtil.checkIntervalTimeAndVibrate(context);
            }
            if (SharedPreferenceUtil.getInstance().getSound()) {
                SoundVibrateUtil.checkIntervalTimeAndSound(context);
            }
        }
    }

    /**
     * 点击回调
     *
     * @param context
     * @param message 消息数据
     */
    @Override
    protected void onNotificationClicked(Context context, Object message) {
        super.onNotificationClicked(context, message);
        if (message instanceof PayLoadMessage) {
            PayLoadMessage payLoadMessage = (PayLoadMessage) message;
            Log.d("click:" + payLoadMessage.toString());
            Map map = payLoadMessage.customDictionary;
            toIntent(context, map);
        } else if (message instanceof MiPushMessage) {
            MiPushMessage miPushMessage = (MiPushMessage) message;
            Log.d("mi click:" + miPushMessage.toString());
            Map<String, String> map = miPushMessage.getExtra();
            toIntent(context, map);
        }
    }

    private void toIntent(Context context, Map<String, String> map) {
        Toast.makeText(context, "扩展信息：" + map.toString(), Toast.LENGTH_LONG).show();
    }

}
