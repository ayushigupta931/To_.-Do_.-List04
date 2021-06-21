package com.example.todolist.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "TASKS")
public class Tasks {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name="tasks")
    public String task;

    @ColumnInfo(name="DoneOrNotDone")
    public int status;

    @ColumnInfo(name="Date")
    public String date;
}
