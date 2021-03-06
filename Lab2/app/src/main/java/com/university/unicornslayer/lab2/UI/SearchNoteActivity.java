package com.university.unicornslayer.lab2.UI;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.university.unicornslayer.lab2.Note;
import com.university.unicornslayer.lab2.NotesDataAccess;
import com.university.unicornslayer.lab2.Utils.QuickWarning;
import com.university.unicornslayer.lab2.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SearchNoteActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private TextView mNoNotesMsgView;
    private SearchView mSearchView;

    private NotesDataAccess mNotesDataAccess;
    private RecyclerViewAdapter mRecyclerAdapter;

    private SearchNotesTask mSearchNotesTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_note);

        mRecyclerView = findViewById(R.id.recycler_view);
        mNoNotesMsgView = findViewById(R.id.no_notes_msg);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mNotesDataAccess = new NotesDataAccess(this, getString(R.string.notes_file));
        mRecyclerAdapter = new RecyclerViewAdapter(this, getString(R.string.full_date_format));

        mNoNotesMsgView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mSearchView != null)
            performSearch(mSearchView.getQuery().toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_note, menu);

        MenuItem searchMenuItem = menu.findItem(R.id.app_bar_search);
        mSearchView = (SearchView) searchMenuItem.getActionView();

        mSearchView.setIconified(false);
        mSearchView.requestFocusFromTouch();
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                performSearch(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                performSearch(s);
                return false;
            }
        });

        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                finish();
                return false;
            }
        });

        return true;
    }

    void performSearch(String querry) {
        String[] tags = querry.toLowerCase().split("\\W+");
        if (!(tags.length == 1 && tags[0].equals("")) && tags.length != 0) {
            SearchNotesTask task = new SearchNotesTask();
            SearchNotesModel model = new SearchNotesModel();

            List<Note> allNotes;

            try {
                allNotes = new ArrayList<>(mNotesDataAccess.getNotes().values());
            } catch (IOException e) {
                new QuickWarning(this, "Failed to load notes: " + e.getCause());
                return;
            }

            model.notes = allNotes;
            model.tags = tags;
            model.postExecute = () -> {
                setNotes(task.resultingNotes);
            };

            if (mSearchNotesTask != null) {
                mSearchNotesTask.cancel(true);
            }
            mSearchNotesTask = task;

            task.execute(model);
        }
        else
            setNotes(new ArrayList<>());
    }

    void setNotes(List<Note> notes) {
        if (notes.size() == 0)
            mNoNotesMsgView.setVisibility(View.VISIBLE);
        else
            mNoNotesMsgView.setVisibility(View.GONE);

        mRecyclerAdapter.setNotes(notes);
        mRecyclerView.setAdapter(mRecyclerAdapter);
    }
}

class SearchNotesModel {
    List<Note> notes;
    String[] tags;
    Runnable postExecute;

}

class SearchNotesTask extends AsyncTask<SearchNotesModel, Void, List<Note>> {
    List<Note> resultingNotes;

    private List<Runnable> mPostExecutes;

    @Override
    protected List<Note> doInBackground(SearchNotesModel... searchNotesModels) {
        List<Note> notes = new ArrayList<>();

        for (SearchNotesModel model : searchNotesModels) {
            notes.addAll(model.notes.stream().parallel()
                    .filter(x -> wordsContainsItemFromList(x.content, model.tags))
                    .collect(Collectors.toList()));
        }

        mPostExecutes = Arrays.stream(searchNotesModels)
                .map(x -> x.postExecute)
                .collect(Collectors.toList());
        return notes;
    }

    @Override
    protected void onPostExecute(List<Note> notes) {
        resultingNotes = notes;
        mPostExecutes.parallelStream().forEach(Runnable::run);
    }

    private static boolean wordsContainsItemFromList(String inputStr, String[] items) {
        String[] words = inputStr.toLowerCase().split("\\W+");

        return Arrays.stream(words).anyMatch(x -> Arrays.stream(items).anyMatch(y -> x.startsWith(y) || x.contains(y)));
    }
}
