package com.geotask.myapplication.Controllers.SyncServices;

//https://developer.android.com/training/sync-adapters/creating-sync-adapter.html

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class SyncService extends Service {

    private static SyncAdapter syncAdapter;
    private static final Object syncAdapterLock = new Object();


    @Override
    public void onCreate() {
        synchronized (syncAdapterLock) {
            if (syncAdapter == null) {
                syncAdapter = new SyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return syncAdapter.getSyncAdapterBinder();
    }
}
