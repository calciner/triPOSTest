package com.vantiv.sampleapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vantiv.triposmobilesdk.ChoiceInputDevice;
import com.vantiv.triposmobilesdk.ChoiceInputListener;
import com.vantiv.triposmobilesdk.exceptions.ChoiceInputEnableException;
import com.vantiv.sampleapp.R;
import com.vantiv.sampleapp.singlebuttoncomponent.SingleButtonFragment;
import com.vantiv.triposmobilesdk.exceptions.DeviceNotConnectedException;
import com.vantiv.triposmobilesdk.exceptions.DeviceNotInitializedException;

public class ChoiceInputFragment extends SingleButtonFragment implements ChoiceInputListener
{
    private static String[] choices = new String[] { "One", "Two", "Three" };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);

        actionButton.setOnClickListener(actionButtonOnClickListener);

        return rootView;
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

        if (!(device instanceof ChoiceInputDevice))
        {
            handleResponse("Device is not choice input device");
            return false;
        }

        try
        {
            ((ChoiceInputDevice)device).enableChoiceInput(choices, this);
        }
        catch (ChoiceInputEnableException | DeviceNotInitializedException | DeviceNotConnectedException e)
        {
            e.printStackTrace();
            handleResponse(e.getMessage());
            return false;
        }

        return true;
    }

    /**
     * Choice input Callbacks
     */
    @Override
    public void onChoiceInputCompleted(int choice)
    {
        super.handleResponse(String.format("Choice is %s", choices[choice]));
    }

    @Override
    public void onChoiceInputError(Exception exception)
    {
        handleResponse(exception.getMessage());
    }
}
