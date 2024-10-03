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
import com.vantiv.sampleapp.singlebuttoncomponent.SingleButtonFragment;
import com.vantiv.triposmobilesdk.CardInputDevice;
import com.vantiv.triposmobilesdk.CreateTokenRequestListener;
import com.vantiv.triposmobilesdk.DisplayDevice;
import com.vantiv.triposmobilesdk.VtpStatus;
import com.vantiv.triposmobilesdk.enums.CardHolderPresentCode;
import com.vantiv.triposmobilesdk.enums.TokenType;
import com.vantiv.triposmobilesdk.requests.CreateTokenRequest;
import com.vantiv.triposmobilesdk.responses.CreateTokenResponse;

public class CreateTokenFragment extends SingleButtonFragment implements CreateTokenRequestListener
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

        if (device == null)
        {
            handleResponse(getResources().getString(R.string.device_was_null));
            return false;
        }

        if (!(device instanceof CardInputDevice))
        {
            handleResponse("Device is not card input device");
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

        try
        {
            sharedVtp.processCreateTokenRequest(setupCreateOmniTokenRequest(), this, this);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    public static CreateTokenRequest setupCreateOmniTokenRequest()
    {
        final CreateTokenRequest request = new CreateTokenRequest();
        request.setLaneNumber("1");
        request.setReferenceNumber("1234567890A");
        request.setCardholderPresentCode(CardHolderPresentCode.Present);
        request.setClerkNumber("123456");
        request.setShiftID("9876");
        request.setTicketNumber("5555");
        request.setTokenType(TokenType.OmniToken);

        return request;
    }

    /**
     * Create OmniToken request Callbacks
     */
    @Override
    public void onCreateTokenRequestCompleted(CreateTokenResponse response)
    {
        try
        {
            getActivity().runOnUiThread(() -> {
                dismissInteractiveDialogs();
                manageKeyedUI(true);
            });

            handleResponse(ReflectionUtils.recursiveToString(response));
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
            handleResponse(e.getMessage());
        }
    }

    @Override
    public void onCreateTokenRequestError(Exception exception)
    {
        String errorMessage = exception.toString();

        if (exception.getCause() != null)
        {
            errorMessage += "\n--->\n" + exception.getCause().toString();
        }

        getActivity().runOnUiThread(() -> {
            dismissInteractiveDialogs();
            manageKeyedUI(true);
        });

        handleResponse(errorMessage);
    }
}
