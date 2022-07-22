package com.example.licentarotaruteodorgabriel.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.TransitionManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.licentarotaruteodorgabriel.R;
import com.example.licentarotaruteodorgabriel.classes.Habit;
import com.example.licentarotaruteodorgabriel.date_utils.LocalDateConverter;
import com.example.licentarotaruteodorgabriel.enums.HabitEvaluationType;
import com.example.licentarotaruteodorgabriel.enums.HabitFrequency;
import com.example.licentarotaruteodorgabriel.enums.HabitNumericalComparisonType;
import com.example.licentarotaruteodorgabriel.interfaces.CategoriesRecyclerViewClickListener;
import com.example.licentarotaruteodorgabriel.list_adaptors.CategoriesRecyclerViewAdapter;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import petrov.kristiyan.colorpicker.ColorPicker;

public class AddHabitActivity extends AppCompatActivity implements CategoriesRecyclerViewClickListener {

    private AppCompatButton btnYesNo;
    private AppCompatButton btnNumerical;
    private TextInputLayout tilHabitName;
    private TextInputEditText tietHabitName;
    private Spinner spnHabitComparisonType;
    private TextInputLayout tilHabitNumericalGoal;
    private TextInputEditText tietHabitNumericalGoal;
    private TextInputLayout tilHabitNumericalUnit;
    private TextInputEditText tietHabitNumericalUnit;
    private TextView tvFinalNumericalHabit;
    private RadioGroup rgHabitFrequency;
    private ConstraintLayout constraintLayoutDaysOfWeek;
    private FlexboxLayout flexboxLayoutDaysOfMonth;
    private ConstraintLayout constraintLayoutRepeat;
    private EditText editTextNumberOfDaysToRepeatAt;
    private MaterialCardView cardViewAddHabitCategories;
    private TextView tvSelectCategory;
    private RecyclerView recyclerViewSelectCategory;
    private TextInputLayout tilHabitDescription;
    private TextInputEditText tietHabitDescription;
    private TextView tvAddHabitStartDate;
    private TextView tvAddHabitDifficulty;
    private TextView tvAddHabitColor;
    private TextView tvAddHabitFinish;
    private TextView tvAddHabitCancel;

    private RecyclerView.Adapter recyclerViewCategoriesAdapter;
    private RecyclerView.LayoutManager recyclerViewCategoriesLayoutManager;
    private DividerItemDecoration dividerItemDecoration;

    private String habitName;
    private String habitDescription;
    private LocalDate habitStartDate;
    private HabitEvaluationType evaluationType;
    private Double numericalGoal;
    private HabitNumericalComparisonType numericalComparisonType;
    private String numericalUnit;
    private HabitFrequency frequency;
    private ArrayList<Integer> daysOfWeek = new ArrayList<>();
    private ArrayList<Integer> daysOfMonth = new ArrayList<>();
    private Integer numberOfDaysForRepeat;
    private String habitCategory;
    private int habitColor;
    private int daysToFormHabit;
    private Habit finalHabitToBeAdded;
    private Drawable defaultColorTvDrawable;

    private ArrayList<String> categories = new ArrayList<>();
    private DatePickerDialog.OnDateSetListener onHabitStartDateSet;
    private DatePickerDialog.OnDateSetListener onHabitEndDateSet;
    private AlertDialog confirmCancelDialog;
    private AlertDialog selectHabitDifficultyAlertDialog;

    private ActivityResultLauncher<Intent> openMainActivity;
    private Habit receivedHabit;
    private int[] daysToFormHabitArray;
    private String[] habitDifficulties;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_habit);

        initializeComponents();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initializeComponents() {
        receivedHabit = getIntent().getParcelableExtra("HABIT_TO_BE_EDITED");
        categories = getIntent().getStringArrayListExtra("CATEGORIES_FROM_HABITS_FRAGMENT");

        btnYesNo = findViewById(R.id.btnYesNo);
        btnNumerical = findViewById(R.id.btnNumerical);
        tilHabitName = findViewById(R.id.tilHabitName);
        tietHabitName = findViewById(R.id.tietHabitName);
        spnHabitComparisonType = findViewById(R.id.spnHabitComparisonType);
        tilHabitNumericalGoal = findViewById(R.id.tilHabitNumericalGoal);
        tietHabitNumericalGoal = findViewById(R.id.tietHabitNumericalGoal);
        tilHabitNumericalUnit = findViewById(R.id.tilHabitNumericalUnit);
        tietHabitNumericalUnit = findViewById(R.id.tietHabitNumericalUnit);
        tvFinalNumericalHabit = findViewById(R.id.tvFinalNumericalHabit);
        rgHabitFrequency = findViewById(R.id.rgHabitFrequency);
        constraintLayoutDaysOfWeek = findViewById(R.id.constraintLayoutDaysOfWeek);
        flexboxLayoutDaysOfMonth = findViewById(R.id.flexboxLayoutDaysOfMonth);
        constraintLayoutRepeat = findViewById(R.id.constraintLayoutRepeat);
        editTextNumberOfDaysToRepeatAt = findViewById(R.id.editTextNumberOfDaysToRepeatAt);
        cardViewAddHabitCategories = findViewById(R.id.cardViewAddHabitCategories);
        tvSelectCategory = findViewById(R.id.tvSelectCategory);
        recyclerViewSelectCategory = findViewById(R.id.recyclerViewSelectHabitCategory);
        tilHabitDescription = findViewById(R.id.tilHabitDescription);
        tietHabitDescription = findViewById(R.id.tietHabitDescription);
        tvAddHabitStartDate = findViewById(R.id.tvAddHabitStartDate);
        tvAddHabitDifficulty = findViewById(R.id.tvAddHabitDifficulty);
        tvAddHabitColor = findViewById(R.id.tvAddHabitColor);
        defaultColorTvDrawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.round_corners_30);
        tvAddHabitFinish = findViewById(R.id.tvAddHabitFinish);
        tvAddHabitCancel = findViewById(R.id.tvAddHabitCancel);

        habitStartDate = LocalDate.now();

        recyclerViewCategoriesLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewCategoriesAdapter = new CategoriesRecyclerViewAdapter(categories, getApplicationContext(), this);
        dividerItemDecoration = new DividerItemDecoration(recyclerViewSelectCategory.getContext(), DividerItemDecoration.VERTICAL);

        onHabitStartDateSet = (datePicker, day, month, year) -> {
            habitStartDate = LocalDate.of(day, month + 1, year);
            tvAddHabitStartDate.setText("Start date: ".concat(LocalDateConverter.getStandardFormatStringFromDate(habitStartDate)));
        };

        confirmCancelDialog = new AlertDialog.Builder(AddHabitActivity.this)
                .setTitle("Are you sure you want to cancel?")
                .setMessage("Your inputs will be deleted")
                .setPositiveButton("Yes", (dialogInterface, i) -> {
                    finish();
                })
                .setNegativeButton("No", (dialogInterface, i) -> {
                }).create();

        daysToFormHabitArray = getResources().getIntArray(R.array.habitDaysToFormHabit);
        habitDifficulties = getResources().getStringArray(R.array.habitDifficulties);
        selectHabitDifficultyAlertDialog = new AlertDialog.Builder(AddHabitActivity.this,
                AlertDialog.THEME_DEVICE_DEFAULT_DARK)
                .setTitle("How difficult would you say this habit is?")
//                .setMessage("This will affect the way habit strength is calculated")
                .setSingleChoiceItems(R.array.habitDifficulties, -1,
                        (dialogInterface, i) -> {
                            tvAddHabitDifficulty.setText("Difficulty: ".concat(habitDifficulties[i]));
                            daysToFormHabit = daysToFormHabitArray[i];
                            selectHabitDifficultyAlertDialog.dismiss();
                        }).create();

        configureComponents();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void configureComponents() {
        ArrayAdapter<CharSequence> arrayAdapterForSpinner = ArrayAdapter.createFromResource(
                getApplicationContext(),
                R.array.habitNumericalComparisonTypes,
                android.R.layout.simple_spinner_dropdown_item);
        spnHabitComparisonType.setAdapter(arrayAdapterForSpinner);

        if (receivedHabit == null) {
            evaluationType = HabitEvaluationType.YESNO;
            btnYesNo.setBackgroundResource(R.drawable.round_corners_green_white_border);
            habitColor = getResources().getColor(R.color.main_transparent_white);
            daysToFormHabit = 66;
            tvAddHabitDifficulty.setText("Difficulty: Average");
        } else {
            habitName = receivedHabit.getName();
            habitDescription = receivedHabit.getDescription();
            habitStartDate = LocalDate.ofEpochDay(receivedHabit.getStartDate());
            evaluationType = receivedHabit.getEvaluationType();
            numericalGoal = receivedHabit.getNumericalGoal();
            numericalComparisonType = receivedHabit.getNumericalComparisonType();
            numericalUnit = receivedHabit.getNumericalUnit();
            frequency = receivedHabit.getFrequency();
            daysOfWeek = receivedHabit.getDaysOfWeek();
            daysOfMonth = receivedHabit.getDaysOfMonth();
            numberOfDaysForRepeat = receivedHabit.getNumberOfDaysForRepeat();
            habitCategory = receivedHabit.getCategory();
            habitColor = receivedHabit.getColor();
            Drawable customDrawable = defaultColorTvDrawable.getConstantState()
                    .newDrawable()
                    .mutate();
            customDrawable.setTint(habitColor);
            tvAddHabitColor.setBackground(customDrawable);
            daysToFormHabit = receivedHabit.getDaysToFormHabit();
            int indexOfNrDaysToFormHabit = Arrays.binarySearch(daysToFormHabitArray, daysToFormHabit);
            tvAddHabitDifficulty.setText("Difficulty: ".concat(habitDifficulties[indexOfNrDaysToFormHabit]));

            if (evaluationType == HabitEvaluationType.YESNO) {
                configureUIForYesNoEvaluationType();
            } else {
                configureUIForNumericalEvaluationType();
                if (numericalComparisonType == HabitNumericalComparisonType.EXACTLY) {
                    spnHabitComparisonType.setSelection(0);
                } else if (numericalComparisonType == HabitNumericalComparisonType.ATLEAST) {
                    spnHabitComparisonType.setSelection(1);
                } else if (numericalComparisonType == HabitNumericalComparisonType.LESSTHAN) {
                    spnHabitComparisonType.setSelection(2);
                }

                tietHabitNumericalGoal.setText(numericalGoal.toString());
                tietHabitNumericalUnit.setText(numericalUnit);
                tvFinalNumericalHabit.setText(new StringBuilder().append("Final habit: ")
                        .append(habitName)
                        .append(" ")
                        .append(spnHabitComparisonType.getSelectedItem())
                        .append(" ")
                        .append((numericalGoal != null) ? (((numericalGoal % 1) == 0) ? String.format("%.0f", numericalGoal) : numericalGoal) : "")
                        .append(" ")
                        .append(numericalUnit != null ? numericalUnit : ""));
            }
            tietHabitName.setText(habitName);
            if (frequency == HabitFrequency.DAILY) {
                rgHabitFrequency.check(R.id.rbFrequencyDaily);
                configureComponentsForDailyFrequencySelected();
            } else if (frequency == HabitFrequency.SPECIFICDAYSOFWEEK) {
                rgHabitFrequency.check(R.id.rbFrequencyDaysOfWeek);
                configureComponentsForDaysOfWeekFrequencySelected();

                for (int i = 1; i < constraintLayoutDaysOfWeek.getChildCount() + 1; i++) {
                    if (daysOfWeek.contains(i)) {
                        TextView textView = (TextView) constraintLayoutDaysOfWeek.getChildAt(i - 1);
                        textView.setBackgroundResource(R.drawable.round_corners_green_white_border);
                    }
                }
            } else if (frequency == HabitFrequency.SPECIFICDAYSOFMONTH) {
                rgHabitFrequency.check(R.id.rbFrequencyDaysOfMonth);
                configureComponentsForDaysOfMonthFrequencySelected();

                for (int i = 1; i < flexboxLayoutDaysOfMonth.getChildCount() + 1; i++) {
                    if (daysOfMonth.contains(i)) {
                        TextView textView = (TextView) flexboxLayoutDaysOfMonth.getChildAt(i - 1);
                        textView.setBackgroundResource(R.drawable.round_corners_green_white_thin_border);
                    }
                }
            } else if (frequency == HabitFrequency.REPEAT) {
                rgHabitFrequency.check(R.id.rbFrequencyRepeat);
                configureComponentsForRepeatFrequencySelected();
                editTextNumberOfDaysToRepeatAt.setText(numberOfDaysForRepeat.toString());
            }

            if (habitCategory != null) {
                tvSelectCategory.setText("Category: ".concat(habitCategory));
            }
            if (habitDescription != null) {
                tietHabitDescription.setText(habitDescription);
            }
            tvAddHabitStartDate.setText("Start date: ".concat(LocalDateConverter.getStandardFormatStringFromDate(habitStartDate)));
            if (numericalComparisonType == HabitNumericalComparisonType.EXACTLY) {
                spnHabitComparisonType.setSelection(0);
            } else if (numericalComparisonType == HabitNumericalComparisonType.ATLEAST) {
                spnHabitComparisonType.setSelection(1);
            } else {
                spnHabitComparisonType.setSelection(2);
            }
        }

        btnYesNo.setOnClickListener(view -> configureUIForYesNoEvaluationType());
        btnNumerical.setOnClickListener(view -> configureUIForNumericalEvaluationType());

        spnHabitComparisonType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (((TextView) (parent.getChildAt(0))) != null) {
                    ((TextView) (parent.getChildAt(0))).setTextColor(Color.WHITE);
                    ((TextView) (parent.getChildAt(0))).setTextSize(15);
                    ((TextView) (parent.getChildAt(0))).setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    ((TextView) (parent.getChildAt(0))).setTypeface(getResources().getFont(R.font.russo_one));

                    tvFinalNumericalHabit.setText(
                            new StringBuilder().append("Final habit: ")
                                    .append(habitName != null ? habitName : "")
                                    .append(" ")
                                    .append(spnHabitComparisonType.getSelectedItem())
                                    .append(" ")
                                    .append((numericalGoal != null) ? (((numericalGoal % 1) == 0) ? String.format("%.0f", numericalGoal) : numericalGoal) : "")
                                    .append(" ")
                                    .append(numericalUnit != null ? numericalUnit : ""));

                    numericalComparisonType = HabitNumericalComparisonType.valueOf(
                            spnHabitComparisonType.getSelectedItem()
                                    .toString()
                                    .toUpperCase()
                                    .replaceAll(" ", ""));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        tietHabitName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                tvFinalNumericalHabit.setText(
                        new StringBuilder().append("Final habit: ")
                                .append(charSequence != null ? charSequence : "")
                                .append(" ")
                                .append(spnHabitComparisonType.getSelectedItem())
                                .append(" ")
                                .append((numericalGoal != null) ? (((numericalGoal % 1) == 0) ? String.format("%.0f", numericalGoal) : numericalGoal) : "")
                                .append(" ")
                                .append(numericalUnit != null ? numericalUnit : "")
                );
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (isHabitNameValid()) {
                    habitName = editable.toString();
                } else {
                    setHabitNameError();
                }
            }
        });

        tietHabitNumericalGoal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                tvFinalNumericalHabit.setText(
                        new StringBuilder().append("Final habit: ")
                                .append(habitName != null ? habitName : "")
                                .append(" ")
                                .append(spnHabitComparisonType.getSelectedItem())
                                .append(" ")
                                .append(charSequence != null ? charSequence : "")
                                .append(" ")
                                .append(numericalUnit != null ? numericalUnit : "")
                );
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (isHabitNumericalGoalValid()) {
                    numericalGoal = Double.parseDouble(tietHabitNumericalGoal.getText().toString().trim());
                } else {
                    setHabitNumericalGoalError();
                }
            }
        });

        tietHabitNumericalUnit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                tvFinalNumericalHabit.setText(
                        new StringBuilder().append("Final habit: ")
                                .append(habitName != null ? habitName : "")
                                .append(" ")
                                .append(spnHabitComparisonType.getSelectedItem())
                                .append(" ")
                                .append((numericalGoal != null) ? (((numericalGoal % 1) == 0) ? String.format("%.0f", numericalGoal) : numericalGoal) : "")
                                .append(" ")
                                .append(charSequence != null ? charSequence : "")
                );
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (isHabitNumericalUnitValid()) {
                    numericalUnit = editable.toString();
                } else {
                    setHabitNumericalUnitError();
                }
            }
        });

        rgHabitFrequency.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedRadioButtonId) {
                if (checkedRadioButtonId == R.id.rbFrequencyDaily) {
                    configureComponentsForDailyFrequencySelected();
                    leaveOnlyDailyFrequencyVariablesNonNull();
                } else if (checkedRadioButtonId == R.id.rbFrequencyDaysOfWeek) {
                    configureComponentsForDaysOfWeekFrequencySelected();
                    leaveOnlyDaysOfWeekFrequencyVariablesNonNull();
                } else if (checkedRadioButtonId == R.id.rbFrequencyDaysOfMonth) {
                    configureComponentsForDaysOfMonthFrequencySelected();
                    leaveOnlyDaysOfMonthFrequencyVariablesNonNull();
                } else if (checkedRadioButtonId == R.id.rbFrequencyRepeat) {
                    configureComponentsForRepeatFrequencySelected();
                    leaveOnlyRepeatFrequencyVariablesNonNull();
                }
            }
        });

        for (int i = 1; i < constraintLayoutDaysOfWeek.getChildCount() + 1; i++) {
            TextView textView = (TextView) constraintLayoutDaysOfWeek.getChildAt(i - 1);
            int finalI = i;
            textView.setOnClickListener(view1 -> {
                if (!daysOfWeek.contains(finalI)) {
                    textView.setBackgroundResource(R.drawable.round_corners_green_white_border);
                    daysOfWeek.add(finalI);
                } else {
                    textView.setBackgroundResource(R.drawable.circle_transparent_white);
                    daysOfWeek.remove(Integer.valueOf(finalI));
                }
            });
        }

        for (int i = 1; i < flexboxLayoutDaysOfMonth.getChildCount() + 1; i++) {
            TextView textView = (TextView) flexboxLayoutDaysOfMonth.getChildAt(i - 1);
            int finalI = i;
            textView.setOnClickListener(view1 -> {
                if (!daysOfMonth.contains(finalI)) {
                    textView.setBackgroundResource(R.drawable.round_corners_green_white_thin_border);
                    daysOfMonth.add(finalI);
                } else {
                    textView.setBackgroundResource(R.drawable.circle_transparent_white);
                    daysOfMonth.remove(Integer.valueOf(finalI));
                }
            });
        }

        openMainActivity = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                });

        tvSelectCategory.setOnClickListener(view -> {
            if (recyclerViewSelectCategory.getVisibility() == View.GONE) {
                TransitionManager.beginDelayedTransition(cardViewAddHabitCategories);
                recyclerViewSelectCategory.setVisibility(View.VISIBLE);
            } else {
                recyclerViewSelectCategory.setVisibility(View.GONE);
            }
        });

        recyclerViewSelectCategory.setHasFixedSize(true);
        recyclerViewSelectCategory.setLayoutManager(recyclerViewCategoriesLayoutManager);
        recyclerViewSelectCategory.setAdapter(recyclerViewCategoriesAdapter);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(AddHabitActivity.this, R.drawable.custom_recycle_view_divider));
        recyclerViewSelectCategory.addItemDecoration(dividerItemDecoration);
        tvAddHabitStartDate.setOnClickListener(view -> showDatePickerDialog(onHabitStartDateSet, habitStartDate));
        tvAddHabitColor.setOnClickListener(view -> showColorPickerDialog());
        tvAddHabitDifficulty.setOnClickListener(view -> selectHabitDifficultyAlertDialog.show());

        tvAddHabitFinish.setOnClickListener(view ->
        {
            if (areInputsValid()) {
                DatabaseReference habitsFirebasePath;
                if (receivedHabit == null) {
                    habitsFirebasePath = FirebaseDatabase.getInstance("https://rotaru-teodor-gabriel-projfb-default-rtdb.europe-west1.firebasedatabase.app/")
                            .getReference("Users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child("habits")
                            .push();

                    finalHabitToBeAdded = new Habit(habitName, habitDescription, habitStartDate.toEpochDay(),
                            evaluationType, numericalGoal, numericalComparisonType, numericalUnit, frequency,
                            daysOfWeek, daysOfMonth, numberOfDaysForRepeat, habitCategory,
                            habitsFirebasePath.getKey(), false, habitColor, daysToFormHabit);
                } else {
                    habitsFirebasePath = FirebaseDatabase.getInstance("https://rotaru-teodor-gabriel-projfb-default-rtdb.europe-west1.firebasedatabase.app/")
                            .getReference("Users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child("habits")
                            .child(receivedHabit.getFirebaseId());

                    HashMap<String, Double> realizationsAfterStartDate = receivedHabit.getRealizations();
                    if (habitStartDate.toEpochDay() > receivedHabit.getStartDate()) {
                        HashMap<String, Double> realizations = receivedHabit.getRealizations();
                        realizationsAfterStartDate = new HashMap<>();
                        for (Map.Entry<String, Double> realization : realizations.entrySet()) {
                            if (Long.parseLong(realization.getKey()) >= habitStartDate.toEpochDay()) {
                                realizationsAfterStartDate.put(realization.getKey(), realization.getValue());
                            }
                        }
                    }

                    finalHabitToBeAdded = new Habit(habitName, habitDescription, habitStartDate.toEpochDay(),
                            evaluationType, numericalGoal, numericalComparisonType, numericalUnit,
                            frequency, daysOfWeek, daysOfMonth, numberOfDaysForRepeat, habitCategory,
                            receivedHabit.getFirebaseId(), receivedHabit.getArchived(), habitColor,
                            realizationsAfterStartDate, daysToFormHabit);
                }
                habitsFirebasePath
                        .setValue(finalHabitToBeAdded)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(AddHabitActivity.this, "Habit added", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(AddHabitActivity.this, "Failed to add habit", Toast.LENGTH_SHORT).show();
                            }
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            openMainActivity.launch(intent);
                            finish();
                        });
            }
        });
        tvAddHabitCancel.setOnClickListener(view -> confirmCancelDialog.show());
    }


    private void showColorPickerDialog() {
        ColorPicker colorPicker = new ColorPicker(AddHabitActivity.this);
        ArrayList<String> colorsForColorPicker = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.tasks_and_habits_colors)));

        colorPicker.setColors(colorsForColorPicker)
                .setColumns(5)
                .setRoundColorButton(true)
                .disableDefaultButtons(true)
                .setOnFastChooseColorListener(new ColorPicker.OnFastChooseColorListener() {
                    @Override
                    public void setOnFastChooseColorListener(int position, int color) {
                        habitColor = color;
                        Drawable customDrawable = ContextCompat.getDrawable(AddHabitActivity.this,
                                R.drawable.round_corners_30)
                                .getConstantState()
                                .newDrawable()
                                .mutate();
                        customDrawable.setTint(habitColor);
                        tvAddHabitColor.setBackground(customDrawable);
                    }

                    @Override
                    public void onCancel() {

                    }
                }).show();
    }


    private void configureComponentsForRepeatFrequencySelected() {
        frequency = HabitFrequency.REPEAT;
        constraintLayoutDaysOfWeek.setVisibility(View.GONE);
        flexboxLayoutDaysOfMonth.setVisibility(View.GONE);
        constraintLayoutRepeat.setVisibility(View.VISIBLE);
        leaveOnlyRepeatFrequencyVariablesNonNull();
        unselectDaysOfMonth();
        unselectDaysOfWeek();
    }


    private void configureComponentsForDaysOfMonthFrequencySelected() {
        if (daysOfMonth == null) {
            daysOfMonth = new ArrayList<>();
        }
        frequency = HabitFrequency.SPECIFICDAYSOFMONTH;
        constraintLayoutDaysOfWeek.setVisibility(View.GONE);
        flexboxLayoutDaysOfMonth.setVisibility(View.VISIBLE);
        constraintLayoutRepeat.setVisibility(View.GONE);
        leaveOnlyDaysOfMonthFrequencyVariablesNonNull();
        unselectDaysOfWeek();
    }


    private void configureComponentsForDaysOfWeekFrequencySelected() {
        if (daysOfWeek == null) {
            daysOfWeek = new ArrayList<>();
        }
        frequency = HabitFrequency.SPECIFICDAYSOFWEEK;
        constraintLayoutDaysOfWeek.setVisibility(View.VISIBLE);
        flexboxLayoutDaysOfMonth.setVisibility(View.GONE);
        constraintLayoutRepeat.setVisibility(View.GONE);
        leaveOnlyDaysOfWeekFrequencyVariablesNonNull();
        unselectDaysOfMonth();
    }


    private void configureComponentsForDailyFrequencySelected() {
        frequency = HabitFrequency.DAILY;
        constraintLayoutDaysOfWeek.setVisibility(View.GONE);
        flexboxLayoutDaysOfMonth.setVisibility(View.GONE);
        constraintLayoutRepeat.setVisibility(View.GONE);
        leaveOnlyDailyFrequencyVariablesNonNull();
        unselectDaysOfWeek();
        unselectDaysOfMonth();
    }


    private void unselectDaysOfWeek() {
        for (int i = 1; i < constraintLayoutDaysOfWeek.getChildCount() + 1; i++) {
            TextView textView = (TextView) constraintLayoutDaysOfWeek.getChildAt(i - 1);
            textView.setBackgroundResource(R.drawable.circle_transparent_white);
        }
    }


    public void unselectDaysOfMonth() {
        for (int i = 1; i < flexboxLayoutDaysOfMonth.getChildCount() + 1; i++) {
            TextView textView = (TextView) flexboxLayoutDaysOfMonth.getChildAt(i - 1);
            textView.setBackgroundResource(R.drawable.circle_transparent_white);
        }
    }


    @Override
    public void onBackPressed() {
        confirmCancelDialog.show();
    }


    private void configureUIForYesNoEvaluationType() {
        evaluationType = HabitEvaluationType.YESNO;
        btnYesNo.setBackgroundResource(R.drawable.round_corners_green_white_border);
        btnNumerical.setBackgroundResource(R.drawable.round_corners_green);
        spnHabitComparisonType.setVisibility(View.GONE);
        tilHabitNumericalGoal.setVisibility(View.GONE);
        tilHabitNumericalUnit.setVisibility(View.GONE);
        tvFinalNumericalHabit.setVisibility(View.GONE);
        leaveOnlyYesNoEvaluationTypeVariablesNonNull();
    }


    private void configureUIForNumericalEvaluationType() {
        evaluationType = HabitEvaluationType.NUMERICAL;
        btnYesNo.setBackgroundResource(R.drawable.round_corners_green);
        btnNumerical.setBackgroundResource(R.drawable.round_corners_green_white_border);
        spnHabitComparisonType.setVisibility(View.VISIBLE);
        tilHabitNumericalGoal.setVisibility(View.VISIBLE);
        tilHabitNumericalUnit.setVisibility(View.VISIBLE);
        tvFinalNumericalHabit.setVisibility(View.VISIBLE);
    }


    private void leaveOnlyYesNoEvaluationTypeVariablesNonNull() {
        numericalGoal = null;
        numericalUnit = null;
        numericalComparisonType = null;
    }


    private void leaveOnlyDailyFrequencyVariablesNonNull() {
        daysOfWeek = null;
        daysOfMonth = null;
    }

    private void leaveOnlyDaysOfWeekFrequencyVariablesNonNull() {
        daysOfMonth = null;
    }


    private void leaveOnlyDaysOfMonthFrequencyVariablesNonNull() {
        daysOfWeek = null;
    }


    private void leaveOnlyRepeatFrequencyVariablesNonNull() {
        daysOfWeek = null;
        daysOfMonth = null;
    }


    @Override
    public void recyclerViewListClicked(View v, int position) {
        tvSelectCategory.setText("Category: ".concat(categories.get(position)));
        habitCategory = categories.get(position);
        recyclerViewSelectCategory.setVisibility(View.GONE);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showDatePickerDialog(DatePickerDialog.OnDateSetListener onDateSetListener, LocalDate initialDate) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                AddHabitActivity.this,
                AlertDialog.THEME_DEVICE_DEFAULT_DARK,
                onDateSetListener,
                initialDate.getYear(),
                initialDate.getMonthValue() - 1,
                initialDate.getDayOfMonth());

        datePickerDialog.show();
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public boolean areInputsValid() {
        if (!isHabitNameValid()) {
            setHabitNameError();
            return false;
        }
        if (evaluationType == HabitEvaluationType.NUMERICAL) {
            if (!isHabitNumericalGoalValid()) {
                setHabitNumericalGoalError();
                return false;
            }
            if (!isHabitNumericalUnitValid()) {
                setHabitNumericalUnitError();
                return false;
            }
        }
        if (frequency == null) {
            showFrequencyErrorToast();
            return false;
        }
        if (frequency == HabitFrequency.SPECIFICDAYSOFWEEK) {
            if (daysOfWeek.size() == 0) {
                Toast.makeText(AddHabitActivity.this, "Please select at least one week day", Toast.LENGTH_LONG).show();
                return false;
            }
        } else if (frequency == HabitFrequency.SPECIFICDAYSOFMONTH) {
            if (daysOfMonth.size() == 0) {
                Toast.makeText(AddHabitActivity.this, "Please select at least one month day", Toast.LENGTH_LONG).show();
                return false;
            }
        } else if (frequency == HabitFrequency.REPEAT) {
            if (!isNumberOfTimesToRepeatValid()) {
                setNumberOfTimesToRepeatError();
                return false;
            } else {
                numberOfDaysForRepeat = Integer.parseInt(editTextNumberOfDaysToRepeatAt.getText().toString().trim());
            }
        }
        if (isHabitDescriptionValid()) {
            habitDescription = tietHabitDescription.getText().toString().trim();
        }
        return true;
    }


    private boolean isHabitNumericalUnitValid() {
        return tietHabitNumericalUnit.getText() != null &&
                tietHabitNumericalUnit.getText().toString().trim().length() >= 2;
    }


    private void setHabitNumericalUnitError() {
        tietHabitNumericalUnit.setError("Habit numerical unit must have more than one character!");
        tietHabitNumericalUnit.requestFocus();
    }


    private boolean isHabitDescriptionValid() {
        if (tietHabitDescription.getText() != null &&
                tietHabitDescription.getText().toString().trim().length() > 0) {
            return tietHabitDescription.getText().toString().trim().length() >= 2;
        }
        return false;
    }


    private boolean isHabitNumericalGoalValid() {
        try {
            Double.parseDouble(tietHabitNumericalGoal.getText().toString().trim());
        } catch (Exception exception) {
            return false;
        }
        return true;
    }


    private void setHabitNumericalGoalError() {
        tietHabitNumericalGoal.setError("Please enter a valid number!");
        tietHabitNumericalGoal.requestFocus();
    }


    private boolean isHabitNameValid() {
        return tietHabitName.getText() != null &&
                tietHabitName.getText().toString().trim().length() >= 2;
    }

    private void setHabitNameError() {
        tietHabitName.setError("Habit must have more than one character!");
        tietHabitName.requestFocus();
    }

    private boolean isNumberOfTimesToRepeatValid() {
        try {
            Integer.parseInt(editTextNumberOfDaysToRepeatAt.getText().toString().trim());
        } catch (Exception exception) {
            return false;
        }
        return true;
    }

    private void setNumberOfTimesToRepeatError() {
        editTextNumberOfDaysToRepeatAt.setError("Please enter a valid number!");
        editTextNumberOfDaysToRepeatAt.requestFocus();
    }

    private void showFrequencyErrorToast() {
        Toast.makeText(AddHabitActivity.this, "Please select a frequency", Toast.LENGTH_LONG).show();
    }
}