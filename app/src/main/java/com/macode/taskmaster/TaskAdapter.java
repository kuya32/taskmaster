package com.macode.taskmaster;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amplifyframework.datastore.generated.model.Task;

import java.util.ArrayList;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    public List<Task> tasks;
    public TaskListener taskListener;

    public TaskAdapter(List<Task> tasks, TaskListener taskListener) {
        this.tasks = tasks;
        this.taskListener = taskListener;
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        public Task task;
        public View taskView;

        public TaskViewHolder(@NonNull View taskView) {
            super(taskView);
            this.taskView = taskView;
        }
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_task, parent, false);

        TaskViewHolder taskViewHolder = new TaskViewHolder(view);

        view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                taskListener.taskListener(taskViewHolder.task);
            }
        });
        return taskViewHolder;
    }

    public static interface TaskListener {
        public void taskListener(Task task);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        holder.task = tasks.get(position);

        TextView titleTextView = holder.itemView.findViewById(R.id.taskDetailTitle);
        TextView bodyTextView = holder.itemView.findViewById(R.id.taskDetailBody);
        TextView stateTextView = holder.itemView.findViewById(R.id.taskDetailState);

        titleTextView.setText(holder.task.getTitle());
        bodyTextView.setText(holder.task.getBody());
        stateTextView.setText(holder.task.getState());
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }
}
