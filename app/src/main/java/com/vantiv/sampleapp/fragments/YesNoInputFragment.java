package com.vantiv.sampleapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vantiv.triposmobilesdk.YesNoInputDevice;
import com.vantiv.triposmobilesdk.YesNoInputListener;
import com.vantiv.triposmobilesdk.exceptions.DeviceNotConnectedException;
import com.vantiv.triposmobilesdk.exceptions.DeviceNotInitializedException;
import com.vantiv.triposmobilesdk.exceptions.YesNoInputEnableException;
import com.vantiv.sampleapp.R;
import com.vantiv.sampleapp.singlebuttoncomponent.SingleButtonFragment;

public class YesNoInputFragment extends SingleButtonFragment implements YesNoInputListener
{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
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

        if (!(device instanceof YesNoInputDevice))
        {
            handleResponse("Device is not yes/no input device");
            return false;
        }

        try
        {
            ((YesNoInputDevice) device).enableYesNoInput(this);
        }
        catch (DeviceNotConnectedException | DeviceNotInitializedException | YesNoInputEnableException e) {
            e.printStackTrace();
        }

        return true;
    }

    /**
     * Yes/No input Callbacks
     */
    @Override
    public void onYesNoInputCompleted(boolean yes)
    {
        handleResponse(String.format("Yes/No input is %s", yes ? "YES" : "NO"));
    }

    @Override
    public void onYesNoInputError(Exception exception)
    {
        super.handleResponse(exception.getMessage());
    }
}
