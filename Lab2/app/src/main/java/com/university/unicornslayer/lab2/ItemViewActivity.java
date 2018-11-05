package com.university.unicornslayer.lab2;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ItemViewActivity extends AppCompatActivity {

    private Date mDate = new Date();
    private TextView mDateView;
    private final DateFormat mDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_view);

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
}
