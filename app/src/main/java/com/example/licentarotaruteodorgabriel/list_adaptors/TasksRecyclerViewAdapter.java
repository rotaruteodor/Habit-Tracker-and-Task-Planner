package com.example.licentarotaruteodorgabriel.list_adaptors;

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
import com.example.licentarotaruteodorgabriel.classes.Task;
import com.example.licentarotaruteodorgabriel.interfaces.RecyclerViewClickListener;

import java.lang.ref.WeakReference;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;

public class TasksRecyclerViewAdapter extends RecyclerView.Adapter<TasksRecyclerViewAdapter.TasksViewHolder> {

    ArrayList<Task> tasks;
    Context context;
    RecyclerViewClickListener clickListener;
    Drawable defaultTaskDrawable;

    public TasksRecyclerViewAdapter(ArrayList<Task> tasks, Context context, RecyclerViewClickListener clickListener) {
        this.tasks = tasks;
        this.context = context;
        this.clickListener = clickListener;
        this.defaultTaskDrawable = ContextCompat.getDrawable(context, R.drawable.round_corners_30);
    }

    @NonNull
    @Override
    public TasksRecyclerViewAdapter.TasksViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_task_row, parent, false);
        return new TasksViewHolder(view, clickListener);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull TasksRecyclerViewAdapter.TasksViewHolder holder, int position) {
        Task currentTask = tasks.get(position);

        Drawable customDrawable = defaultTaskDrawable.getConstantState().newDrawable().mutate();
        customDrawable.setTint(currentTask.getColor());
        holder.constraintLayoutCustomTaskRow.setBackground(customDrawable);


        holder.tvTaskName.setText(currentTask.getName());
        if (currentTask.getIsDone()) {
            holder.tvTaskDueDate.setText("Done");
            holder.tvTaskDueDate.setTextColor(Color.GREEN);
        } else {
            Long taskDueDate = currentTask.getDueDate();
            long currentDate = LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond();
            StringBuilder stringBuilderDue = new StringBuilder().append("Due ");

            long difference;
            if (taskDueDate > currentDate) {
                difference = taskDueDate - currentDate;
                stringBuilderDue.append("in: ");
                holder.tvTaskDueDate.setTextColor(Color.WHITE);
            } else {
                difference = currentDate - taskDueDate;
                stringBuilderDue.append("by: ");
                holder.tvTaskDueDate.setTextColor(Color.RED);
            }
            int days = (int) (difference / 86400);
            int hours = (int) (difference / 3600);

            if (days >= 1) {
                stringBuilderDue.append(String.valueOf(days).concat("d "));
                if(hours >= 25){
                    stringBuilderDue.append(String.valueOf(hours - days * 24).concat("h"));
                }
            } else if (hours >= 1){
                stringBuilderDue.append(hours).append("h");
            } else {
                stringBuilderDue.append("less than 1h");
            }

            holder.tvTaskDueDate.setText(stringBuilderDue);
        }
        holder.tvTaskPriority.setText("Priority: ".concat(currentTask.getPriority().toString()));
    }


    @Override
    public int getItemCount() {
        return tasks.size();
    }


    public ArrayList<Task> getTasks() {
        return tasks;
    }

    public void setTasks(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    public static class TasksViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private WeakReference<RecyclerViewClickListener> clickListenerWeakReference;
        private final ConstraintLayout constraintLayoutCustomTaskRow;
        private final TextView tvTaskName;
        private final TextView tvTaskDueDate;
        private final TextView tvTaskPriority;
        private final ImageButton btnIndividualTaskOptionsMenu;

        public TasksViewHolder(@NonNull View itemView, RecyclerViewClickListener clickListener) {
            super(itemView);
            clickListenerWeakReference = new WeakReference<>(clickListener);
            constraintLayoutCustomTaskRow = itemView.findViewById(R.id.constraintLayoutCustomTaskRow);
            tvTaskName = itemView.findViewById(R.id.tvTaskName);
            tvTaskDueDate = itemView.findViewById(R.id.tvTaskDueDate);
            tvTaskPriority = itemView.findViewById(R.id.tvTaskPriority);
            btnIndividualTaskOptionsMenu = itemView.findViewById(R.id.btnIndividualTaskOptionsMenu);

            itemView.setOnClickListener(this);
            btnIndividualTaskOptionsMenu.setOnClickListener(this);
            tvTaskDueDate.setOnClickListener(this);
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
