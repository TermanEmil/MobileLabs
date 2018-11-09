package com.university.unicornslayer.lab2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ItemViewActivity extends AppCompatActivity {
    private Date mDate = new Date();
    private TextView mDateView;
    private DateFormat mDateFormat;
    private RecyclerView mRecyclerView;
    private TextView mNoNotesMsgView;

    private NoteManager mNoteManager;
    private NotificationManager mNotificationManager;
    private RecyclerViewAdapter mRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_view);

        mDateFormat = new SimpleDateFormat(getString(R.string.date_format));
        mNoteManager = new NoteManager(this, getString(R.string.notes_file));
        mNotificationManager = new NotificationManager(this);
        mRecyclerAdapter = new RecyclerViewAdapter(this);

        mDateView = findViewById(R.id.date_text_view);
        mRecyclerView = findViewById(R.id.recycler_view);
        mNoNotesMsgView = findViewById(R.id.no_notes_msg);

        Intent intent = getIntent();
        mDate.setTime(intent.getLongExtra(MainActivity.ITEM_DATE_MSG, -1));

        setDate(mDate);
        loadNotes();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadNotes();
    }

    private void setDate(Date newDate)
    {
        mDate = newDate;
        mDateView.setText(mDateFormat.format(mDate));
    }

    private void loadNotes()
    {
        NotesMap notes;

        try {
            notes = mNoteManager.getNotesOnDay(mDate);
        } catch (IOException e) {
            new QuickWarning(this, "Failed to load the notes: " + e.getCause());
            return;
        }

        List<Note> noteList = new ArrayList<>(notes.values());

        if (noteList.size() == 0)
            mNoNotesMsgView.setVisibility(View.VISIBLE);
        else
            mNoNotesMsgView.setVisibility(View.GONE);

        mRecyclerAdapter.setNotes(noteList);
        mRecyclerView.setAdapter(mRecyclerAdapter);
    }

    public void onNewNoteClick(View view) {
        Intent intent = new Intent(this, ViewNoteActivity.class);

        Calendar cal = Calendar.getInstance();
        cal.setTime(mDate);

        Calendar today = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, today.get(Calendar.HOUR_OF_DAY));
        cal.set(Calendar.MINUTE, today.get(Calendar.MINUTE));
        cal.set(Calendar.SECOND, today.get(Calendar.SECOND));
        cal.set(Calendar.MILLISECOND, today.get(Calendar.MILLISECOND));

        Note note = new Note();
        note.content = "A notification";
        note.date = cal.getTime();
        note.notifyMe = false;

        intent.putExtra("note", note);
        intent.putExtra("add_as_new", true);
        startActivity(intent);
    }

    public void onClearAllClick(View view) {
        NotesMap notes;

        try {
            notes = mNoteManager.getNotes();
        } catch (Exception e) {
            new QuickWarning(this, "Failed to load the notes: " + e.getCause());
            return;
        }

        notes.forEach((key, value) -> {
            if (DateTools.isTheSameDay(mDate, value.date))
                mNotificationManager.cancelNotification(value.id);
        });

        notes.entrySet().removeIf(x -> DateTools.isTheSameDay(mDate, x.getValue().date));

        try {
            mNoteManager.writeNotes(notes);
        } catch (IOException e) {
            new QuickWarning(this, "Failed to modify the notes: " + e.getCause());
            return;
        }

        mRecyclerAdapter.removeAllViews();
    }
}
