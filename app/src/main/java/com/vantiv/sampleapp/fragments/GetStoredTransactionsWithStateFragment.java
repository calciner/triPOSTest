package com.vantiv.sampleapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.vantiv.triposmobilesdk.storeandforward.StoredTransactionRecord;
import com.vantiv.triposmobilesdk.storeandforward.StoredTransactionState;
import com.vantiv.sampleapp.R;
import com.vantiv.sampleapp.ReflectionUtils;
import com.vantiv.sampleapp.singlebuttoncomponent.SingleButtonFragment;

import java.util.ArrayList;

public class GetStoredTransactionsWithStateFragment extends SingleButtonFragment
{
    Spinner storedTransactionStateSpinner;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);

        actionButton.setOnClickListener(actionButtonOnClickListener);

        addWidgets(rootView);

        return rootView;
    }

    void addWidgets(View view)
    {
        LinearLayout addedStuffLinearLayout = (LinearLayout)view.findViewById(R.id.addedStuffLinearLayout);
        LinearLayout.LayoutParams addedStuffLinearLayoutParams = (LinearLayout.LayoutParams)addedStuffLinearLayout.getLayoutParams();
        addedStuffLinearLayoutParams.setMargins(0, 10, 0, 0);
        addedStuffLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        addedStuffLinearLayout.setLayoutParams(addedStuffLinearLayoutParams);

        TextView storedTransactionStatePrompt = new TextView(getActivity());
        storedTransactionStatePrompt.setText("Select stored transaction state:");

        addedStuffLinearLayout.addView(storedTransactionStatePrompt);

        ArrayAdapter<StoredTransactionState> numericInputPromptIdsAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, StoredTransactionState.values());
        storedTransactionStateSpinner = new Spinner(getActivity());
        storedTransactionStateSpinner.setAdapter(numericInputPromptIdsAdapter);

        addedStuffLinearLayout.addView(storedTransactionStateSpinner);
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

        ArrayList<StoredTransactionRecord> transactions;

        try
        {
            transactions = sharedVtp.getStoredTransactionsWithState((StoredTransactionState)storedTransactionStateSpinner.getSelectedItem());
        }
        catch (Exception e)
        {
            handleResponse(e.getLocalizedMessage());

            return false;
        }

        handleResponse(transactions);

        return true;
    }

    public void handleResponse(final ArrayList<StoredTransactionRecord> transactions)
    {
        if (outputTextView == null)
        {
            return;
        }

        if (transactions.size() <= 0)
        {
            getActivity().runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    outputTextView.setText("No transactions were found");

                    showOutputTextView();
                    hideProgressBarSpinner();
                }
            });

            return;
        }

        StringBuilder response = new StringBuilder();

        for (StoredTransactionRecord record : transactions)
        {
            try
            {
                response.append(ReflectionUtils.recursiveToString(record));
            }
            catch (IllegalAccessException e)
            {
                e.printStackTrace();
            }
        }

        final String finalResult = response.toString();

        getActivity().runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                outputTextView.setText(finalResult);

                showOutputTextView();
                hideProgressBarSpinner();
            }
        });
    }
}
