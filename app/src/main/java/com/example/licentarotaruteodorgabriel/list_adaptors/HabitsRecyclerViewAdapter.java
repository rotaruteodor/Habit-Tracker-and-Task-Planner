package com.example.licentarotaruteodorgabriel.list_adaptors;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.licentarotaruteodorgabriel.R;
import com.example.licentarotaruteodorgabriel.classes.Habit;
import com.example.licentarotaruteodorgabriel.enums.HabitEvaluationType;
import com.example.licentarotaruteodorgabriel.enums.HabitNumericalComparisonType;
import com.example.licentarotaruteodorgabriel.habit_statistics.streaks.HabitCurrentStreak;
import com.example.licentarotaruteodorgabriel.interfaces.RecyclerViewClickListener;

import java.lang.ref.WeakReference;
import java.time.LocalDate;
import java.util.ArrayList;

public class HabitsRecyclerViewAdapter extends RecyclerView.Adapter<HabitsRecyclerViewAdapter.HabitsViewHolder> {

    ArrayList<Habit> habits;
    Context context;
    RecyclerViewClickListener clickListener;
    LocalDate selectedDate;
    Drawable defaultHabitDrawable;

    public HabitsRecyclerViewAdapter(ArrayList<Habit> habits, Context context, RecyclerViewClickListener clickListener, LocalDate selectedDate) {
        this.habits = habits;
        this.context = context;
        this.clickListener = clickListener;
        this.selectedDate = selectedDate;
        this.defaultHabitDrawable = ContextCompat.getDrawable(context, R.drawable.round_corners_30);
    }

    @NonNull
    @Override
    public HabitsRecyclerViewAdapter.HabitsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_habit_row, parent, false);

        return new HabitsViewHolder(view, clickListener);
    }

    @SuppressLint("ResourceType")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull HabitsRecyclerViewAdapter.HabitsViewHolder holder, int position) {
        Habit currentHabit = habits.get(position);

        Drawable customDrawable = defaultHabitDrawable.getConstantState().newDrawable().mutate();
        customDrawable.setTint(currentHabit.getColor());
        holder.constraintLayoutCustomHabitRow.setBackground(customDrawable);

        if (currentHabit.getEvaluationType() == HabitEvaluationType.YESNO) {
            holder.tvHabitName.setText(currentHabit.getName());
        } else {
            holder.tvHabitName.setText(getNumericalHabitFullName(currentHabit));
        }

        if (!currentHabit.getArchived()) {
            holder.tvHabitStatus.setClickable(true);
            holder.tvHabitStreak.setVisibility(View.VISIBLE);
            if (currentHabit.getRealizations() != null) {
                if (currentHabit.getRealizations().size() > 0) {
                    if (currentHabit.getRealizations().containsKey(String.valueOf(selectedDate.toEpochDay()))) {
                        Double currentRealization = Double.parseDouble(String.valueOf(
                                currentHabit.getRealizations().get(
                                        String.valueOf(selectedDate.toEpochDay()))));

                        if (currentHabit.getEvaluationType() == HabitEvaluationType.YESNO) {
                            if (currentRealization == 0) {
                                holder.tvHabitStatus.setText("Status: Ongoing");
                                holder.tvHabitStatus.setTextColor(Color.RED);
                            } else {
                                holder.tvHabitStatus.setText("Status: Done");
                                holder.tvHabitStatus.setTextColor(Color.GREEN);
                            }
                        } else {
                            holder.tvHabitStatus.setText(getStatusStringOfHabitWithInputedRealization(currentRealization,
                                    currentHabit.getNumericalGoal()));
                            holder.tvHabitStatus.setTextColor(getNumericalStatusColor(currentHabit.getNumericalGoal(),
                                    currentRealization,
                                    currentHabit.getNumericalComparisonType()));
                        }
                    } else {
                        if (currentHabit.getEvaluationType() == HabitEvaluationType.YESNO) {
                            holder.tvHabitStatus.setText("Status: Ongoing");
                            holder.tvHabitStatus.setTextColor(Color.RED);
                        } else {
                            holder.tvHabitStatus.setText(
                                    getStatusStringOfHabitWithoutInputedRealization(
                                            currentHabit.getNumericalGoal()));
                            holder.tvHabitStatus.setTextColor(Color.WHITE);
                        }
                    }
                } else {
                    if (currentHabit.getEvaluationType() == HabitEvaluationType.YESNO) {
                        holder.tvHabitStatus.setText("Status: Ongoing");
                        holder.tvHabitStatus.setTextColor(Color.RED);
                    } else {
                        holder.tvHabitStatus.setText(
                                getStatusStringOfHabitWithoutInputedRealization(currentHabit.getNumericalGoal()));
                        holder.tvHabitStatus.setTextColor(Color.WHITE);
                    }
                }
            } else {
                holder.tvHabitStatus.setText("Status:\nUnknown");
                holder.tvHabitStatus.setTextColor(Color.RED);
            }
            holder.tvHabitStreak.setText("Streak: ".concat(
                    HabitCurrentStreak.getCurrentStreak(currentHabit).toString()));
        } else {
            holder.tvHabitStatus.setText("Archived");
            holder.tvHabitStatus.setTextColor(Color.GRAY);
            holder.tvHabitStatus.setClickable(false);
            holder.tvHabitStreak.setVisibility(View.GONE);
        }
    }

    @NonNull
    private StringBuilder getNumericalHabitFullName(Habit currentHabit) {
        StringBuilder sbHabitName = new StringBuilder();

        sbHabitName.append(currentHabit.getName())
                .append(" ");
        if (currentHabit.getNumericalComparisonType() == HabitNumericalComparisonType.EXACTLY) {
            sbHabitName.append("exactly");
        } else if (currentHabit.getNumericalComparisonType() == HabitNumericalComparisonType.ATLEAST) {
            sbHabitName.append("at least");
        } else {
            sbHabitName.append("less than");
        }

        sbHabitName.append(" ");
        if (isDoubleAnInteger(currentHabit.getNumericalGoal())) {
            sbHabitName.append(String.format("%.0f", currentHabit.getNumericalGoal()));
        } else {
            sbHabitName.append(currentHabit.getNumericalGoal());
        }
        sbHabitName.append(" ").append(currentHabit.getNumericalUnit());

        return sbHabitName;
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

    private String getStatusStringOfHabitWithInputedRealization(Double currentRealization, Double selectedHabitNumericalGoal) {

        StringBuilder stringBuilder = new StringBuilder().append("Status: ");

        if (isDoubleAnInteger(currentRealization)) {
            stringBuilder.append(String.format("%.0f", currentRealization));
        } else {
            stringBuilder.append(currentRealization);
        }
        stringBuilder.append("/");
        if (isDoubleAnInteger(selectedHabitNumericalGoal)) {
            stringBuilder.append(String.format("%.0f", selectedHabitNumericalGoal));
        } else {
            stringBuilder.append(selectedHabitNumericalGoal.toString());
        }

        return stringBuilder.toString();
    }

    private String getStatusStringOfHabitWithoutInputedRealization(Double selectedHabitNumericalGoal) {

        StringBuilder stringBuilder = new StringBuilder().append("Status: -/");
        if (isDoubleAnInteger(selectedHabitNumericalGoal)) {
            stringBuilder.append(String.format("%.0f", selectedHabitNumericalGoal));
        } else {
            stringBuilder.append(selectedHabitNumericalGoal.toString());
        }

        return stringBuilder.toString();
    }

    private boolean isDoubleAnInteger(Double numberAsDouble) {
        return numberAsDouble % 1 == 0;
    }

    @Override
    public int getItemCount() {
        return habits.size();
    }

    public void setHabits(ArrayList<Habit> habits) {
        this.habits = habits;
    }

    public void setSelectedDate(LocalDate selectedDate) {
        this.selectedDate = selectedDate;
    }

    public static class HabitsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private WeakReference<RecyclerViewClickListener> clickListenerWeakReference;
        private final ConstraintLayout constraintLayoutCustomHabitRow;
        private final TextView tvHabitName;
        private final TextView tvHabitStatus;
        private final TextView tvHabitStreak;
        private final ImageButton btnIndividualHabitOptionsMenu;

        public HabitsViewHolder(@NonNull View itemView, RecyclerViewClickListener clickListener) {
            super(itemView);

            clickListenerWeakReference = new WeakReference<>(clickListener);
            constraintLayoutCustomHabitRow = itemView.findViewById(R.id.constraintLayoutCustomHabitRow);
            tvHabitName = itemView.findViewById(R.id.tvHabitName);
            tvHabitStatus = itemView.findViewById(R.id.tvHabitStatus);
            tvHabitStreak = itemView.findViewById(R.id.tvHabitStreak);
            btnIndividualHabitOptionsMenu = itemView.findViewById(R.id.btnIndividualHabitOptionsMenu);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            tvHabitStatus.setOnClickListener(this);
            btnIndividualHabitOptionsMenu.setOnClickListener(this);
        }

        @Override
        public void onClick(View clickedView) {
            clickListenerWeakReference.get().onPositionClicked(clickedView, getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View clickedView) {
            clickListenerWeakReference.get().onLongPositionClicked(clickedView, getAdapterPosition());
            return true;
        }
    }
}
