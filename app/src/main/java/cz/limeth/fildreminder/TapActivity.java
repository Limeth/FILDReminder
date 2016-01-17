package cz.limeth.fildreminder;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class TapActivity extends AppCompatActivity implements View.OnTouchListener {
    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private static final int PROGRESS_INTERVAL_UPDATE = 20;
    private final Handler mHideHandler = new Handler();
    private final Handler mProgressHandler = new Handler();
    private View mContentView;
    private ProgressBar mProgressBar;
    private ReminderPreferences reminderPreferences;
    private Long lastTouch; //null if hasn't started
    private AtomicBoolean updateRunning = new AtomicBoolean(false);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        reminderPreferences = new ReminderPreferences().load(this);
        setContentView(R.layout.activity_tap);

        mContentView = findViewById(R.id.fullscreen_content);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);

        mContentView.setOnTouchListener(this);
        mProgressBar.setMax(getProgressBarMax());

        hide();
    }

    private int getProgressBarMax()
    {
        return reminderPreferences.getDelaySeconds() * 1000;
    }

    @Override
    protected void onStop() {
        super.onStop();
        endProgressUpdater();
        reminderPreferences.release();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getActionMasked();

        if(action == MotionEvent.ACTION_UP
                || action == MotionEvent.ACTION_DOWN
                || action == MotionEvent.ACTION_POINTER_UP
                || action == MotionEvent.ACTION_POINTER_DOWN)
        {
            recordTouch();

            return true;
        }

        return false;
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    private void updateProgressBar()
    {
        long now = System.currentTimeMillis();
        int progress = (int) (now - lastTouch);

        if(progress >= getProgressBarMax()) {
            endProgressUpdater();
            reminderPreferences.remind(this);
        }

        mProgressBar.setProgress(progress);
    }

    private void recordTouch()
    {
        boolean start = lastTouch == null;
        lastTouch = System.currentTimeMillis();

        updateProgressBar();

        if(start)
            startProgressUpdater();
    }

    private void startProgressUpdater() {
        updateRunning.set(true);
        progressUpdateTick();
        mContentView.setKeepScreenOn(true);
    }

    private void endProgressUpdater() {
        updateRunning.set(false);
        lastTouch = null;
        mContentView.setKeepScreenOn(false);
    }

    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };

    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
        }
    };

    private void progressUpdateTick()
    {
        // Listen for cancellation
        if(!updateRunning.get())
            return;

        updateProgressBar();

        mProgressHandler.postDelayed(runProgressUpdateTick, PROGRESS_INTERVAL_UPDATE);
    }

    private final Runnable runProgressUpdateTick = new Runnable() {
        @Override
        public void run() {
            progressUpdateTick();
        }
    };
}
