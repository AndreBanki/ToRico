package com.banki.torico;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ContadorActivity extends AppCompatActivity implements ServiceConnection {

    private ContadorService contadorService;
    private Handler activityHandler;
    private Button startPauseBtn, stopBtn;
    private Intent serviceIntent;
    CalculoHoraExtra calculador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contador);

        inicializaBotoes();
        inicializaHandler();
        desligarBotoes();

        calculador = new CalculoHoraExtra();

        serviceIntent = new Intent(ContadorActivity.this, ContadorService.class);
        startService(serviceIntent);
    }

    private void inicializaBotoes() {
        startPauseBtn = (Button) findViewById(R.id.startBtn);
        stopBtn = (Button) findViewById(R.id.stopBtn);

        startPauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (contadorService.isRunning()) {
                    contadorService.pausar();
                    desligarBotoes();
                } else {
                    contadorService.iniciar();
                    ligarBotoes();
                }
            }
        });

        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contadorService.reset();
                atualizaResultadoContagem(0);
                desligarBotoes();
                stopService(serviceIntent);
            }
        });
    }

    private void inicializaHandler() {
        activityHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Bundle envelope = msg.getData();
                int segundos = envelope.getInt("count");
                atualizaResultadoContagem(segundos);
                ligarBotoes();
            }
        };
    }

    private void atualizaResultadoContagem(int totalSegundos) {
        int minutos = totalSegundos / 60;
        int horas = minutos / 60;
        int segundos = totalSegundos - minutos * 60;

        TextView horasMinutosTxt = (TextView) findViewById(R.id.horasMinutos);
        horasMinutosTxt.setText(String.format("%02d:%02d",horas,minutos));

        TextView segundosTxt = (TextView) findViewById(R.id.segundos);
        segundosTxt.setText(String.format(":%02d",segundos));

        TextView valorHorasTxt = (TextView) findViewById(R.id.valorHoras);
        float valorHoras = calculador.getValorSegundo() * totalSegundos;
        valorHorasTxt.setText(String.format("R$ %.2f",valorHoras));
    }

    private void ligarBotoes() {
        stopBtn.setEnabled(true);
        startPauseBtn.setText("Pausar");
    }

    private void desligarBotoes() {
        stopBtn.setEnabled(false);
        if (contadorService != null && contadorService.getCount() > 0)
            startPauseBtn.setText("Continuar");
        else
            startPauseBtn.setText("Iniciar");
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        calculador.carregaPrefs(settings);
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
        atualizaResultadoContagem(contadorService.getCount());
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        contadorService.setActivityHandler(null);
        contadorService = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_contador, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent config = new Intent(ContadorActivity.this, SettingsActivity.class);
            startActivity(config);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
