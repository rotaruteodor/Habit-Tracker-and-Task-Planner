package com.example.licentarotaruteodorgabriel.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.licentarotaruteodorgabriel.R;
import com.example.licentarotaruteodorgabriel.activities.AddHabitActivity;
import com.example.licentarotaruteodorgabriel.activities.HabitStatisticsActivity;
import com.example.licentarotaruteodorgabriel.activities.LoginActivity;
import com.example.licentarotaruteodorgabriel.classes.Habit;
import com.example.licentarotaruteodorgabriel.date_utils.LocalDateConverter;
import com.example.licentarotaruteodorgabriel.enums.HabitEvaluationType;
import com.example.licentarotaruteodorgabriel.enums.HabitFrequency;
import com.example.licentarotaruteodorgabriel.enums.HabitNumericalComparisonType;
import com.example.licentarotaruteodorgabriel.habit_statistics.streaks.HabitCurrentStreak;
import com.example.licentarotaruteodorgabriel.interfaces.RecyclerViewClickListener;
import com.example.licentarotaruteodorgabriel.list_adaptors.HabitsRecyclerViewAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.stream.Collectors;


public class HabitsFragment extends Fragment implements DatePickerDialog.OnDateSetListener, RecyclerViewClickListener {

    private TextView tvHabitsDate;
    private ImageButton btnIncrementHabitsDate;
    private ImageButton btnDecrementHabitsDate;
    private ImageButton btnHabitsOptionsMenu;
    private FloatingActionButton btnAddHabit;
    private ImageView imgViewNoHabits;


    private RecyclerView recyclerViewHabits;
    private RecyclerView.Adapter recyclerViewHabitsAdapter;
    private RecyclerView.LayoutManager recyclerViewHabitsLayoutManager;
    private DividerItemDecoration dividerItemDecoration;

    private LocalDate selectedDate;
    private ArrayList<Habit> habits = new ArrayList<>();
    private ArrayList<String> categories = new ArrayList<>();

    private ActivityResultLauncher<Intent> openAddHabitActivity;
    private ActivityResultLauncher<Intent> openHabitStatisticsActivity;
    private ActivityResultLauncher<Intent> openLoginActivity;

    private AlertDialog.Builder habitIndividualOptionsDialogBuilder;
    private AlertDialog inputNumericalRealizationDialog;
    private EditText editTextNumericalRealizationInput;
    private TextView tvFinishNumericalRealizationInput;

    private Boolean showArchivedHabits;

    private DatabaseReference currentLoggedInUserHabitsDbPath;

    public HabitsFragment() {
    }

    public static HabitsFragment newInstance() {
        HabitsFragment fragment = new HabitsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeNonGraphicalComponents();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_habits, container, false);
        initializeGraphicalComponents(v);
        return v;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initializeNonGraphicalComponents() {
        showArchivedHabits = false;
        habits = getArguments().getParcelableArrayList("HABITS_FROM_MAIN_ACTIVITY_TO_HABITS_FRAGMENT");
        categories = getArguments().getStringArrayList("CATEGORIES_FROM_MAIN_ACTIVITY_TO_HABITS_FRAGMENT");

        currentLoggedInUserHabitsDbPath = FirebaseDatabase.getInstance("https://rotaru-teodor-gabriel-projfb-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("habits");

        selectedDate = LocalDate.now();
        recyclerViewHabitsLayoutManager = new LinearLayoutManager(this.getContext());
        recyclerViewHabitsAdapter = new HabitsRecyclerViewAdapter(getSelectedDateHabits(), this.getContext(), this, selectedDate);

        configureNonGraphicalComponents();
    }

    private void configureNonGraphicalComponents() {
        openAddHabitActivity = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                });

        openHabitStatisticsActivity = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                });

        openLoginActivity = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                });
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initializeGraphicalComponents(View v) {
        tvHabitsDate = v.findViewById(R.id.tvHabitsDate);
        btnIncrementHabitsDate = v.findViewById(R.id.btnIncrementHabitsDate);
        btnDecrementHabitsDate = v.findViewById(R.id.btnDecrementHabitsDate);
        btnHabitsOptionsMenu = v.findViewById(R.id.btnHabitsOptionsMenu);
        btnAddHabit = v.findViewById(R.id.btnAddHabit);
        imgViewNoHabits = v.findViewById(R.id.imgViewNoHabits);
        recyclerViewHabits = v.findViewById(R.id.recyclerViewHabits);

        dividerItemDecoration = new DividerItemDecoration(recyclerViewHabits.getContext(), DividerItemDecoration.VERTICAL);

        habitIndividualOptionsDialogBuilder = new AlertDialog.Builder(getActivity(),
                AlertDialog.THEME_DEVICE_DEFAULT_DARK)
                .setTitle("Options");

        View inputNumericalRealizationDialogView = getActivity().getLayoutInflater()
                .inflate(R.layout.add_numerical_realization_custom_view, null);

        inputNumericalRealizationDialog = new AlertDialog.Builder(getActivity(),
                AlertDialog.THEME_DEVICE_DEFAULT_DARK)
                .setView(inputNumericalRealizationDialogView)
                .create();

        editTextNumericalRealizationInput = inputNumericalRealizationDialogView.findViewById(R.id.editTextInputNumericalRealization);
        tvFinishNumericalRealizationInput = inputNumericalRealizationDialogView.findViewById(R.id.tvFinishNumericalRealizationInput);

        configureGraphicalComponents(v);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void configureGraphicalComponents(View v) {
        tvHabitsDate.setText(LocalDateConverter.getWeekDayFormatStringFromDate(selectedDate));

        btnIncrementHabitsDate.setOnClickListener(view -> incrementHabitsDate());
        btnDecrementHabitsDate.setOnClickListener(view -> decrementHabitsDate());
        tvHabitsDate.setOnClickListener(view -> showDatePickerDialog());
        btnHabitsOptionsMenu.setOnClickListener(view -> showOptionsMenu(btnHabitsOptionsMenu));
        btnAddHabit.setOnClickListener(view -> launchAddHabitActivity(null));

        recyclerViewHabits.setHasFixedSize(true);
        recyclerViewHabits.setLayoutManager(recyclerViewHabitsLayoutManager);
        recyclerViewHabits.setAdapter(recyclerViewHabitsAdapter);
        recyclerViewHabits.addItemDecoration(dividerItemDecoration);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(this.getContext(), R.drawable.custom_recycle_view_divider));

        inputNumericalRealizationDialog.getWindow()
                .setBackgroundDrawable(ContextCompat.getDrawable(getContext(),
                        R.drawable.round_corners_30));
        updateHabitsRecyclerView();
    }

    private void launchAddHabitActivity(Habit habitToBeEdited) {
        Intent intent = new Intent(getContext(), AddHabitActivity.class);
        intent.putExtra("HABIT_TO_BE_EDITED", habitToBeEdited);
        intent.putStringArrayListExtra("CATEGORIES_FROM_HABITS_FRAGMENT", categories);
        openAddHabitActivity.launch(intent);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void incrementHabitsDate() {
        selectedDate = selectedDate.plusDays(1);
        tvHabitsDate.setText(LocalDateConverter.getWeekDayFormatStringFromDate(selectedDate));
        ((HabitsRecyclerViewAdapter) recyclerViewHabitsAdapter).setSelectedDate(selectedDate);
        updateHabitsRecyclerView();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void decrementHabitsDate() {
        selectedDate = selectedDate.minusDays(1);
        tvHabitsDate.setText(LocalDateConverter.getWeekDayFormatStringFromDate(selectedDate));
        ((HabitsRecyclerViewAdapter) recyclerViewHabitsAdapter).setSelectedDate(selectedDate);
        updateHabitsRecyclerView();
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void updateHabitsRecyclerView() {
        if (getSelectedDateHabits().size() > 0) {
            imgViewNoHabits.setVisibility(View.GONE);
            recyclerViewHabits.setVisibility(View.VISIBLE);
            ((HabitsRecyclerViewAdapter) recyclerViewHabitsAdapter).setHabits(getSelectedDateHabits());
            recyclerViewHabitsAdapter.notifyDataSetChanged();
        } else {
            imgViewNoHabits.setVisibility(View.VISIBLE);
            recyclerViewHabits.setVisibility(View.GONE);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private ArrayList<Habit> getSelectedDateHabits() {
        ArrayList<Habit> selectedDateHabits = new ArrayList<>();
        if (showArchivedHabits) {
            return (ArrayList<Habit>) habits.stream().filter(Habit::getArchived).collect(Collectors.toList());
        } else {
            for (Habit habit : habits) {
                if (isHabitValid(habit)) {
                    selectedDateHabits.add(habit);
                }
            }
        }
        return selectedDateHabits;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean isHabitValid(Habit habit) {
        if (habit.getStartDate() > selectedDate.toEpochDay() || habit.getArchived()) {
            return false;
        }
        if (habit.getFrequency() == HabitFrequency.SPECIFICDAYSOFWEEK) {
            return habit.getDaysOfWeek().contains(selectedDate.getDayOfWeek().getValue());
        } else if (habit.getFrequency() == HabitFrequency.SPECIFICDAYSOFMONTH) {
            return habit.getDaysOfMonth().contains(selectedDate.getDayOfMonth());
        } else if (habit.getFrequency() == HabitFrequency.REPEAT) {
            return (selectedDate.toEpochDay() - habit.getStartDate()) % habit.getNumberOfDaysForRepeat() == 0;
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                AlertDialog.THEME_DEVICE_DEFAULT_DARK,
                this,
                selectedDate.getYear(),
                selectedDate.getMonthValue() - 1,
                selectedDate.getDayOfMonth()
        );

        datePickerDialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {  //triggered when a date is selected
        selectedDate = LocalDate.of(year, month + 1, dayOfMonth);
        tvHabitsDate.setText(LocalDateConverter.getWeekDayFormatStringFromDate(selectedDate));
        ((HabitsRecyclerViewAdapter) recyclerViewHabitsAdapter).setSelectedDate(selectedDate);
        updateHabitsRecyclerView();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void showOptionsMenu(View v) {

        PopupMenu popup = new PopupMenu(getActivity(), v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.habits_drop_down_menu, popup.getMenu());

        if (showArchivedHabits) {
            popup.getMenu().getItem(0).setTitle("See active habits");
        } else {
            popup.getMenu().getItem(0).setTitle("See archived habits");
        }

        popup.setOnMenuItemClickListener(menuItem -> {
            int menuItemId = menuItem.getItemId();

            if (menuItemId == R.id.habitsArchiveDropDownMenuItem) {
                showArchivedHabits = !showArchivedHabits;
                if (showArchivedHabits) {
                    btnDecrementHabitsDate.setClickable(false);
                    btnDecrementHabitsDate.setColorFilter(Color.GRAY);
                    btnIncrementHabitsDate.setClickable(false);
                    btnIncrementHabitsDate.setColorFilter(Color.GRAY);
                    tvHabitsDate.setClickable(false);
                    tvHabitsDate.setTextColor(Color.GRAY);
                } else {
                    btnDecrementHabitsDate.setClickable(true);
                    btnDecrementHabitsDate.setColorFilter(Color.WHITE);
                    btnIncrementHabitsDate.setClickable(true);
                    btnIncrementHabitsDate.setColorFilter(Color.WHITE);
                    tvHabitsDate.setClickable(true);
                    tvHabitsDate.setTextColor(Color.WHITE);
                }
                updateHabitsRecyclerView();
                return true;
            } else if (menuItemId == R.id.habitsSignOutDropDownMenuItem) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                openLoginActivity.launch(intent);
                if(getActivity() != null){
                    getActivity().finish();
                }
                return true;
            } else {
                return false;
            }
        });
        popup.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onPositionClicked(View clickedView, int clickedRecyclerItemPosition) {
        Habit selectedHabit = habits.get(habits.indexOf(getSelectedDateHabits().get(clickedRecyclerItemPosition)));
        if (clickedView.getId() == R.id.tvHabitStatus) {
            TextView selectedTvHabitStatus = (TextView) clickedView;
            TextView tvHabitStreak = recyclerViewHabitsLayoutManager.findViewByPosition(clickedRecyclerItemPosition).findViewById(R.id.tvHabitStreak);
            if (selectedHabit.getEvaluationType() == HabitEvaluationType.YESNO) {
                if (selectedTvHabitStatus.getText() == "Status: Ongoing") {
                    setHabitStatusAsDone(selectedHabit, selectedTvHabitStatus);
                } else {
                    setHabitStatusAsOngoing(selectedHabit, selectedTvHabitStatus);
                }
                tvHabitStreak.setText("Streak: ".concat(HabitCurrentStreak.getCurrentStreak(selectedHabit).toString()));
            } else { // NUMERICAL
                numericalHabitStatusClicked(selectedHabit, selectedTvHabitStatus, tvHabitStreak);
            }
        } else if (clickedView.getId() == R.id.btnIndividualHabitOptionsMenu) {
            if (selectedHabit.getArchived()) {
                displayIndividualHabitOptions(selectedHabit, R.array.archivedHabitOptions);
            } else {
                displayIndividualHabitOptions(selectedHabit, R.array.unarchivedHabitOptions);
            }
        } else {
            // row clicked
            Intent intent = new Intent(getContext(), HabitStatisticsActivity.class);
            intent.putExtra("HABIT_FOR_STATISTICS", selectedHabit);
            openHabitStatisticsActivity.launch(intent);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void displayIndividualHabitOptions(Habit selectedHabit, int resourceArrayId) {
        habitIndividualOptionsDialogBuilder.setItems(resourceArrayId,
                (dialogInterface, i) -> {
                    if (i == 0) { // edit
                        launchAddHabitActivity(selectedHabit);
                    } else if (i == 1) { // unarchive / archive
                        if (resourceArrayId == R.array.archivedHabitOptions) {
                            setHabitArchiveStatus(selectedHabit, false);
                        } else {
                            setHabitArchiveStatus(selectedHabit, true);
                        }
                    } else if (i == 2) { // DELETE
                        configureDeleteHabitDialog(selectedHabit);
                    }
                    dialogInterface.dismiss();
                })
                .create().show();

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void configureDeleteHabitDialog(Habit selectedHabit) {
        AlertDialog.Builder deleteHabitDialog = new AlertDialog.Builder(getActivity(),
                AlertDialog.THEME_DEVICE_DEFAULT_DARK)
                .setTitle("Are you sure you want to delete this habit?")
                .setPositiveButton("Yes", (dialog, index) -> {
                    habits.remove(selectedHabit);
                    updateHabitsRecyclerView();
                    currentLoggedInUserHabitsDbPath
                            .child(selectedHabit.getFirebaseId())
                            .removeValue();
                })
                .setNegativeButton("No", (dialog, index) -> {
                });
        if (!selectedHabit.getArchived()) {
            deleteHabitDialog.setMessage("You could just archive it in order to easily reactivate it whenever you want");
        }
        deleteHabitDialog.create().show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setHabitArchiveStatus(Habit selectedHabit, boolean archive) {
        selectedHabit.setArchived(archive);
        FirebaseDatabase.getInstance("https://rotaru-teodor-gabriel-projfb-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("habits")
                .child(selectedHabit.getFirebaseId())
                .child("archived")
                .setValue(archive);
        updateHabitsRecyclerView();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void numericalHabitStatusClicked(Habit selectedHabit, TextView selectedTvHabitStatus, TextView tvHabitStreak) {
        tvFinishNumericalRealizationInput.setOnClickListener(view -> {
            String editTextInputString = editTextNumericalRealizationInput.getText().toString().trim();
            if (isNumberADouble(editTextInputString)) {
                Double selectedHabitNumericalGoal = selectedHabit.getNumericalGoal();
                Double inputtedNumber = Double.parseDouble(editTextInputString);

                selectedTvHabitStatus.setText(getHabitStatusString(inputtedNumber, selectedHabitNumericalGoal));
                selectedTvHabitStatus.setTextColor(getNumericalStatusColor(selectedHabitNumericalGoal,
                        inputtedNumber,
                        selectedHabit.getNumericalComparisonType()));

                currentLoggedInUserHabitsDbPath
                        .child(selectedHabit.getFirebaseId())
                        .child("realizations")
                        .child(String.valueOf(selectedDate.toEpochDay()))
                        .setValue(inputtedNumber);

                selectedHabit.updateRealizationValue(String.valueOf(selectedDate.toEpochDay()),
                        inputtedNumber);

                tvHabitStreak.setText("Streak: "
                        .concat(HabitCurrentStreak.getCurrentStreak(selectedHabit).toString()));
                inputNumericalRealizationDialog.dismiss();
                editTextNumericalRealizationInput.setText("");
            } else {
                editTextNumericalRealizationInput.setError("");
                editTextNumericalRealizationInput.requestFocus();
            }
        });

        inputNumericalRealizationDialog.show();
        inputNumericalRealizationDialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setHabitStatusAsOngoing(Habit selectedHabit, TextView selectedTvHabitStatus) {
        selectedTvHabitStatus.setText("Status: Ongoing");
        selectedTvHabitStatus.setTextColor(Color.RED);

        currentLoggedInUserHabitsDbPath
                .child(selectedHabit.getFirebaseId())
                .child("realizations")
                .child(String.valueOf(selectedDate.toEpochDay()))
                .removeValue();

        selectedHabit.removeRealization(String.valueOf(selectedDate.toEpochDay()));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setHabitStatusAsDone(Habit selectedHabit, TextView selectedTvHabitStatus) {
        selectedTvHabitStatus.setText("Status: Done");
        selectedTvHabitStatus.setTextColor(Color.GREEN);

        currentLoggedInUserHabitsDbPath
                .child(selectedHabit.getFirebaseId())
                .child("realizations")
                .child(String.valueOf(selectedDate.toEpochDay()))
                .setValue(1);

        selectedHabit.updateRealizationValue(String.valueOf(selectedDate.toEpochDay()), 1.);
    }

    private int getNumericalStatusColor(Double numericalGoal,
                                        Double inputtedNumber,
                                        HabitNumericalComparisonType comparisonType) {

        if (comparisonType == HabitNumericalComparisonType.ATLEAST) {
            if (inputtedNumber >= numericalGoal) {
                return Color.GREEN;
            }
            return Color.RED;
        } else if (comparisonType == HabitNumericalComparisonType.LESSTHAN) {
            if (inputtedNumber < numericalGoal) {
                return Color.GREEN;
            }
            return Color.RED;
        } else {
            if (inputtedNumber.equals(numericalGoal)) {
                return Color.GREEN;
            }
            return Color.RED;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onLongPositionClicked(View clickedView, int clickedRecyclerItemPosition) {
        Habit selectedHabit = habits.get(habits.indexOf(getSelectedDateHabits().get(clickedRecyclerItemPosition)));
        if (selectedHabit.getArchived()) {
            displayIndividualHabitOptions(selectedHabit, R.array.archivedHabitOptions);
        } else {
            displayIndividualHabitOptions(selectedHabit, R.array.unarchivedHabitOptions);
        }
    }

    private String getHabitStatusString(Double inputtedNumber, Double selectedHabitNumericalGoal) {

        StringBuilder stringBuilder = new StringBuilder().append("Status: ");

        if (isDoubleAnInteger(inputtedNumber)) {
            stringBuilder.append(String.format("%.0f", inputtedNumber));
        } else {
            stringBuilder.append(inputtedNumber);
        }
        stringBuilder.append("/");
        if (isDoubleAnInteger(selectedHabitNumericalGoal)) {
            stringBuilder.append(String.format("%.0f", selectedHabitNumericalGoal));
        } else {
            stringBuilder.append(selectedHabitNumericalGoal.toString());
        }

        return stringBuilder.toString();
    }

    private boolean isNumberADouble(String numberAsString) {
        try {
            Double.parseDouble(numberAsString);
        } catch (Exception ex) {
            return false;
        }
        return true;
    }

    private boolean isDoubleAnInteger(Double numberAsDouble) {
        return numberAsDouble % 1 == 0;
    }
}