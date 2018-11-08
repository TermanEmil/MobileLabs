package com.university.unicornslayer.lab2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.squareup.timessquare.CalendarPickerView;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {

    public static final String ITEM_DATE_MSG = "item_date";

    private DrawerLayout mDrawerLayout;
    private CalendarPickerView mCalendar;

    private Calendar minCalendarDate;
    private Calendar maxCalendarDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*
        ** Load Calendar View
         */

        minCalendarDate = Calendar.getInstance();
        minCalendarDate.add(Calendar.YEAR, -1);

        maxCalendarDate = Calendar.getInstance();
        maxCalendarDate.add(Calendar.YEAR, 1);

        mCalendar = findViewById(R.id.calendar_view);

        Date today = new Date();
        mCalendar.init(minCalendarDate.getTime(), maxCalendarDate.getTime())
                 .withSelectedDate(today);

        mCalendar.setCellClickInterceptor(
                new CalendarPickerView.CellClickInterceptor() {
                    @Override
                    public boolean onCellClicked(Date date) {
                        Intent intent = new Intent(
                                MainActivity.this,
                                ItemViewActivity.class);

                        intent.putExtra(ITEM_DATE_MSG, date.getTime());
                        startActivity(intent);
                        return true;
                    }
                }
        );

        highlightDates();
    }

    @Override
    public void onResume() {
        super.onResume();
        highlightDates();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.app_bar_search_btn:
                Intent intent = new Intent(MainActivity.this,  SearchNoteActivity.class);

                startActivity(intent);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    private void highlightDates() {
        NoteManager noteManager = new NoteManager(this, getString(R.string.notes_file));
        NotesMap notes;

        try {
            notes = noteManager.getNotes();
        } catch (IOException e) {
            new QuickWarning(this, "Failed to load the notes: " + e.getCause());
            return;
        }

        mCalendar.clearHighlightedDates();

        List<Date> notesToHighlight = notes.values().stream()
                .map(x -> x.date)
                .filter(x -> x.after(minCalendarDate.getTime()) && x.before(maxCalendarDate.getTime()))
                .collect(Collectors.toList());

        mCalendar.highlightDates(notesToHighlight);
    }
}
