package com.example.licentarotaruteodorgabriel.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.licentarotaruteodorgabriel.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText tietLoginEmail;
    private TextInputEditText tietLoginPassword;
    private Switch switchRememberLoginCredentials;
    private Button btnLogin;
    private TextView tvCreateAccount;
    private TextView tvForgotPassword;
    private ProgressBar progBarLogin;

    private TextView tvCustomToast;
    private ImageView imgViewCustomToast;
    private ConstraintLayout constraintLayoutMainToast;
    private Toast customToast;

    private ActivityResultLauncher<Intent> openCreateAccountActivity;
    private ActivityResultLauncher<Intent> openResetPasswordActivity;
    private ActivityResultLauncher<Intent> openRedirectActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeComponents();
    }


    private void initializeComponents() {
        tietLoginEmail = findViewById(R.id.tietLoginEmail);
        tietLoginPassword = findViewById(R.id.tietLoginPassword);
        switchRememberLoginCredentials = findViewById(R.id.switchRememberLoginCreditentials);
        btnLogin = findViewById(R.id.btnLogin);
        tvCreateAccount = findViewById(R.id.tvCreateAccount);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        progBarLogin = findViewById(R.id.progBarLogin);

        View customToastView = getLayoutInflater().inflate(
                R.layout.main_custom_toast,
                findViewById(R.id.constraintLayoutCustomToast));

        constraintLayoutMainToast = customToastView.findViewById(R.id.constraintLayoutCustomToast);
        tvCustomToast = customToastView.findViewById(R.id.tvMainToast);
        imgViewCustomToast = customToastView.findViewById(R.id.imgViewMainToast);
        customToast = new Toast(getApplicationContext());
        customToast.setView(customToastView);

        configureComponents();
    }

    private void configureComponents() {

        openCreateAccountActivity = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                });

        openResetPasswordActivity = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                });

        openRedirectActivity = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                });

        tvCreateAccount.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, CreateAccountActivity.class);
            openCreateAccountActivity.launch(intent);
        });

        tvForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
            openResetPasswordActivity.launch(intent);
        });

        btnLogin.setOnClickListener(v -> loginUser());
    }


    private void loginUser() {
        if (areInputsValid()) {
            progBarLogin.setVisibility(View.VISIBLE);

            FirebaseAuth.getInstance().signInWithEmailAndPassword(tietLoginEmail.getText().toString().trim(),
                    tietLoginPassword.getText().toString())
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()) {
                                FirebaseDatabase.getInstance("https://rotaru-teodor-gabriel-projfb-default-rtdb.europe-west1.firebasedatabase.app/")
                                        .getReference("Users")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .child("rememberLoginCredentials")
                                        .setValue(switchRememberLoginCredentials.isChecked());
                                Intent intent = new Intent(LoginActivity.this, RedirectActivity.class);
                                openRedirectActivity.launch(intent);
                                finish();
                            } else {
                                progBarLogin.setVisibility(View.GONE);
                                tvCustomToast.setText(R.string.email_not_verified_error);
                                imgViewCustomToast.setImageResource(R.drawable.ic_baseline_error_24);
                                constraintLayoutMainToast.setBackground(getDrawable(R.drawable.round_corners_red));
                                customToast.show();
                            }
                        } else {
                            progBarLogin.setVisibility(View.GONE);
                            tvCustomToast.setText(R.string.wrong_credentials);
                            imgViewCustomToast.setImageResource(R.drawable.ic_baseline_error_24);
                            constraintLayoutMainToast.setBackground(getDrawable(R.drawable.round_corners_red));
                            customToast.show();
                        }
                    });
        }
    }


    private boolean areInputsValid() {
        if (!Patterns.EMAIL_ADDRESS.matcher(tietLoginEmail.getText().toString().trim()).matches()) {
            tietLoginEmail.setError("Please enter a valid e-mail");
            tietLoginEmail.requestFocus();
            return false;
        }

        if (tietLoginPassword.getText().toString().isEmpty()) {
            tietLoginPassword.setError("Field can't be empty");
            tietLoginPassword.requestFocus();
            return false;
        }

        return true;
    }
}