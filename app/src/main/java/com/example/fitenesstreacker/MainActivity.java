package com.example.fitenesstreacker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.RV_ativity_main);

        List<Meu_Item> meu_items = new ArrayList<>();
        meu_items.add(new Meu_Item(1, R.drawable.ic_sunny_24, R.string.imc, Color.CYAN));
        meu_items.add(new Meu_Item(2, R.drawable.ic_baseline_fitness_center_24, R.string.TMB, Color.YELLOW));


        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        TesteAdapter adapter = new TesteAdapter(meu_items);
        adapter.setListener(id -> {
            switch (id) {
                case 1:
                    Intent imc = new Intent(MainActivity.this, Activity_Imc.class);
                    startActivity(imc);
                    break;
                case 2:
                    Intent Tmb = new Intent(MainActivity.this, Activity_Tmb.class);
                    startActivity(Tmb);
                    break;
            }

        });
        recyclerView.setAdapter(adapter);

    }

    private class TesteAdapter extends RecyclerView.Adapter<TesteAdapter.MainViewHolder> {
        private final List<Meu_Item> maiItem;
        private OnItemClickListener listener;

        public TesteAdapter(List<Meu_Item> maiItem) {
            this.maiItem = maiItem;
        }

        public void setListener(OnItemClickListener listener) {
            this.listener = listener;
        }

        @NonNull
        @Override
        public MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MainViewHolder(getLayoutInflater().inflate(R.layout.meu_item, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull MainViewHolder holder, int position) {
            Meu_Item MainItemCurrente = maiItem.get(position);
            holder.Bid(MainItemCurrente);
        }

        @Override
        public int getItemCount() {
            return maiItem.size();
        }

        private class MainViewHolder extends RecyclerView.ViewHolder {
            public MainViewHolder(@NonNull View itemView) {
                super(itemView);

            }

            public void Bid(Meu_Item p) {
                TextView textView = itemView.findViewById(R.id.item_txt_name);
                ImageView imageIcon = itemView.findViewById(R.id.item_imagem);
                LinearLayout botao = itemView.findViewById(R.id.Button_imc);

                botao.setOnClickListener(v -> listener.onClick(p.getId()));
                textView.setText(p.getTexStringId());
                imageIcon.setImageResource(p.getDrowableId());
                botao.setBackgroundColor(p.getColor());
            }
        }
    }

}