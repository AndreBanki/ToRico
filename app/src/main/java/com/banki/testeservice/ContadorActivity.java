package com.banki.testeservice;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ContadorActivity extends AppCompatActivity implements ServiceConnection {

    private ContadorService contadorService;
    private Button startBtn, stopBtn, resetBtn;
    private TextView textView;
    private boolean running;
    private Handler repeatUpdateHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contador);

        startBtn = (Button) findViewById(R.id.startBtn);
        stopBtn = (Button) findViewById(R.id.stopBtn);
        resetBtn = (Button) findViewById(R.id.resetBtn);
        desligarBotoes();

        textView = (TextView) findViewById(R.id.texto);

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contadorService.setAtivo(true);
                repeatUpdateHandler.post(new RepetitiveUpdater(textView, ContadorActivity.this));
                ligarBotoes();
            }
        });

        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contadorService.setAtivo(false);
                desligarBotoes();
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
        running = true;
        stopBtn.setEnabled(true);
        resetBtn.setEnabled(true);
        startBtn.setEnabled(false);
    }

    private void desligarBotoes() {
        running = false;
        stopBtn.setEnabled(false);
        resetBtn.setEnabled(false);
        startBtn.setEnabled(true);
    }

    public ContadorService getContadorService() {
        return contadorService;
    }

    public boolean isRunning() {
        return running;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(ContadorActivity.this, ContadorService.class);
        bindService(intent, ContadorActivity.this, Context.BIND_AUTO_CREATE);
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
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        contadorService = null;
    }
}
