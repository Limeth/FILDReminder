package cz.limeth.fildreminder.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import cz.limeth.fildreminder.R;

/**
 * A login screen that offers login via email/password.
 */
public class FrontActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front);

        // Make the "learn more..." text clickable
        TextView textMore = (TextView) findViewById(R.id.text_front_more);
        textMore.setMovementMethod(LinkMovementMethod.getInstance());

        // Pre-set default preference values, because they do not get set automatically. Don't ask me why.
        PreferenceManager.setDefaultValues(getApplicationContext(), R.xml.preferences, true);
    }

    public void onLaunchClick(View view) {
        activate(TapActivity.class);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.front, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id)
        {
            case R.id.action_settings:
                activate(SettingsActivity.class);
                return true;

            case R.id.action_about:
                activate(AboutActivity.class);
                return true;

            default: return false;
        }
    }

    public void activate(Class<? extends Activity> activityClass)
    {
        Intent intent = new Intent(this, activityClass);
        startActivity(intent);
    }
}

