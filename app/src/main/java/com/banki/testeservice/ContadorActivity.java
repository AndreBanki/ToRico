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
    private Button startBtn, stopBtn, viewBtn;
    private TextView textView;
    private boolean running;
    private Handler repeatUpdateHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contador);

        startBtn = (Button) findViewById(R.id.startBtn);
        stopBtn = (Button) findViewById(R.id.stopBtn);
        viewBtn = (Button) findViewById(R.id.viewBtn);
        desligarBotoes();

        textView = (TextView) findViewById(R.id.texto);

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContadorActivity.this, ContadorService.class);
                bindService(intent, ContadorActivity.this, Context.BIND_AUTO_CREATE);
                ligarBotoes();
            }
        });

        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unbindService(ContadorActivity.this);
                desligarBotoes();
            }
        });

        viewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = contadorService.getCount();
                Toast.makeText(ContadorActivity.this, "C: " + count, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void ligarBotoes() {
        running = true;
        stopBtn.setEnabled(true);
        viewBtn.setEnabled(true);
        startBtn.setEnabled(false);
    }

    private void desligarBotoes() {
        running = false;
        stopBtn.setEnabled(false);
        viewBtn.setEnabled(false);
        startBtn.setEnabled(true);
    }

    public ContadorService getContadorService() {
        return contadorService;
    }

    public boolean isRunning() {
        return running;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        ContadorService.ContadorBinder binder = (ContadorService.ContadorBinder) service;
        contadorService = binder.getContador();
        repeatUpdateHandler.post(new RepetitiveUpdater(textView, this));

    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        contadorService = null;
    }
}
