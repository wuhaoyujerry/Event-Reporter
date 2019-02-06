package com.laioffer.eventreporter;

import android.app.Application;
import android.content.res.Configuration;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;

public class CustomApplication extends Application {
  @Override
  public void onCreate() {
    super.onCreate();
    FirebaseMessaging.getInstance().subscribeToTopic("android");
    Log.d("FirebaseMessage", "Subscribe to the topic");
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
  }

  @Override
  public void onLowMemory() {
    super.onLowMemory();
  }
}
