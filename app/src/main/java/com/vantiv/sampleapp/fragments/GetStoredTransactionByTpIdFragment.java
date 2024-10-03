package com.vantiv.sampleapp.fragments;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.vantiv.triposmobilesdk.exceptions.StoredTransactionDatabaseErrorException;
import com.vantiv.triposmobilesdk.storeandforward.StoredTransactionRecord;
import com.vantiv.sampleapp.R;
import com.vantiv.sampleapp.ReflectionUtils;
import com.vantiv.sampleapp.singlebuttoncomponent.SingleButtonFragment;

public class GetStoredTransactionByTpIdFragment extends SingleButtonFragment
{
    EditText tpIdEditText;

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

        tpIdEditText = new EditText(getActivity());
        tpIdEditText.setGravity(Gravity.CENTER_HORIZONTAL);
        tpIdEditText.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        tpIdEditText.setHint("tpId");

        addedStuffLinearLayout.addView(tpIdEditText);
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

        StoredTransactionRecord record = new StoredTransactionRecord();

        try
        {
            record = sharedVtp.getStoredTransactionByTpId(tpIdEditText.getText().toString());
        }
        catch (StoredTransactionDatabaseErrorException | Exception e)
        {
            handleResponse(e.getLocalizedMessage());

            return false;
        }

        handleResponse(record);

        return true;
    }

    public void handleResponse(final StoredTransactionRecord transaction)
    {
        if (outputTextView == null)
        {
            return;
        }

        if (transaction == null)
        {
            getActivity().runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    outputTextView.setText("Transaction was null");

                    showOutputTextView();
                    hideProgressBarSpinner();
                }
            });

            return;
        }

        StringBuilder response = new StringBuilder();

        try
        {
            response.append(ReflectionUtils.recursiveToString(transaction));
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
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
