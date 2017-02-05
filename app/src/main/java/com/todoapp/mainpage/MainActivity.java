package com.todoapp.mainpage;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.todoapp.R;
import com.todoapp.editdialog.EditItemDialogFragment;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity implements EditItemDialogFragment.EditItemDialogListener {

    ArrayList<Todo> items;
    TodosAdapter itemsAdapter;
    ListView lvItems;
    int lastIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lvItems = (ListView) findViewById(R.id.lvItems);
        readItems();
        itemsAdapter = new TodosAdapter(this, items);
        lvItems.setAdapter(itemsAdapter);
        setupListViewListeners();
        setupSpinner();
    }

    private void setupSpinner() {
        Spinner spinner = (Spinner) findViewById(R.id.spinnerPriorities);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.priority_values, android.R.layout.simple_spinner_item);

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
                        writeItems();
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
        writeItems();
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

    private void readItems() {

        File filesDir = getFilesDir();
        File todoFile = new File(filesDir, "todo.txt");

        ArrayList<String> strItems = null;
        try {
            strItems = new ArrayList<String>(FileUtils.readLines(todoFile));
        } catch (IOException e) {
            strItems = new ArrayList<String>();
        }

        items = new ArrayList<Todo>();
        for(String line : strItems) {
            String priority = line.substring(0, line.indexOf(' '));
            String text = line.substring(line.indexOf(' ')+1);
            items.add(new Todo(text, priority));
        }
    }

    private void writeItems() {
        File filesDir = getFilesDir();
        File todoFile = new File(filesDir, "todo.txt");

        ArrayList<String> strItems = new ArrayList<String>();
        for (Todo item : items) {
            String str = item.priority + " " + item.text;
            strItems.add(str);
        }
        try {
            FileUtils.writeLines(todoFile, strItems);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
