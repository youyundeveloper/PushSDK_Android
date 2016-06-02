package cn.youyunpushdome;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.weimi.push.WeimiPush;

/**
 * Created by 卫彪 on 2016/5/13.
 */
public class BootBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        WeimiPush.connect(context, WeimiPush.pushServerIp, false);

    }
}
