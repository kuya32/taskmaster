package com.macode.taskmaster;

import androidx.room.RoomDatabase;

import com.amplifyframework.datastore.generated.model.Task;

@androidx.room.Database(entities = {Task.class}, version = 2)
public abstract class Database extends RoomDatabase {
    public abstract TaskDAO taskDAO();
}
