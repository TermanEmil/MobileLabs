package com.university.unicornslayer.lab2;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {

    private List<Note> mNotes;
    private static final DateFormat mHourMinuteFormat = new SimpleDateFormat("HH:mm");

    RecyclerViewAdapter(List<Note> notes) {
        mNotes = notes;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                               .inflate(R.layout.recycler_view_item, viewGroup, false);

        return new RecyclerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder recyclerViewHolder, int i) {
        final Note note = mNotes.get(i);

        recyclerViewHolder.noteContent.setText(note.content);
        recyclerViewHolder.time.setText(mHourMinuteFormat.format(note.date));
        recyclerViewHolder.notifImg.setImageResource(
                note.notifyMe ? R.drawable.alarm_on : R.drawable.alarm_off
        );
    }

    @Override
    public int getItemCount() {
        return mNotes.size();
    }
}
