package cz.limeth.fildreminder;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import de.schildbach.wallet.integration.android.BitcoinIntegration;

/**
 * Created by limeth on 17.1.16.
 */
public class AboutActivity extends AppCompatActivity {
    private AlertDialog donateDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        createDonateDialog();

        // Make the "GitHub" text clickable
        TextView textMore = (TextView) findViewById(R.id.text_donate_bottom);
        textMore.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public AlertDialog createDonateDialog()
    {
        View donateDialogView = View.inflate(this, R.layout.view_donate, null);
        Button walletButton = (Button) donateDialogView.findViewById(R.id.walletButton);

        if(isWalletInstalled()) {
            walletButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onWalletClick(v);
                }
            });
        } else {
            walletButton.setEnabled(false);
        }

        return donateDialog = new AlertDialog.Builder(this)
                .setTitle("Donate BTC")
                .setView(donateDialogView)
                .create();
    }

    public void onRateClick(View view) {
        startActivity(new Intent(Intent.ACTION_VIEW, Package.getStoreUri()));
    }

    public void onDonateClick(View view) {
        donateDialog.show();
    }

    public void onWalletClick(View view) {
        BitcoinIntegration.request(this, getString(R.string.donation_address));
    }

    // Here follows the BTC API, because the authors of the BitcoinIntegration library decided to make the functions private. Please, don't do that.
    private static final int SATOSHIS_PER_COIN = 100000000;

    private static Intent makeBitcoinUriIntent(final String address, final Long amount)
    {
        final StringBuilder uri = new StringBuilder("bitcoin:");
        if (address != null)
            uri.append(address);
        if (amount != null)
            uri.append("?amount=").append(String.format("%d.%08d", amount / SATOSHIS_PER_COIN, amount % SATOSHIS_PER_COIN));

        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri.toString()));

        return intent;
    }

    private boolean isWalletInstalled()
    {
        final PackageManager pm = getPackageManager();
        final Intent intent = makeBitcoinUriIntent(getString(R.string.donation_address), null);

        return pm.resolveActivity(intent, 0) != null;
    }
}
