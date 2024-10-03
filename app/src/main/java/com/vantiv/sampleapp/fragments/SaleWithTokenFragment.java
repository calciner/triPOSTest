package com.vantiv.sampleapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vantiv.sampleapp.ReflectionUtils;
import com.vantiv.triposmobilesdk.SaleWithTokenRequestListener;
import com.vantiv.triposmobilesdk.enums.CardHolderPresentCode;
import com.vantiv.triposmobilesdk.express.TokenProvider;
import com.vantiv.triposmobilesdk.requests.SaleWithTokenRequest;
import com.vantiv.triposmobilesdk.responses.SaleWithTokenResponse;

import java.math.BigDecimal;

public class SaleWithTokenFragment extends WithTokenBaseFragment implements SaleWithTokenRequestListener
{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);

        return rootView;
    }

    @Override
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

        SaleWithTokenRequest requestObj = setupSaleWithTokenRequest();

        try
        {
            sharedVtp.processSaleWithTokenRequest(requestObj, this);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return true;
    }

    private SaleWithTokenRequest setupSaleWithTokenRequest()
    {
        final SaleWithTokenRequest request = new SaleWithTokenRequest();

        request.setTokenId(tokenEditText.getText().toString());
        request.setTokenProvider(TokenProvider.OmniToken);

        request.setTransactionAmount(new BigDecimal("10.00"));
        request.setReferenceNumber("1234567890A");

        request.setCardLogo(cardLogoSpinner.getSelectedItem().toString());

        request.setCardholderPresentCode(CardHolderPresentCode.NotPresent);
        request.setLaneNumber("1");
        request.setClerkNumber("123456");
        request.setShiftID("9876");
        request.setTicketNumber("5555");
        return request;
    }

    @Override
    public void onSaleWithTokenRequestCompleted(SaleWithTokenResponse saleWithTokenResponse)
    {
        try
        {
            handleResponse(ReflectionUtils.recursiveToString(saleWithTokenResponse));
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
            handleResponse(e.getMessage());
        }
    }

    @Override
    public void onSaleWithTokenRequestError(Exception exception)
    {
        String errorMessage = exception.toString();

        if (exception.getCause() != null)
        {
            errorMessage += "\n--->\n" + exception.getCause().toString();
        }

        handleResponse(errorMessage);
    }
}
