package com.vantiv.sampleapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vantiv.sampleapp.ReflectionUtils;
import com.vantiv.triposmobilesdk.AuthorizationWithTokenRequestListener;
import com.vantiv.triposmobilesdk.enums.CardHolderPresentCode;
import com.vantiv.triposmobilesdk.express.TokenProvider;
import com.vantiv.triposmobilesdk.requests.AuthorizationWithTokenRequest;
import com.vantiv.triposmobilesdk.responses.AuthorizationWithTokenResponse;

import java.math.BigDecimal;

public class AuthorizationWithTokenFragment extends WithTokenBaseFragment implements AuthorizationWithTokenRequestListener
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

        try
        {

            AuthorizationWithTokenRequest requestObj = setupAuthorizationWithTokenRequest(tokenEditText.getText().toString(), cardLogoLabelForSelectedItem());

            sharedVtp.processAuthorizationWithTokenRequest(requestObj, this);
        }
        catch (Exception e)
        {
            handleResponse(e.getMessage());
            e.printStackTrace();
        }

        return true;
    }

    private AuthorizationWithTokenRequest setupAuthorizationWithTokenRequest(String tokenId, String cardLogo)
    {
        final AuthorizationWithTokenRequest request = new AuthorizationWithTokenRequest();

        request.setTokenId(tokenId);
        request.setTokenProvider(TokenProvider.OmniToken);

        request.setTransactionAmount(new BigDecimal("10.00"));
        request.setReferenceNumber("1234567890A");

        request.setCardLogo(cardLogo);

        request.setCardholderPresentCode(CardHolderPresentCode.NotPresent);
        request.setLaneNumber("1");
        request.setClerkNumber("123456");
        request.setShiftID("9876");
        request.setTicketNumber("5555");

        return request;
    }

    /**
     * Authorization with token request Callbacks
     */
    @Override
    public void onAuthorizationWithTokenRequestCompleted(AuthorizationWithTokenResponse response)
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
    public void onAuthorizationWithTokenRequestError(Exception exception)
    {
        String errorMessage = exception.toString();

        if (exception.getCause() != null)
        {
            errorMessage += "\n--->\n" + exception.getCause().toString();
        }

        handleResponse(errorMessage);
    }

}
