package com.vantiv.sampleapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vantiv.sampleapp.ReflectionUtils;
import com.vantiv.sampleapp.singlebuttoncomponent.SingleButtonFragment;
import com.vantiv.triposmobilesdk.GiftCardBalanceInquiryRequestListener;
import com.vantiv.triposmobilesdk.enums.CardHolderPresentCode;
import com.vantiv.triposmobilesdk.enums.GiftProgramType;
import com.vantiv.triposmobilesdk.requests.GiftCardBalanceInquiryRequest;
import com.vantiv.triposmobilesdk.responses.GiftCardBalanceInquiryResponse;

public class GiftCardBalanceInquiryFragment extends SingleButtonFragment implements GiftCardBalanceInquiryRequestListener
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

        GiftCardBalanceInquiryRequest giftCardBalanceInquiryRequest = setupGiftCardBalanceInquiryRequest();

        try
        {
            sharedVtp.processGiftCardBalanceInquiryRequest(giftCardBalanceInquiryRequest, this, this);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return true;
    }

    private GiftCardBalanceInquiryRequest setupGiftCardBalanceInquiryRequest()
    {
        final GiftCardBalanceInquiryRequest giftCardBalanceInquiryRequest = new GiftCardBalanceInquiryRequest();
        giftCardBalanceInquiryRequest.setLaneNumber("1");
        giftCardBalanceInquiryRequest.setReferenceNumber("1234567890A");
        giftCardBalanceInquiryRequest.setCardholderPresentCode(CardHolderPresentCode.Present);
        giftCardBalanceInquiryRequest.setClerkNumber("123456");
        giftCardBalanceInquiryRequest.setShiftID("9876");
        giftCardBalanceInquiryRequest.setTicketNumber("5555");
        giftCardBalanceInquiryRequest.setGiftProgramType(GiftProgramType.Gift);


        return giftCardBalanceInquiryRequest;
    }

    /**
     * Gift Card Balance Inquiry request Callbacks
     */
    @Override
    public void onGiftCardBalanceInquiryRequestCompleted(GiftCardBalanceInquiryResponse giftCardBalanceInquiryResponse)
    {
        try
        {
            handleResponse(ReflectionUtils.recursiveToString(giftCardBalanceInquiryResponse));
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
            handleResponse(e.getMessage());
        }
    }

    @Override
    public void onGiftCardBalanceInquiryRequestError(Exception exception)
    {
        handleResponse(exception.getMessage());
    }
}
