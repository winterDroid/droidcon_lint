package de.mprengemann.customlintrules;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import timber.log.Timber;

public class WrongTimberTestActivity extends FragmentActivity {

    private static final String TAG = "WrongTimberTestActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Log.d(TAG, "Test android logging");
        Timber.d("Test timber logging");
    }
}