package com.vantiv.sampleapp.fragments;

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
import com.vantiv.triposmobilesdk.CreditCardAdjustmentRequestListener;
import com.vantiv.triposmobilesdk.enums.CardHolderPresentCode;
import com.vantiv.triposmobilesdk.requests.CreditCardAdjustmentRequest;
import com.vantiv.triposmobilesdk.responses.CreditCardAdjustmentResponse;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;


public class CreditCardAdjustmentFragment extends SingleButtonFragment implements CreditCardAdjustmentRequestListener {

    private EditText transactionIdEditText, transactionAmountEditText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);

        actionButton.setOnClickListener(actionButtonOnClickListener);

        addInputOptions(rootView);

        transactionIdEditText.setText(SharedData.getLastTransactionId());
        if (SharedData.getLastTransactionAmount() != null) {
            transactionAmountEditText.setText(SharedData.getLastTransactionAmount().toString());
        }
        else
        {
            transactionAmountEditText.setText("0.00");
        }

        return rootView;
    }

    private void addInputOptions(View view) {
        LinearLayout addedStuffLinearLayout = (LinearLayout) view.findViewById(R.id.addedStuffLinearLayout);

        LinearLayout.LayoutParams addedStuffLinearLayoutParams = (LinearLayout.LayoutParams) addedStuffLinearLayout.getLayoutParams();

        addedStuffLinearLayoutParams.setMargins(0, 20, 0, 0);

        addedStuffLinearLayout.setLayoutParams(addedStuffLinearLayoutParams);

        TextView amount = new TextView(getActivity());

        amount.setText("Enter transaction amount");

        addedStuffLinearLayout.addView(amount);

        transactionAmountEditText = new EditText(getActivity());

        transactionAmountEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        addedStuffLinearLayout.addView(transactionAmountEditText);


        TextView prompt = new TextView(getActivity());

        prompt.setText("Enter transaction ID");

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

        CreditCardAdjustmentRequest creditCardAdjustmentRequest = setupCreditCardAdjustmentRequest();

        try {
            sharedVtp.processCreditCardAdjustmentRequest(creditCardAdjustmentRequest, this);
        } catch (Exception e) {
            handleResponse(e.getMessage());
            e.printStackTrace();
        }

        return true;
    }

    private CreditCardAdjustmentRequest setupCreditCardAdjustmentRequest() {
        final CreditCardAdjustmentRequest request = new CreditCardAdjustmentRequest();
        request.setTransactionAmount(new BigDecimal(StringUtils.isEmpty(transactionAmountEditText.getText()) ? "0.00" : transactionAmountEditText.getText().toString()));
        request.setTransactionId(transactionIdEditText.getText().toString().trim());
        request.setReferenceNumber("1234567890A");
        request.setTicketNumber("5555");
        request.setLaneNumber("1");
        request.setCardholderPresentCode(CardHolderPresentCode.Present);
        request.setClerkNumber("123456");
        request.setShiftID("9876");
        return request;
    }

    /**
     * CreditCardAdjustment request Callbacks
     */
    @Override
    public void onCreditCardAdjustmentRequestCompleted(CreditCardAdjustmentResponse creditCardAdjustmentResponse) {
        try {
            handleResponse(ReflectionUtils.recursiveToString(creditCardAdjustmentResponse));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            handleResponse(e.getMessage());
        }
    }

    @Override
    public void onCreditCardAdjustmentRequestError(Exception e) {
        handleResponse(e.getMessage());
    }
}
