package com.university.unicornslayer.lab2;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
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

    public void setNote(Note note, boolean addAsNew) throws IOException {
        initFileIfNotExist();

        NotesMap notes = getNotes();

        if (addAsNew) {
            note.id = getNewNoteId(note, notes);
        }

        notes.put(note.id, note);
        writeNotes(notes);
    }

    public NotesMap getNotes() throws IOException {
        initFileIfNotExist();
        String fileContent = readFile();

        NotesMap notes;

        Gson gson = new Gson();
        notes = gson.fromJson(fileContent, new TypeToken<NotesMap>() {}.getType());

        return notes;
    }

    public NotesMap getNotesOnDay(Date date) throws IOException {
        NotesMap notes = getNotes();

        notes.entrySet().removeIf(x -> !DateTools.isTheSameDay(date, x.getValue().date));
        return notes;
    }

    public void writeNotes(NotesMap notes) throws IOException {
        FileWriter writer = new FileWriter(getFile());

        Gson gson = new Gson();
        writer.write(gson.toJson(notes));
        writer.flush();
        writer.close();
    }

    public Integer getNewNoteId(Note note, NotesMap existingNotes) {

        Integer id;

        do
        {
            id =
                    note.date.hashCode() ^
                    Boolean.hashCode(note.notifyMe) ^
                    note.content.hashCode() ^
                    Double.hashCode(Math.random());
        } while (existingNotes.containsKey(id));

        return id;
    }

    private void initFileIfNotExist() throws IOException {
        File file = getFile();
        if (!file.exists())
        {
            file.createNewFile();
            writeNotes(new NotesMap());
        }
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
