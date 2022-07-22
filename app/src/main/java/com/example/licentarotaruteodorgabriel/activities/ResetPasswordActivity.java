package com.example.licentarotaruteodorgabriel.activities;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.licentarotaruteodorgabriel.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {

    private TextInputEditText tietEmailFromResetPassword;
    private Button btnSendEmailForPasswordReset;
    private TextView tvForgotPasswordCheckEmail;
    private ProgressBar progBarResetPassword;

    private TextView tvCustomToast;
    private ImageView imgViewCustomToast;
    private ConstraintLayout constraintLayoutMainToast;
    private Toast customToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        initializeComponents();
    }

    private void initializeComponents() {
        tietEmailFromResetPassword = findViewById(R.id.tietEmailFromResetPassword);
        btnSendEmailForPasswordReset = findViewById(R.id.btnSendEmailForPasswordReset);
        tvForgotPasswordCheckEmail = findViewById(R.id.tvForgotPasswordCheckEmail);
        progBarResetPassword = findViewById(R.id.progBarResetPassword);

        View customToastView = getLayoutInflater().inflate(
                R.layout.main_custom_toast,
                findViewById(R.id.constraintLayoutCustomToast)
        );
        constraintLayoutMainToast = customToastView.findViewById(R.id.constraintLayoutCustomToast);
        tvCustomToast = customToastView.findViewById(R.id.tvMainToast);
        imgViewCustomToast = customToastView.findViewById(R.id.imgViewMainToast);
        customToast = new Toast(getApplicationContext());
        customToast.setView(customToastView);

        configureComponents();
    }

    private void configureComponents() {
        btnSendEmailForPasswordReset.setOnClickListener(v -> resetPassword());
    }

    private void resetPassword() {
        if(Patterns.EMAIL_ADDRESS.matcher(tietEmailFromResetPassword.getText().toString().trim()).matches()) {
            progBarResetPassword.setVisibility(View.VISIBLE);
            FirebaseAuth.getInstance()
                    .sendPasswordResetEmail(tietEmailFromResetPassword.getText().toString().trim())
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            progBarResetPassword.setVisibility(View.GONE);
                            tvForgotPasswordCheckEmail.setVisibility(View.VISIBLE);
                        }
                        else {
                            progBarResetPassword.setVisibility(View.GONE);

                            tvCustomToast.setText(R.string.error_sending_password_reset_email); // posibil eroare ca nu exista cont cu mail-ul
                            imgViewCustomToast.setImageResource(R.drawable.ic_baseline_error_24);
                            constraintLayoutMainToast.setBackground(getDrawable(R.drawable.round_corners_red));
                            customToast.show();
                        }
                    });
        } else {
            tietEmailFromResetPassword.setError("Please enter a valid email");
            tietEmailFromResetPassword.requestFocus();
        }
    }

}