package com.vantiv.sampleapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.vantiv.sampleapp.R;
import com.vantiv.sampleapp.singlebuttoncomponent.SingleButtonFragment;
import com.vantiv.triposmobilesdk.KeyboardNumericInputDevice;
import com.vantiv.triposmobilesdk.KeyboardNumericInputListener;
import com.vantiv.triposmobilesdk.enums.KeyboardNumericInputPromptId;
import com.vantiv.triposmobilesdk.exceptions.DeviceNotConnectedException;
import com.vantiv.triposmobilesdk.exceptions.DeviceNotInitializedException;
import com.vantiv.triposmobilesdk.exceptions.KeyboardNumericInputEnableException;

public class KeyboardNumericInputFragment extends SingleButtonFragment implements KeyboardNumericInputListener
{
    Spinner numericPromptIdsSpinner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);

        actionButton.setOnClickListener(actionButtonOnClickListener);

        addNumericInputOptions(rootView);

        return rootView;
    }

    void addNumericInputOptions(View view)
    {
        LinearLayout addedStuffLinearLayout = (LinearLayout)view.findViewById(R.id.addedStuffLinearLayout);

        LinearLayout.LayoutParams addedStuffLinearLayoutParams = (LinearLayout.LayoutParams)addedStuffLinearLayout.getLayoutParams();

        addedStuffLinearLayoutParams.setMargins(0, 20, 0, 0);

        addedStuffLinearLayout.setLayoutParams(addedStuffLinearLayoutParams);

        TextView numericPromptIdPrompt = new TextView(getActivity());

        numericPromptIdPrompt.setText("Select numeric input type");

        addedStuffLinearLayout.addView(numericPromptIdPrompt);

        ArrayAdapter<KeyboardNumericInputPromptId> numericInputPromptIdsAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, KeyboardNumericInputPromptId.values());

        numericPromptIdsSpinner = new Spinner(getActivity());

        numericPromptIdsSpinner.setAdapter(numericInputPromptIdsAdapter);

        addedStuffLinearLayout.addView(numericPromptIdsSpinner);
    }

    private View.OnClickListener actionButtonOnClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            hideOutputTextView();
            showProgressBarSpinner();

            sendAction();
        }
    };

    public boolean sendAction()
    {
        if (!super.sendAction())
        {
            return false;
        }

        if (device == null)
        {
            handleResponse(getResources().getString(R.string.device_was_null));
            return false;
        }

        if (!(device instanceof KeyboardNumericInputDevice))
        {
            handleResponse("Device is not numeric input device");
            return false;
        }

        KeyboardNumericInputPromptId numericInputPromptId = (KeyboardNumericInputPromptId)numericPromptIdsSpinner.getSelectedItem();

        try
        {
            ((KeyboardNumericInputDevice)device).enableKeyboardNumericInputWithPromptId(numericInputPromptId, this);
        }
        catch (DeviceNotConnectedException | DeviceNotInitializedException | KeyboardNumericInputEnableException e)
        {
            e.printStackTrace();
            handleResponse(e.getMessage());
            return false;
        }

        return true;
    }

    /**
     * Numeric Input Callbacks
     */
    @Override
    public void onKeyboardNumericInputCompleted(String numericInput)
    {
        handleResponse(String.format("Numeric input is %s", numericInput));
    }

    @Override
    public void onKeyboardNumericInputError(Exception exception)
    {
        handleResponse(exception.getMessage());
    }
}
