package com.banki.torico;

import android.content.SharedPreferences;

public class CalculoHoraExtra {

    private int salario_bruto = 5000;
    private int horas_mensais = 220;
    private int percentual_extra = 100;
    private float valorSegundo;

    public void carregaPrefs(SharedPreferences settings) {
        salario_bruto = Integer.parseInt(settings.getString("salario_bruto", String.valueOf(salario_bruto)));
        horas_mensais = Integer.parseInt(settings.getString("horas_mensais", String.valueOf(horas_mensais)));
        percentual_extra = Integer.parseInt(settings.getString("percentual_extra", String.valueOf(percentual_extra)));
        atualizaValorSegundo();
    }

    private void atualizaValorSegundo() {
        int segundosMes = horas_mensais * 60 * 60;
        float fator = 1 + (float)percentual_extra / 100;
        valorSegundo = salario_bruto * fator / segundosMes;
    }

    public float getValorSegundo() {
        return valorSegundo;
    }
}
