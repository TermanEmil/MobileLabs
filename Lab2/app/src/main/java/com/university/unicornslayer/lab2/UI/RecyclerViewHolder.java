package com.university.unicornslayer.lab2.UI;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.university.unicornslayer.lab2.Note;
import com.university.unicornslayer.lab2.R;

import java.text.DateFormat;

public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private DateFormat mDateFormat;

    public Note note;

    TextView time;
    TextView noteContent;
    Button deleteButton;
    ImageView notifImg;

    public RecyclerViewHolder(@NonNull View itemView, DateFormat dateFormat) {
        super(itemView);

        itemView.setOnClickListener(this);

        mDateFormat = dateFormat;
        noteContent = itemView.findViewById(R.id.note_content);
        time = itemView.findViewById(R.id.note_time);
        notifImg = itemView.findViewById(R.id.notif_img);
        deleteButton = itemView.findViewById(R.id.delete_button);
    }

    // Do all the visual work.
    public void setNote(@NonNull Note note) {
        noteContent.setText(note.content);
        time.setText(mDateFormat.format(note.date));
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
