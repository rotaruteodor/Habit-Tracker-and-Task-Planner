package com.example.licentarotaruteodorgabriel.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.example.licentarotaruteodorgabriel.activities.CreateAccountActivity;
import com.example.licentarotaruteodorgabriel.activities.LoginActivity;
import com.example.licentarotaruteodorgabriel.activities.ResetPasswordActivity;
import com.example.licentarotaruteodorgabriel.classes.User;
import com.example.licentarotaruteodorgabriel.data.DefaultCategoriesReader;
import com.example.licentarotaruteodorgabriel.interfaces.CategoriesRecyclerViewClickListener;
import com.example.licentarotaruteodorgabriel.list_adaptors.CategoriesRecyclerViewAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


public class ProfileFragment extends Fragment implements CategoriesRecyclerViewClickListener {

    private TextView tvUserNameAndAge;
    private TextView tvEditProfileInformation;
    private TextView tvProfileChangePassword;
    private TextView tvProfileSignOut;
    private TextView tvProfileActiveHabits;
    private TextView tvProfileActiveTasks;
    private TextView tvProfileCategories;
    private RecyclerView recyclerViewProfileCategories;
    private ImageView imgViewNoCategories;
    private TextInputLayout tilAddCategoryName;
    private TextInputEditText tietAddCategoryName;
    private FloatingActionButton btnAddCategory;


    private RecyclerView.Adapter recyclerViewCategoriesAdapter;
    private RecyclerView.LayoutManager recyclerViewCategoriesLayoutManager;
    private DividerItemDecoration dividerItemDecoration;

    private User loggedInUser;
    private int nrOfActiveHabits;
    private int nrOfPendingTasks;
    private ArrayList<String> categories;

    private ActivityResultLauncher<Intent> openCreateAccountActivity;
    private ActivityResultLauncher<Intent> openLoginActivity;
    private ActivityResultLauncher<Intent> openResetPasswordActivity;
    private DatabaseReference categoriesDbPath;

    public ProfileFragment() {
    }

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeNonGraphicalComponents();
    }

    private void initializeNonGraphicalComponents() {
        loggedInUser = getArguments().getParcelable("USER_FROM_MAIN_ACTIVITY_TO_PROFILE_FRAGMENT");
        nrOfActiveHabits = getArguments().getInt("NR_HABITS_FROM_MAIN_ACTIVITY_TO_PROFILE_FRAGMENT");
        nrOfPendingTasks = getArguments().getInt("NR_TASKS_MAIN_ACTIVITY_TO_PROFILE_FRAGMENT");
        categories = loggedInUser.getCategories();

        categoriesDbPath = FirebaseDatabase.getInstance("https://rotaru-teodor-gabriel-projfb-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("categories");

        recyclerViewCategoriesLayoutManager = new LinearLayoutManager(this.getContext());
        recyclerViewCategoriesAdapter = new CategoriesRecyclerViewAdapter(categories, this.getContext(), this);

        openCreateAccountActivity = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                });


        openLoginActivity = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                });


        openResetPasswordActivity = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                });


        configureNonGraphicalComponents();
    }

    private void configureNonGraphicalComponents() {

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        initializeGraphicalComponents(v);
        return v;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initializeGraphicalComponents(View v) {
        tvUserNameAndAge = v.findViewById(R.id.tvUserNameAndAge);
        tvEditProfileInformation = v.findViewById(R.id.tvEditProfileInformation);
        tvProfileChangePassword = v.findViewById(R.id.tvProfileChangePassword);
        tvProfileSignOut = v.findViewById(R.id.tvProfileSignOut);
        tvProfileActiveHabits = v.findViewById(R.id.tvProfileActiveHabits);
        tvProfileActiveTasks = v.findViewById(R.id.tvProfileActiveTasks);
        tvProfileCategories = v.findViewById(R.id.tvProfileCategories);
        recyclerViewProfileCategories = v.findViewById(R.id.recyclerViewProfileCategories);
        imgViewNoCategories = v.findViewById(R.id.imgViewNoCategories);
        tilAddCategoryName = v.findViewById(R.id.tilAddCategoryName);
        tietAddCategoryName = v.findViewById(R.id.tietAddCategoryName);
        btnAddCategory = v.findViewById(R.id.btnAddCategory);

        dividerItemDecoration = new DividerItemDecoration(recyclerViewProfileCategories.getContext(), DividerItemDecoration.VERTICAL);
        recyclerViewProfileCategories.setHasFixedSize(true);
        recyclerViewProfileCategories.setLayoutManager(recyclerViewCategoriesLayoutManager);
        recyclerViewProfileCategories.setAdapter(recyclerViewCategoriesAdapter);
        recyclerViewProfileCategories.addItemDecoration(dividerItemDecoration);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(this.getContext(), R.drawable.custom_recycle_view_divider));
        configureGraphicalComponents(v);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void configureGraphicalComponents(View v) {
        StringBuilder sbUserNameAndAge = new StringBuilder().append(loggedInUser.getFirstname())
                .append(" ")
                .append(loggedInUser.getLastname())
                .append(", ")
                .append(loggedInUser.obtainAgeInYears());

        tvUserNameAndAge.setText(sbUserNameAndAge);

        tvEditProfileInformation.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), CreateAccountActivity.class);
            intent.putExtra("USER_FOR_EDITING", loggedInUser);
            openCreateAccountActivity.launch(intent);
        });

        tvProfileChangePassword.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), ResetPasswordActivity.class);
            openResetPasswordActivity.launch(intent);
        });

        tvProfileSignOut.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getContext(), LoginActivity.class);
            openLoginActivity.launch(intent);
            if(getActivity() != null){
                getActivity().finish();
            }
        });

        tvProfileActiveHabits.setText("Active habits: ".concat(String.valueOf(nrOfActiveHabits)));
        tvProfileActiveTasks.setText("Pending tasks: ".concat(String.valueOf(nrOfPendingTasks)));

        btnAddCategory.setOnClickListener(view -> {
            if (tilAddCategoryName.getVisibility() == View.GONE) {
                tilAddCategoryName.setVisibility(View.VISIBLE);
            } else {
                String categoryToBeAdded = tietAddCategoryName.getText().toString().trim();
                if (isInputtedCategoryValid(categoryToBeAdded)) {
                    categories.add(categoryToBeAdded);
                    updateCategoriesRecyclerView();
                    tilAddCategoryName.setVisibility(View.GONE);

                    categoriesDbPath.setValue(categories)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getContext(), "Category added", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(getContext(), "Failed to add category", Toast.LENGTH_LONG).show();
                                }
                            });
                    tietAddCategoryName.setText("");
                } else {
                    setCategoryError();
                }
            }
        });
    }

    private boolean isInputtedCategoryValid(String category) {

        if (category.length() < 3 || categories.contains(category)) {
            return false;
        }
        return true;
    }

    private void setCategoryError() {
        tietAddCategoryName.setError("Invalid category!");
        tietAddCategoryName.requestFocus();
    }

    @Override
    public void recyclerViewListClicked(View v, int position) {
        String selectedCategory = categories.get(position);
        new AlertDialog.Builder(getActivity(), AlertDialog.THEME_DEVICE_DEFAULT_DARK)
                .setTitle("Delete ".concat(selectedCategory).concat("?"))
                .setPositiveButton("Yes", (dialog, index) -> {
                    if (!isDefaultCategory(selectedCategory)) {
                        categories.remove(selectedCategory);
                        updateCategoriesRecyclerView();
                        categoriesDbPath.setValue(categories);
                    } else {
                        Toast.makeText(getContext(), "You can't delete this category!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", (dialog, index) -> {
                }).create().show();
    }

    private void updateCategoriesRecyclerView() {
        ((CategoriesRecyclerViewAdapter) recyclerViewCategoriesAdapter).setCategories(categories);
        recyclerViewCategoriesAdapter.notifyDataSetChanged();
    }

    private boolean isDefaultCategory(String category) {
        return DefaultCategoriesReader.get().contains(category);
    }
}