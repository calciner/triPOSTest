package com.vantiv.sampleapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vantiv.sampleapp.ReflectionUtils;
import com.vantiv.sampleapp.singlebuttoncomponent.SingleButtonFragment;
import com.vantiv.triposmobilesdk.GiftCardUnloadRequestListener;
import com.vantiv.triposmobilesdk.enums.CardHolderPresentCode;
import com.vantiv.triposmobilesdk.requests.GiftCardUnloadRequest;
import com.vantiv.triposmobilesdk.responses.GiftCardUnloadResponse;

public class GiftCardUnloadFragment extends SingleButtonFragment implements GiftCardUnloadRequestListener
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

        if (sharedVtp == null)
        {
            handleResponse("sharedVtp is null");
            return false;
        }

        if (!sharedVtp.getIsInitialized())
        {
            handleResponse("sharedVtp is not initialized");
            return false;
        }

        setStatusListener();

        GiftCardUnloadRequest giftCardUnloadRequest = setupGiftCardUnloadRequest();

        try
        {
            sharedVtp.processGiftCardUnloadRequest(giftCardUnloadRequest, this, this);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return true;
    }

    private GiftCardUnloadRequest setupGiftCardUnloadRequest()
    {
        final GiftCardUnloadRequest request = new GiftCardUnloadRequest();
        request.setLaneNumber("1");
        request.setReferenceNumber("1234567890A");
        request.setCardholderPresentCode(CardHolderPresentCode.Present);
        request.setClerkNumber("123456");
        request.setShiftID("9876");
        request.setTicketNumber("5555");

        return request;
    }

    /**
     * Gift Card Unload request Callbacks
     */
    @Override
    public void onGiftCardUnloadRequestCompleted(GiftCardUnloadResponse giftCardUnloadResponse)
    {
        try
        {
            handleResponse(ReflectionUtils.recursiveToString(giftCardUnloadResponse));
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
            handleResponse(e.getMessage());
        }
    }

    @Override
    public void onGiftCardUnloadRequestError(Exception exception)
    {
        handleResponse(exception.getMessage());
    }
}
