package com.vantiv.sampleapp.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
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
import com.vantiv.triposmobilesdk.IncrementalAuthorizationRequestListener;
import com.vantiv.triposmobilesdk.enums.CardHolderPresentCode;
import com.vantiv.triposmobilesdk.enums.MarketCode;
import com.vantiv.triposmobilesdk.requests.IncrementalAuthorizationRequest;
import com.vantiv.triposmobilesdk.responses.IncrementalAuthorizationResponse;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

public class IncrementalAuthorizationFragment extends SingleButtonFragment implements IncrementalAuthorizationRequestListener {

    private EditText transactionIdEditText, transactionAmountEditText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);

        actionButton.setOnClickListener(actionButtonOnClickListener);

        addInputOptions(rootView);

        transactionIdEditText.setText(SharedData.getLastTransactionId());
        if (SharedData.getLastTransactionAmount() != null) {
            transactionAmountEditText.setText(SharedData.getLastTransactionAmount().toString());
        } else {
            transactionAmountEditText.setText("0.00");
        }

        return rootView;
    }

    private void addInputOptions(View view) {
        LinearLayout addedStuffLinearLayout = (LinearLayout) view.findViewById(R.id.addedStuffLinearLayout);

        LinearLayout.LayoutParams addedStuffLinearLayoutParams = (LinearLayout.LayoutParams) addedStuffLinearLayout.getLayoutParams();

        addedStuffLinearLayoutParams.setMargins(0, 20, 0, 0);

        addedStuffLinearLayout.setLayoutParams(addedStuffLinearLayoutParams);

        TextView helperText = new TextView(getActivity());

        helperText.setTextColor(Color.parseColor("#000000"));

        helperText.setText("Please first perform a Authorization, which caches the transaction Id. Then send the request.");

        addedStuffLinearLayout.addView(helperText);


        TextView amount = new TextView(getActivity());

        amount.setText("Enter transaction amount");

        addedStuffLinearLayout.addView(amount, addedStuffLinearLayoutParams);

        transactionAmountEditText = new EditText(getActivity());

        transactionAmountEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        addedStuffLinearLayout.addView(transactionAmountEditText);


        TextView prompt = new TextView(getActivity());

        prompt.setText("Enter Authorization transaction ID");

        addedStuffLinearLayout.addView(prompt);

        transactionIdEditText = new EditText(getActivity());

        addedStuffLinearLayout.addView(transactionIdEditText);
    }

    private View.OnClickListener actionButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            hideOutputTextView();
            showProgressBarSpinner();
            sendAction();
        }
    };

    public boolean sendAction() {
        if (!super.sendAction()) {
            return false;
        }

        if (sharedVtp == null) {
            handleResponse("sharedVtp is null");
            return false;
        }

        IncrementalAuthorizationRequest incrementalAuthorizationRequest = setUpIncrementalAuthorizationRequest();

        try {
            sharedVtp.processIncrementalAuthorizationRequest(incrementalAuthorizationRequest, this);
        } catch (Exception e) {
            handleResponse(e.getMessage());
            e.printStackTrace();
        }

        return true;
    }

    private IncrementalAuthorizationRequest setUpIncrementalAuthorizationRequest() {
        IncrementalAuthorizationRequest incrementalAuthorizationRequest = new IncrementalAuthorizationRequest();

        incrementalAuthorizationRequest.setTerminalId("1");
        incrementalAuthorizationRequest.setLaneNumber("1");
        incrementalAuthorizationRequest.setReferenceNumber("1234567890A");
        incrementalAuthorizationRequest.setTicketNumber("5555");
        incrementalAuthorizationRequest.setMarketCode(MarketCode.Retail);
        incrementalAuthorizationRequest.setCardholderPresentCode(CardHolderPresentCode.Present);
        incrementalAuthorizationRequest.setTransactionAmount(new BigDecimal(StringUtils.isEmpty(transactionAmountEditText.getText()) ? "0.00" : transactionAmountEditText.getText().toString()));
        incrementalAuthorizationRequest.setTransactionId(transactionIdEditText.getText().toString().trim());
        return incrementalAuthorizationRequest;
    }

    /**
     * Incremental Authorization request Callbacks
     */
    @Override
    public void onIncrementalAuthorizationRequestCompleted(IncrementalAuthorizationResponse incrementalAuthorizationResponse) {
        try {
            handleResponse(ReflectionUtils.recursiveToString(incrementalAuthorizationResponse));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            handleResponse(e.getMessage());
        }
    }

    @Override
    public void onIncrementalAuthorizationRequestError(Exception e) {
        handleResponse(e.getMessage());
    }
}