package com.banki.testeservice;

import android.os.Handler;
import android.widget.TextView;

class RepetitiveUpdater implements Runnable{

    private TextView textView;
    private ContadorActivity contadorActivity;
    private Handler repeatUpdateHandler = new Handler();
    private final long REPEAT_DELAY = 50;

    public RepetitiveUpdater(TextView textView, ContadorActivity contadorActivity) {
        this.contadorActivity = contadorActivity;
        this.textView = textView;
    }

    @Override
    public void run() {
        changeValue();
        if (contadorActivity.isRunning())
            repeatUpdateHandler.postDelayed(new RepetitiveUpdater(textView, contadorActivity), REPEAT_DELAY);
    }

    private void changeValue() {
        textView.setText(String.valueOf(contadorActivity.getContadorService().getCount()));
    }
}

