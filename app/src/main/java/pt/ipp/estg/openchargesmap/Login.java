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

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import pt.ipp.estg.openchargesmap.Database.UserDao;
import pt.ipp.estg.openchargesmap.Database.UserDatabase;

public class Login extends AppCompatActivity {

    private Button btSignIn;
    SignInButton googleSIButton;
    private Button btSignUp;
    private EditText edtEmail;
    private EditText edtPassword;
    private UserDatabase database;
    private String TAG = "Login";

    private UserDao userDao;
    private ProgressDialog progressDialog;

    FirebaseAuth mAuth;
    static final int RC_SIGN_IN= 1;
    GoogleApiClient mGoogleApiClient;
    GoogleSignInClient mGoogleSignInClient;
    FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("A verificar utilizador...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setProgress(0);

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
                                Intent i = new Intent(Login.this, UserActivity.class);
                                i.putExtra("User", user);
                                startActivity(i);
                                finish();
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


        edtEmail = findViewById(R.id.emailinput);
        edtPassword = findViewById(R.id.passwordinput);


        database = Room.databaseBuilder(this, UserDatabase.class, "mi-database.db")
                .allowMainThreadQueries()
                .build();

        userDao = database.getUserDao();


        googleSIButton = (SignInButton) findViewById(R.id.googleSIButton);


        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gSIo = new GoogleSignInOptions.Builder()
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gSIo);

        mAuth=FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    startActivity(new Intent(Login.this,NavigationDrawerMap.class));
                }
            }
        };

        googleSIButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

    }

    private boolean emptyValidation() {
        if (TextUtils.isEmpty(edtEmail.getText().toString()) || TextUtils.isEmpty(edtPassword.getText().toString())) {
            return true;
        }else {
            return false;
        }
    }

    private void signIn(){
        Intent signIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if(account != null){
                    firebaseAuthWithGoogle(account);
                }
            } catch(ApiException e){
                Log.w(TAG,"Google Sign In Failed",e);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.d("TAG","firebaseAuthWithGoogle: " + account.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    //Sign In succes, updateUI with the signed-in user's information
                    Log.d(TAG,"Credenciais de Login:Sucesso");
                    FirebaseUser user=mAuth.getCurrentUser();
                    updateUI(user);
                } else{
                    //if sign in fails, display a message to the user
                    Log.w(TAG,"Credenciais de Login:Falha", task.getException());
                    Toast.makeText(Login.this,"Não foi possível fazer login na conta Google ",Toast.LENGTH_LONG).show();
                    //updateUI(user);
                }
            }
        });

    }

    private void updateUI(FirebaseUser user) {
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if (acct != null) {
            String personName = acct.getDisplayName();
            String personGivenName = acct.getGivenName();
            String personFamilyName = acct.getFamilyName();
            String personEmail = acct.getEmail();
            String personId = acct.getId();
            Uri personPhoto = acct.getPhotoUrl();

            Toast.makeText(this,"Nome do utilizador: " +personName + "ID Utilizador: " +personId ,Toast.LENGTH_SHORT).show();
        }

    }
}
