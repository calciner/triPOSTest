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
import com.vantiv.sampleapp.SharedData;
import com.vantiv.sampleapp.singlebuttoncomponent.SingleButtonFragment;
import com.vantiv.triposmobilesdk.AuthorizationRequestListener;
import com.vantiv.triposmobilesdk.DisplayDevice;
import com.vantiv.triposmobilesdk.VtpStatus;
import com.vantiv.triposmobilesdk.enums.CardHolderPresentCode;
import com.vantiv.triposmobilesdk.requests.AuthorizationRequest;
import com.vantiv.triposmobilesdk.responses.AuthorizationResponse;

import java.math.BigDecimal;

public class AuthorizationFragment extends SingleButtonFragment implements AuthorizationRequestListener
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

        AuthorizationRequest authorizationRequest = setupAuthorizationRequest();

        try
        {
            sharedVtp.processAuthorizationRequest(authorizationRequest, this, this);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return true;
    }

    private AuthorizationRequest setupAuthorizationRequest()
    {
        final AuthorizationRequest authorizationRequest = new AuthorizationRequest();
        authorizationRequest.setTransactionAmount(new BigDecimal("1.32"));
        authorizationRequest.setLaneNumber("1");
        authorizationRequest.setReferenceNumber("1234567890A");
        authorizationRequest.setCardholderPresentCode(CardHolderPresentCode.Present);
        authorizationRequest.setClerkNumber("123456");
//        authorizationRequest.setConvenienceFeeAmount(new BigDecimal("1.50"));
//        authorizationRequest.setSalesTaxAmount(new BigDecimal("0.50"));
        authorizationRequest.setShiftID("9876");
        authorizationRequest.setTicketNumber("5555");
//        authorizationRequest.setTipAmount(new BigDecimal("1.00"));
        authorizationRequest.setSurchargeFeeAmount(new BigDecimal("1.00"));

        return authorizationRequest;
    }


    /**
     * Authorization request Callbacks
     */
    @Override
    public void onAuthorizationRequestCompleted(AuthorizationResponse authorizationResponse)
    {
        try
        {
            getActivity().runOnUiThread(() -> {
                dismissInteractiveDialogs();
                manageKeyedUI(true);
            });

            handleResponse(ReflectionUtils.recursiveToString(authorizationResponse));
            if(authorizationResponse != null && authorizationResponse.getHost() != null)
            {
                String txnId = authorizationResponse.getHost().getTransactionID();
                if(txnId != null)
                {
                    SharedData.setLastTransactionId(txnId);
                }

                BigDecimal txnAmount = authorizationResponse.getApprovedAmount();
                if(txnAmount != null)
                {
                    SharedData.setLastTransactionAmount(authorizationResponse.getApprovedAmount());
                }

            }
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
            handleResponse(e.getMessage());
        }
    }

    @Override
    public void onAuthorizationRequestError(Exception e)
    {
        getActivity().runOnUiThread(() -> {
            dismissInteractiveDialogs();
            manageKeyedUI(true);
        });

        handleResponse(e.getMessage());
    }
}
