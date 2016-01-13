package com.pham.accessmap.Object;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;

import com.pham.accessmap.R;

/**
 * Created by mc976 on 1/13/16.
 */
public class AddLocationDialogFragment extends android.support.v4.app.DialogFragment {


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        View tView = inflater.inflate(R.layout.custom_user_dialog, null);
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(tView)
                // Add action buttons
                .setPositiveButton(getResources().getString(R.string.alert_ok_title), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // post to server
                        mListener.onDialogPostButtonClick(AddLocationDialogFragment.this);
                    }
                })
                .setNegativeButton(getResources().getString(R.string.dialog_cancel_button), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
//                        AddLocationDialogFragment.this.getDialog().cancel();
                        mListener.onDialogCancelButtonClick(AddLocationDialogFragment.this);
                    }
                });

        final EditText tPassword = (EditText)tView.findViewById(R.id.add_location_password);
        tPassword.setVisibility(View.INVISIBLE);
        tPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mListener.onPasswordChanged(tPassword.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
//                mPassword = tPassword.getText().toString();
            }
        });

        final EditText tUserPhone = (EditText)tView.findViewById(R.id.add_location_user_phone);
        tUserPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mListener.onUserPhoneChanged(tUserPhone.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
//                mUserPhone = tUserPhone.getText().toString();

            }
        });


        RadioButton tUserRadioButton = (RadioButton)tView.findViewById(R.id.add_location_user_radio_buton);
        tUserRadioButton.setChecked(true);
        tUserRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    tPassword.setVisibility(View.INVISIBLE);
                } else {
                    tPassword.setVisibility(View.VISIBLE);
                }
                mListener.onUserCheckedChanged(!isChecked);
            }
        });

        return builder.create();
    }


    public interface NoticeDialogListener {
        void onDialogPostButtonClick(AddLocationDialogFragment dialog);
        void onDialogCancelButtonClick(AddLocationDialogFragment dialog);
        void onUserPhoneChanged (String userPhone);
        void onPasswordChanged (String password);
        void onUserCheckedChanged (boolean isAdmin);
    }

    // Use this instance of the interface to deliver action events
    NoticeDialogListener mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

}
