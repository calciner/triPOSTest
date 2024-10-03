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
import com.vantiv.triposmobilesdk.AuthorizationCompletionRequestListener;
import com.vantiv.triposmobilesdk.enums.CardHolderPresentCode;
import com.vantiv.triposmobilesdk.enums.CardInputCode;
import com.vantiv.triposmobilesdk.enums.CardPresentCode;
import com.vantiv.triposmobilesdk.requests.AuthorizationCompletionRequest;
import com.vantiv.triposmobilesdk.responses.AuthorizationCompletionResponse;

import java.math.BigDecimal;

public class AuthorizationCompletionFragment extends SingleButtonFragment implements AuthorizationCompletionRequestListener
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

        addedStuffLinearLayoutParams.setMargins(0, 0, 0, 0);

        addedStuffLinearLayout.setLayoutParams(addedStuffLinearLayoutParams);

        infoTextView = new TextView(getActivity());
        infoTextView.setText("Please first perform a Authorization, which caches the transaction Id. Then send the request.\n");
        infoTextView.setTextColor(getResources().getColor(R.color.black));

        addedStuffLinearLayout.addView(infoTextView);
    }

    public boolean sendAction()
    {
        if (sharedVtp == null)
        {
            handleResponse("sharedVtp is null");
            return false;
        }

        if (!super.sendAction())
        {
            return false;
        }

        try
        {
            sharedVtp.processAuthorizationCompletionRequest(setupAuthorizationCompletionRequest(), this);
        }
        catch (Exception e)
        {
            handleResponse(e.getMessage());
            e.printStackTrace();
        }

        return true;
    }

    public AuthorizationCompletionRequest setupAuthorizationCompletionRequest()
    {
        final AuthorizationCompletionRequest request = new AuthorizationCompletionRequest();

        request.setLaneNumber("1");
        request.setReferenceNumber("1234567890A");
        request.setClerkNumber("123456");
        request.setShiftID("9876");
        request.setTicketNumber("5555");
        request.setCardholderPresentCode(CardHolderPresentCode.Present);
        request.setCardPresentCode(CardPresentCode.Present);
        request.setCardInputCode(CardInputCode.MagstripeRead);
        request.setTransactionId(transactionIdEditText.getText().toString());
        request.setTransactionAmount(new BigDecimal(transactionAmtEditText.getText().toString()));

        return request;
    }

    /**
     * Authorization Completion request Callbacks
     */
    @Override
    public void onAuthorizationCompletionRequestCompleted(AuthorizationCompletionResponse response)
    {
        try
        {
            handleResponse(ReflectionUtils.recursiveToString(response));
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
            handleResponse(e.getMessage());
        }
    }

    @Override
    public void onAuthorizationCompletionRequestError(Exception exception)
    {
        String errorMessage = exception.toString();

        if (exception.getCause() != null)
        {
            errorMessage += "\n--->\n" + exception.getCause().toString();
        }

        handleResponse(errorMessage);
    }

    public static AuthorizationCompletionRequest resetAuthorizationCompletionRequest()
    {
        AuthorizationCompletionRequest authorizationCompletionRequest = new AuthorizationCompletionRequest();

        authorizationCompletionRequest.setLaneNumber("1");
        authorizationCompletionRequest.setReferenceNumber("1234567890A");
        authorizationCompletionRequest.setTicketNumber("5555");
        authorizationCompletionRequest.setClerkNumber("123456");
        authorizationCompletionRequest.setShiftID("9876");
        authorizationCompletionRequest.setCardholderPresentCode(CardHolderPresentCode.Present);
        authorizationCompletionRequest.setCardPresentCode(CardPresentCode.Present);
        authorizationCompletionRequest.setCardInputCode(CardInputCode.MagstripeRead);
        authorizationCompletionRequest.setTransactionAmount(new BigDecimal("10.00"));
        return authorizationCompletionRequest;

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

}
