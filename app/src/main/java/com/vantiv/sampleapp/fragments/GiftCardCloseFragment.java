package com.vantiv.sampleapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vantiv.sampleapp.ReflectionUtils;
import com.vantiv.sampleapp.singlebuttoncomponent.SingleButtonFragment;
import com.vantiv.triposmobilesdk.GiftCardCloseRequestListener;
import com.vantiv.triposmobilesdk.enums.CardHolderPresentCode;
import com.vantiv.triposmobilesdk.enums.GiftProgramType;
import com.vantiv.triposmobilesdk.requests.GiftCardCloseRequest;
import com.vantiv.triposmobilesdk.responses.GiftCardCloseResponse;

public class GiftCardCloseFragment extends SingleButtonFragment implements GiftCardCloseRequestListener
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

        GiftCardCloseRequest giftCardCloseRequest = setupGiftCardCloseRequest();

        try
        {
            sharedVtp.processGiftCardCloseRequest(giftCardCloseRequest, this, this);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return true;
    }

    private GiftCardCloseRequest setupGiftCardCloseRequest()
    {
        final GiftCardCloseRequest giftCardCloseRequestRequest = new GiftCardCloseRequest();

        giftCardCloseRequestRequest.setLaneNumber("1");
        giftCardCloseRequestRequest.setReferenceNumber("1234567890A");
        giftCardCloseRequestRequest.setCardholderPresentCode(CardHolderPresentCode.Present);
        giftCardCloseRequestRequest.setClerkNumber("123456");
        giftCardCloseRequestRequest.setShiftID("9876");
        giftCardCloseRequestRequest.setTicketNumber("5555");
        giftCardCloseRequestRequest.setGiftProgramType(GiftProgramType.Gift);

        return giftCardCloseRequestRequest;
    }

    /**
     * Gift Card Close request Callbacks
     */
    @Override
    public void onGiftCardCloseRequestCompleted(GiftCardCloseResponse giftCardCloseResponse)
    {
        try
        {
            handleResponse(ReflectionUtils.recursiveToString(giftCardCloseResponse));
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
            handleResponse(e.getMessage());
        }
    }

    @Override
    public void onGiftCardCloseRequestError(Exception exception)
    {
        handleResponse(exception.getMessage());
    }
}
