package com.todoapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

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
//        setupListViewListeners();
        setupSpinner();
    }

    private void setupSpinner() {
        Spinner spinner = (Spinner) findViewById(R.id.spinnerPriorities);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.priority_values, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void setupListViewListener() {
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
//        lvItems.setOnItemClickListener(
//                new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> adapter,
//                                                   View item, int pos, long id) {
//                        Intent i = new Intent(MainActivity.this, EditItemActivity.class);
//                        i.putExtra("itemText", items.get(pos));
//                        i.putExtra("itemPos", pos);
//                        startActivityForResult(i, REQUEST_CODE);
//                    }
//                }
//        );

    }

//    private final int REQUEST_CODE = 20;
//
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//
//        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
//
//            String itemText = data.getExtras().getString("itemText");
//            int itemPos = data.getExtras().getInt("itemPos", 0);
//
//            items.set(itemPos, itemText);
//            itemsAdapter.notifyDataSetChanged();
//            writeItems();
//        }
//    }

    public void onAddItem(View v) {
        EditText etNewItem = (EditText) findViewById(R.id.etNewItem);
        String itemText = etNewItem.getText().toString();
        Spinner spinner = (Spinner) findViewById(R.id.spinnerPriorities);
        String priority = spinner.getSelectedItem().toString();
        itemsAdapter.add(new Todo(itemText, priority));
        etNewItem.setText("");
        writeItems();
    }

    private void readItems() {
        items = new ArrayList<Todo>();
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
