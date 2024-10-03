package com.vantiv.sampleapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vantiv.sampleapp.singlebuttoncomponent.SingleButtonFragment;
import com.vantiv.sampleapp.ReflectionUtils;
import com.vantiv.triposmobilesdk.storeandforward.StoredTransactionRecord;

import java.util.ArrayList;

public class GetAllStoredTransactionsFragment extends SingleButtonFragment
{
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

        ArrayList<StoredTransactionRecord> transactions = new ArrayList<>();

        try
        {
            transactions = sharedVtp.getAllStoredTransactions();
        }
        catch (Exception e)
        {
            e.printStackTrace();
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
