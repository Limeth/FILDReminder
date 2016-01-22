package cz.limeth.fildreminder.util;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.FileDescriptor;

import cz.limeth.fildreminder.R;

public class ReminderPreferences {
    private int delaySeconds;
    private int vibratorDurationMillis;
    private double vibratorIntensity;
    private MediaPlayer audioPlayer;

    @SuppressWarnings("ConstantConditions")
    public ReminderPreferences load(Context context)
    {
        Resources resources = context.getResources();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        // How long to wait after touching the screen to remind
        delaySeconds = preferences.getInt(resources.getString(R.string.preference_delay_key), resources.getInteger(R.integer.preference_delay_default));

        // Reminder vibrator
        vibratorDurationMillis = preferences.getInt(resources.getString(R.string.preference_vibrator_duration_key), resources.getInteger(R.integer.preference_vibrator_duration_default));
        vibratorIntensity = (double) preferences.getInt(resources.getString(R.string.preference_vibrator_intensity_key), resources.getInteger(R.integer.constant_vibrator_period))
                / (double) resources.getInteger(R.integer.constant_vibrator_period);

        // Prepare for playing an audio file as a reminder
        String reminderAudioPath = preferences.getString(resources.getString(R.string.preference_audio_file_key), null);
        float volume = (float) preferences.getInt(resources.getString(R.string.preference_audio_volume_key), resources.getInteger(R.integer.preference_audio_volume_default))
                / (float) resources.getInteger(R.integer.preference_audio_volume_max);

        if(reminderAudioPath != null && volume > 0) {
            Uri reminderAudioURI = Uri.parse(reminderAudioPath);

            try {
                ContentResolver contentResolver = context.getContentResolver();
                AssetFileDescriptor assetFileDescriptor = contentResolver.openAssetFileDescriptor(reminderAudioURI, "r");
                //NPE warning suppressed, because we're going to catch it anyway
                FileDescriptor fileDescriptor = assetFileDescriptor.getFileDescriptor();
                audioPlayer = new MediaPlayer();
                audioPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                audioPlayer.setDataSource(fileDescriptor);
                audioPlayer.prepare();
                audioPlayer.setVolume(volume, volume);
            } catch (Exception e) {
                if (audioPlayer != null) {
                    audioPlayer.release();
                    audioPlayer = null;
                }

                Log.e(context.getString(R.string.constant_log_tag), "Could not prepare the reminder audio.", e);
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

            if(vibrator.hasVibrator()) {
                Resources resources = context.getResources();
                long[] pattern = getVibrationPattern(vibratorDurationMillis, resources.getInteger(R.integer.constant_vibrator_period), vibratorIntensity);

                // TODO: Use the repeat argument to avoid creating 1000 longs.
                vibrator.vibrate(pattern, -1);
            }
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

    public MediaPlayer getAudioPlayer() {
        return audioPlayer;
    }

    /**
     * Creates a PWM vibration pattern for {@link Vibrator#vibrate(long[], int)}.
     *
     * @param durationMillis Total duration in milliseconds
     * @param periodMillis The period at which to pulse-width-modulate in milliseconds (a fraction of a second recommended)
     * @param intensity The intensity in percentage (0 = no vibration, 1 = full vibration)
     * @return PWM vibration pattern
     */
    public static long[] getVibrationPattern(int durationMillis, int periodMillis, double intensity)
    {
        if(periodMillis < 2)
            throw new IllegalArgumentException("PeriodMillis must be larger or equal to 2.");

        if(durationMillis <= 0)
            return new long[]{0};

        int periods = (int) Math.ceil((double) durationMillis / periodMillis);
        int durationMillisRounded = periods * periodMillis;

        if(intensity <= 0)
            return new long[]{0};
        else if(intensity >= 1)
            return new long[]{0, durationMillisRounded};

        int durationOn = (int) (periodMillis * intensity);
        int durationOff = periodMillis - durationOn;

        if(durationOn == 0)
            return new long[]{0};
        else if(durationOff == 0)
            return new long[]{0, durationMillisRounded};

        long[] result = new long[periods * 2];

        for(int i = 0; i < periods - 1; i++) {
            int index = i * 2 + 1;
            result[index] = durationOn;
            result[index + 1] = durationOff;
        }

        result[0] = 0;
        result[result.length - 1] = durationOn;

        return result;
    }
}
