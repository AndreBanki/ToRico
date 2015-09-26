package com.banki.testeservice;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;

import java.util.logging.Handler;

public class ContadorService extends Service implements Runnable {

    private android.os.Handler handler = new ContadorHandler();
    private final IBinder connection = new ContadorBinder();
    private boolean ativo;
    private int count;

    public ContadorService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ativo = false;
        handler.post(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return connection;
    }

    @Override
    public void run() {
        if (ativo) {
            handler.postAtTime(this, SystemClock.uptimeMillis() + 1000);
            count++;
            return;
        }
        stopSelf();
    }

    @Override
    public void onDestroy() {
        ativo = false;
        super.onDestroy();
    }

    public int getCount() {
        return count;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
        run();
    }

    public void reset() {
        count = 0;
        ativo = false;
    }

    private class ContadorHandler extends android.os.Handler {
    }

    public class ContadorBinder extends Binder {
        public ContadorService getContador() {
            return ContadorService.this;
        }
    }
}
