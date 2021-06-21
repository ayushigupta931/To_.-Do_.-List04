package com.example.todolist.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface tasksDAO {

    @Query("SELECT * FROM Tasks")
    List<Tasks> getAllTasks();

    @Query("SELECT * FROM Tasks WHERE id =:taskID")
    Tasks getTaskById(int taskID);

    @Insert
    void insertTask(Tasks... tasks);

    @Delete
    void deleteTask(Tasks task);

    @Update
    void updateTask(Tasks task);



}
