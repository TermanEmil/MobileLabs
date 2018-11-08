package com.university.unicornslayer.lab2;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ViewNoteActivity extends AppCompatActivity {

    private Note mNote;
    private TextView mDateView;
    private DateFormat mDateFormat;
    private TextInputEditText mInputEditText;
    private ToggleButton mToggleButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_note);

        mDateFormat = new SimpleDateFormat(getString(R.string.date_format));

        Intent intent = getIntent();
        mNote = (Note) intent.getSerializableExtra("note");

        mDateView = findViewById(R.id.date_text_view);
        mInputEditText = findViewById(R.id.input_text);
        mToggleButton = findViewById(R.id.notification_toggle);

        updateDateView();
        mInputEditText.setText(mNote.content);
        mToggleButton.setChecked(mNote.notifyMe);
    }

    public void onChooseTimeClick(View view) {
        TimePickerDialog tp1 = new TimePickerDialog(
                this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(mNote.date);
                        cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        cal.set(Calendar.MINUTE, minute);
                        mNote.date = cal.getTime();

                        updateDateView();
                    }
                },
                Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                Calendar.getInstance().get(Calendar.MINUTE), true);
        tp1.show();
    }

    private void updateDateView() {
        mDateView.setText(mDateFormat.format(mNote.date));
    }

    public void onDoneClick(View view) {
        mNote.content = mInputEditText.getText().toString();
        mNote.notifyMe = mToggleButton.isChecked();

        NoteManager noteManager = new NoteManager(this, getString(R.string.notes_file));

        boolean addAsNew = getIntent().getBooleanExtra("add_as_new", true);
        try {
            noteManager.setNote(mNote, addAsNew);
        } catch (Exception e)
        {
            new QuickWarning(this, "Failed to save the note: " + e.getCause());
            return;
        }

        finish();
    }
}