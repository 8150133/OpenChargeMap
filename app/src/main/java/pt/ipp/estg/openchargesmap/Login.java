package pt.ipp.estg.openchargesmap;

import android.app.ProgressDialog;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.Update;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import pt.ipp.estg.openchargesmap.Database.UserDao;
import pt.ipp.estg.openchargesmap.Database.UserDatabase;

public class Login extends AppCompatActivity {

    private Button btSignIn;
    private Button btSignUp;
    private EditText edtEmail;
    private EditText edtPassword;
    private UserDatabase database;

    private UserDao userDao;
    private ProgressDialog progressDialog;


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.d("MYTAG", "inicio ");
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("A verificar utilizador...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setProgress(0);

        edtEmail = findViewById(R.id.emailinput);

        btSignIn = findViewById(R.id.btSignIn);
        btSignUp = findViewById(R.id.btSignUp);
        btSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, Registo.class));
            }
        });
        btSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!emptyValidation()) {
                    progressDialog.show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            User user = userDao.getUser(edtEmail.getText().toString(), edtPassword.getText().toString());
                            if (user != null) {
                                Log.d("testes", "preparado ");
                                Intent i = new Intent(Login.this, MapsActivity.class);
                                i.putExtra("UserEmail", user.getEmail());
                                startActivity(i);
                                //finish();
                            } else {
                                Toast.makeText(Login.this, "Utilizador não registado, ou não existente", Toast.LENGTH_SHORT).show();
                            }
                            progressDialog.dismiss();
                        }
                    }, 1000);

                } else {
                    Toast.makeText(Login.this, "Erro:Campos Vazios", Toast.LENGTH_SHORT).show();
                }
            }
        });

        edtPassword = findViewById(R.id.passwordinput);


        database = Room.databaseBuilder(this, UserDatabase.class, "mi-database.db")
                .allowMainThreadQueries()
                .build();

        userDao = database.getUserDao();

    }

    private boolean emptyValidation() {
        if (TextUtils.isEmpty(edtEmail.getText().toString()) || TextUtils.isEmpty(edtPassword.getText().toString())) {
            return true;
        } else {
            return false;
        }
    }

}
