package com.vantiv.sampleapp.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vantiv.sampleapp.R;
import com.vantiv.sampleapp.ReflectionUtils;
import com.vantiv.sampleapp.SharedData;
import com.vantiv.sampleapp.singlebuttoncomponent.SingleButtonFragment;
import com.vantiv.triposmobilesdk.DisplayDevice;
import com.vantiv.triposmobilesdk.SaleRequestListener;
import com.vantiv.triposmobilesdk.VtpProcessStatusListener;
import com.vantiv.triposmobilesdk.VtpStatus;
import com.vantiv.triposmobilesdk.enums.CardHolderPresentCode;
import com.vantiv.triposmobilesdk.enums.DeviceType;
import com.vantiv.triposmobilesdk.enums.GiftProgramType;
import com.vantiv.triposmobilesdk.requests.SaleRequest;
import com.vantiv.triposmobilesdk.responses.SaleResponse;

import java.math.BigDecimal;

public class SaleFragment extends SingleButtonFragment implements SaleRequestListener
{

    Button keyedEntryButton;
    TextView transactionStatusTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);

        transactionStatusTextView = (TextView) rootView.findViewById(R.id.transactionStatusTextView);

        actionButton.setOnClickListener(actionButtonOnClickListener);

        addKeyedEntryControl(rootView);

        return rootView;
    }

    void addKeyedEntryControl(View view)
    {
        LinearLayout addedStuffLinearLayout = (LinearLayout)view.findViewById(R.id.addedStuffLinearLayout);
        LinearLayout.LayoutParams addedStuffLinearLayoutParams = (LinearLayout.LayoutParams)addedStuffLinearLayout.getLayoutParams();
        addedStuffLinearLayoutParams.setMargins(0, 10, 0, 0);
        addedStuffLinearLayout.setOrientation(LinearLayout.VERTICAL);
        addedStuffLinearLayout.setLayoutParams(addedStuffLinearLayoutParams);

        keyedEntryButton = new Button(getActivity());
        keyedEntryButton.setGravity(Gravity.CENTER_HORIZONTAL);
        keyedEntryButton.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        keyedEntryButton.setText("KEYED ENTRY SALE");
        keyedEntryButton.setOnClickListener(keyedSaleButtonOnClickListener);

        int childCount = addedStuffLinearLayout.getChildCount();

        addedStuffLinearLayout.addView(keyedEntryButton, childCount - 1, addedStuffLinearLayoutParams);
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

    private View.OnClickListener keyedSaleButtonOnClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            hideOutputTextView();
            showProgressBarSpinner();

            sendKeyedAction();
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

        SaleRequest saleRequest = setupSaleRequest();

        try
        {
            sharedVtp.processSaleRequest(saleRequest, this, this);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return true;
    }

    public boolean sendKeyedAction()
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

        SaleRequest saleRequest = setupSaleRequest();
        saleRequest.setKeyedOnly(true);

        try
        {
            sharedVtp.processSaleRequest(saleRequest, this, this);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return true;
    }

    private SaleRequest setupSaleRequest()
    {
        final SaleRequest saleRequest = new SaleRequest();
        saleRequest.setTransactionAmount(new BigDecimal("1.31"));
        saleRequest.setLaneNumber("1");
        saleRequest.setReferenceNumber("1234567890A");
        saleRequest.setCardholderPresentCode(CardHolderPresentCode.Present);
        saleRequest.setClerkNumber("123456");
//        saleRequest.setConvenienceFeeAmount(new BigDecimal("1.50"));
//        saleRequest.setSalesTaxAmount(new BigDecimal("0.50"));
        saleRequest.setShiftID("9876");
        saleRequest.setTicketNumber("5555");
        //saleRequest.setTipAmount(new BigDecimal("1.00"));
        saleRequest.setGiftProgramType(GiftProgramType.Gift);
        saleRequest.setPinLessposConversionIndicator(false);
        saleRequest.setSurchargeFeeAmount(new BigDecimal("1.00"));


        return saleRequest;
    }

    /**
     * Sale request Callbacks
     */
    @Override
    public void onSaleRequestCompleted(SaleResponse saleResponse)
    {
        try
        {
            getActivity().runOnUiThread(() -> {
                dismissInteractiveDialogs();
                manageKeyedUI(true);
            });

            handleResponse(ReflectionUtils.recursiveToString(saleResponse));

            if (saleResponse != null && saleResponse.getHost() != null)
            {
                SharedData.setLastTransactionId(saleResponse.getHost().getTransactionID());
                SharedData.setLastTransactionAmount(saleResponse.getApprovedAmount());
            }
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
            handleResponse(e.getMessage());
        }
    }

    @Override
    public void onSaleRequestError(Exception exception)
    {
        getActivity().runOnUiThread(() -> {
            dismissInteractiveDialogs();
            manageKeyedUI(true);
        });
        handleResponse(exception.getMessage());
    }
}
