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
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
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
    private Snackbar snackbar = null;
    CalculoHoraExtra calculador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contador);

        inicializaBotoes();
        inicializaHandler();
        atualizaBotoes();

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
                // foi clicado no stop, vai destruir ao sair da activity se não for iniciado de novo
                if (contadorService.isSeraDestruido())
                    startService(serviceIntent);
                // foi clicado no stop e a snackbar está visível ainda, remover
                if (snackbar != null)
                    snackbar.dismiss();

                contadorService.toggleState();
                atualizaBotoes();
            }
        });

        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                criaSnackBarOpcaoDesfazerStop();

                contadorService.reset();
                stopService(serviceIntent);

                atualizaResultadoContagem(0);
                atualizaBotoes();
            }
        });
    }

    private void criaSnackBarOpcaoDesfazerStop() {
        final CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.mainLayout);
        final int countToKeep = contadorService.getCount();
        snackbar = Snackbar
                .make(coordinatorLayout, "Contador zerado!", Snackbar.LENGTH_INDEFINITE)
                .setAction("DESFAZER", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        contadorService.setCount(countToKeep);
                        startService(serviceIntent);
                        atualizaResultadoContagem(countToKeep);
                        atualizaBotoes();

                        Snackbar snackbar1 = Snackbar.make(coordinatorLayout, "Contador restaurado!", Snackbar.LENGTH_SHORT);
                        snackbar1.show();
                    }
                });
        snackbar.show();
    }

    private void inicializaHandler() {
        activityHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Bundle envelope = msg.getData();
                int totalSegundos = envelope.getInt("count");
                atualizaResultadoContagem(totalSegundos);
                atualizaBotoes();
            }
        };
    }

    private void atualizaResultadoContagem(int totalSegundos) {
        TextView horasMinutosTxt = (TextView) findViewById(R.id.horasMinutos);
        horasMinutosTxt.setText(calculador.horasMinutosAsString(totalSegundos));

        TextView segundosTxt = (TextView) findViewById(R.id.segundos);
        segundosTxt.setText(calculador.segundosAsString(totalSegundos));

        TextView valorHorasTxt = (TextView) findViewById(R.id.valorHoras);
        valorHorasTxt.setText(calculador.valorHorasExtrasAsString(totalSegundos));
    }

    private void atualizaBotoes() {
        if (contadorService == null) {
            startPauseBtn.setText("Começar a trabalhar");
            stopBtn.setEnabled(false);
        }
        else if (contadorService.isRunning()) {
            stopBtn.setEnabled(true);
            startPauseBtn.setText("Pausa para um intervalo");
        }
        else if (contadorService.getCount() == 0) {
            stopBtn.setEnabled(false);
            startPauseBtn.setText("Começar a trabalhar");
        }
        else {
            stopBtn.setEnabled(true);
            startPauseBtn.setText("Retomar o trabalho");
        }
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
        atualizaBotoes();
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
