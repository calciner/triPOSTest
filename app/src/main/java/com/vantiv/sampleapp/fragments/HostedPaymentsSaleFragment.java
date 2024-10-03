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
import com.vantiv.triposmobilesdk.enums.GiftProgramType;
import com.vantiv.triposmobilesdk.exceptions.NotInitializedException;
import com.vantiv.triposmobilesdk.exceptions.VTPRequestInProcessException;
import com.vantiv.triposmobilesdk.requests.SaleRequest;
import com.vantiv.triposmobilesdk.responses.HostedPaymentResponse;
import com.vantiv.triposmobilesdk.responses.SaleResponse;

import java.math.BigDecimal;

public class HostedPaymentsSaleFragment extends SingleButtonFragment implements HostedPaymentRequestListener {

    public static final String HOSTED_PAYMENTS_SALE = "Hosted Payments Sale";
    private static final String TAG = HostedPaymentsSaleFragment.class.getSimpleName();

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

        SaleRequest saleRequest = setupSaleRequest();

        try {
            sharedVtp.processHostedPaymentSaleRequest(saleRequest, this);
        } catch (VTPRequestInProcessException e)
        {
            e.printStackTrace();
        }
        catch (NotInitializedException e)
        {
            e.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return true;
    }

    private SaleRequest setupSaleRequest() {
        final SaleRequest saleRequest = new SaleRequest();
        saleRequest.setTransactionAmount(new BigDecimal("10.00"));
        saleRequest.setLaneNumber("1");
        saleRequest.setReferenceNumber("1234567890A");
        saleRequest.setCardholderPresentCode(CardHolderPresentCode.Present);
        saleRequest.setClerkNumber("123456");
        saleRequest.setConvenienceFeeAmount(new BigDecimal("1.50"));
        saleRequest.setSalesTaxAmount(new BigDecimal("0.50"));
        saleRequest.setShiftID("9876");
        saleRequest.setTicketNumber("5555");
        saleRequest.setGiftProgramType(GiftProgramType.Gift);
        saleRequest.setPinLessposConversionIndicator(false);
        return saleRequest;
    }

    /**
     * Hosted Payments Sale request Callback
     */
    @Override
    public void onHostedPaymentRequestCompleted(HostedPaymentResponse hostedPaymentResponse) {

        Log.d(TAG, "HostedPaymentsURL: " + hostedPaymentResponse.getHostedPaymentsURL());
        handleResponse("");
        Intent intent = new Intent(getActivity(), HostedPaymentsWebViewActivity.class);
        intent.putExtra(HostedPaymentsWebViewActivity.HOSTED_PAYMENTS_TITLE, HOSTED_PAYMENTS_SALE);
        intent.putExtra(HostedPaymentsWebViewActivity.HOSTED_PAYMENTS_URL, hostedPaymentResponse.getHostedPaymentsURL());
        activityResultLauncher.launch(intent);
    }

    /**
     * Hosted Payments Sale request Callback
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
                        SaleResponse saleResponse = new Gson().fromJson(responseData, SaleResponse.class);
                        handleResponse(ReflectionUtils.recursiveToString(saleResponse));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        handleResponse(e.getMessage());
                    }
                }
            });

}