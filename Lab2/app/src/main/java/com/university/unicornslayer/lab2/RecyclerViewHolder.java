package com.university.unicornslayer.lab2;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

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
        deleteButton.setOnClickListener(this::buttonClick);
    }

    @Override
    public void onClick(View v) {

    }

    private void buttonClick(View button) {

    }
}
