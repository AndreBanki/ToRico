package com.banki.testeservice;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.SystemClock;

public class ContadorService extends Service implements Runnable {

    private android.os.Handler handler = new ContadorHandler();
    private final IBinder connection = new ContadorBinder();
    private boolean running;
    private int count;

    public ContadorService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        running = false;
        handler.post(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return connection;
    }

    @Override
    public void run() {
        if (running) {
            handler.postAtTime(this, SystemClock.uptimeMillis() + 1000);
            count++;
            return;
        }
        stopSelf();
    }

    @Override
    public void onDestroy() {
        running = false;
        super.onDestroy();
    }

    public int getCount() {
        return count;
    }

    public void setRunning(boolean running) {
        this.running = running;
        run();
    }

    public boolean isRunning() {
        return running;
    }

    public void reset() {
        count = 0;
        running = false;
    }

    private class ContadorHandler extends android.os.Handler {
    }

    public class ContadorBinder extends Binder {
        public ContadorService getContador() {
            return ContadorService.this;
        }
    }
}
