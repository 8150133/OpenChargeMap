package pt.ipp.estg.openchargesmap;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

public class PostosActivity extends AppCompatActivity {
    TextView pNome, pMorada, pCidade;
    Button retroceder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postos);

        Intent intent2 = getIntent();
        String posto = intent2.getStringExtra("posto");
        String morada = intent2.getStringExtra("morada");
        String cidade = intent2.getStringExtra("cidade");

        pNome = findViewById(R.id.nome);
        pNome.setText(posto);

        pMorada = findViewById(R.id.morada);
        pMorada.setText(morada);

        pCidade = findViewById(R.id.cidade);
        pCidade.setText(cidade);

    }
}
