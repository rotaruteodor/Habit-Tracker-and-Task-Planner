package com.example.licentarotaruteodorgabriel.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.licentarotaruteodorgabriel.R;
import com.example.licentarotaruteodorgabriel.classes.User;
import com.example.licentarotaruteodorgabriel.date_utils.LocalDateConverter;
import com.example.licentarotaruteodorgabriel.enums.Gender;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;


public class CreateAccountActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private TextInputEditText tietRegisterFirstname;
    private TextInputEditText tietRegisterLastname;
    private TextInputEditText tietRegisterEmail;
    private TextInputLayout tilRegisterPassword;
    private TextInputEditText tietRegisterPassword;
    private TextInputLayout tilRegisterConfirmPassword;
    private TextInputEditText tietRegisterConfirmPassword;
    private TextView tvRegisterDateOfBirth;
    private Spinner spnRegisterGender;
    private TextView tvRegisterWakeupHourTag;
    private TextView tvRegisterSleepHourTag;
    private Button btnRegisterCreateAccount;
    private ProgressBar progBarRegister;

    private TextView tvCustomToast;
    private ImageView imgViewCustomToast;
    private ConstraintLayout constraintLayoutMainToast;
    private Toast customToast;

    private ActivityResultLauncher<Intent> openMainActivity;
    private ActivityResultLauncher<Intent> openLoginActivity;
    private User receivedUser;
    private AlertDialog confirmCancelDialog;
    private AlertDialog hourAlertDialog;
    private NumberPicker numberPickerHours;
    private NumberPicker numberPickerMinutes;

    private int wakeupHour;
    private int wakeupMinute;
    private int sleepHour;
    private int sleepMinute;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        initializeComponents();
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initializeComponents() {
        receivedUser = getIntent().getParcelableExtra("USER_FOR_EDITING");
        tietRegisterFirstname = findViewById(R.id.tietRegisterFirstname);
        tietRegisterLastname = findViewById(R.id.tietRegisterLastname);
        tietRegisterEmail = findViewById(R.id.tietRegisterEmail);
        tilRegisterPassword = findViewById(R.id.tilRegisterPassword);
        tietRegisterPassword = findViewById(R.id.tietRegisterPassword);
        tilRegisterConfirmPassword = findViewById(R.id.tilRegisterConfirmPassword);
        tietRegisterConfirmPassword = findViewById(R.id.tietRegisterConfirmPassword);
        tvRegisterDateOfBirth = findViewById(R.id.tvRegisterDateOfBirth);
        spnRegisterGender = findViewById(R.id.spnRegisterGender);
        tvRegisterWakeupHourTag = findViewById(R.id.tvRegisterWakeupHourTag);
        tvRegisterSleepHourTag = findViewById(R.id.tvRegisterSleepHourTag);
        btnRegisterCreateAccount = findViewById(R.id.btnRegisterCreateAccount);
        progBarRegister = findViewById(R.id.progBarRegister);

        View customToastView = getLayoutInflater().inflate(
                R.layout.main_custom_toast,
                findViewById(R.id.constraintLayoutCustomToast)
        );
        constraintLayoutMainToast = customToastView.findViewById(R.id.constraintLayoutCustomToast);
        tvCustomToast = customToastView.findViewById(R.id.tvMainToast);
        imgViewCustomToast = customToastView.findViewById(R.id.imgViewMainToast);
        customToast = new Toast(getApplicationContext());
        customToast.setView(customToastView);

        if (receivedUser == null) {
            wakeupHour = 7;
            wakeupMinute = 0;
            sleepHour = 23;
            sleepMinute = 0;
        } else {
            wakeupHour = receivedUser.getWakeupHour();
            wakeupMinute = receivedUser.getWakeupMinute();
            sleepHour = receivedUser.getSleepHour();
            sleepMinute = receivedUser.getSleepMinute();
        }

        confirmCancelDialog = new AlertDialog.Builder(CreateAccountActivity.this)
                .setTitle("Are you sure you want to cancel?")
                .setMessage("Your inputs will be deleted")
                .setPositiveButton("Yes", (dialogInterface, i) -> {
                    finish();
                })
                .setNegativeButton("No", (dialogInterface, i) -> {
                }).create();

        configureComponents();
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void configureComponents() {
        tvRegisterDateOfBirth.setOnClickListener(v -> showDatePickerDialog());
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(getApplicationContext(),
                R.array.genders,
                android.R.layout.simple_spinner_dropdown_item);
        spnRegisterGender.setAdapter(arrayAdapter);
        spnRegisterGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) (parent.getChildAt(0))).setTextColor(Color.WHITE);
                ((TextView) (parent.getChildAt(0))).setTextSize(20);
                ((TextView) (parent.getChildAt(0))).setTypeface(getResources().getFont(R.font.russo_one));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        btnRegisterCreateAccount.setOnClickListener(v -> addUserToFirebase());
        if (receivedUser != null) {
            tietRegisterFirstname.setText(receivedUser.getFirstname());
            tietRegisterLastname.setText(receivedUser.getLastname());
            tietRegisterEmail.setVisibility(View.GONE);
            tilRegisterPassword.setVisibility(View.GONE);
            tilRegisterConfirmPassword.setVisibility(View.GONE);
            tvRegisterDateOfBirth.setText(LocalDateConverter.getStandardFormatStringFromDate(
                    LocalDate.ofEpochDay(receivedUser.getDateOfBirth())));
            btnRegisterCreateAccount.setText("FINISH");
            spnRegisterGender.setSelection(getSpinnerElementIndex(receivedUser.getGender().toString()));

            tvRegisterWakeupHourTag.setText(getHourTextviewString("I usually wake up at ", wakeupHour, wakeupMinute));
            tvRegisterSleepHourTag.setText(getHourTextviewString("I usually go to sleep at ", sleepHour, sleepMinute));
        }

        openMainActivity = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                });

        openLoginActivity = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                });


        tvRegisterWakeupHourTag.setOnClickListener(view -> {
            showHourPickerDialog(false);
        });

        tvRegisterSleepHourTag.setOnClickListener(view -> {
            showHourPickerDialog(true);
        });
    }

    @Override
    public void onBackPressed() {
        confirmCancelDialog.show();
    }

    @NonNull
    private StringBuilder getHourTextviewString(String s, int wakeupHour, int wakeupMinute) {
        StringBuilder sbWakeup = new StringBuilder().append(s);
        if (wakeupHour < 10) {
            sbWakeup.append("0");
        }
        sbWakeup.append(wakeupHour).append(":");
        if (wakeupMinute < 10) {
            sbWakeup.append("0");
        }
        sbWakeup.append(wakeupMinute);
        return sbWakeup;
    }

    private void showHourPickerDialog(boolean calledForSleepHour) {

        View customHourPickerDialogView = this.getLayoutInflater().inflate(R.layout.hour_picker_view, null);
        numberPickerHours = customHourPickerDialogView.findViewById(R.id.numberPickerHours);
        numberPickerMinutes = customHourPickerDialogView.findViewById(R.id.numberPickerMinutes);
        numberPickerHours.setMinValue(0);
        numberPickerHours.setMaxValue(23);
        numberPickerMinutes.setMinValue(0);
        numberPickerMinutes.setMaxValue(59);
        if (calledForSleepHour) {
            numberPickerHours.setValue(23);
        } else {
            numberPickerHours.setValue(7);
        }
        numberPickerMinutes.setValue(0);
        new AlertDialog.Builder(this).setView(customHourPickerDialogView)
                .setPositiveButton("Ok", (dialog, id) -> {
                    setWakeupAndSleepTimes(calledForSleepHour);
                    setHourTextview(calledForSleepHour);
                })
                .setNegativeButton("Cancel", (dialog, id) -> {
                })
                .create().show();
    }

    private void setHourTextview(boolean calledForSleepHour) {
        if (calledForSleepHour) {
            tvRegisterSleepHourTag.setText(getHourTextviewString("I usually go to sleep at ", sleepHour, sleepMinute));
        } else {
            tvRegisterWakeupHourTag.setText(getHourTextviewString("I usually wake up at ", wakeupHour, wakeupMinute));
        }
    }

    private void setWakeupAndSleepTimes(boolean calledForSleepHour) {
        if (calledForSleepHour) {
            sleepHour = numberPickerHours.getValue();
            sleepMinute = numberPickerMinutes.getValue();
        } else {
            wakeupHour = numberPickerHours.getValue();
            wakeupMinute = numberPickerMinutes.getValue();
        }
    }

    private int getSpinnerElementIndex(String myString) {
        for (int i = 0; i < spnRegisterGender.getCount(); i++) {
            if (spnRegisterGender.getItemAtPosition(i).toString().toUpperCase(Locale.ROOT).equals(myString)) {
                return i;
            }
        }
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showDatePickerDialog() {
        LocalDate dateOfBirth = receivedUser == null ? null : LocalDate.ofEpochDay(receivedUser.getDateOfBirth());
        int year = receivedUser == null ? LocalDate.now().getYear() : dateOfBirth.getYear();
        int month = receivedUser == null ? LocalDate.now().getMonthValue() - 1 : dateOfBirth.getMonthValue() - 1;
        int day = receivedUser == null ? LocalDate.now().getDayOfMonth() : dateOfBirth.getDayOfMonth();

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                AlertDialog.THEME_DEVICE_DEFAULT_DARK,
                this,
                year,
                month,
                day);

        datePickerDialog.getDatePicker().getTouchables().get(0).performClick();
        datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
        datePickerDialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        tvRegisterDateOfBirth.setText(LocalDateConverter.getStandardFormatStringFromDate(LocalDate.of(year, month + 1, dayOfMonth)));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void addUserToFirebase() {
        if (areEditInputsValid() && receivedUser != null) {
            DatabaseReference currentUserDbPath = FirebaseDatabase.getInstance("https://rotaru-teodor-gabriel-projfb-default-rtdb.europe-west1.firebasedatabase.app/")
                    .getReference("Users")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

            HashMap<String, Object> updateUserMap = new HashMap<>();
            updateUserMap.put("firstname", tietRegisterFirstname.getText().toString().trim());
            updateUserMap.put("lastname", tietRegisterLastname.getText().toString().trim());
            updateUserMap.put("dateOfBirth", LocalDateConverter.getStandardFormatDateFromString(tvRegisterDateOfBirth.getText().toString()).toEpochDay());
            updateUserMap.put("gender", Gender.valueOf(spnRegisterGender.getSelectedItem().toString().toUpperCase()));
            updateUserMap.put("wakeupHour", wakeupHour);
            updateUserMap.put("wakeupMinute", wakeupMinute);
            updateUserMap.put("sleepHour", sleepHour);
            updateUserMap.put("sleepMinute", sleepMinute);

            currentUserDbPath.updateChildren(updateUserMap).addOnCompleteListener(task -> {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                openMainActivity.launch(intent);
                finish();
            });
        } else if (areInputsValid() && receivedUser == null) {
            progBarRegister.setVisibility(View.VISIBLE);
            FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(tietRegisterEmail.getText().toString().trim(),
                            tietRegisterPassword.getText().toString())
                    .addOnCompleteListener(task -> {
                        progBarRegister.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            User newUser = new User(
                                    tietRegisterFirstname.getText().toString().trim(),
                                    tietRegisterLastname.getText().toString().trim(),
                                    tietRegisterEmail.getText().toString().trim(),
                                    tietRegisterPassword.getText().toString(),
                                    LocalDateConverter.getStandardFormatDateFromString(tvRegisterDateOfBirth.getText().toString()).toEpochDay(),
                                    Gender.valueOf(spnRegisterGender.getSelectedItem().toString().toUpperCase()),
                                    true,
                                    FirebaseAuth.getInstance().getCurrentUser().getUid(),
                                    wakeupHour, wakeupMinute, sleepHour, sleepMinute);

                            DatabaseReference currentUserDbPath = FirebaseDatabase.getInstance("https://rotaru-teodor-gabriel-projfb-default-rtdb.europe-west1.firebasedatabase.app/")
                                    .getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

                            currentUserDbPath.setValue(newUser)
                                    .addOnCompleteListener(task12 -> {
                                        progBarRegister.setVisibility(View.GONE);
                                        if (task12.isSuccessful()) {
                                            progBarRegister.setVisibility(View.GONE);
                                            FirebaseAuth.getInstance()
                                                    .signInWithEmailAndPassword(tietRegisterEmail.getText().toString().trim(),
                                                            tietRegisterPassword.getText().toString())
                                                    .addOnCompleteListener(task1 -> {
                                                        if (task1.isSuccessful()) {
                                                            FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification();

                                                            tvCustomToast.setText(R.string.check_email_for_verifying);
                                                            imgViewCustomToast.setImageResource(R.drawable.ic_baseline_done_24);
                                                            constraintLayoutMainToast.setBackground(getDrawable(R.drawable.round_corners_green));
                                                            customToast.setDuration(Toast.LENGTH_LONG);
                                                            customToast.show();
                                                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                                            openLoginActivity.launch(intent);
                                                            finish();
                                                        } else {
                                                            tvCustomToast.setText(R.string.error_sending_verification_email);
                                                            imgViewCustomToast.setImageResource(R.drawable.ic_baseline_error_24);
                                                            constraintLayoutMainToast.setBackground(getDrawable(R.drawable.round_corners_red));
                                                            customToast.show();
                                                        }
                                                    });
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Failed to register account", Toast.LENGTH_LONG).show();
                                            progBarRegister.setVisibility(View.GONE);
                                        }
                                    });
                        } else {
                            Toast.makeText(getApplicationContext(), "Failed to register account", Toast.LENGTH_LONG).show();
                            progBarRegister.setVisibility(View.GONE);
                        }
                    });
        }
    }


    public boolean areInputsValid() {
        if (tietRegisterPassword.getText() == null ||
                tietRegisterPassword.getText().toString().length() < 6) {
            tietRegisterPassword.setError("Password must be at least 6 characters long!");
            tietRegisterPassword.requestFocus();
            return false;
        }
        if (tietRegisterConfirmPassword.getText() == null ||
                tietRegisterConfirmPassword.getText().toString().length() < 6) {
            tietRegisterConfirmPassword.setError("Password must be at least 6 characters long!");
            tietRegisterConfirmPassword.requestFocus();
            return false;
        }
        if (!tietRegisterPassword.getText().toString().equals(tietRegisterConfirmPassword.getText().toString())) {
            tietRegisterConfirmPassword.setError("Passwords do not match!");
            tietRegisterConfirmPassword.requestFocus();
            return false;
        }
        if (tietRegisterEmail.getText() == null ||
                !Patterns.EMAIL_ADDRESS.matcher(tietRegisterEmail.getText().toString().trim()).matches()) {
            tietRegisterEmail.setError("A valid email is required");
            tietRegisterEmail.requestFocus();
            return false;
        }
        return areEditInputsValid();
    }


    public boolean areEditInputsValid() {
        if (tietRegisterFirstname.getText() == null ||
                tietRegisterFirstname.getText().toString().trim().length() < 2) {
            tietRegisterFirstname.setError("A valid first name is required!");
            tietRegisterFirstname.requestFocus();
            return false;
        }
        if (tietRegisterLastname.getText() == null ||
                tietRegisterLastname.getText().toString().trim().length() < 2) {
            tietRegisterLastname.setError("A valid last name is required!");
            tietRegisterLastname.requestFocus();
            return false;
        }
        if (tvRegisterDateOfBirth.getText().toString().equals(getResources().getString(R.string.date_of_birth))) {
            tvRegisterDateOfBirth.requestFocus();
            tvRegisterDateOfBirth.setError("A date of birth is required");
            return false;
        }
        return true;
    }
}