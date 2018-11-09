package com.university.unicornslayer.lab2;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.university.unicornslayer.lab2.Utils.DateTools;
import com.university.unicornslayer.lab2.Utils.QuickWarning;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Scanner;

public class NotesDataAccess {
    private Context mContext;
    private String mFileName;
    private Gson mGson;

    public NotesDataAccess(Context context, String fileName)
    {
        mContext = context;
        mFileName = fileName;

        mGson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
                .create();
    }

    private File getFile()
    {
        return new File(mContext.getFilesDir(), mFileName);
    }

    public NotesMap quickGetNotes() {
        try {
            return getNotes();
        } catch (IOException e) {
            new QuickWarning(mContext, "Failed to load the notes: " + e.getCause());
            return null;
        }
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
        notes = mGson.fromJson(fileContent, new TypeToken<NotesMap>() {}.getType());

        return notes;
    }

    public NotesMap getNotesOnDay(Date date) throws IOException {
        NotesMap notes = getNotes();

        notes.entrySet().removeIf(x -> !DateTools.isTheSameDay(date, x.getValue().date));
        return notes;
    }

    public void writeNotes(NotesMap notes) throws IOException {
        FileWriter writer = new FileWriter(getFile());
;
        String notesToJson = mGson.toJson(notes);
        writer.write(notesToJson);
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

    public void removeNote(Integer noteId) throws IOException {
        NotesMap notes = getNotes();
        notes.remove(noteId);
        writeNotes(notes);
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
