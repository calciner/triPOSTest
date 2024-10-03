package com.vantiv.sampleapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vantiv.sampleapp.ReflectionUtils;
import com.vantiv.sampleapp.singlebuttoncomponent.SingleButtonFragment;
import com.vantiv.triposmobilesdk.GiftCardReloadRequestListener;
import com.vantiv.triposmobilesdk.enums.CardHolderPresentCode;
import com.vantiv.triposmobilesdk.enums.GiftProgramType;
import com.vantiv.triposmobilesdk.requests.GiftCardReloadRequest;
import com.vantiv.triposmobilesdk.responses.GiftCardReloadResponse;

import java.math.BigDecimal;

public class GiftCardReloadFragment extends SingleButtonFragment implements GiftCardReloadRequestListener
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

        GiftCardReloadRequest giftCardReloadRequest = setupGiftCardReloadRequest();

        try
        {
            sharedVtp.processGiftCardReloadRequest(giftCardReloadRequest, this, this);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return true;
    }

    private GiftCardReloadRequest setupGiftCardReloadRequest()
    {
        final GiftCardReloadRequest giftCardReloadRequest = new GiftCardReloadRequest();
        giftCardReloadRequest.setTransactionAmount(new BigDecimal("10.00"));
        giftCardReloadRequest.setLaneNumber("1");
        giftCardReloadRequest.setReferenceNumber("1234567890A");
        giftCardReloadRequest.setCardholderPresentCode(CardHolderPresentCode.Present);
        giftCardReloadRequest.setClerkNumber("123456");
        giftCardReloadRequest.setShiftID("9876");
        giftCardReloadRequest.setTicketNumber("5555");
        giftCardReloadRequest.setGiftProgramType(GiftProgramType.Gift);

        return giftCardReloadRequest;
    }

    /**
     * Gift Card Reload request Callbacks
     */
    @Override
    public void onGiftCardReloadRequestCompleted(GiftCardReloadResponse giftCardReloadResponse)
    {
        try
        {
            handleResponse(ReflectionUtils.recursiveToString(giftCardReloadResponse));
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
            handleResponse(e.getMessage());
        }
    }

    @Override
    public void onGiftCardReloadRequestError(Exception exception)
    {
        handleResponse(exception.getMessage());
    }
}
