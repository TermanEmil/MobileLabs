package com.university.unicornslayer.lab2;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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
        ActionBar actionbar = getSupportActionBar();
        assert actionbar != null;
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
            new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    // set item as selected to persist highlight
                    menuItem.setChecked(true);
                    // close drawer when item is tapped
                    mDrawerLayout.closeDrawers();
                    return true;
                }
            });

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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void highlightDates() {
        NoteManager noteManager = new NoteManager(this, getString(R.string.notes_file));
        List<Note> notes;

        try {
            notes = noteManager.getNotes();
        } catch (IOException e) {
            new QuickWarning(this, "Failed to load the notes: " + e.getCause());
            return;
        }

        mCalendar.clearHighlightedDates();

        List<Date> notesToHighlight = notes.stream()
                .map(x -> x.date)
                .filter(x -> x.after(minCalendarDate.getTime()) && x.before(maxCalendarDate.getTime()))
                .collect(Collectors.toList());

        mCalendar.highlightDates(notesToHighlight);
    }
}
