package com.example.fitenesstreacker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.hardware.biometrics.BiometricManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ListCalcImc_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_calc_imc);

        Bundle extra = getIntent().getExtras();

        RecyclerView recyclerView = findViewById(R.id.recycler_view_list);

        if (extra != null) {
            String type = extra.getString("type");
            new Thread(() -> {

                List<Registro> registro = SqlHelper.getInstance(this).getRegisterBy(type);

                runOnUiThread(() -> {
                    Log.d("Teste", registro.toString());
                    ListeCalcAdapter adapter = new ListeCalcAdapter(registro);
                    recyclerView.setLayoutManager(new LinearLayoutManager(this));
                    recyclerView.setAdapter(adapter);
                });

            }).start();


        }
    }

    private class ListeCalcAdapter extends RecyclerView.Adapter<ListeCalcViewHolder> implements OnAdapterItemClickeListener {
        private final List<Registro> datas;

        public ListeCalcAdapter(List<Registro> datas) {
            this.datas = datas;
        }

        @NonNull
        public ListeCalcViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            //  return new ListeCalcViewHolder(getLayoutInflater().inflate(android.R.layout.simple_expandable_list_item_1, parent,false));
            return new ListeCalcViewHolder(getLayoutInflater().inflate(R.layout.item_lista_imc, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ListeCalcViewHolder holder, int position) {
            Registro data = datas.get(position);
            holder.Bid(data, this);
        }

        @Override
        public int getItemCount() {
            return datas.size();
        }

        public void onLongClick(int position, String type, int id) {
            // evento de exclusão (PERGUNTAR ANTES PARA O USUARIO)
            Log.d("TESTE", String.valueOf(id));

            AlertDialog alertDialog = new AlertDialog.Builder(ListCalcImc_Activity.this)
                    .setMessage(getString(R.string.delete_message))
                    .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> new Thread(() -> {

                        SqlHelper sqlHelper = SqlHelper.getInstance(ListCalcImc_Activity.this);
                        long calcId = sqlHelper.removeItem(type, id);

                        runOnUiThread(() -> {
                            if (calcId > 0) {
                                Toast.makeText(ListCalcImc_Activity.this, R.string.calc_removed, Toast.LENGTH_LONG).show();
                                datas.remove(position);
                                notifyDataSetChanged();
                            }
                        });
                    }).start())
                    .create();

            alertDialog.show();
        }

        @Override
        public void onClick(int id, String type) {
            // verificar qual tipo de dado deve ser EDITADO na tela seguinte
            switch (type) {
                case "imc":
                    Intent intent = new Intent(ListCalcImc_Activity.this, Activity_Imc.class);
                    intent.putExtra("updateId", id);
                    startActivity(intent);
                    break;
                case "tmb":
                    Intent i = new Intent(ListCalcImc_Activity.this, Activity_Tmb.class);
                    i.putExtra("updateId", id);
                    startActivity(i);
                    break;
            }
        }
    }

    private class ListeCalcViewHolder extends RecyclerView.ViewHolder {

        public ListeCalcViewHolder(@NonNull View itemView) {
            super(itemView);

        }

        public void Bid(Registro data, final OnAdapterItemClickeListener onItemClickListener) {

            String Agora = "";
            try {
                SimpleDateFormat dataFormatada = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale("pt", "BR"));
                Date datasalva = dataFormatada.parse(data.createDate);
                SimpleDateFormat minhadata = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", new Locale("pt", "BR"));
                Agora = minhadata.format(datasalva);

            } catch (ParseException e) {
            }

            TextView texto = itemView.findViewById(R.id.item_txt_IMC);

            /**
             *((TextView) itemView).setText(getString(R.string.Lista_resposta, data.response, Agora));
             *
             *listener para ouvir evento de click (ABRIR EDIÇAO)
             *
             *itemView.setOnClickListener(view -> onItemClickListener.onClick(data.id, data.type));
             *
             *listener para ouvir evento de long-click (segurar touch - EXCLUIR)
             *onItemClickListener.onLongClick(getAdapterPosition(), data.type, data.id);
             *itemView.setOnLongClickListener(view -> {
             *onItemClickListener.onLongClick(getAdapterPosition(), data.type, data.id);
             *return false;
             *});
             **/

            texto.setText(getString(R.string.Lista_resposta, data.response, Agora));
            // listener para ouvir evento de click (ABRIR EDIÇAO)
            texto.setOnClickListener(view -> onItemClickListener.onClick(data.id, data.type));

           // listener para ouvir evento de long-click (segurar touch - EXCLUIR)
            texto.setOnLongClickListener(view -> {
                Log.d("TESTE", String.valueOf(data.id));
                onItemClickListener.onLongClick(getAdapterPosition(), data.type, data.id);
                return false;
            });

        }
    }
}

