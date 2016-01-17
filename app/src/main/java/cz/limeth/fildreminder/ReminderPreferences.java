package cz.limeth.fildreminder;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by limeth on 17.1.16.
 */
public class ReminderPreferences {
    private int delaySeconds;
    private int vibratorDurationMillis;
    private File audioFile;
    private MediaPlayer audioPlayer;

    public ReminderPreferences()
    {

    }

    public ReminderPreferences load(Context context)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        // How long to wait after touching the screen to remind
        delaySeconds = preferences.getInt("pref_key_category_reminder_delay", R.integer.pref_default_category_reminder_delay);

        // Reminder vibrator duration
        vibratorDurationMillis = preferences.getInt("pref_key_category_reminder_vibrator", R.integer.pref_default_category_reminder_vibrator);

        // Prepare for playing an audio file as a reminder
        String reminderAudioPath = preferences.getString("pref_key_category_reminder_audio", null);

        if(reminderAudioPath != null) {
            audioFile = new File(reminderAudioPath);
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(audioFile);
                audioPlayer = new MediaPlayer();
                audioPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                audioPlayer.setDataSource(fis.getFD());
                audioPlayer.prepare();
            } catch (IOException e) {
                if(fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e1) {}

                    if (audioPlayer != null) {
                        audioPlayer.release();
                        audioPlayer = null;
                    }
                }

                Log.e(context.getString(R.string.log_tag), "Could not prepare the reminder audio.", e);
            }
        }

        return this;
    }

    public void remind(Context context)
    {
        vibrate(context);
        playAudio();
    }

    private void vibrate(Context context)
    {
        if(vibratorDurationMillis > 0) {
            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

            vibrator.vibrate(vibratorDurationMillis);
        }
    }

    private void playAudio()
    {
        if(audioPlayer != null)
            audioPlayer.start();
    }

    public void release()
    {
        if(audioPlayer != null)
            audioPlayer.release();
    }

    public int getDelaySeconds() {
        return delaySeconds;
    }

    public int getVibratorDurationMillis() {
        return vibratorDurationMillis;
    }

    public File getAudioFile() {
        return audioFile;
    }

    public MediaPlayer getAudioPlayer() {
        return audioPlayer;
    }
}
