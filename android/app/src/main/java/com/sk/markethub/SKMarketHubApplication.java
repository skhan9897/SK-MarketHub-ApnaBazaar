package com.sk.markethub;

import android.app.Application;
import com.sk.markethub.util.TokenManager;

public class SKMarketHubApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        TokenManager.init(this);
    }
}
