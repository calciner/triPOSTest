package com.vantiv.sampleapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vantiv.triposmobilesdk.ManuallyForwardRequestListener;
import com.vantiv.triposmobilesdk.VtpProcessStatusListener;
import com.vantiv.triposmobilesdk.VtpStatus;
import com.vantiv.triposmobilesdk.requests.ManuallyForwardRequest;
import com.vantiv.triposmobilesdk.responses.ManuallyForwardResponse;
import com.vantiv.sampleapp.R;
import com.vantiv.sampleapp.ReflectionUtils;
import com.vantiv.sampleapp.singlebuttoncomponent.SingleButtonFragment;

public class ManuallyForwardFragment extends SingleButtonFragment implements ManuallyForwardRequestListener
{
    EditText tpIdEditText;

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

        prompt.setText("Enter TP ID");

        addedStuffLinearLayout.addView(prompt);

        tpIdEditText = new EditText(getActivity());

        addedStuffLinearLayout.addView(tpIdEditText);
    }

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

        try
        {
            ManuallyForwardRequest manuallyForwardRequest = new ManuallyForwardRequest();

            manuallyForwardRequest.setTpId(tpIdEditText.getText().toString());

            sharedVtp.processManuallyForwardRequest(manuallyForwardRequest, this);
        }
        catch (Exception e)
        {
            handleResponse(e.getMessage());
        }

        return true;
    }

    /**
     * Manually Forward Callbacks
     **/
    @Override
    public void onManuallyForwardRequestCompleted(ManuallyForwardResponse manuallyForwardResponse)
    {
        try
        {
            handleResponse(ReflectionUtils.recursiveToString(manuallyForwardResponse));
        }
        catch (IllegalAccessException e)
        {
            handleResponse(e.getMessage());
        }
    }

    @Override
    public void onManuallyForwardRequestError(Exception exception)
    {
        handleResponse(exception.getMessage());
    }
}
