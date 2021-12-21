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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    EditText mEdtEmail, mEdtPassword;
    Button mBtnRegister;
    TextView mTvHaveAccount;

    //progessbar hien thi load register user
    ProgressDialog progressDialog;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //tieu de actionbar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Create Account");
        //back
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        mEdtEmail = findViewById(R.id.edtEmail);
        mEdtPassword = findViewById(R.id.edtPassword);
        mBtnRegister = findViewById(R.id.btnRegister);
        mTvHaveAccount = findViewById(R.id.tvHaveAccount);

        // Initialize(khoi tao) Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Register User...");

        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //input email, password
                String email = mEdtEmail.getText().toString().trim();
                String password = mEdtPassword.getText().toString().trim();
                //validate
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    //set error and forcus cho edt email
                    mEdtEmail.setError("Invalid Email");
                    mEdtEmail.setFocusable(true);
                } else if (password.length() < 6) {
                    //set error and forcus cho edt pass
                    mEdtPassword.setError("Password length at least 6 character");
                    mEdtPassword.setFocusable(true);
                } else {
                    registerUser(email, password);
                }
            }
        });
        mTvHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    private void registerUser(String email, String password) {
        //kiem tra email, pass co gia tri khong va show progess dialog -> chay register
        progressDialog.show();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, bo qua dialog va tien hanh dang ky
                            progressDialog.dismiss();
                            FirebaseUser user = mAuth.getCurrentUser();
                            //nhận email của người dùng và uid từ auth
                            String email = user.getEmail();
                            String uid = user.getUid();
                            //khi người dùng được đăng ký, lưu trữ thông tin người dùng trong cơ sở dữ liệu thời gian thực của firebase
                            //sử dụng hashmap
                            HashMap<Object, String> hashMap = new HashMap<>();
                            //đưa thông tin vào hasmap
                            hashMap.put("email", email);
                            hashMap.put("uid", uid);
                            hashMap.put("name", "");
                            hashMap.put("phone", "");
                            hashMap.put("image", "");
                            hashMap.put("cover", "");

                            //firebase database instance
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            //path to store user data name "users"
                            DatabaseReference reference = database.getReference("Users");
                            //put data within hasmap in database
                            reference.child(uid).setValue(hashMap);

                            Toast.makeText(RegisterActivity.this, "Registered...\n" + user.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterActivity.this, DashbroadActivity.class));
                            finish();
                        } else {
                            //Nếu đăng nhập không thành công, hiển thị thông báo cho người dùng.
                            progressDialog.dismiss();
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //loi, bo qua progress dialog va bao loi
                progressDialog.dismiss();
                Toast.makeText(RegisterActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}