package com.todoapp;

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

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements EditItemDialogFragment.EditItemDialogListener {

    ArrayList<Todo> items;
    TodosAdapter itemsAdapter;
    ListView lvItems;

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
        itemsAdapter.add(new Todo(itemText, priority));
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
        items.set(pos, new Todo(todoText, priority));
        itemsAdapter.notifyDataSetChanged();
    }

    private void readItems() {
        items = new ArrayList<Todo>();
        items.add(new Todo("Go to mall.", "MEDIUM"));
        items.add(new Todo("Buy new shoes.", "HIGH"));
        items.add(new Todo("Clean garage.", "LOW"));
//        File filesDir = getFilesDir();
//        File todoFile = new File(filesDir, "todo.txt");
//        try {
//            items = new ArrayList<String>(FileUtils.readLines(todoFile));
//        } catch (IOException e) {
//            items = new ArrayList<String>();
//        }
    }

    private void writeItems() {
//        File filesDir = getFilesDir();
//        File todoFile = new File(filesDir, "todo.txt");
//        try {
//            FileUtils.writeLines(todoFile, items);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}
