package com.vantiv.sampleapp.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.vantiv.sampleapp.R;
import com.vantiv.sampleapp.ReflectionUtils;
import com.vantiv.sampleapp.singlebuttoncomponent.SingleButtonFragment;
import com.vantiv.triposmobilesdk.DisplayDevice;
import com.vantiv.triposmobilesdk.PreReadCardDataRequestListener;
import com.vantiv.triposmobilesdk.VtpStatus;
import com.vantiv.triposmobilesdk.enums.TransactionType;
import com.vantiv.triposmobilesdk.responses.PreReadCardDataResponse;

public class PreReadFragment extends SingleButtonFragment implements PreReadCardDataRequestListener
{
    Spinner transactionTypeSpinner;
    final String PreReadButtonText = "Pre-read Card";
    final String CancelPreReadButtonText = "Cancel Pre-read";
    TextView transactionStatusTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);

        transactionStatusTextView = (TextView) rootView.findViewById(R.id.transactionStatusTextView);

        actionButton.setOnClickListener(actionButtonOnClickListener);

        addWidgets(rootView);

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

        try
        {
            if (actionButton.getText().equals(CancelPreReadButtonText))
            {
                if (sharedVtp.cancelPreRead())
                {
                    handleResponse("Pre-read cancelled successfully.");
                    actionButton.setText(PreReadButtonText);
                }
            }
            else
            {
                TransactionType transactionType = TransactionType.valueOf(transactionTypeSpinner.getSelectedItem().toString());
                sharedVtp.processPreReadRequestWithTransactionType(transactionType, this, this);
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return true;
    }

    void addWidgets(View view)
    {
        LinearLayout addedStuffLinearLayout = (LinearLayout)view.findViewById(R.id.addedStuffLinearLayout);
        LinearLayout.LayoutParams addedStuffLinearLayoutParams = (LinearLayout.LayoutParams)addedStuffLinearLayout.getLayoutParams();
        addedStuffLinearLayoutParams.setMargins(0, 10, 0, 0);
        addedStuffLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        addedStuffLinearLayout.setLayoutParams(addedStuffLinearLayoutParams);

        TextView storedTransactionStatePrompt = new TextView(getActivity());
        storedTransactionStatePrompt.setText("Transaction Type:");

        addedStuffLinearLayout.addView(storedTransactionStatePrompt);

        ArrayAdapter<String> numericInputPromptIdsAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, new String[]{"AuthorizationOnly", "Sale", "Refund"});
        transactionTypeSpinner = new Spinner(getActivity());
        transactionTypeSpinner.setAdapter(numericInputPromptIdsAdapter);

        addedStuffLinearLayout.addView(transactionTypeSpinner);
    }

    @Override
    public void onPreReadRequestCompleted(PreReadCardDataResponse preReadCardDataResponse)
    {
        getActivity().runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                dismissInteractiveDialogs();
                manageKeyedUI(true);

                actionButton.setText(CancelPreReadButtonText);
                hideProgressBarSpinner();
                try
                {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(ReflectionUtils.recursiveToString(preReadCardDataResponse.getFinancialCardData()));
                    stringBuilder.append("\n");
                    stringBuilder.append("pinEntered: ");
                    stringBuilder.append(preReadCardDataResponse.isPinEntered());
                    stringBuilder.append("\n");
                    stringBuilder.append("applicationIdentifier: ");
                    stringBuilder.append(preReadCardDataResponse.getApplicationIdentifier());
                    String response = stringBuilder.toString();
                    handleResponse(response);
                }
                catch (IllegalAccessException e)
                {
                    handleResponse("Error displaying response");
                }
            }
        });
    }

    @Override
    public void onPreReadRequestError(Exception e)
    {

        getActivity().runOnUiThread(() -> {
            dismissInteractiveDialogs();
            manageKeyedUI(true);
        });

        handleResponse(e.getMessage());
    }
}
