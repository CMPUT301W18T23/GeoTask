package com.geotask.myapplication.Controllers.SyncServices;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;


public class AuthenticatorService extends Service {

    private StubAuthenticator authenticator;

    public AuthenticatorService() {

    }

    @Override
    public void onCreate() {
        authenticator = new StubAuthenticator(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return authenticator.getIBinder();
    }
}
