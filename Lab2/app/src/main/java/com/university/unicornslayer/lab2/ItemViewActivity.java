package com.university.unicornslayer.lab2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.text.format.DateUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

public class ItemViewActivity extends AppCompatActivity {

    public static final String ITEM_DATE_MSG = "item_date";

    private Date mDate = new Date();
    private TextView mDateView;
    private DateFormat mDateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_view);

        mDateFormat = new SimpleDateFormat(getString(R.string.full_date_format));
        Intent intent = getIntent();
        mDateView = findViewById(R.id.date_text_view);
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
        NoteManager noteManager = new NoteManager(this, getString(R.string.notes_file));
        List<Note> notes;

        try {
            notes = noteManager.getNotesOnDay(mDate);
        } catch (Exception e) {
            new QuickWarning(this, "Failed to load the notes: " + e.getCause());
            return;
        }

        if (notes.size() == 0)
            return;

        DateFormat timeFormat = new SimpleDateFormat("HH:mm");
        ViewGroup noteContainer = findViewById(R.id.items_scroll_view);
        noteContainer.removeAllViews();

        for (Note note : notes) {
            View noteItem = getLayoutInflater().inflate(R.layout.note_item, noteContainer, false);

            TextView contentView = noteItem.findViewById(R.id.note_content);
            contentView.setText(note.content);

            TextView timeView = noteItem.findViewById(R.id.note_time);
            timeView.setText(timeFormat.format(note.date));

            TextView notifView = noteItem.findViewById(R.id.note_notif);
            notifView.setText("Notif " + (note.notifyMe ? "ON" : "OFF"));

            noteContainer.addView(noteItem);
        }
    }

    public void onNewNoteClick(View view) {
        Intent intent = new Intent(this, AddNoteActivity.class);

        Calendar cal = Calendar.getInstance();
        cal.setTime(mDate);

        Calendar today = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, today.get(Calendar.HOUR_OF_DAY));
        cal.set(Calendar.MINUTE, today.get(Calendar.MINUTE));
        Date date = cal.getTime();

        intent.putExtra(ITEM_DATE_MSG, date.getTime());
        startActivity(intent);
    }

    public void onClearAllClick(View view) {
        NoteManager noteManager = new NoteManager(this, getString(R.string.notes_file));
        List<Note> notes;

        try {
            notes = noteManager.getNotes();
        } catch (Exception e) {
            new QuickWarning(this, "Failed to load the notes: " + e.getCause());
            return;
        }

        for (ListIterator<Note> iter = notes.listIterator(); iter.hasNext(); ) {
            Note note = iter.next();

            if (DateTools.isTheSameDay(mDate, note.date))
                iter.remove();
        }

        try {
            noteManager.writeListOfNotes(notes);
        } catch (IOException e) {
            new QuickWarning(this, "Failed to modify the notes: " + e.getCause());
            return;
        }

        recreate();
    }
}
