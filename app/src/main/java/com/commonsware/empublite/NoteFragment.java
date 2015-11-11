package com.commonsware.empublite;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import de.greenrobot.event.EventBus;

/**
 * Created by ivan on 10/11/15.
 */
public class NoteFragment extends Fragment {
    private static final String KEY_POSITION = "position";

    private EditText editor;

    public interface Contract {
        void closeNotes();
    }

    static NoteFragment newInstance(int position) {
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_POSITION, position);

        NoteFragment frg = new NoteFragment();
        frg.setArguments(bundle);

        return  frg;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.editor, null, false);

        setHasOptionsMenu(true);

        editor = (EditText) result.findViewById(R.id.editor);

        return result;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.notes, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.delete) {
            editor.setText(null);
            getContract().closeNotes();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();

        EventBus.getDefault().register(this);

        if(TextUtils.isEmpty(editor.getText())) {
            DatabaseHelper.getInstance(getActivity()).loadNote(getPosition());
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        DatabaseHelper.getInstance(getActivity())
                .updateNote(getPosition(), editor.getText().toString());

        EventBus.getDefault().unregister(this);
    }

    private Contract getContract() {
        return (Contract) getActivity();
    }

    private int getPosition() {
        return getArguments().getInt(KEY_POSITION, -1);
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(NoteLoadedEvent event) {
        if(event.getPosition() == getPosition()) {
            editor.setText(event.getProse());
        }
    }
}
