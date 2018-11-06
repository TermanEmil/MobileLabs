package com.university.unicornslayer.lab2;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
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
import java.util.Date;

public class AddNoteActivity extends AppCompatActivity {

    private Date mDate = new Date();
    private TextView mDateView;
    private DateFormat mDateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        mDateFormat = new SimpleDateFormat(getString(R.string.full_date_format));

        Intent intent = getIntent();
        mDateView = findViewById(R.id.date_text_view);
        mDate.setTime(intent.getLongExtra(MainActivity.ITEM_DATE_MSG, -1));

        setDate(mDate);
    }


    public void onChooseTimeClick(View view) {
        TimePickerDialog tp1 = new TimePickerDialog(
                this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(mDate);
                        cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        cal.set(Calendar.MINUTE, minute);

                        setDate(cal.getTime());
                    }
                },
                Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                Calendar.getInstance().get(Calendar.MINUTE), true);
        tp1.show();
    }

    private void setDate(Date newDate)
    {
        mDate = newDate;
        mDateView.setText(mDateFormat.format(mDate));
    }

    public void onSetNoteClick(View view) {
        ToggleButton toggleButton = findViewById(R.id.notification_toggle);
        TextInputEditText inputEditText = findViewById(R.id.input_text);

        Note note = new Note();
        note.content = inputEditText.getText().toString();
        note.date = mDate;
        note.notifyMe = toggleButton.isChecked();

        NoteManager noteManager = new NoteManager(
                this,
                getResources().getString(R.string.notes_file));

        try {
            noteManager.setNote(note);
        } catch (Exception e)
        {
            new QuickWarning(this, "Failed to save the note: " + e.getCause());
            return;
        }

        finish();
    }
}