package com.example.licentarotaruteodorgabriel.activities;

import android.content.ComponentName;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.agrawalsuneet.dotsloader.loaders.LightsLoader;
import com.example.licentarotaruteodorgabriel.interfaces.FirebaseDataCallback;
import com.example.licentarotaruteodorgabriel.R;
import com.example.licentarotaruteodorgabriel.classes.Habit;
import com.example.licentarotaruteodorgabriel.classes.Task;
import com.example.licentarotaruteodorgabriel.classes.TasksStats;
import com.example.licentarotaruteodorgabriel.classes.User;
import com.example.licentarotaruteodorgabriel.fragments.HabitsFragment;
import com.example.licentarotaruteodorgabriel.fragments.ProfileFragment;
import com.example.licentarotaruteodorgabriel.fragments.TasksFragment;
import com.example.licentarotaruteodorgabriel.notifications.NotificationsUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private FrameLayout mainActivityFrameLayout;
    private BottomNavigationView mainBottomNavigationView;
    private LightsLoader lightsLoaderMainActivity;

    private User loggedInUser;
    private ArrayList<Habit> habits;
    private ArrayList<Task> tasks;
    private TasksStats tasksStats;
    DatabaseReference loggedInUserDbPath;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeComponents();
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initializeComponents() {
        mainActivityFrameLayout = findViewById(R.id.mainActivityFrameLayout);
        mainBottomNavigationView = findViewById(R.id.mainBottomNavigationView);
        lightsLoaderMainActivity = findViewById(R.id.lightsLoaderMainActivity);
        loggedInUserDbPath = FirebaseDatabase.getInstance("https://rotaru-teodor-gabriel-projfb-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        readAllUserData(new FirebaseDataCallback() {
            @Override
            public void onComplete() {
                configureComponents();
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void configureComponents() {
        lightsLoaderMainActivity.setVisibility(View.GONE);
        mainBottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.habitsBotNavMenuItem) {
                setFragment(getHabitsFragment());
            } else if (item.getItemId() == R.id.tasksBotNavMenuItem) {
                setFragment(getTasksFragment());
            } else if (item.getItemId() == R.id.profileBotNavMenuItem) {
                setFragment(getProfileFragment());
            }
            return true;
        });

        setAppropriateStartupFragment();
        NotificationsUtils.setUpNotificationChannel(MainActivity.this);
        setDailyNotifications();
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setDailyNotifications() {
        NotificationsUtils.setUpDailyNotification(
                getCalendar(loggedInUser.getWakeupHour(), loggedInUser.getWakeupMinute()),
                0,
                MainActivity.this);

        NotificationsUtils.setUpDailyNotification(
                getCalendar(loggedInUser.getSleepHour(), loggedInUser.getSleepMinute()),
                1,
                MainActivity.this);
    }

    private Calendar getCalendar(int hourOfDay, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        return calendar;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setAppropriateStartupFragment() {
        ComponentName parentActivityLauncher = getCallingActivity();
        if (parentActivityLauncher != null &&
                parentActivityLauncher.getClassName().equals(AddTaskActivity.class.getName())) {
            setFragment(getTasksFragment());
            mainBottomNavigationView.setSelectedItemId(R.id.tasksBotNavMenuItem);
        } else if (parentActivityLauncher != null &&
                parentActivityLauncher.getClassName().equals(CreateAccountActivity.class.getName())) {
            setFragment(getProfileFragment());
            mainBottomNavigationView.setSelectedItemId(R.id.profileBotNavMenuItem);
        } else {
            setFragment(getHabitsFragment());
            mainBottomNavigationView.setSelectedItemId(R.id.habitsBotNavMenuItem);
        }
    }

    public void readAllUserData(FirebaseDataCallback callback) {
        lightsLoaderMainActivity.setVisibility(View.VISIBLE);
        loggedInUserDbPath.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                loggedInUser = snapshot.getValue(User.class);
                readHabitsData(callback);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Failed to retrieve user data", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void readHabitsData(FirebaseDataCallback callback) {
        loggedInUserDbPath.child("habits")
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                habits = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Habit habit = dataSnapshot.getValue(Habit.class);
                    if (habit.getRealizations() == null) {
                        habit.setRealizations(new HashMap<>());
                    }
                    habits.add(habit);
                }
                readTaskStatsData(callback);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Failed to retrieve habits", Toast.LENGTH_LONG).show();
            }
        });
    }


    public void readTaskStatsData(FirebaseDataCallback callback) {
        loggedInUserDbPath.child("taskStats")
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tasksStats = snapshot.getValue(TasksStats.class);
                if (tasksStats == null) {
                    tasksStats = new TasksStats();
                }
                readTasksData(callback);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Failed to retrieve task stats", Toast.LENGTH_LONG).show();
            }
        });
    }


    public void readTasksData(FirebaseDataCallback callback) {
        loggedInUserDbPath.child("tasks")
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tasks = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Task task = dataSnapshot.getValue(Task.class);
                    tasks.add(task);
                }
                callback.onComplete();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Failed to retrieve tasks", Toast.LENGTH_LONG).show();
            }
        });
    }


    private void setFragment(Fragment fragmentToSet) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(mainActivityFrameLayout.getId(), fragmentToSet)
                .commit();
    }

    public HabitsFragment getHabitsFragment() {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("HABITS_FROM_MAIN_ACTIVITY_TO_HABITS_FRAGMENT", habits);
        bundle.putStringArrayList("CATEGORIES_FROM_MAIN_ACTIVITY_TO_HABITS_FRAGMENT", loggedInUser.getCategories());
        HabitsFragment habitsFragment = new HabitsFragment();
        habitsFragment.setArguments(bundle);
        return habitsFragment;
    }

    public TasksFragment getTasksFragment() {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("TASKS_FROM_MAIN_ACTIVITY_TO_TASKS_FRAGMENT", tasks);
        bundle.putStringArrayList("CATEGORIES_FROM_MAIN_ACTIVITY_TO_TASKS_FRAGMENT", loggedInUser.getCategories());
        bundle.putParcelable("TASK_STATS_FROM_MAIN_ACTIVITY_TO_TASKS_FRAGMENT", tasksStats);
        TasksFragment tasksFragment = new TasksFragment();
        tasksFragment.setArguments(bundle);
        return tasksFragment;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public ProfileFragment getProfileFragment() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("USER_FROM_MAIN_ACTIVITY_TO_PROFILE_FRAGMENT", loggedInUser);
        int nrOfActiveHabits = (int) habits.stream().filter(h -> !h.getArchived()).count();
        int nrOfPendingTasks = (int) tasks.stream().filter(t -> !t.getIsDone()).count();
        bundle.putInt("NR_HABITS_FROM_MAIN_ACTIVITY_TO_PROFILE_FRAGMENT", nrOfActiveHabits);
        bundle.putInt("NR_TASKS_MAIN_ACTIVITY_TO_PROFILE_FRAGMENT", nrOfPendingTasks);
        ProfileFragment profileFragment = new ProfileFragment();
        profileFragment.setArguments(bundle);
        return profileFragment;
    }
}


