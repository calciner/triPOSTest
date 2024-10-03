package com.vantiv.sampleapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vantiv.sampleapp.ReflectionUtils;
import com.vantiv.sampleapp.singlebuttoncomponent.SingleButtonFragment;
import com.vantiv.sampleapp.triPOSConfig;
import com.vantiv.triposmobilesdk.HealthCheckRequestListener;
import com.vantiv.triposmobilesdk.enums.CardHolderPresentCode;
import com.vantiv.triposmobilesdk.requests.HealthCheckRequest;
import com.vantiv.triposmobilesdk.responses.HealthCheckResponse;

public class HealthCheckFragment extends SingleButtonFragment implements HealthCheckRequestListener
{

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

        try
        {

            HealthCheckRequest request = setupHealthCheckRequest();

            sharedVtp.processHealthCheckRequest(request, this);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return true;
    }

    /**
     * healthCheck request
     **/
    private HealthCheckRequest setupHealthCheckRequest()
    {
        HealthCheckRequest healthCheckRequest = new HealthCheckRequest();
        healthCheckRequest.setCredentials(triPOSConfig.getCredentials());
        healthCheckRequest.setApplication(triPOSConfig.getApp());

        //Extra
        healthCheckRequest.setCardholderPresentCode(CardHolderPresentCode.Present);

        return healthCheckRequest;
    }

    /**
     * Request Callbacks
     **/
    @Override
    public void onHealthCheckRequestCompleted(HealthCheckResponse healthCheckResponse)
    {
        try
        {
            handleResponse(ReflectionUtils.recursiveToString(healthCheckResponse));
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
            handleResponse(e.getMessage());
        }
    }

    @Override
    public void onHealthCheckRequestError(Exception exception)
    {
        String errorMessage = exception.toString();

        if (exception.getCause() != null)
        {
            errorMessage += "\n--->\n" + exception.getCause().toString();
        }

        handleResponse(errorMessage);
    }

}
