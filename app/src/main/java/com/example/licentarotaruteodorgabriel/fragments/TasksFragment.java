package com.example.licentarotaruteodorgabriel.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.licentarotaruteodorgabriel.R;
import com.example.licentarotaruteodorgabriel.activities.AddTaskActivity;
import com.example.licentarotaruteodorgabriel.activities.LoginActivity;
import com.example.licentarotaruteodorgabriel.activities.TasksStatisticsActivity;
import com.example.licentarotaruteodorgabriel.classes.Task;
import com.example.licentarotaruteodorgabriel.classes.TasksStats;
import com.example.licentarotaruteodorgabriel.date_utils.LocalDateTimeConverter;
import com.example.licentarotaruteodorgabriel.interfaces.RecyclerViewClickListener;
import com.example.licentarotaruteodorgabriel.list_adaptors.TasksRecyclerViewAdapter;
import com.example.licentarotaruteodorgabriel.notifications.NotificationIdManager;
import com.example.licentarotaruteodorgabriel.notifications.NotificationsUtils;
import com.example.licentarotaruteodorgabriel.tasks_statistics.utils.Calculations;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;


public class TasksFragment extends Fragment implements RecyclerViewClickListener {

    private TextView tvTasksSort;
    private ImageButton btnTasksOptionsMenu;

    private RecyclerView recyclerViewTasks;
    private RecyclerView.Adapter recyclerViewTasksAdapter;
    private RecyclerView.LayoutManager recyclerViewTasksLayoutManager;
    private DividerItemDecoration dividerItemDecoration;
    private ImageView imgViewNoTasks;

    private FloatingActionButton btnAddTask;

    private String sortCriteria;
    private String[] sortCriterias;
    private AlertDialog sortCriteriasDialog;
    private AlertDialog.Builder taskIndividualOptionsDialogBuilder;

    private ArrayList<Task> tasks = new ArrayList<>();
    private ArrayList<String> categories = new ArrayList<>();
    private TasksStats tasksStats;

    private ActivityResultLauncher<Intent> openAddTaskActivity;
    private ActivityResultLauncher<Intent> openMainActivity;
    private ActivityResultLauncher<Intent> openTasksStatisticsActivity;
    private ActivityResultLauncher<Intent> openLoginActivity;
    private DatabaseReference currentLoggedInUserTasksDbPath;
    private DatabaseReference currentLoggedInUserDbPath;

    public TasksFragment() {
    }

    public static TasksFragment newInstance() {
        TasksFragment fragment = new TasksFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeNonGraphicalComponents();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initializeNonGraphicalComponents() {
        tasks = getArguments().getParcelableArrayList("TASKS_FROM_MAIN_ACTIVITY_TO_TASKS_FRAGMENT");
        categories = getArguments().getStringArrayList("CATEGORIES_FROM_MAIN_ACTIVITY_TO_TASKS_FRAGMENT");
        tasks.sort(Comparator.comparing(Task::getDueDate));
        tasksStats = getArguments().getParcelable("TASK_STATS_FROM_MAIN_ACTIVITY_TO_TASKS_FRAGMENT");

        currentLoggedInUserDbPath = FirebaseDatabase.getInstance("https://rotaru-teodor-gabriel-projfb-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        currentLoggedInUserTasksDbPath = currentLoggedInUserDbPath.child("tasks");

        openAddTaskActivity = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                });

        openMainActivity = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                });

        openTasksStatisticsActivity = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                });

        openLoginActivity = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                });

        recyclerViewTasksLayoutManager = new LinearLayoutManager(this.getContext());
        recyclerViewTasksAdapter = new TasksRecyclerViewAdapter(tasks, this.getContext(), this);
        sortCriterias = getActivity().getResources().getStringArray(R.array.sortTasksCriterias);

        configureNonGraphicalComponents();
    }

    private void configureNonGraphicalComponents() {
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tasks, container, false);
        initializeGraphicalComponents(v);
        return v;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initializeGraphicalComponents(View v) {
        tvTasksSort = v.findViewById(R.id.tvTasksSort);
        btnTasksOptionsMenu = v.findViewById(R.id.btnTasksOptionsMenu);
        btnAddTask = v.findViewById(R.id.btnAddTask);

        recyclerViewTasks = v.findViewById(R.id.recyclerViewTasks);
        dividerItemDecoration = new DividerItemDecoration(recyclerViewTasks.getContext(), DividerItemDecoration.VERTICAL);
        imgViewNoTasks = v.findViewById(R.id.imgViewNoTasks);

        sortCriteriasDialog = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_DEVICE_DEFAULT_DARK)
                .setTitle("Sort by: ")
                .setSingleChoiceItems(R.array.sortTasksCriterias, -1,
                        (dialogInterface, i) -> {
                            sortCriteria = sortCriterias[i];
                            tvTasksSort.setText("Sort by: ".concat(sortCriteria));
                            sortTasks();
                            updateTasksRecyclerView();
                            sortCriteriasDialog.dismiss();
                        })
                .create();

        taskIndividualOptionsDialogBuilder = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_DEVICE_DEFAULT_DARK)
                .setTitle("Options");

        configureGraphicalComponents(v);
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void configureGraphicalComponents(View v) {
        tvTasksSort.setOnClickListener(view -> sortCriteriasDialog.show());
        btnTasksOptionsMenu.setOnClickListener(view -> showOptionsMenu(btnTasksOptionsMenu));
        recyclerViewTasks.setHasFixedSize(true);
        recyclerViewTasks.setLayoutManager(recyclerViewTasksLayoutManager);
        recyclerViewTasks.setAdapter(recyclerViewTasksAdapter);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(this.getContext(), R.drawable.custom_recycle_view_divider));
        recyclerViewTasks.addItemDecoration(dividerItemDecoration);
        btnAddTask.setOnClickListener(view -> launchAddTaskActivity(null));

        updateTasksRecyclerView();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void sortTasks() {
        try {
            if (sortCriteria.equals(sortCriterias[0])) {
                tasks.sort(Comparator.comparing(Task::getDueDate));
            } else if (sortCriteria.equals(sortCriterias[1])) {
                tasks.sort(Comparator.comparing(Task::getPriority).reversed());
            } else if (sortCriteria.equals(sortCriterias[2])) {
                tasks.sort(Comparator.comparing(Task::getName));
            }
        } catch (Exception exception) {
            Toast.makeText(getContext(), "Couldn't sort by selected criteria...", Toast.LENGTH_LONG).show();
        }
    }

    private void updateTasksRecyclerView() {
        if (tasks.size() > 0) {
            recyclerViewTasks.setVisibility(View.VISIBLE);
            imgViewNoTasks.setVisibility(View.GONE);
            ((TasksRecyclerViewAdapter) recyclerViewTasksAdapter).setTasks(tasks);
            recyclerViewTasksAdapter.notifyDataSetChanged();
        } else {
            recyclerViewTasks.setVisibility(View.GONE);
            imgViewNoTasks.setVisibility(View.VISIBLE);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void showOptionsMenu(View v) {
        PopupMenu popup = new PopupMenu(getContext(), v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.tasks_drop_down_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(menuItem -> {
            int menuItemId = menuItem.getItemId();
            if (menuItemId == R.id.tasksStatisticsDropDownMenuItem) {
                Intent intent = new Intent(getContext(), TasksStatisticsActivity.class);
                intent.putExtra("TASK_STATS_FROM_TASKS_FRAGMENT_TO_TASKS_STATISTICS_ACTIVITY", tasksStats);
                intent.putParcelableArrayListExtra("TASKS_FROM_TASKS_FRAGMENT_TO_TASKS_STATISTICS_ACTIVITY", tasks);
                openTasksStatisticsActivity.launch(intent);
                return true;
            } else if (menuItemId == R.id.tasksSignOutDropDownMenuItem) {
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
        Task selectedTask = tasks.get(clickedRecyclerItemPosition);
        if (clickedView.getId() == R.id.btnIndividualTaskOptionsMenu || clickedView.getId() == R.id.tvTaskDueDate) {
            TextView tvTaskDueDate = recyclerViewTasksLayoutManager.findViewByPosition(clickedRecyclerItemPosition).findViewById(R.id.tvTaskDueDate);
            if (selectedTask.getIsDone()) {
                displayTaskIndividualOptionsAlertDialog(selectedTask, tvTaskDueDate, R.array.doneTaskOptions);
            } else {
                displayTaskIndividualOptionsAlertDialog(selectedTask, tvTaskDueDate, R.array.ongoingTaskOptions);
            }
        } else {
            // row clicked
            StringBuilder stringBuilderTaskDetails = new StringBuilder();
            if (selectedTask.getDescription() != null && selectedTask.getDescription().length() > 0) {
                stringBuilderTaskDetails.append("Description: ")
                        .append(selectedTask.getDescription())
                        .append("\n");
            }
            if (selectedTask.getCategory() != null) {
                stringBuilderTaskDetails.append("Category: ")
                        .append(selectedTask.getCategory())
                        .append("\n");
            }
            stringBuilderTaskDetails.append("Due date: ")
                    .append(LocalDateTimeConverter.getStringFromDate(selectedTask.getDueDate()))
                    .append("\n");

            stringBuilderTaskDetails.append("Priority: ")
                    .append(selectedTask.getPriority())
                    .append("\n");

            if (selectedTask.getIsDone()) {
                stringBuilderTaskDetails.append("Completed on: ")
                        .append(LocalDateTimeConverter.getStringFromDate(selectedTask.getCompletionDate()));
            }

            AlertDialog taskDetailsDialog = new AlertDialog.Builder(getActivity(),
                    AlertDialog.THEME_DEVICE_DEFAULT_DARK)
                    .setTitle(selectedTask.getName())
                    .setMessage(stringBuilderTaskDetails)
                    .create();
            taskDetailsDialog.getWindow()
                    .setBackgroundDrawable(ContextCompat.getDrawable(getContext(),
                            R.drawable.round_corners_30));
            taskDetailsDialog.setOnDismissListener(dialogInterface -> {
                recyclerViewTasks.setVisibility(View.VISIBLE);
            });
            taskDetailsDialog.show();
            recyclerViewTasks.setVisibility(View.GONE);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void displayTaskIndividualOptionsAlertDialog(Task selectedTask, TextView tvTaskDueDate, int idResourceArray) {
        taskIndividualOptionsDialogBuilder.setItems(idResourceArray,
                (dialogInterface, i) -> {
                    if (i == 0) { // edit
                        launchAddTaskActivity(selectedTask);
                    } else if (i == 1) { // mark as done/ongoing
                        if (idResourceArray == R.array.doneTaskOptions) {
                            markAsOngoingIndividualOptionClicked(selectedTask, tvTaskDueDate);
                        } else {
                            markAsDoneIndividualOptionClicked(selectedTask, tvTaskDueDate);
                        }
                    } else if (i == 2) { // DELETE
                        deleteIndividualOptionClicked(selectedTask);
                    } else if (i == 3) { // MARK AS DONE AND DELETE
                        if (!selectedTask.getIsDone()) {
                            markAsDoneIndividualOptionClicked(selectedTask, tvTaskDueDate);
                        }
                        deleteIndividualOptionClicked(selectedTask);
                    }
                    dialogInterface.dismiss();
                })
                .create().show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void markAsOngoingIndividualOptionClicked(Task selectedTask, TextView tvTaskDueDate) {
        DatabaseReference selectedTaskDbPath = currentLoggedInUserTasksDbPath
                .child(selectedTask.getFirebaseId());

        selectedTask.setIsDone(false);
        selectedTaskDbPath.child("isDone")
                .setValue(false);

        updateTasksRecyclerView();

        Long taskDueDate = selectedTask.getDueDate();
        long currentDate = LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond();
        StringBuilder stringBuilderDue = new StringBuilder().append("Due ");

        long difference;
        if (taskDueDate > currentDate) {
            difference = taskDueDate - currentDate;
            stringBuilderDue.append("in: ");
            tvTaskDueDate.setTextColor(Color.WHITE);
        } else {
            difference = currentDate - taskDueDate;
            stringBuilderDue.append("by: ");
            tvTaskDueDate.setTextColor(Color.RED);
        }
        int days = (int) (difference / 86400);
        int hours = (int) (difference / 3600);
        stringBuilderDue.append(days >= 1 ? String.valueOf(days).concat("d ") : "")
                .append(hours >= 1 ? String.valueOf(hours - days * 24).concat("h") : "less than 1h");
        tvTaskDueDate.setText(stringBuilderDue);

        tasksStats.subtractFromNrOfAllTasksCompleted(1);
        if (selectedTask.getCompletionDate() < selectedTask.getDueDate()) {
            tasksStats.subtractFromNrOfAllTasksCompletedBeforeDue(1);
        } else {
            tasksStats.subtractFromNrOfAllTasksCompletedAfterDue(1);
        }
        tasksStats.subtractFromTotalDeviation(Calculations.getTaskDeviationScore(selectedTask));
        selectedTask.setCompletionDate(null);
        selectedTaskDbPath.child("completionDate").setValue(null);
        currentLoggedInUserDbPath.child("taskStats").setValue(tasksStats);
        configureTaskNotification(selectedTask);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void markAsDoneIndividualOptionClicked(Task selectedTask, TextView tvTaskDueDate) {
        selectedTask.setIsDone(true);
        DatabaseReference selectedTaskDbPath = currentLoggedInUserTasksDbPath
                .child(selectedTask.getFirebaseId());
        selectedTaskDbPath.child("isDone")
                .setValue(true);

        Long completionDate = LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond();
        selectedTask.setCompletionDate(completionDate);
        selectedTaskDbPath.child("completionDate")
                .setValue(completionDate);

        tvTaskDueDate.setText("Done");
        tvTaskDueDate.setTextColor(Color.GREEN);
        updateTasksRecyclerView();

        tasksStats.addToNrOfAllTasksCompleted(1);
        if (completionDate < selectedTask.getDueDate()) {
            tasksStats.addToNrOfAllTasksCompletedBeforeDue(1);
        } else {
            tasksStats.addToNrOfAllTasksCompletedAfterDue(1);
        }
        tasksStats.addToTotalDeviation(Calculations.getTaskDeviationScore(selectedTask));
        currentLoggedInUserDbPath.child("taskStats").setValue(tasksStats);
        NotificationsUtils.removeNotification(selectedTask.getNotificationId(), getContext());
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void deleteIndividualOptionClicked(Task selectedTask) {
        new AlertDialog.Builder(getActivity(), AlertDialog.THEME_DEVICE_DEFAULT_DARK)
                .setTitle("Are you sure you want to delete this task?")
                .setPositiveButton("Yes", (dialog, index) -> {
                    tasks.remove(selectedTask);
                    updateTasksRecyclerView();
                    currentLoggedInUserTasksDbPath
                            .child(selectedTask.getFirebaseId())
                            .removeValue();
                    NotificationsUtils.removeNotification(selectedTask.getNotificationId(), getContext());
                })
                .setNegativeButton("No", (dialog, index) -> {
                }).create().show();
    }

    @Override
    public void onLongPositionClicked(View clickedView, int clickedRecyclerItemPosition) {

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void configureTaskNotification(Task selectedTask) {
        LocalDateTime taskDueDate = LocalDateTime.ofInstant(
                Instant.ofEpochSecond(selectedTask.getDueDate()),
                ZoneId.systemDefault());

        Calendar calendarDate = Calendar.getInstance();
        calendarDate.set(taskDueDate.getYear(),
                taskDueDate.getMonthValue() - 1,
                taskDueDate.getDayOfMonth(),
                taskDueDate.getHour() == 0 ? 23 : taskDueDate.getHour() - 1,
                taskDueDate.getMinute(),
                0);

        String title = "Reminder!";
        String message = "Task "
                .concat(selectedTask.getName())
                .concat(" is due in 1 hour");

        NotificationsUtils.setUpTaskNotification(calendarDate,
                title,
                message,
                selectedTask.getNotificationId(),
                getContext());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void launchAddTaskActivity(Task taskToBeEdited) {
        Intent intent = new Intent(getContext(), AddTaskActivity.class);
        intent.putExtra("TASK_TO_BE_EDITED", taskToBeEdited);
        intent.putStringArrayListExtra("CATEGORIES_FROM_TASKS_FRAGMENT", categories);
        intent.putExtra("NR_OF_ALL_TASKS_FROM_TASKS_FRAGMENT_TO_ADD_TASK_ACTIVITY", tasksStats.getNrOfAllTasks());
        int x = NotificationIdManager.getId(tasks);
        Log.e("DEBUGSESHMISC", String.valueOf(x));
        intent.putExtra("TASK_NOTIFICATION_ID_FROM_TASKS_FRAGMENT_TO_ADD_TASK_ACTIVITY", x);
        openAddTaskActivity.launch(intent);
    }
}