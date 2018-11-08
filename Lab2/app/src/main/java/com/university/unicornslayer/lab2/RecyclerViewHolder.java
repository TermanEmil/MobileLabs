package com.university.unicornslayer.lab2;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private static final DateFormat mHourMinuteFormat = new SimpleDateFormat("HH:mm");

    public Note note;

    TextView time;
    TextView noteContent;
    Button deleteButton;
    ImageView notifImg;

    public RecyclerViewHolder(@NonNull View itemView) {
        super(itemView);

        itemView.setOnClickListener(this);

        noteContent = itemView.findViewById(R.id.note_content);
        time = itemView.findViewById(R.id.note_time);
        notifImg = itemView.findViewById(R.id.notif_img);
        deleteButton = itemView.findViewById(R.id.delete_button);
    }

    // Do all the visual work.
    public void setNote(@NonNull Note note) {
        noteContent.setText(note.content);
        time.setText(mHourMinuteFormat.format(note.date));
        notifImg.setImageResource(
                note.notifyMe ? R.drawable.alarm_on : R.drawable.alarm_off
        );

        this.note = note;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(v.getContext(), ViewNoteActivity.class);

        intent.putExtra("note", note);
        intent.putExtra("add_as_new", false);
        v.getContext().startActivity(intent);
    }
}
