package com.banki.testeservice;

import android.os.Handler;
import android.widget.TextView;

class RepetitiveUpdater implements Runnable{

    private TextView textView;
    private ContadorService contadorService;
    private Handler repeatUpdateHandler = new Handler();
    private final long REPEAT_DELAY = 50;

    public RepetitiveUpdater(TextView textView, ContadorService contadorService) {
        this.contadorService = contadorService;
        this.textView = textView;
    }

    @Override
    public void run() {
        changeValue();
        if (contadorService.isRunning())
            repeatUpdateHandler.postDelayed(new RepetitiveUpdater(textView, contadorService), REPEAT_DELAY);
    }

    private void changeValue() {
        textView.setText(String.valueOf(contadorService.getCount()));
    }
}

