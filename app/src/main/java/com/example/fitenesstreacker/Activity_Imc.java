package com.example.fitenesstreacker;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Activity_Imc extends AppCompatActivity {
    private EditText editpeso;
    private EditText editaltura;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imc);

        editpeso = findViewById(R.id.Edt_text_peso);
        editaltura = findViewById(R.id.Edt_text_altura);
        Button buttonEnviar = findViewById(R.id.Btn_enviar);

        buttonEnviar.setOnClickListener(v -> {
            // Tratamento de erros.
            if (!Invalidate()) {
                Toast.makeText(Activity_Imc.this, R.string.msg_falha, Toast.LENGTH_SHORT).show();
                return;
            }
            //Campo de sucesso
            String sAltura = editaltura.getText().toString();
            String sPeso = editpeso.getText().toString();
            int Altura = Integer.parseInt(sAltura);
            int Peso = Integer.parseInt(sPeso);

            double resultado = Calculo_IMC(Peso, Altura);
            //Teste codigo no console
            Log.d("Teste", "Resultado: " + resultado);
            int ImcrespostaId = imcResposta(resultado);

            AlertDialog alertDialog = new AlertDialog.Builder(Activity_Imc.this)
                    .setTitle(getString(R.string.IMC_resposta, resultado))
                    .setMessage(ImcrespostaId)
                    .setNegativeButton(android.R.string.ok, (dialogInterface, which) -> dialogInterface.dismiss())
                    .setPositiveButton(R.string.save, (dialogInterface, which) -> {
                       SqlHelper sqlHelper = SqlHelper.getInstance(Activity_Imc.this);
                        new Thread(() -> {
                            int updateId = 0;

                            if (getIntent().getExtras() != null)
                                updateId = getIntent().getExtras().getInt("updateId", 0);

                            long calcId;
                            if (updateId > 0) {
                                calcId = SqlHelper.getInstance(Activity_Imc.this).updateItem("imc", resultado, updateId);
                            } else {
                                calcId = SqlHelper.getInstance(Activity_Imc.this).additem("imc", resultado);
                            }

                            runOnUiThread(() -> {
                                if (calcId > 0) {
                                    Toast.makeText(Activity_Imc.this, R.string.registro, Toast.LENGTH_SHORT).show();
                                    Abrir_Lista_de_Calc();
                                }
                            });
                        }).start();

                    })
                    .create();

            alertDialog.show();
            // Pra esconder o teclado
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editaltura.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(editpeso.getWindowToken(), 0);

        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_lista:
                Abrir_Lista_de_Calc();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void Abrir_Lista_de_Calc() {
        Intent intent = new Intent(Activity_Imc.this, ListCalcImc_Activity.class);
        // para passar dados para a tela ListCalcImc_Activity
        intent.putExtra("type", "imc");
        startActivity(intent);
    }

    @StringRes
    private int imcResposta(double imc) {

        if (imc < 15)
            return R.string.imc_severely_low_weight;
        else if (imc < 16)
            return R.string.imc_very_low_weight;
        else if (imc < 18.5)
            return R.string.imc_low_weight;
        else if (imc < 25)
            return R.string.normal;
        else if (imc < 30)
            return R.string.imc_high_weight;
        else if (imc < 35)
            return R.string.imc_so_high_weight;
        else if (imc < 40)
            return R.string.imc_severely_high_weight;
        else
            return R.string.imc_extreme_weight;
    }

    private double Calculo_IMC(int P, int A) {
        return P / (((double) A / 100) * ((double) A / 100));
    }

    //Validação
    private boolean Invalidate() {
        return (!editaltura.getText().toString().startsWith("0") &&
                !editaltura.getText().toString().startsWith("0")
                && !editaltura.getText().toString().isEmpty()
                && !editpeso.getText().toString().isEmpty());

    }
}