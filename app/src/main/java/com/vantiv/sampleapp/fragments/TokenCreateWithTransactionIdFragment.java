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
import com.vantiv.sampleapp.singlebuttoncomponent.SingleButtonFragment;
import com.vantiv.sampleapp.triPOSConfig;
import com.vantiv.triposmobilesdk.CreateTokenWithTransactionIdRequestListener;
import com.vantiv.triposmobilesdk.enums.CardHolderPresentCode;
import com.vantiv.triposmobilesdk.express.TokenProvider;
import com.vantiv.triposmobilesdk.requests.CreateTokenWithTransactionIdRequest;
import com.vantiv.triposmobilesdk.responses.CreateTokenWithTransactionIdResponse;

public class TokenCreateWithTransactionIdFragment extends SingleButtonFragment implements CreateTokenWithTransactionIdRequestListener
{
    EditText transactionIdEditText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);

        actionButton.setOnClickListener(actionButtonOnClickListener);

        addInputOptions(rootView);

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


    void addInputOptions(View view)
    {
        LinearLayout addedStuffLinearLayout = (LinearLayout)view.findViewById(R.id.addedStuffLinearLayout);

        LinearLayout.LayoutParams addedStuffLinearLayoutParams = (LinearLayout.LayoutParams)addedStuffLinearLayout.getLayoutParams();
        addedStuffLinearLayoutParams.setMargins(0, 20, 0, 0);
        addedStuffLinearLayout.setLayoutParams(addedStuffLinearLayoutParams);

        TextView prompt = new TextView(getActivity());
        prompt.setText("Enter transaction ID");

        addedStuffLinearLayout.addView(prompt);

        transactionIdEditText = new EditText(getActivity());

        addedStuffLinearLayout.addView(transactionIdEditText);
    }

    public boolean sendAction()
    {

        if (!super.sendAction())
        {
            return false;
        }

        try
        {

            CreateTokenWithTransactionIdRequest requestObj = setupCreateOmniTokenWithTransactionIdRequest(transactionIdEditText.getText().toString());

            sharedVtp.processCreateTokenWithTransactionIdRequest(requestObj, this);
        }
        catch (Exception e)
        {
            handleResponse(e.getMessage());
            e.printStackTrace();
        }

        return true;
    }

    private CreateTokenWithTransactionIdRequest setupCreateOmniTokenWithTransactionIdRequest(String transactionId)
    {

        final CreateTokenWithTransactionIdRequest request = new CreateTokenWithTransactionIdRequest();

        request.setTransactionId(transactionId);
        request.setTokenProvider(TokenProvider.OmniToken);
        request.setVaultId(triPOSConfig.getSharedConfig().getHostConfiguration().getVaultId());

        // default values
        request.setLaneNumber("1");
        request.setReferenceNumber("1234567890A");
        request.setCardholderPresentCode(CardHolderPresentCode.Present);
        request.setClerkNumber("123456");
        request.setShiftID("9876");
        request.setTicketNumber("5555");

        return request;
    }

    /**
     * Create token with transaction id request Callbacks
     */
    @Override
    public void onCreateTokenWithTransactionIdRequestCompleted(CreateTokenWithTransactionIdResponse response)
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
    public void onCreateTokenWithTransactionIdRequestError(Exception exception)
    {
        String errorMessage = exception.toString();

        if (exception.getCause() != null)
        {
            errorMessage += "\n--->\n" + exception.getCause().toString();
        }

        handleResponse(errorMessage);
    }

}
