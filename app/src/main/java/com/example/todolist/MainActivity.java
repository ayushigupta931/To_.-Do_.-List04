package com.example.todolist;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.todolist.db.AppDatabase;
import com.example.todolist.db.Tasks;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton add;
    private RecyclerView recyclerView;
    private TaskAdapter myAdapter;
    private List<Tasks> tasklist;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        add = (FloatingActionButton) findViewById(R.id.addTask);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddNewTask.class);
                startActivityForResult(intent, 1);
            }
        });

       initRecyclerView();

        loadTaskList();
    }

    public  void initRecyclerView(){

        recyclerView = (RecyclerView) findViewById(R.id.recycleView);
        myAdapter = new TaskAdapter(this);
        recyclerView.setAdapter(myAdapter);

        LinearLayoutManager linearLayout = new LinearLayoutManager(this);
        linearLayout.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayout);
    }

    public void loadTaskList() {

        AppDatabase db = AppDatabase.getDatabase(this.getApplicationContext());
        tasklist = db.tasksDao().getAllTasks();
        Collections.reverse(tasklist);

        myAdapter.setTasksList(tasklist);
    }


    // onActivityResult() will be called when AddNewTask activity will get closed
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1 || requestCode == 2) {
            loadTaskList();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem item = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                myAdapter.getFilter().filter(newText);
                return false;
            }
        });



        return super.onCreateOptionsMenu(menu);
    }
}