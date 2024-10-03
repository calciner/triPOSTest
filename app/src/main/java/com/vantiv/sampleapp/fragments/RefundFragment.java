package com.vantiv.sampleapp.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vantiv.sampleapp.R;
import com.vantiv.sampleapp.ReflectionUtils;
import com.vantiv.sampleapp.singlebuttoncomponent.SingleButtonFragment;
import com.vantiv.triposmobilesdk.DisplayDevice;
import com.vantiv.triposmobilesdk.RefundRequestListener;
import com.vantiv.triposmobilesdk.VtpStatus;
import com.vantiv.triposmobilesdk.enums.CardHolderPresentCode;
import com.vantiv.triposmobilesdk.enums.GiftProgramType;
import com.vantiv.triposmobilesdk.requests.RefundRequest;
import com.vantiv.triposmobilesdk.responses.RefundResponse;

import java.math.BigDecimal;

public class RefundFragment extends SingleButtonFragment implements RefundRequestListener
{

    TextView transactionStatusTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);

        transactionStatusTextView = (TextView) rootView.findViewById(R.id.transactionStatusTextView);

        actionButton.setOnClickListener(actionButtonOnClickListener);

        return rootView;
    }

    private View.OnClickListener actionButtonOnClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            if (!(sharedVtp.getDevice() instanceof DisplayDevice))
            {
                transactionStatusTextView.setVisibility(View.VISIBLE);
            }

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

        sharedVtp.setStatusListener(status -> {
            if (getActivity() != null)
            {
                getActivity().runOnUiThread(() -> {
                    transactionStatusTextView.setText(status.name());
                    setStatusView(status);
                    Log.i(SaleFragment.class.getSimpleName(), "VtpStatus: " + status.name());
                    if(status == VtpStatus.CardRemoved || status == VtpStatus.TransactionCancelled){
                        new Handler().postDelayed(() -> {
                            if (transactionStatusTextView.getVisibility() == View.GONE)
                            {
                                manageInteractiveDialogs(true);
                            }
                        }, 500);
                    }
                });
            }
        });

        RefundRequest request = setupRequest();

        try
        {
            sharedVtp.processRefundRequest(request, this, this);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return true;
    }

    private RefundRequest setupRequest()
    {
        final RefundRequest request = new RefundRequest();
        request.setTransactionAmount(new BigDecimal("10.00"));
        request.setLaneNumber("1");
        request.setReferenceNumber("1234567890A");
        request.setCardholderPresentCode(CardHolderPresentCode.Present);
        request.setClerkNumber("123456");
        request.setConvenienceFeeAmount(new BigDecimal("1.50"));
        request.setSalesTaxAmount(new BigDecimal("0.50"));
        request.setShiftID("9876");
        request.setTicketNumber("5555");
        request.setGiftProgramType(GiftProgramType.Gift);
        request.setPinLessposConversionIndicator(false);

        return request;
    }

    /**
     * Request Callbacks
     */
    @Override
    public void onRefundRequestCompleted(RefundResponse response)
    {
        try
        {
            getActivity().runOnUiThread(() -> {
                dismissInteractiveDialogs();
                manageKeyedUI(true);
            });

            handleResponse(ReflectionUtils.recursiveToString(response));
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
            handleResponse(e.getMessage());
        }
    }

    @Override
    public void onRefundRequestError(Exception exception)
    {
        getActivity().runOnUiThread(() -> {
            dismissInteractiveDialogs();
            manageKeyedUI(true);
        });

        handleResponse(exception.getMessage());
    }
}
