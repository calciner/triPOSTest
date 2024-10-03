package com.vantiv.sampleapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vantiv.sampleapp.ReflectionUtils;
import com.vantiv.sampleapp.singlebuttoncomponent.SingleButtonFragment;
import com.vantiv.triposmobilesdk.GiftCardBalanceTransferRequestListener;
import com.vantiv.triposmobilesdk.enums.CardHolderPresentCode;
import com.vantiv.triposmobilesdk.enums.GiftProgramType;
import com.vantiv.triposmobilesdk.requests.GiftCardBalanceTransferRequest;
import com.vantiv.triposmobilesdk.responses.GiftCardBalanceTransferResponse;

import java.math.BigDecimal;

public class GiftCardBalanceTransferFragment extends SingleButtonFragment implements GiftCardBalanceTransferRequestListener
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

        GiftCardBalanceTransferRequest giftCardBalanceTransferRequest = setupGiftCardBalanceTransferRequest();

        try
        {
            sharedVtp.processGiftCardBalanceTransferRequest(giftCardBalanceTransferRequest, this, this);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return true;
    }

    private GiftCardBalanceTransferRequest setupGiftCardBalanceTransferRequest()
    {
        final GiftCardBalanceTransferRequest giftCardBalanceTransferRequest = new GiftCardBalanceTransferRequest();
        giftCardBalanceTransferRequest.setTransactionAmount(new BigDecimal("10.00"));
        giftCardBalanceTransferRequest.setLaneNumber("1");
        giftCardBalanceTransferRequest.setReferenceNumber("1234567890A");
        giftCardBalanceTransferRequest.setCardholderPresentCode(CardHolderPresentCode.Present);
        giftCardBalanceTransferRequest.setClerkNumber("123456");
        giftCardBalanceTransferRequest.setShiftID("9876");
        giftCardBalanceTransferRequest.setTicketNumber("5555");
        giftCardBalanceTransferRequest.setSourceCardNumber1("37876576576767635");
        giftCardBalanceTransferRequest.setGiftProgramType(GiftProgramType.Gift);


        return giftCardBalanceTransferRequest;
    }


    @Override
    public void onGiftCardBalanceTransferRequestCompleted(GiftCardBalanceTransferResponse giftCardBalanceTransferResponse)
    {
        try
        {
            handleResponse(ReflectionUtils.recursiveToString(giftCardBalanceTransferResponse));
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
            handleResponse(e.getMessage());
        }
    }

    @Override
    public void onGiftCardBalanceTransferRequestError(Exception exception)
    {
        handleResponse(exception.getMessage());
    }
}
