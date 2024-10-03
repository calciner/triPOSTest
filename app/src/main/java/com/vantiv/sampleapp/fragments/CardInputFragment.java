package com.vantiv.sampleapp.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vantiv.sampleapp.R;
import com.vantiv.sampleapp.ReflectionUtils;
import com.vantiv.sampleapp.singlebuttoncomponent.SingleButtonFragment;
import com.vantiv.triposmobilesdk.CardData;
import com.vantiv.triposmobilesdk.CardInputDevice;
import com.vantiv.triposmobilesdk.CardInputListener;
import com.vantiv.triposmobilesdk.exceptions.CardInputEnableException;
import com.vantiv.sampleapp.R;
import com.vantiv.sampleapp.ReflectionUtils;
import com.vantiv.sampleapp.singlebuttoncomponent.SingleButtonFragment;
import com.vantiv.triposmobilesdk.exceptions.DeviceNotConnectedException;
import com.vantiv.triposmobilesdk.exceptions.DeviceNotInitializedException;

public class CardInputFragment extends SingleButtonFragment implements CardInputListener
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

        if (!(device instanceof CardInputDevice))
        {
            handleResponse("Device is not card input device");
            return false;
        }
        try
        {
            ((CardInputDevice) device).enableCardInput(this);
        }
        catch (DeviceNotConnectedException | DeviceNotInitializedException | CardInputEnableException e) {
            e.printStackTrace();
        }

        return true;
    }

    /**
     * Card input Callbacks
     **/
    @Override
    public void onCardInputCompleted(CardData cardData)
    {
        String output = "";

        try
        {
            output = ReflectionUtils.recursiveToString(cardData);
            Log.d(CardInputFragment.class.getSimpleName(),String.format("recursiveToString : %s", output));
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }

        super.handleResponse(output);
    }

    @Override
    public void onCardInputError(Exception exception)
    {
        super.handleResponse(exception.getMessage());
    }
}
