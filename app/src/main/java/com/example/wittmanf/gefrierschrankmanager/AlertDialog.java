package com.example.wittmanf.gefrierschrankmanager;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class AlertDialog extends DialogFragment {

    public interface OnInputListener {
        void sendAlertCriteria(String input);
    }

    public OnInputListener mOnInputListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setTitle("Vor Ablauf benachrichtigen");
        dialog.setContentView(R.layout.alert_dialog_framgent);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        RadioGroup radioGroup = dialog.findViewById(R.id.radioGroup);

        Button sortButton = dialog.findViewById(R.id.chooseBTN);
        sortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int checkedRadioButtonId = radioGroup.getCheckedRadioButtonId();
                RadioButton radioButton = dialog.findViewById(checkedRadioButtonId);
                mOnInputListener.sendAlertCriteria(radioButton.getText().toString());
                getDialog().dismiss();
            }
        });

        dialog.show();
        return dialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mOnInputListener = (OnInputListener) getActivity();
        } catch (ClassCastException e) {
            Log.e("", "onAttach: ClassCastException: " + e.getMessage());
        }
    }
}
