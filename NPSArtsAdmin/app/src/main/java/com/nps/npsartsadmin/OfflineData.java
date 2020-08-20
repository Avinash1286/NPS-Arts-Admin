package com.nps.npsartsadmin;

import com.google.firebase.database.FirebaseDatabase;

public class OfflineData extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
