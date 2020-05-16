package com.sd.a3kleingroup.classes;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.sd.a3kleingroup.R;

public class UnfriendDialogFragment extends DialogFragment {

    public interface UnfriendDialogListener {
        public void onDialogPositiveClick();
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    UnfriendDialogListener listener;

    // Override the Fragment.onAttach() method to instantiate the UnfriendDialogListener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = (UnfriendDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException("Host activity must implement UnfriendDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("If you remove this user from your list of friends you will no longer be able to view their public files.")
                .setTitle("Unfriend?");
        // Add action buttons
        builder.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onDialogPositiveClick();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        UnfriendDialogFragment.this.getDialog().cancel();
                    }
                });

        //to change button colour see: https://stackoverflow.com/questions/4095758/change-button-color-in-alertdialog/29647126
        return builder.create();

    }
}
