package com.vantiv.sampleapp.fragments;

import android.os.Bundle;
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
import com.vantiv.triposmobilesdk.ReturnListener;
import com.vantiv.triposmobilesdk.enums.CardHolderPresentCode;
import com.vantiv.triposmobilesdk.enums.PaymentType;
import com.vantiv.triposmobilesdk.express.Terminal;
import com.vantiv.triposmobilesdk.requests.ReturnRequest;
import com.vantiv.triposmobilesdk.enums.GiftProgramType;
import com.vantiv.triposmobilesdk.responses.ReturnResponse;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

public class CreditCardReturnFragment extends SingleButtonFragment implements ReturnListener
{
    EditText transactionIdEditText;
    EditText transactionAmtEditText;
    TextView infoTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);

        actionButton.setOnClickListener(actionButtonOnClickListener);

        addInfoTextView(rootView);

        addAmountInputOptions(rootView);

        addInputOptions(rootView);

        setStoredTransactionIdAndAmount();

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

    void addAmountInputOptions(View view)
    {
        LinearLayout addedStuffLinearLayout = (LinearLayout)view.findViewById(R.id.addedStuffLinearLayout);

        LinearLayout.LayoutParams addedStuffLinearLayoutParams = (LinearLayout.LayoutParams)addedStuffLinearLayout.getLayoutParams();
        addedStuffLinearLayoutParams.setMargins(0, 40, 0, 0);
        addedStuffLinearLayout.setLayoutParams(addedStuffLinearLayoutParams);

        TextView prompt = new TextView(getActivity());
        prompt.setText("Enter Transaction Amount");

        addedStuffLinearLayout.addView(prompt);

        transactionAmtEditText = new EditText(getActivity());

        addedStuffLinearLayout.addView(transactionAmtEditText);
    }

    void addInputOptions(View view)
    {
        LinearLayout addedStuffLinearLayout = (LinearLayout)view.findViewById(R.id.addedStuffLinearLayout);

        LinearLayout.LayoutParams addedStuffLinearLayoutParams = (LinearLayout.LayoutParams)addedStuffLinearLayout.getLayoutParams();
        addedStuffLinearLayoutParams.setMargins(0, 60, 0, 0);
        addedStuffLinearLayout.setLayoutParams(addedStuffLinearLayoutParams);

        TextView prompt = new TextView(getActivity());
        prompt.setText("Enter Transaction ID");

        addedStuffLinearLayout.addView(prompt);

        transactionIdEditText = new EditText(getActivity());

        addedStuffLinearLayout.addView(transactionIdEditText);
    }


    void addInfoTextView(View view)
    {
        LinearLayout addedStuffLinearLayout = (LinearLayout) view.findViewById(R.id.addedStuffLinearLayout);

        LinearLayout.LayoutParams addedStuffLinearLayoutParams = (LinearLayout.LayoutParams) addedStuffLinearLayout.getLayoutParams();

        addedStuffLinearLayoutParams.setMargins(0, 20, 0, 0);

        addedStuffLinearLayout.setLayoutParams(addedStuffLinearLayoutParams);

        infoTextView = new TextView(getActivity());

        infoTextView.setText("Usage: First perform a Sale with a credit card, which caches the transaction ID. Then send the request.");

        addedStuffLinearLayout.addView(infoTextView);
    }

    private void setStoredTransactionIdAndAmount()
    {
        if(SharedData.getLastTransactionId() != null)
        {
            transactionIdEditText.setText(SharedData.getLastTransactionId());
        }

        if(SharedData.getLastTransactionAmount() != null)
        {
            transactionAmtEditText.setText(SharedData.getLastTransactionAmount().toString());
        }
        else
        {
            transactionAmtEditText.setText("0.00");
        }
    }

    public boolean sendAction()
    {
        if (!super.sendAction())
        {
            return false;
        }

        try
        {
            sharedVtp.processReturnRequest(setUpCreditCardReturnRequest(), this);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return true;
    }

    public ReturnRequest setUpCreditCardReturnRequest()
    {
        final ReturnRequest returnRequest = new ReturnRequest();
        returnRequest.setPinLessposConversionIndicator(false);
        returnRequest.setCardholderPresentCode(CardHolderPresentCode.Present);
        returnRequest.setCardPresentCode(Terminal.CardPresentCode.Present);
        returnRequest.setCardInputCode(Terminal.CardInputCode.MagstripeRead);
        returnRequest.setReferenceNumber("12345678");
        returnRequest.setPaymentType(PaymentType.Credit);
        returnRequest.setGiftProgramType(GiftProgramType.Gift);
        returnRequest.setTransactionAmount(new BigDecimal(StringUtils.isEmpty(transactionAmtEditText.getText()) ? "0.00" : transactionAmtEditText.getText().toString()));
        returnRequest.setTransactionID(transactionIdEditText.getText().toString());

        return returnRequest;
    }

    @Override
    public void onReturnRequestCompleted(ReturnResponse returnResponse)
    {
        try
        {
            handleResponse(ReflectionUtils.recursiveToString(returnResponse));
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
            handleResponse(e.getMessage());
        }
    }

    @Override
    public void onReturnRequestError(Exception exception)
    {
        String errorMessage = exception.toString();

        if (exception.getCause() != null)
        {
            errorMessage += "\n--->\n" + exception.getCause().toString();
        }

        handleResponse(errorMessage);
    }
}
