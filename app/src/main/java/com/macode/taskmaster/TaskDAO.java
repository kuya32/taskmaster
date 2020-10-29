package com.macode.taskmaster;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.amplifyframework.datastore.generated.model.Task;

import java.util.List;

@Dao
public interface TaskDAO {
    @Insert
    public void saveTask(Task task);

    @Query("SELECT * FROM Task")
    public List<Task> getTasks();

    @Query("SELECT * FROM Task ORDER BY id DESC")
    public List<Task> getTasksSortedByRecent();
}
