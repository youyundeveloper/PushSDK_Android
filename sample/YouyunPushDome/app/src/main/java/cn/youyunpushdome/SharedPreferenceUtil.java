package cn.youyunpushdome;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by 卫彪 on 2016/5/11.
 */
public class SharedPreferenceUtil {

    public static final String NAME = "youyun_push_preference";
    public static final String KEY_VIBRATION = "key_vibration";
    public static final String KEY_SOUND = "key_sound";
    private static SharedPreferenceUtil sharedPreferenceUtil;
    private SharedPreferences sharedPreferences;

    public static synchronized SharedPreferenceUtil getInstance(){
        if(sharedPreferenceUtil == null){
            sharedPreferenceUtil = new SharedPreferenceUtil();
        }
        return sharedPreferenceUtil;
    }

    private SharedPreferenceUtil(){
        sharedPreferences = PushUtil.context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
    }

    public void setVibration(boolean vibration){
        sharedPreferences.edit().putBoolean(KEY_VIBRATION, vibration).commit();
    }

    public boolean getVibration(){
        return sharedPreferences.getBoolean(KEY_VIBRATION, false);
    }

    public void setSound(boolean sound){
        sharedPreferences.edit().putBoolean(KEY_SOUND, sound).commit();
    }

    public boolean getSound(){
        return sharedPreferences.getBoolean(KEY_SOUND, false);
    }

}
