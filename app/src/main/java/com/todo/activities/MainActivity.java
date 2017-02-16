package com.todo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.todo.R;
import com.todo.adapters.PrioritiesAdapter;
import com.todo.adapters.TodosAdapter;
import com.todo.db.TodosDBHelper;
import com.todo.models.Priority;
import com.todo.models.Todo;
import com.todo.utils.CalendarParcel;
import com.todo.utils.CommonDueDate;
import com.todo.utils.Constants;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    ArrayList<Todo> items;
    TodosAdapter itemsAdapter;
    ListView lvItems;
    int lastIndex;
    TodosDBHelper todosDBHelper;
    HashMap<String, CommonDueDate> commonDueDatesMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        todosDBHelper = TodosDBHelper.getInstance(this);
        items = todosDBHelper.getTodos();

        //dependent upon todosDBHelper
//        initPriorities();
//        items = createDummyItems();

        itemsAdapter = new TodosAdapter(this, items);

        lvItems = (ListView) findViewById(R.id.lvItems);
        lvItems.setAdapter(itemsAdapter);
        setupListViewListeners();
        setupSpinners();
        setupCommonDueDates();
    }

    private void setupCommonDueDates() {
        commonDueDatesMap = new HashMap<>();
        commonDueDatesMap.put(getResources().getString(R.string.dueDate_4Hours),
                new CommonDueDate(Calendar.HOUR, 4));
        commonDueDatesMap.put(getResources().getString(R.string.dueDate_8Hours),
                new CommonDueDate(Calendar.HOUR, 8));
        commonDueDatesMap.put(getResources().getString(R.string.dueDate_tmw),
                new CommonDueDate(Calendar.DATE, 1));
        commonDueDatesMap.put(getResources().getString(R.string.dueDate_3Days),
                new CommonDueDate(Calendar.DATE, 3));
        commonDueDatesMap.put(getResources().getString(R.string.dueDate_nextWeek),
                new CommonDueDate(Calendar.DATE, 7));
        commonDueDatesMap.put(getResources().getString(R.string.dueDate_2Weeks),
                new CommonDueDate(Calendar.DATE, 14));
        commonDueDatesMap.put(getResources().getString(R.string.dueDate_nextMonth),
                new CommonDueDate(Calendar.MONTH, 1));
    }

    private void setupSpinners() {
        Spinner spinnerP = (Spinner) findViewById(R.id.spinnerPriorities);

        //TODO: make an 'orderIndex' field to indicate ordering of priorities in database
        //      - will be good for 4th/Nth priority addition, and robustness
        TodosDBHelper todosDBHelper = TodosDBHelper.getInstance(this);
        ArrayList<Priority> priorities = todosDBHelper.getPriorities();
        //note: could have done SimpleCursorAdapter here, but it is deprecated

        PrioritiesAdapter adapterP = new PrioritiesAdapter(this, priorities);

        adapterP.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerP.setAdapter(adapterP);

        //-----------------------------------------

        Spinner spinnerD = (Spinner) findViewById(R.id.spinnerDueDate);

        ArrayAdapter<CharSequence> adapterD = ArrayAdapter.createFromResource(this,
                R.array.duedate_values, android.R.layout.simple_spinner_item);

        adapterD.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerD.setAdapter(adapterD);
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
                        Todo todo = items.get(pos);

                        Intent i = new Intent(MainActivity.this, EditItemActivity.class);
                        i.putExtra(Constants.TODO_TEXT_KEY, todo.text);
                        i.putExtra(Constants.TODO_PRIORITY_KEY, todo.priority);
                        i.putExtra(Constants.TODO_POS_KEY, pos);

                        CalendarParcel calendarParcel = new CalendarParcel(todo.dueDate);
                        i.putExtra(Constants.TODO_DUEDATE_KEY, calendarParcel);

                        startActivityForResult(i, REQUEST_CODE);
                    }
                }
        );
    }
    private final int REQUEST_CODE = 20;

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {

            Bundle extras = data.getExtras();
            String todoText = extras.getString(Constants.TODO_TEXT_KEY);
            String todoPriority = extras.getString(Constants.TODO_PRIORITY_KEY);
            int todoPos = extras.getInt(Constants.TODO_POS_KEY, 0);
            CalendarParcel calendarParcel = extras.getParcelable(Constants.TODO_DUEDATE_KEY);

            Todo todo = new Todo(todoText, todoPriority, calendarParcel.cal);
            items.set(todoPos, todo);

            sortNotifyAndScroll(todo);
        }
    }

    public void onAddItem(View v) {
        EditText etNewItem = (EditText) findViewById(R.id.etNewItem);
        String itemText = etNewItem.getText().toString();
        Spinner spinnerP = (Spinner) findViewById(R.id.spinnerPriorities);
        String priority = spinnerP.getSelectedItem().toString();
        Spinner spinnerD = (Spinner) findViewById(R.id.spinnerDueDate);
        String dueDate = spinnerD.getSelectedItem().toString();

        CommonDueDate cdd = commonDueDatesMap.get(dueDate);

        Calendar cal = Calendar.getInstance();
        cal.add(cdd.dateType, cdd.amount);

        Todo todo = new Todo(itemText, priority, cal);
        items.add(todo);
        sortNotifyAndScroll(todo);

        etNewItem.setText("");
    }

    private void sortNotifyAndScroll(Todo todo) {
        Collections.sort(items);
        lastIndex = items.indexOf(todo);
        itemsAdapter.notifyDataSetChanged();
        lvItems.smoothScrollToPosition(lastIndex);
    }

    //app process can be killed while paused and memory reclaimed,
    //so save data if pause occurs (this covers onStop(), which is after onPause())
    @Override
    protected void onPause() {
        todosDBHelper.saveTodos(items);
        super.onPause();
    }

    private ArrayList<Todo> createDummyItems() {

        TodosDBHelper todosDBHelper = TodosDBHelper.getInstance(this);
        ArrayList<Priority> priorities = todosDBHelper.getPriorities();

        ArrayList<Todo> todos = new ArrayList<>();

        //Note: below assumes at least 3 priorities, which should always be the case.
        todos.add(new Todo(Constants.DUMMY_TODO_1,
                priorities.get(0).value,
                Calendar.getInstance()));
        todos.add(new Todo(Constants.DUMMY_TODO_2,
                priorities.get(1).value,
                Calendar.getInstance()));
        todos.add(new Todo(Constants.DUMMY_TODO_3,
                priorities.get(2).value,
                Calendar.getInstance()));

        return todos;
    }
}
