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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.sd.a3kleingroup.R;

public class AddFriendDialogFragment extends DialogFragment {

    public interface AddFriendDialogListener {
        public void onDialogPositiveClick(String email);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    AddFriendDialogListener listener;

    // Override the Fragment.onAttach() method to instantiate the AddFriendDialogListener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = (AddFriendDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException("Host activity must implement AddFriendDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View viewInflated = inflater.inflate(R.layout.dialog_add_friend, null);
        final EditText txtEmail = (EditText) viewInflated.findViewById(R.id.txtEmail);
        builder.setView(viewInflated)
                // Add action buttons
                .setPositiveButton(R.string.send_friend_request, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String email = txtEmail.getText().toString();
                        Log.d("DialogFragment", email);
                        listener.onDialogPositiveClick(email);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        AddFriendDialogFragment.this.getDialog().cancel();
                    }
                });

        //to change button colour see: https://stackoverflow.com/questions/4095758/change-button-color-in-alertdialog/29647126
        return builder.create();

    }
}
