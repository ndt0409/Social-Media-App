package com.ndt.mysocial;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    EditText mEdtEmail, mEdtPassword;
    TextView notTvHaveAccount;
    Button mBtnLogin;

    private FirebaseAuth mAuth;

    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //tieu de actionbar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Create Account");
        //back
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        mEdtEmail = findViewById(R.id.edtEmail1);
        mEdtPassword = findViewById(R.id.edtPassword1);
        notTvHaveAccount = findViewById(R.id.tvNotHaveAccount);
        mBtnLogin = findViewById(R.id.btnLogin1);

        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //input data
                String email = mEdtEmail.getText().toString();
                String password = mEdtPassword.getText().toString().trim();
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    //invalid email pattern set error
                    mEdtEmail.setError("Invalid Email");
                    mEdtEmail.setFocusable(true);
                } else {
                    //valid email pattern
                    loginUser(email, password);
                }
            }
        });
        notTvHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
        pd = new ProgressDialog(this);
        pd.setMessage("Logging In...");
    }

    private void loginUser(String email, String password) {
        //show progress dialog
        pd.show();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //dismis progress dialog
                            pd.dismiss();
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            startActivity(new Intent(LoginActivity.this, ProfileActivity.class));
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this, "" + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}