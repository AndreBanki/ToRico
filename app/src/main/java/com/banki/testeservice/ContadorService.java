package com.banki.testeservice;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

public class ContadorService extends Service implements Runnable {

    private android.os.Handler handler = new ContadorHandler();
    private final IBinder connection = new ContadorBinder();
    private boolean running;
    private int count;
    private Handler activityHandler;

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
            atualizaActivity();
        }
    }

    private void atualizaActivity() {
        if (activityHandler != null) {
            Bundle envelope = new Bundle();
            envelope.putInt("count", count);
            Message msg = new Message();
            msg.setData(envelope);
            activityHandler.sendMessage(msg);
        }
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(this);
        super.onDestroy();
    }

    public int getCount() {
        return count;
    }

    public void setRunning(boolean running) {
        this.running = running;
        if (running)
            run();
        else
            handler.removeCallbacks(this);
    }

    public boolean isRunning() {
        return running;
    }

    public void reset() {
        count = 0;
        running = false;
    }

    public void setActivityHandler(Handler activityHandler) {
        this.activityHandler = activityHandler;
    }

    private class ContadorHandler extends android.os.Handler {
    }

    public class ContadorBinder extends Binder {
        public ContadorService getContador() {
            return ContadorService.this;
        }
    }
}
