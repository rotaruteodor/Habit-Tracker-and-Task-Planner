package com.example.licentarotaruteodorgabriel.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.licentarotaruteodorgabriel.R;
import com.example.licentarotaruteodorgabriel.classes.Task;
import com.example.licentarotaruteodorgabriel.date_utils.LocalDateTimeConverter;
import com.example.licentarotaruteodorgabriel.interfaces.CategoriesRecyclerViewClickListener;
import com.example.licentarotaruteodorgabriel.list_adaptors.CategoriesRecyclerViewAdapter;
import com.example.licentarotaruteodorgabriel.notifications.NotificationsUtils;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import petrov.kristiyan.colorpicker.ColorPicker;

public class AddTaskActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, CategoriesRecyclerViewClickListener, TimePickerDialog.OnTimeSetListener {

    private TextInputLayout tilTaskName;
    private TextInputEditText tietTaskName;
    private TextInputLayout tilTaskDescription;
    private TextInputEditText tietTaskDescription;
    private TextView tvSelectTaskPriority;
    private TextView tvSelectTaskDueDate;
    private MaterialCardView cardViewAddTaskCategories;
    private TextView tvSelectTaskCategory;
    private RecyclerView recyclerViewSelectTaskCategory;
    private TextView tvAddTaskColor;
    private TextView tvAddTaskFinish;
    private TextView tvAddTaskCancel;

    private RecyclerView.Adapter recyclerViewCategoriesAdapter;
    private RecyclerView.LayoutManager recyclerViewCategoriesLayoutManager;
    private DividerItemDecoration dividerItemDecoration;

    private ArrayList<String> categories = new ArrayList<>();
    private int nrOfAllTasks;

    private AlertDialog selectTaskPriorityDialog;
    private AlertDialog confirmCancelDialog;

    private Integer taskPriority;
    private LocalDateTime taskDueDate;
    private String taskCategory;
    private int taskColor;
    private ActivityResultLauncher<Intent> openMainActivity;
    private Task taskReceivedFromMainActivity;
    private int currentTaskNotificationId;
    private SharedPreferences sharedPreferences;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        initializeComponents();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initializeComponents() {
        taskReceivedFromMainActivity = getIntent().getParcelableExtra("TASK_TO_BE_EDITED");
        categories = getIntent().getStringArrayListExtra("CATEGORIES_FROM_TASKS_FRAGMENT");
        nrOfAllTasks = getIntent().getIntExtra("NR_OF_ALL_TASKS_FROM_TASKS_FRAGMENT_TO_ADD_TASK_ACTIVITY", 0);
        currentTaskNotificationId = getIntent().getIntExtra("TASK_NOTIFICATION_ID_FROM_TASKS_FRAGMENT_TO_ADD_TASK_ACTIVITY", 1);

        tilTaskName = findViewById(R.id.tilTaskName);
        tietTaskName = findViewById(R.id.tietTaskName);
        tilTaskDescription = findViewById(R.id.tilTaskDescription);
        tietTaskDescription = findViewById(R.id.tietTaskDescription);
        tvSelectTaskPriority = findViewById(R.id.tvSelectTaskPriority);
        tvSelectTaskDueDate = findViewById(R.id.tvSelectTaskDueDate);
        cardViewAddTaskCategories = findViewById(R.id.cardViewAddTaskCategories);
        tvSelectTaskCategory = findViewById(R.id.tvSelectTaskCategory);
        recyclerViewSelectTaskCategory = findViewById(R.id.recyclerViewSelectTaskCategory);
        tvAddTaskColor = findViewById(R.id.tvAddTaskColor);
        taskColor = getResources().getColor(R.color.main_transparent_white);
        tvAddTaskFinish = findViewById(R.id.tvAddTaskFinish);
        tvAddTaskCancel = findViewById(R.id.tvAddTaskCancel);

        taskPriority = 5;
        recyclerViewCategoriesLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewCategoriesAdapter = new CategoriesRecyclerViewAdapter(categories, getApplicationContext(), this);
        dividerItemDecoration = new DividerItemDecoration(recyclerViewSelectTaskCategory.getContext(), DividerItemDecoration.VERTICAL);
        selectTaskPriorityDialog = new AlertDialog.Builder(AddTaskActivity.this,
                android.app.AlertDialog.THEME_DEVICE_DEFAULT_DARK)
                .setTitle("Priority: ")
                .setSingleChoiceItems(R.array.taskPriorities, -1,
                        (dialogInterface, i) -> {
                            taskPriority = i + 1;
                            tvSelectTaskPriority.setText("Priority: ".concat(String.valueOf(i + 1)));
                            selectTaskPriorityDialog.dismiss();
                        })
                .create();
        confirmCancelDialog = new AlertDialog.Builder(AddTaskActivity.this)
                .setTitle("Are you sure you want to cancel?")
                .setMessage("Your inputs will be deleted")
                .setPositiveButton("Yes", (dialogInterface, i) -> {
                    finish();
                })
                .setNegativeButton("No", (dialogInterface, i) -> {
                }).create();

        if (taskReceivedFromMainActivity != null) {
            tietTaskName.setText(taskReceivedFromMainActivity.getName());
            if (taskReceivedFromMainActivity.getDescription() != null) {
                tietTaskDescription.setText(taskReceivedFromMainActivity.getDescription());
            }
            taskPriority = taskReceivedFromMainActivity.getPriority();
            tvSelectTaskPriority.setText("Priority: ".concat(taskPriority.toString()));
            taskDueDate = Instant.ofEpochSecond(taskReceivedFromMainActivity.getDueDate())
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
            tvSelectTaskDueDate.setText("Due date: ".concat(LocalDateTimeConverter.getStringFromDate(taskDueDate)));
            taskCategory = taskReceivedFromMainActivity.getCategory();
            if (taskCategory != null) {
                tvSelectTaskCategory.setText("Category: ".concat(taskCategory));
            }
            taskColor = taskReceivedFromMainActivity.getColor();
            Drawable customDrawable = ContextCompat.getDrawable(AddTaskActivity.this,
                    R.drawable.round_corners_30)
                    .getConstantState()
                    .newDrawable()
                    .mutate();
            customDrawable.setTint(taskColor);
            tvAddTaskColor.setBackground(customDrawable);
            currentTaskNotificationId = taskReceivedFromMainActivity.getNotificationId();
        }

        configureComponents();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void configureComponents() {
        tvSelectTaskPriority.setOnClickListener(view -> selectTaskPriorityDialog.show());
        tvSelectTaskDueDate.setOnClickListener(view -> showDatePickerDialog());
        tvSelectTaskCategory.setOnClickListener(view -> {
            if (recyclerViewSelectTaskCategory.getVisibility() == View.GONE) {
                TransitionManager.beginDelayedTransition(cardViewAddTaskCategories);
                recyclerViewSelectTaskCategory.setVisibility(View.VISIBLE);
            } else {
                recyclerViewSelectTaskCategory.setVisibility(View.GONE);
            }
        });
        recyclerViewSelectTaskCategory.setHasFixedSize(true);
        recyclerViewSelectTaskCategory.setLayoutManager(recyclerViewCategoriesLayoutManager);
        recyclerViewSelectTaskCategory.setAdapter(recyclerViewCategoriesAdapter);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(AddTaskActivity.this, R.drawable.custom_recycle_view_divider));
        recyclerViewSelectTaskCategory.addItemDecoration(dividerItemDecoration);
        tvAddTaskColor.setOnClickListener(view -> showColorPickerDialog());
        openMainActivity = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                });

        tvAddTaskFinish.setOnClickListener(view -> {
            DatabaseReference loggedInUserDbPath = FirebaseDatabase.getInstance("https://rotaru-teodor-gabriel-projfb-default-rtdb.europe-west1.firebasedatabase.app/")
                    .getReference("Users")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

            if (areInputsValid()) {
                loggedInUserDbPath.child("taskStats")
                        .child("nrOfAllTasks")
                        .setValue(++nrOfAllTasks);
                DatabaseReference tasksFirebasePath;
                Task newTask;

                String taskDescription = tietTaskDescription.getText() == null ||
                        tietTaskDescription.getText().length() == 0 ?
                        null : tietTaskDescription.getText().toString().trim();

                if (taskReceivedFromMainActivity == null) { // add
                    tasksFirebasePath = loggedInUserDbPath.child("tasks").push();
                    newTask = new Task(
                            tietTaskName.getText().toString().trim(),
                            taskDescription,
                            taskDueDate.atZone(ZoneId.systemDefault()).toEpochSecond(),
                            taskPriority,
                            taskCategory,
                            tasksFirebasePath.getKey(),
                            taskColor,
                            currentTaskNotificationId);
                } else { // edit
                    tasksFirebasePath = loggedInUserDbPath.child("tasks")
                            .child(taskReceivedFromMainActivity.getFirebaseId());
                    newTask = new Task(
                            tietTaskName.getText().toString().trim(),
                            taskDescription,
                            taskDueDate.atZone(ZoneId.systemDefault()).toEpochSecond(),
                            taskPriority,
                            taskCategory,
                            taskReceivedFromMainActivity.getFirebaseId(),
                            taskColor,
                            currentTaskNotificationId);
                }

                tasksFirebasePath.setValue(newTask)
                        .addOnCompleteListener(firebaseTask -> {
                            if (firebaseTask.isSuccessful()) {
                                Toast.makeText(AddTaskActivity.this, "Task added", Toast.LENGTH_SHORT).show();
                                configureTaskNotification();
                            } else {
                                Toast.makeText(AddTaskActivity.this, "Failed to add task", Toast.LENGTH_LONG).show();
                            }
                            launchMainActivity();
                            finish();
                        });
            }
        });

        tvAddTaskCancel.setOnClickListener(view -> confirmCancelDialog.show());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void configureTaskNotification() {
        Calendar calendarDate = Calendar.getInstance();
        calendarDate.set(taskDueDate.getYear(),
                taskDueDate.getMonthValue() - 1,
                taskDueDate.getDayOfMonth(),
                taskDueDate.getHour() == 0 ? 23 : taskDueDate.getHour() - 1,
                taskDueDate.getMinute(),
                0);

        String title = "Reminder!";
        String message = "Task "
                .concat(tietTaskName.getText().toString().trim())
                .concat(" is due in 1 hour");

        NotificationsUtils.setUpTaskNotification(calendarDate,
                title,
                message,
                currentTaskNotificationId,
                AddTaskActivity.this);
    }

    private void showColorPickerDialog() {
        ColorPicker colorPicker = new ColorPicker(AddTaskActivity.this);
        ArrayList<String> colorsForColorPicker = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.tasks_and_habits_colors)));

        colorPicker.setColors(colorsForColorPicker)
                .setColumns(5)
                .setRoundColorButton(true)
                .disableDefaultButtons(true)
                .setOnFastChooseColorListener(new ColorPicker.OnFastChooseColorListener() {
                    @Override
                    public void setOnFastChooseColorListener(int position, int color) {
                        taskColor = color;
                        Drawable customDrawable = ContextCompat.getDrawable(AddTaskActivity.this,
                                R.drawable.round_corners_30)
                                .getConstantState()
                                .newDrawable()
                                .mutate();
                        customDrawable.setTint(taskColor);
                        tvAddTaskColor.setBackground(customDrawable);
                    }

                    @Override
                    public void onCancel() {
                    }
                }).show();
    }


    private void launchMainActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        openMainActivity.launch(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                AddTaskActivity.this,
                android.app.AlertDialog.THEME_DEVICE_DEFAULT_DARK,
                this,
                LocalDate.now().getYear(),
                LocalDate.now().getMonthValue() - 1,
                LocalDate.now().getDayOfMonth()
        );

        datePickerDialog.getDatePicker().setMinDate(new Date().getTime());
        datePickerDialog.show();
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        LocalDateTime today = LocalDateTime.now();
        taskDueDate = LocalDateTime.of(year, month + 1, day, today.getHour() + 1, today.getMinute());
        tvSelectTaskDueDate.setText("Due date: ".concat(LocalDateTimeConverter.getStringFromDate(taskDueDate)));
        TimePickerDialog timePickerDialog = new TimePickerDialog(AddTaskActivity.this,
                android.app.AlertDialog.THEME_DEVICE_DEFAULT_DARK,
                AddTaskActivity.this,
                today.getHour() + 1,
                today.getMinute(),
                true);
        timePickerDialog.show();
    }

    @Override
    public void onBackPressed() {
        confirmCancelDialog.show();
    }

    private boolean areInputsValid() {
        if (!isTaskNameValid()) {
            setTaskNameError();
            return false;
        }

        if (taskDueDate == null) {
            setTaskDueDateError();
            return false;
        }

        return true;
    }

    private boolean isTaskNameValid() {
        if (tietTaskName.getText() == null ||
                tietTaskName.getText().toString().trim().length() < 2) {
            return false;
        }
        return true;
    }

    private void setTaskNameError() {
        tietTaskName.setError("Task must have at least one character!");
        tietTaskName.requestFocus();
    }

    private void setTaskDueDateError() {
        tvSelectTaskDueDate.setError("Please select a due date!");
        tvSelectTaskDueDate.requestFocus();
    }

    @Override
    public void recyclerViewListClicked(View v, int position) {
        tvSelectTaskCategory.setText("Category: ".concat(categories.get(position)));
        taskCategory = categories.get(position);
        recyclerViewSelectTaskCategory.setVisibility(View.GONE);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
        taskDueDate = LocalDateTime.of(taskDueDate.getYear(),
                taskDueDate.getMonthValue(),
                taskDueDate.getDayOfMonth(),
                hour,
                minute);
        tvSelectTaskDueDate.setText("Due date: ".concat(LocalDateTimeConverter.getStringFromDate(taskDueDate)));
    }
}