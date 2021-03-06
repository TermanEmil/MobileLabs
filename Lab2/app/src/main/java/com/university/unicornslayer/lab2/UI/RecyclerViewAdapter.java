package com.university.unicornslayer.lab2.UI;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.university.unicornslayer.lab2.Note;
import com.university.unicornslayer.lab2.NotesDataAccess;
import com.university.unicornslayer.lab2.NotificationManager;
import com.university.unicornslayer.lab2.Utils.QuickWarning;
import com.university.unicornslayer.lab2.R;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {

    private List<Note> mNotes;
    private NotesDataAccess mNotesDataAccess;
    private NotificationManager mNotificationManager;
    private DateFormat mDateFormat;

    RecyclerViewAdapter(Context context, String dateFormatStr) {
        mNotes = new ArrayList<>();
        mNotesDataAccess = new NotesDataAccess(context, context.getString(R.string.notes_file));
        mNotificationManager = new NotificationManager(context);
        mDateFormat = new SimpleDateFormat(dateFormatStr);
    }

    RecyclerViewAdapter(Context context) {
        this(context, "HH:mm");
    }

    public void setNotes(List<Note> notes) {
        mNotes = notes;
        mNotes .sort((x, y) -> y.date.compareTo(x.date));
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                               .inflate(R.layout.recycler_view_item, viewGroup, false);

        RecyclerViewHolder item = new RecyclerViewHolder(v, mDateFormat);
        item.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mNotesDataAccess.removeNote(item.note.id);
                } catch (IOException e) {
                    new QuickWarning(v.getContext(), "Failed to modify the notes: " + e.getCause());
                }

                mNotificationManager.cancelNotification(item.note.id);

                Note note = mNotes.stream().filter(x -> x.id == item.note.id).findFirst().get();
                int position = mNotes.indexOf(note);

                mNotes.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, mNotes.size());
            }
        });


        return item;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int i) {
        holder.setNote(mNotes.get(i));
    }

    @Override
    public int getItemCount() {
        return mNotes.size();
    }

    public void removeAllViews() {
        int size = mNotes.size();
        mNotes.clear();

        notifyItemRangeRemoved(0, size);
    }
}
