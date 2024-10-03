package com.vantiv.sampleapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vantiv.triposmobilesdk.BluetoothScanRequestListener;
import com.vantiv.triposmobilesdk.DeviceInteractionListener;
import com.vantiv.triposmobilesdk.enums.SelectionType;
import com.vantiv.triposmobilesdk.exceptions.MissingRequiredPermissionsException;
import com.vantiv.sampleapp.singlebuttoncomponent.SingleButtonFragment;
import com.vantiv.sampleapp.triPOSConfig;

import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

public class ScanBtDevicesFragment extends SingleButtonFragment implements BluetoothScanRequestListener
{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        actionButton.setOnClickListener(this.actionButtonOnClickListener);
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
        try
        {
            this.sharedVtp.scanBluetoothDevicesWithConfiguration(this.getContext(), triPOSConfig.getSharedConfig(), this);
            return true;
        }
        catch (Exception e)
        {
            handleResponse(e.getMessage());
        }
        return false;
    }

    @Override
    public void onScanRequestCompleted(final ArrayList<String> bluetoothDevices)
    {
        this.onChoiceSelections(bluetoothDevices.toArray(new String[bluetoothDevices.size()]), SelectionType.Device, new DeviceInteractionListener.SelectChoiceListener()
        {
            @Override
            public void onInputTimeout(TimeoutException exception)
            {
                onScanRequestError(exception);
            }

            @Override
            public void selectChoice(int selectedChoice)
            {
                triPOSConfig.getSharedConfig().getDeviceConfiguration().setIdentifier(bluetoothDevices.get(selectedChoice));
                handleResponse("Selected device updated into coniguration. Initialize SDK.");
            }
        });
    }

    @Override
    public void onScanRequestError(Exception ex)
    {
        StringBuilder message = new StringBuilder();
        message.append(ex.getMessage());

        if (ex instanceof MissingRequiredPermissionsException)
        {
            for (String missingPermission : ((MissingRequiredPermissionsException)ex).missingRequiredPermissions)
            {
                message.append('\n');
                message.append(missingPermission);
            }
        }
        handleResponse(message.toString());
    }
}