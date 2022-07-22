package com.example.licentarotaruteodorgabriel.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.licentarotaruteodorgabriel.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class RedirectActivity extends AppCompatActivity {

    private ProgressBar progBarLoadingActivity;

    private TextView tvCustomToast;
    private ImageView imgViewCustomToast;
    private ConstraintLayout constraintLayoutMainToast;
    private Toast customToast;

    private ActivityResultLauncher<Intent> openMainActivity;
    private ActivityResultLauncher<Intent> openLoginActivity;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redirect);

        initializeComponents();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initializeComponents() {
        progBarLoadingActivity = findViewById(R.id.progBarLoadingActivity);

        View customToastView = getLayoutInflater().inflate(
                R.layout.main_custom_toast,
                findViewById(R.id.constraintLayoutCustomToast)
        );
        constraintLayoutMainToast = customToastView.findViewById(R.id.constraintLayoutCustomToast);
        tvCustomToast = customToastView.findViewById(R.id.tvMainToast);
        imgViewCustomToast = customToastView.findViewById(R.id.imgViewMainToast);
        customToast = new Toast(RedirectActivity.this);
        customToast.setView(customToastView);

        configureComponents();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void configureComponents() {
        progBarLoadingActivity.setVisibility(View.VISIBLE);

        openMainActivity = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                });

        openLoginActivity = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                });

        tvCustomToast.setText(R.string.no_internet_error);
        imgViewCustomToast.setImageResource(R.drawable.ic_baseline_error_24);
        constraintLayoutMainToast.setBackground(getDrawable(R.drawable.round_corners_red));

//        isInternetConnectionAvailable().subscribe((isInternetAvailable) -> {
            if (!isNetworkAvailable()) {
                customToast.show();
            } else {
                FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentFirebaseUser != null && currentFirebaseUser.isEmailVerified()) {

                    FirebaseDatabase.getInstance("https://rotaru-teodor-gabriel-projfb-default-rtdb.europe-west1.firebasedatabase.app/")
                            .getReference("Users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child("rememberLoginCredentials")
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    boolean rememberCredentials = true;
                                    if(snapshot.getValue() != null){
                                        rememberCredentials = (boolean) snapshot.getValue();
                                    }
                                    if (rememberCredentials || getCallingActivity() != null) {
                                        progBarLoadingActivity.setVisibility(View.GONE);
                                        launchMainActivity();
                                    } else {
                                        FirebaseAuth.getInstance().signOut();
                                        progBarLoadingActivity.setVisibility(View.GONE);
                                        launchLoginActivity();
                                    }
                                    finish();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(getApplicationContext(), "Failed to retrieve user data", Toast.LENGTH_LONG).show();
                                }
                            });
                } else {
                    progBarLoadingActivity.setVisibility(View.GONE);
                    launchLoginActivity();
                }
            }
//        });
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    private void launchMainActivity() {
        Intent intent = new Intent(RedirectActivity.this, MainActivity.class);
        openMainActivity.launch(intent);
        finish();
    }


    private void launchLoginActivity() {
        Intent intent = new Intent(RedirectActivity.this, LoginActivity.class);
        openLoginActivity.launch(intent);
        finish();
    }


    public static Single<Boolean> isInternetConnectionAvailable() {
        return Single.fromCallable(() -> {
            try {
                Socket socket = new Socket();
                socket.connect(new InetSocketAddress("8.8.8.8", 53), 2000);
                socket.close();
                return true;
            } catch (IOException e) {
                return false;
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }
}