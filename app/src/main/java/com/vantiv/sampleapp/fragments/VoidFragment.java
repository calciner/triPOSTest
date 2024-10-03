package com.vantiv.sampleapp.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vantiv.sampleapp.R;
import com.vantiv.sampleapp.ReflectionUtils;
import com.vantiv.sampleapp.SharedData;
import com.vantiv.sampleapp.singlebuttoncomponent.SingleButtonFragment;
import com.vantiv.triposmobilesdk.VoidRequestListener;
import com.vantiv.triposmobilesdk.enums.CardHolderPresentCode;
import com.vantiv.triposmobilesdk.enums.GiftProgramType;
import com.vantiv.triposmobilesdk.enums.MarketCode;
import com.vantiv.triposmobilesdk.requests.RefundRequest;
import com.vantiv.triposmobilesdk.requests.VoidRequest;
import com.vantiv.triposmobilesdk.responses.VoidResponse;

import java.math.BigDecimal;

public class VoidFragment extends SingleButtonFragment implements VoidRequestListener
{
    private EditText transactionIdEditText;
    TextView infoTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {

        View rootView = super.onCreateView(inflater, container, savedInstanceState);

        actionButton.setOnClickListener(actionButtonOnClickListener);
        addInputOptions(rootView);
        if (SharedData.getLastTransactionAmount() != null)
            transactionIdEditText.setText(SharedData.getLastTransactionId());

        return rootView;
    }

    private void addInputOptions(View view)
    {
        LinearLayout addedStuffLinearLayout = (LinearLayout) view.findViewById(R.id.addedStuffLinearLayout);
        LinearLayout.LayoutParams addedStuffLinearLayoutParams = (LinearLayout.LayoutParams) addedStuffLinearLayout.getLayoutParams();
        addedStuffLinearLayoutParams.setMargins(0, 20, 0, 0);
        addedStuffLinearLayout.setLayoutParams(addedStuffLinearLayoutParams);

        TextView prompt = new TextView(getActivity());
        prompt.setText("Enter transaction ID");
        addedStuffLinearLayout.addView(prompt);

        transactionIdEditText = new EditText(getActivity());
        addedStuffLinearLayout.addView(transactionIdEditText);
        infoTextView = new TextView(getActivity());
        infoTextView.setText("Usage: Please first perform a Sale with a credit card, which caches the transaction ID. Then send the request.");
        addedStuffLinearLayout.addView(infoTextView);
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

        VoidRequest request = setupRequest();

        try
        {
            sharedVtp.processVoidRequest(request, this);
        }
        catch (Exception e)
        {
            handleResponse(e.getMessage());
            e.printStackTrace();
        }

        return true;
    }

    public  VoidRequest setupRequest()
    {
        VoidRequest voidRequest = new VoidRequest();

        voidRequest.setTransactionAmount(SharedData.getLastTransactionAmount());
        voidRequest.setTransactionID(transactionIdEditText.getText().toString().trim());
        voidRequest.setReferenceNumber("1234567890A");
        voidRequest.setMarketCode(MarketCode.Retail);
        voidRequest.setLaneNumber("1");
        voidRequest.setClerkNumber("123456");
        voidRequest.setShiftID("9876");
        voidRequest.setTicketNumber("5555");
        voidRequest.setPinLessposConversionIndicator(false);
        voidRequest.setCardholderPresentCode(CardHolderPresentCode.Present);

        return voidRequest;

    }

    @Override
    public void onVoidRequestCompleted(VoidResponse voidResponse)
    {
        try
        {
            handleResponse(ReflectionUtils.recursiveToString(voidResponse));
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
            handleResponse(e.getMessage());
        }
    }

    @Override
    public void onVoidRequestError(Exception e)
    {
        handleResponse(e.getMessage());
    }
}