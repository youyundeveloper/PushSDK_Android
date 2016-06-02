package cn.youyunpushdome;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Vibrator;

public class SoundVibrateUtil {
	public static long lastMsgVibrateTime = 0;
    public static void checkIntervalTimeAndVibrate(Context context) {
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - lastMsgVibrateTime > 3 * 1000) {
            vibrate(context);
            lastMsgVibrateTime = currentTimeMillis;
        }
    }
    
    public static void vibrate(Context context) {
        Vibrator vibrator = (Vibrator) context.getSystemService(
                Context.VIBRATOR_SERVICE);
        vibrator.vibrate(new long[]{0, 200}, -1);
    }

    public static long lastMsgSoundTime = 0;
    public static void checkIntervalTimeAndSound(Context context) {
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - lastMsgSoundTime > 3 * 1000) {
            playMsgSound(context);
            lastMsgSoundTime = currentTimeMillis;
        }
    }
    
    public static void playMsgSound(Context context) {
        final MediaPlayer mediaPlayer = new MediaPlayer();
        Uri ringToneUri = RingtoneManager
                .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        try {
            mediaPlayer.setDataSource(context, ringToneUri);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
            mediaPlayer.setLooping(false);
            mediaPlayer.prepare();
            mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    mediaPlayer.release();
                }
            });
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
