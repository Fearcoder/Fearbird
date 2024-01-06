package nl.fearcoder.fearbird;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.ump.ConsentDebugSettings;
import com.google.android.ump.ConsentForm;
import com.google.android.ump.ConsentInformation;
import com.google.android.ump.ConsentRequestParameters;
import com.google.android.ump.UserMessagingPlatform;

public class MainActivity extends AppCompatActivity {
    private ConsentInformation consentInformation;
    private ConsentForm consentForm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getConsentStatus();
        InitButton();
        AppConstants.initialization(this.getApplicationContext());
    }

    public void getConsentStatus(){
        consentInformation = UserMessagingPlatform.getConsentInformation(this);
        ConsentDebugSettings debugSettings= new ConsentDebugSettings.Builder(this)
                .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
                .build();
        ConsentRequestParameters parameters= new ConsentRequestParameters.Builder()
                .setConsentDebugSettings(debugSettings)
                .setTagForUnderAgeOfConsent(false)
                .build();
        consentInformation.requestConsentInfoUpdate(this, parameters,
                () -> {
                    if(consentInformation.isConsentFormAvailable()){
                        loadform();
                    }
                },
                formError -> {

                }
        );
    }

    public void loadform(){
        UserMessagingPlatform.loadConsentForm(this, consentForm -> {
            MainActivity.this.consentForm= consentForm;
            if(consentInformation.getConsentStatus()==ConsentInformation.ConsentStatus.UNKNOWN){
                consentForm.show(MainActivity.this, formError -> loadform());
            }
            if(consentInformation.getConsentStatus()==ConsentInformation.ConsentStatus.REQUIRED){
                consentForm.show(MainActivity.this, formError -> loadform());
            }
            if (consentInformation.getConsentStatus() == ConsentInformation.ConsentStatus.OBTAINED) {
                loadAd();
            }
        },
                formError -> loadform());
    }

    public void startGame(View view){
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
        finish();
    }

    public void loadAd(){
        AdView mAdView = findViewById(R.id.adViewPlay);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    public void InitButton() {
        final Button button = findViewById(R.id.follow_twitch);
        button.setOnClickListener(v -> {
            try {Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.twitch_link)));
                startActivity(browserIntent);
            }
            catch(Exception ex){
                Toast.makeText(getApplicationContext(), "Install the Twitch app", Toast.LENGTH_LONG).show();
            }
        });
    }
}