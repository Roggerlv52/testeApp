package com.example.fitenesstreacker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class Activity_Tmb extends AppCompatActivity {
    EditText edtpeso;
    EditText edtAtura;
    EditText edtIdade;
    Spinner spinne;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tmb);

         edtpeso = findViewById(R.id.Edt_text_peso_TMB);
         edtAtura = findViewById(R.id.Edt_text_altura_TMB);
         edtIdade = findViewById(R.id.Edt_text_Idade);
         spinne = findViewById(R.id.Tmb_Spinner);

        Button BtnEnviar = findViewById(R.id.TMB_Btn_enviar);
        BtnEnviar.setOnClickListener(v -> {
            if (!Validacao()) {
                Toast.makeText(Activity_Tmb.this, R.string.msg_falha, Toast.LENGTH_SHORT).show();
            }
            String sAltura = edtAtura.getText().toString();
            String sPeso = edtpeso.getText().toString();
            String sIdade = edtIdade.getText().toString();

            int Altura = Integer.parseInt(sAltura);
            int Peso = Integer.parseInt(sPeso);
            int idade = Integer.parseInt(sIdade);

            double tmb = Calculo_TMB(Peso, Altura,idade);
            double TMB = tmbResposta(tmb);
            Log.d("Teste","Tmb: "+TMB);

            AlertDialog alertDialog = new AlertDialog.Builder(Activity_Tmb.this)
                    .setMessage(getString(R.string.tmb_response,TMB))
                    .setNegativeButton(android.R.string.ok, (dialog, which) -> dialog.dismiss())
                    .setPositiveButton(R.string.save, (dialogInterface, i) -> {
                       SqlHelper sqlHelper = SqlHelper.getInstance(Activity_Tmb.this);

                        new Thread(() -> {
                            int updateId = 0;

                            // verifica se tem ID vindo da tela anterior quando é UPDATE
                            if (getIntent().getExtras() != null)
                                updateId = getIntent().getExtras().getInt("updateId", 0);

                            long calcId;
                            // verifica se é update ou create
                            if (updateId > 0) {
                                calcId = sqlHelper.updateItem("tmb", TMB, updateId);
                            } else {
                                calcId = sqlHelper.additem("tmb",TMB);
                            }

                            runOnUiThread(() -> {
                                if (calcId > 0) {
                                    Toast.makeText(Activity_Tmb.this, R.string.calc_saved, Toast.LENGTH_LONG).show();
                                    Abrir_Lista_de_Calc();
                                }
                            });
                        }).start();
                    }).create();
            alertDialog.show();



            // Pra esconder o teclado
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(edtAtura.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(edtpeso.getWindowToken(), 0);

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
    private boolean Validacao() {
        return (!edtpeso.getText().toString().startsWith("0")
                && !edtAtura.getText().toString().startsWith("0")
                && !edtIdade.getText().toString().startsWith("0")
                && !edtAtura.getText().toString().isEmpty()
                && !edtpeso.getText().toString().isEmpty()
                && !edtIdade.getText().toString().isEmpty());


    }

    private double Calculo_TMB(int altura ,int  peso, int idade ){
        return 66+(peso*13.8)+(5*altura) - (6.8*idade);
    }
    private double tmbResposta(double TMB){
        int index = spinne.getSelectedItemPosition();
        switch (index){
            case 0: return TMB * 1.2;
            case 1: return TMB * 1.375;
            case 2: return TMB * 1.55;
            case 3: return TMB * 1.725;
            case 4: return TMB * 1.9;
            default: return 0;
        }
    }
    private void Abrir_Lista_de_Calc(){
        Intent intent = new Intent(Activity_Tmb.this,ListCalcImc_Activity.class);
        intent.putExtra("type","tmb");
        startActivity(intent);
    }
}