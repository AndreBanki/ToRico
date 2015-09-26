package com.banki.testeservice;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ContadorActivity extends AppCompatActivity implements ServiceConnection {

    private ContadorService contadorService;
    private Handler activityHandler;
    private Button startBtn, stopBtn, resetBtn;
    private Intent serviceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contador);

        startBtn = (Button) findViewById(R.id.startBtn);
        stopBtn = (Button) findViewById(R.id.stopBtn);
        resetBtn = (Button) findViewById(R.id.resetBtn);
        desligarBotoes();

        serviceIntent = new Intent(ContadorActivity.this, ContadorService.class);
        startService(serviceIntent);

        activityHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                TextView textView = (TextView) findViewById(R.id.texto);
                Bundle envelope = msg.getData();
                textView.setText(String.valueOf(envelope.getInt("count")));
                ligarBotoes();
            }
        };

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contadorService.setRunning(true);
                ligarBotoes();
            }
        });

        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contadorService.setRunning(false);
                desligarBotoes();
                stopService(serviceIntent);
            }
        });

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contadorService.reset();
                desligarBotoes();
            }
        });
    }

    private void ligarBotoes() {
        stopBtn.setEnabled(true);
        resetBtn.setEnabled(true);
        startBtn.setEnabled(false);
    }

    private void desligarBotoes() {
        stopBtn.setEnabled(false);
        resetBtn.setEnabled(false);
        startBtn.setEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindService(serviceIntent, ContadorActivity.this, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(ContadorActivity.this);
        desligarBotoes();
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        ContadorService.ContadorBinder binder = (ContadorService.ContadorBinder) service;
        contadorService = binder.getContador();
        contadorService.setActivityHandler(activityHandler);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        contadorService.setActivityHandler(null);
        contadorService = null;
    }
}
