package com.farthestgate.suspensions.android.ui.notes;


import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.farthestgate.suspensions.android.R;
import com.farthestgate.suspensions.android.base.BaseFragment;
import com.farthestgate.suspensions.android.constant.AppConstant;
import com.farthestgate.suspensions.android.ui.suspensionMain.CaptureEvidenceFragment;
import com.farthestgate.suspensions.android.ui.suspensionMain.model.Suspension;
import com.farthestgate.suspensions.android.utils.AppUtils;
import com.farthestgate.suspensions.android.utils.DialogUtils;

import butterknife.BindView;

public class NotesFragment extends BaseFragment {

    @BindView(R.id.txtChars)
    TextView txtCharLeft;
    @BindView(R.id.noteText)
    EditText noteView;
    private final int maxChars = AppConstant.NOTE_SIZE;
    private int remaining;

    private Suspension suspension;

    public static NotesFragment newInstance(){
        NotesFragment notesFragment = new NotesFragment();
        return notesFragment;
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_notes;
    }

    @Override
    public void initializeUi() {
        setHasOptionsMenu(true);
        AppUtils.showSoftKeyboard(noteView);

        suspension = preferenceUtils.getObject(AppConstant.SUSPENSION, Suspension.class);

        txtCharLeft.append(String.valueOf(AppConstant.NOTE_SIZE));
        noteView.addTextChangedListener(tw);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.notes_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.mnuSaveNote:
                suspension.setNotes(noteView.getText().toString().trim());
                preferenceUtils.saveObject(AppConstant.SUSPENSION, suspension);
                replaceFragment(CaptureEvidenceFragment.newInstance());
                break;

            case R.id.mnuClearNote:
                DialogUtils.showDialog(getContext(), "Cancel Notes","Are you sure you want to clear this note?",
                        "Yes", "No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                noteView.setText("");
                            }
                        }, new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                break;

            case R.id.mnuExitNote:
                DialogUtils.showDialog(getContext(), "Cancel Notes","Are you sure you want to Exit and Cancel this note?",
                        "Yes", "No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                replaceFragment(CaptureEvidenceFragment.newInstance());
                            }
                        }, new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                break;
        }
        return false;
    }


    final TextWatcher tw = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            remaining = maxChars - s.length();
            if (remaining > 0)
                txtCharLeft.setText("Characters remaining : " + remaining);
            else {
                DialogUtils.showToast(getContext(), "You have reached maximum note size");
                noteView.setSelection(start,start + count);
            }

        }

        @Override
        public void afterTextChanged(Editable s) {


        }
    };
}
