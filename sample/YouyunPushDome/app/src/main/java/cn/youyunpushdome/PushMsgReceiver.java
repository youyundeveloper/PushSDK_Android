package cn.youyunpushdome;

import android.content.Context;

import com.weimi.push.ClickReceiver;
import com.weimi.push.data.PayLoadMessage;

/**
 * Created by 卫彪 on 2016/9/23.
 */
public class PushMsgReceiver extends ClickReceiver {

    /**
     * 集成游云PUSH的接收消息会回调
     *
     * @param context
     * @param payLoadMessage 消息数据
     */
    @Override
    protected void onYouYunReceiveMessage(Context context, PayLoadMessage payLoadMessage) {
        super.onYouYunReceiveMessage(context, payLoadMessage);
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
    }
}
