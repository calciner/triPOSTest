package com.vantiv.sampleapp.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.google.gson.Gson;
import com.vantiv.sampleapp.ReflectionUtils;
import com.vantiv.sampleapp.master.HostedPaymentsWebViewActivity;
import com.vantiv.sampleapp.singlebuttoncomponent.SingleButtonFragment;
import com.vantiv.triposmobilesdk.HostedPaymentRequestListener;
import com.vantiv.triposmobilesdk.enums.CardHolderPresentCode;
import com.vantiv.triposmobilesdk.requests.AuthorizationRequest;
import com.vantiv.triposmobilesdk.responses.AuthorizationResponse;
import com.vantiv.triposmobilesdk.responses.HostedPaymentResponse;

import java.math.BigDecimal;

public class HostedPaymentsAuthorizationFragment extends SingleButtonFragment implements HostedPaymentRequestListener {

    public static final String HOSTED_PAYMENTS_AUTHORIZATION = "Hosted Payments Authorization";
    private static final String TAG = HostedPaymentsAuthorizationFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);

        actionButton.setOnClickListener(actionButtonOnClickListener);

        return rootView;
    }

    private final View.OnClickListener actionButtonOnClickListener = v -> {
        hideOutputTextView();
        showProgressBarSpinner();
        sendAction();
    };

    public boolean sendAction() {
        if (!super.sendAction()) {
            return false;
        }

        if (sharedVtp == null) {
            handleResponse("sharedVtp is null");
            return false;
        }

        if (!sharedVtp.getIsInitialized()) {
            handleResponse("sharedVtp is not initialized");
            return false;
        }

        AuthorizationRequest authorizationRequest = setupAuthorizationRequest();

        try {
            sharedVtp.processHostedPaymentAuthorizationRequest(authorizationRequest, this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    private AuthorizationRequest setupAuthorizationRequest() {
        final AuthorizationRequest authorizationRequest = new AuthorizationRequest();
        authorizationRequest.setTransactionAmount(new BigDecimal("10.00"));
        authorizationRequest.setLaneNumber("1");
        authorizationRequest.setReferenceNumber("1234567890A");
        authorizationRequest.setCardholderPresentCode(CardHolderPresentCode.Present);
        authorizationRequest.setClerkNumber("123456");
        authorizationRequest.setConvenienceFeeAmount(new BigDecimal("1.50"));
        authorizationRequest.setSalesTaxAmount(new BigDecimal("0.50"));
        authorizationRequest.setShiftID("9876");
        authorizationRequest.setTicketNumber("5555");
        authorizationRequest.setTipAmount(new BigDecimal("1.00"));
        return authorizationRequest;
    }

    /**
     * Hosted Payments Authorization request Callback
     */
    @Override
    public void onHostedPaymentRequestCompleted(HostedPaymentResponse hostedPaymentResponse) {
        Log.d(TAG, "HostedPaymentsURL: " + hostedPaymentResponse.getHostedPaymentsURL());
        handleResponse("");
        Intent intent = new Intent(getActivity(), HostedPaymentsWebViewActivity.class);
        intent.putExtra(HostedPaymentsWebViewActivity.HOSTED_PAYMENTS_TITLE, HOSTED_PAYMENTS_AUTHORIZATION);
        intent.putExtra(HostedPaymentsWebViewActivity.HOSTED_PAYMENTS_URL, hostedPaymentResponse.getHostedPaymentsURL());
        activityResultLauncher.launch(intent);
    }

    /**
     * Hosted Payments Authorization request Callback
     */
    @Override
    public void onHostedPaymentRequestError(Exception exception) {
        handleResponse(exception.getMessage());
    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    try {
                        Intent data = result.getData();
                        String responseData = data.getStringExtra(HostedPaymentsWebViewActivity.RESPONSE_DATA_HOSTED_PAYMENTS);
                        AuthorizationResponse authorizationResponse = new Gson().fromJson(responseData, AuthorizationResponse.class);
                        handleResponse(ReflectionUtils.recursiveToString(authorizationResponse));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        handleResponse(e.getMessage());
                    }
                }
            });

}
