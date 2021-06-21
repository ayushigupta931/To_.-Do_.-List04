package com.example.todolist;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;

import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolist.db.AppDatabase;
import com.example.todolist.db.Tasks;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> implements Filterable {

    private Context mContext;
    private List<Tasks> tasksList;
    private AppDatabase db;
    private Boolean isEnable = false;
    private ActionMode Mode;
    private Tasks selectedTask;
    private List<Tasks> taskListAll;


    public TaskAdapter(Context context) {
        this.mContext = context;
        db = AppDatabase.getDatabase(mContext);
    }

    public void setTasksList(List<Tasks> tasksList) {
        this.tasksList = new ArrayList<>(tasksList);
        this.taskListAll = new ArrayList<>(tasksList);


        notifyDataSetChanged();
    }


    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {


            List<Tasks> filteredTasks = new ArrayList<>();


            if (constraint.toString().isEmpty()) {
                filteredTasks.addAll(taskListAll);
            } else {

                for (Tasks t : taskListAll) {
                    if ((t.task).toLowerCase().contains(constraint.toString().toLowerCase())) {
                        filteredTasks.add(t);
                    }
                }
            }

            FilterResults Results = new FilterResults();
            Results.values = filteredTasks;
            return Results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            tasksList.clear();
            tasksList.addAll((Collection<? extends Tasks>) results.values);
            notifyDataSetChanged();
        }
    };

    public void ClickItem(TaskAdapter.TaskViewHolder holder, ActionMode mode) {

        selectedTask = tasksList.get(holder.getAdapterPosition());

        if (holder.selected.getVisibility() == View.GONE) {
            holder.selected.setVisibility(View.VISIBLE);
            holder.cardView.setCardBackgroundColor(Color.WHITE);
            holder.cardView.setContentPadding(5, 5, 5, 5);

        } else {
            holder.selected.setVisibility(View.GONE);
            holder.cardView.setContentPadding(0, 0, 0, 0);
            Mode.finish();


        }


    }


    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item, parent, false);
        TaskViewHolder ViewHolder = new TaskViewHolder(view);
        return ViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TaskAdapter.TaskViewHolder holder, int position) {

        if (tasksList != null) {

            Tasks task3 = this.tasksList.get(position);
            holder.taskField.setText(task3.task);

            if (task3.date.equals("")) {
                holder.date.setVisibility(View.GONE);
            } else {
                holder.date.setVisibility(View.VISIBLE);
                holder.date.setText(task3.date);
            }

            if (task3.status == 1)
                holder.checkBox.setChecked(true);
            else
                holder.checkBox.setChecked(false);


            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        task3.status = 1;
                        db.tasksDao().updateTask(task3);
//                        taskListAll.set(position, task3);
//                        tasksList.set(position, task3);
                    } else {
                        task3.status = 0;
                        db.tasksDao().updateTask(task3);
//                        taskListAll.set(position, task3);
//                        tasksList.set(position, task3);
                    }
                }
            });


            holder.taskField.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (!isEnable) {
                        ActionMode.Callback callback = new ActionMode.Callback() {
                            @Override
                            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                                MenuInflater menuInflater = mode.getMenuInflater();
                                menuInflater.inflate(R.menu.menu, menu);
                                return true;
                            }

                            @Override
                            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {

                                isEnable = true;
                                ClickItem(holder, mode);
                                Mode = mode;

                                return true;
                            }

                            @Override
                            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                                int id = item.getItemId();


                                AlertDialog.Builder alertBox = new AlertDialog.Builder(mContext);
                                alertBox.setTitle("Delete Task");
                                alertBox.setMessage("Are you sure you want to delete this task?");
                                alertBox.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        db.tasksDao().deleteTask(selectedTask);
                                        tasksList.remove(selectedTask);
                                        taskListAll.remove(selectedTask);
                                        notifyItemRemoved(position);

                                        mode.finish();
                                    }
                                });
                                alertBox.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                AlertDialog dialog = alertBox.create();
                                dialog.show();


                                return true;
                            }

                            @Override
                            public void onDestroyActionMode(ActionMode mode) {
                                isEnable = false;
                                selectedTask = null;
                                holder.selected.setVisibility(View.GONE);
                                holder.cardView.setCardBackgroundColor(Color.WHITE);
                                holder.cardView.setContentPadding(0, 0, 0, 0);
                                notifyDataSetChanged();
                            }
                        };
                        ((AppCompatActivity) v.getContext()).startActionMode(callback);
                    } else {
                        ClickItem(holder, Mode);
                    }
                    return true;
                }
            });


            holder.relativeLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (!isEnable) {
                        ActionMode.Callback callback = new ActionMode.Callback() {
                            @Override
                            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                                MenuInflater menuInflater = mode.getMenuInflater();
                                menuInflater.inflate(R.menu.menu, menu);
                                return true;
                            }

                            @Override
                            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {

                                isEnable = true;
                                ClickItem(holder, mode);
                                Mode = mode;

                                return true;
                            }

                            @Override
                            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                                int id = item.getItemId();


                                AlertDialog.Builder alertBox = new AlertDialog.Builder(mContext);
                                alertBox.setTitle("Delete Task");
                                alertBox.setMessage("Are you sure you want to delete this task?");
                                alertBox.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        db.tasksDao().deleteTask(selectedTask);
                                        tasksList.remove(selectedTask);
                                        taskListAll.remove(selectedTask);
                                        notifyItemRemoved(position);

                                        mode.finish();
                                    }
                                });
                                alertBox.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                AlertDialog dialog = alertBox.create();
                                dialog.show();


                                return true;
                            }

                            @Override
                            public void onDestroyActionMode(ActionMode mode) {
                                isEnable = false;
                                selectedTask = null;
                                holder.selected.setVisibility(View.GONE);
                                holder.cardView.setCardBackgroundColor(Color.WHITE);
                                holder.cardView.setContentPadding(0, 0, 0, 0);
                                notifyDataSetChanged();
                            }
                        };
                        ((AppCompatActivity) v.getContext()).startActionMode(callback);
                    } else {
                        ClickItem(holder, Mode);
                    }
                    return true;
                }
            });


            holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (!isEnable) {
                        ActionMode.Callback callback = new ActionMode.Callback() {
                            @Override
                            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                                MenuInflater menuInflater = mode.getMenuInflater();
                                menuInflater.inflate(R.menu.menu, menu);
                                return true;
                            }

                            @Override
                            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {

                                isEnable = true;
                                ClickItem(holder, mode);
                                Mode = mode;

                                return true;
                            }

                            @Override
                            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                                int id = item.getItemId();


                                AlertDialog.Builder alertBox = new AlertDialog.Builder(mContext);
                                alertBox.setTitle("Delete Task");
                                alertBox.setMessage("Are you sure you want to delete this task?");
                                alertBox.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        db.tasksDao().deleteTask(selectedTask);
                                        tasksList.remove(selectedTask);
                                        taskListAll.remove(selectedTask);
                                        notifyItemRemoved(position);

                                        mode.finish();
                                    }
                                });
                                alertBox.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                AlertDialog dialog = alertBox.create();
                                dialog.show();


                                return true;
                            }

                            @Override
                            public void onDestroyActionMode(ActionMode mode) {
                                isEnable = false;
                                selectedTask = null;
                                holder.selected.setVisibility(View.GONE);
                                holder.cardView.setCardBackgroundColor(Color.WHITE);
                                holder.cardView.setContentPadding(0, 0, 0, 0);
                                notifyDataSetChanged();
                            }
                        };
                        ((AppCompatActivity) v.getContext()).startActionMode(callback);
                    } else {
                        ClickItem(holder, Mode);
                    }
                    return true;
                }
            });


            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (isEnable) {

                        if (task3 == selectedTask)
                            ClickItem(holder, Mode);
                        else
                            Toast.makeText(mContext, "Only one task can be selected at a time", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent i = new Intent(mContext, AddNewTask.class);
                        i.putExtra("task_id", task3.id);
                        ((Activity) mContext).startActivityForResult(i, 2);
                    }
                }
            });

            holder.taskField.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (isEnable) {

                        if (task3 == selectedTask)
                            ClickItem(holder, Mode);
                        else
                            Toast.makeText(mContext, "Only one task can be selected at a time", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent i = new Intent(mContext, AddNewTask.class);
                        i.putExtra("task_id", task3.id);
                        ((Activity) mContext).startActivityForResult(i, 2);
                    }
                }
            });

            holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (isEnable) {

                        if (task3 == selectedTask)
                            ClickItem(holder, Mode);
                        else
                            Toast.makeText(mContext, "Only one task can be selected at a time", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent i = new Intent(mContext, AddNewTask.class);
                        i.putExtra("task_id", task3.id);
                        ((Activity) mContext).startActivityForResult(i, 2);
                    }
                }
            });


        }
    }

    @Override
    public int getItemCount() {
        if (tasksList != null)
            return this.tasksList.size();
        else
            return 0;
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout relativeLayout;
        TextView taskField;
        CheckBox checkBox;
        TextView date;
        CardView cardView;
        ImageView selected;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkbox);
            cardView = itemView.findViewById(R.id.cardView);
            selected = itemView.findViewById(R.id.selected);
            date = itemView.findViewById(R.id.date);
            taskField = itemView.findViewById(R.id.task);
            relativeLayout = itemView.findViewById(R.id.relativeLayout);

        }
    }
}

