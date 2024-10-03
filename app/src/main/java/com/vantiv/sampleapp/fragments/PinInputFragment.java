package com.vantiv.sampleapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vantiv.triposmobilesdk.PinInputDevice;
import com.vantiv.triposmobilesdk.PinInputListener;
import com.vantiv.triposmobilesdk.exceptions.DeviceNotConnectedException;
import com.vantiv.triposmobilesdk.exceptions.DeviceNotInitializedException;
import com.vantiv.triposmobilesdk.exceptions.PinInputEnableException;
import com.vantiv.sampleapp.R;
import com.vantiv.sampleapp.singlebuttoncomponent.SingleButtonFragment;

public class PinInputFragment extends SingleButtonFragment implements PinInputListener
{

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

        if (!(device instanceof PinInputDevice))
        {
            handleResponse("Device is not PIN input device");
            return false;
        }

        try
        {
            ((PinInputDevice)device).enablePinInput("4012300500809", this);
        }
        catch (DeviceNotConnectedException | DeviceNotInitializedException | PinInputEnableException e)
        {
            e.printStackTrace();
            handleResponse(e.getMessage());
            return false;
        }

        return true;
    }

    /**
     * PIN Input Callbacks
     */
    @Override
    public void onPinInputCompleted(String pinBlock, String keySerialNumber)
    {
        handleResponse(String.format("PIN block is %s\nKey serial number is %s", pinBlock, keySerialNumber));
    }

    @Override
    public void onPinInputError(Exception exception)
    {
        handleResponse(exception.getMessage());
    }
}
