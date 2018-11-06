package com.university.unicornslayer.lab2;

import android.app.Person;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Scanner;
import java.util.function.Predicate;

public class NoteManager {
    private Context mContext;
    private String mFileName;

    NoteManager(Context context, String fileName)
    {
        mContext = context;
        mFileName = fileName;
    }

    private File getFile()
    {
        return new File(mContext.getFilesDir(), mFileName);
    }

    public void setNote(Note note) throws IOException {
        initFileIfNotExist();

        List<Note> notes = getNotes();
        notes.add(note);
        writeListOfNotes(notes);
    }

    public List<Note> getNotes() throws IOException {
        initFileIfNotExist();

        String fileContent = readFile();

        Gson gson = new Gson();
        List<Note> notes = new ArrayList<Note>();
        notes = gson.fromJson(fileContent, new TypeToken<List<Note>>() {}.getType());

        return notes;
    }

    public List<Note> getNotesOnDay(Date date) throws IOException {
        List<Note> notes = getNotes();

        for (ListIterator<Note> iter = notes.listIterator(); iter.hasNext(); ) {
            Note note = iter.next();

            if (!DateTools.isTheSameDay(date, note.date))
                iter.remove();
        }

        return notes;
    }

    private void initFileIfNotExist() throws IOException {
        File file = getFile();
        if (!file.exists())
        {
            file.createNewFile();
            writeListOfNotes(new ArrayList<Note>());
        }
    }

    public void writeListOfNotes(List<Note> notes) throws IOException {
        FileWriter writer = new FileWriter(getFile());

        Gson gson = new Gson();
        writer.write(gson.toJson(notes));
        writer.flush();
        writer.close();
    }

    private String readFile() throws IOException {

        File file = getFile();
        StringBuilder fileContents = new StringBuilder((int)file.length());

        try (Scanner scanner = new Scanner(file)) {
            while(scanner.hasNextLine()) {
                fileContents.append(scanner.nextLine() + System.lineSeparator());
            }
            return fileContents.toString();
        }
    }
}
