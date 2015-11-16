package com.commonsware.empublite;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ShareActionProvider;

import de.greenrobot.event.EventBus;

/**
 * Created by ivan on 10/11/15.
 */
public class NoteFragment extends Fragment implements TextWatcher {
    private static final String KEY_POSITION = "position";

    private EditText editor;
    private ShareActionProvider share = null;
    private Intent shareIntent = new Intent(Intent.ACTION_SEND).setType("text/plain");

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
        editor.addTextChangedListener(this);

        return result;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.notes, menu);

        share = (ShareActionProvider) menu.findItem(R.id.share).getActionProvider();
        share.setShareIntent(shareIntent);

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

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        shareIntent.putExtra(Intent.EXTRA_TEXT, s);
    }

    @Override
    public void afterTextChanged(Editable s) {

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
