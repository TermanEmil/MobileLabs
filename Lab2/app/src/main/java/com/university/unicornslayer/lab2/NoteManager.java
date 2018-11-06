package com.university.unicornslayer.lab2;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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

    private void initFileIfNotExist() throws IOException {
        File file = getFile();
        if (!file.exists())
        {
            file.createNewFile();
            writeListOfNotes(new ArrayList<Note>());
        }
    }

    private void writeListOfNotes(List<Note> notes) throws IOException {
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
