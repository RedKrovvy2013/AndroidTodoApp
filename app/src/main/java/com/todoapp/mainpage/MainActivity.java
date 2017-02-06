package com.todoapp.mainpage;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.todoapp.R;
import com.todoapp.editdialog.EditItemDialogFragment;
import com.todoapp.models.Priority;
import com.todoapp.models.Todo;
import com.todoapp.models.TodosDBHelper;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity implements EditItemDialogFragment.EditItemDialogListener {

    ArrayList<Todo> items;
    TodosAdapter itemsAdapter;
    ListView lvItems;
    int lastIndex;
    TodosDBHelper todosDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        todosDBHelper = TodosDBHelper.getInstance(this);
        items = todosDBHelper.getTodos();
        itemsAdapter = new TodosAdapter(this, items);

        lvItems = (ListView) findViewById(R.id.lvItems);
        lvItems.setAdapter(itemsAdapter);
        setupListViewListeners();
        setupSpinner();
    }

    private void setupSpinner() {
        Spinner spinner = (Spinner) findViewById(R.id.spinnerPriorities);

        //TODO: make an 'orderIndex' field to indicate ordering of priorities in database
        //      - will be good for 4th/Nth priority addition, and robustness
        TodosDBHelper todosDBHelper = TodosDBHelper.getInstance(this);
        ArrayList<Priority> priorities = todosDBHelper.getPriorities();
        //note: could have done SimpleCursorAdapter here, but it is deprecated

        PrioritiesAdapter adapter = new PrioritiesAdapter(this, priorities);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void setupListViewListeners() {
        lvItems.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapter,
                                                   View item, int pos, long id) {
                        items.remove(pos);
                        itemsAdapter.notifyDataSetChanged();
                        return true;
                    }
                }
        );
        lvItems.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapter,
                                                   View item, int pos, long id) {
                        TextView tvTodo = (TextView) item.findViewById(R.id.ivTodoText);
                        String todoText = tvTodo.getText().toString();
                        TextView tvPriority = (TextView) item.findViewById(R.id.ivTodoPriority);
                        String priority = tvPriority.getText().toString();

                        showEditDialog(todoText, priority, pos);
                    }
                }
        );

    }

    public void onAddItem(View v) {
        EditText etNewItem = (EditText) findViewById(R.id.etNewItem);
        String itemText = etNewItem.getText().toString();
        Spinner spinner = (Spinner) findViewById(R.id.spinnerPriorities);
        String priority = spinner.getSelectedItem().toString();

        Todo todo = new Todo(itemText, priority);
        items.add(todo);
        Collections.sort(items);
        lastIndex = items.indexOf(todo);

        itemsAdapter.notifyDataSetChanged();

        lvItems.smoothScrollToPosition(lastIndex);

        etNewItem.setText("");
    }

    private void showEditDialog(String todoText, String priority, int pos) {
        FragmentManager fm = getSupportFragmentManager();
        EditItemDialogFragment editItemDialogFragment =
                EditItemDialogFragment.newInstance(todoText, priority, pos);
        editItemDialogFragment.show(fm, "fragment_edit_item");
    }

    @Override
    public void onFinishEditDialog(String todoText, String priority, int pos) {

        Todo todo = new Todo(todoText, priority);
        items.set(pos, todo);
        Collections.sort(items);
        lastIndex = items.indexOf(todo);

        itemsAdapter.notifyDataSetChanged();

        lvItems.smoothScrollToPosition(lastIndex);
    }

    //app process can be killed while paused and memory reclaimed,
    //so save data if pause occurs (this covers onStop(), which is after onPause())
    @Override
    protected void onPause() {
        super.onPause();
        todosDBHelper.saveTodos(items);
    }

    private ArrayList<Todo> createDummyItems() {

        todosDBHelper = TodosDBHelper.getInstance(this);

        todosDBHelper.addPriorityIfNotExist(new Priority("HIGH"));
        todosDBHelper.addPriorityIfNotExist(new Priority("MEDIUM"));
        todosDBHelper.addPriorityIfNotExist(new Priority("LOW"));
        todosDBHelper.addTodo(new Todo("Clean kitchen.", "HIGH"));
        todosDBHelper.addTodo(new Todo("Buy new clothes.", "MEDIUM"));
        todosDBHelper.addTodo(new Todo("Go to mall.", "MEDIUM"));
        todosDBHelper.addTodo(new Todo("Clean garage.", "LOW"));
        todosDBHelper.addTodo(new Todo("Run errands.", "LOW"));
        todosDBHelper.addTodo(new Todo("Send mail.", "LOW"));

        return todosDBHelper.getTodos();
    }
}
