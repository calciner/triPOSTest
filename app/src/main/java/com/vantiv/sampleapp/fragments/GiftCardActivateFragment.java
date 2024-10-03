package com.vantiv.sampleapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vantiv.sampleapp.ReflectionUtils;
import com.vantiv.sampleapp.singlebuttoncomponent.SingleButtonFragment;
import com.vantiv.triposmobilesdk.DisplayDevice;
import com.vantiv.triposmobilesdk.GiftCardActivateRequestListener;
import com.vantiv.triposmobilesdk.enums.CardHolderPresentCode;
import com.vantiv.triposmobilesdk.requests.GiftCardActivateRequest;
import com.vantiv.triposmobilesdk.responses.GiftCardActivateResponse;
import com.vantiv.triposmobilesdk.enums.GiftProgramType;

import java.math.BigDecimal;

public class GiftCardActivateFragment extends SingleButtonFragment implements GiftCardActivateRequestListener
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

        GiftCardActivateRequest giftCardActivateRequest = setupGiftCardActivateRequest();

        try
        {
            sharedVtp.processGiftCardActivateRequest(giftCardActivateRequest, this, this);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return true;
    }

    private GiftCardActivateRequest setupGiftCardActivateRequest()
    {
        final GiftCardActivateRequest giftCardActivateRequest = new GiftCardActivateRequest();
        giftCardActivateRequest.setTransactionAmount(new BigDecimal("10.00"));
        giftCardActivateRequest.setLaneNumber("1");
        giftCardActivateRequest.setReferenceNumber("1234567890A");
        giftCardActivateRequest.setCardholderPresentCode(CardHolderPresentCode.Present);
        giftCardActivateRequest.setClerkNumber("123456");
        giftCardActivateRequest.setShiftID("9876");
        giftCardActivateRequest.setTicketNumber("5555");
        giftCardActivateRequest.setGiftProgramType(GiftProgramType.Gift);

        return giftCardActivateRequest;
    }

    /**
     * Gift Card Activate request Callbacks
     */
    @Override
    public void onGiftCardActivateRequestCompleted(GiftCardActivateResponse giftCardActivateResponse)
    {
        try
        {
            handleResponse(ReflectionUtils.recursiveToString(giftCardActivateResponse));
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
            handleResponse(e.getMessage());
        }
    }

    @Override
    public void onGiftCardActivateRequestError(Exception exception)
    {
        handleResponse(exception.getMessage());
    }
}
