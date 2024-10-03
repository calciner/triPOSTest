package com.vantiv.sampleapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vantiv.sampleapp.ReflectionUtils;
import com.vantiv.sampleapp.triPOSConfig;
import com.vantiv.triposmobilesdk.RefundWithTokenRequestListener;
import com.vantiv.triposmobilesdk.enums.CardHolderPresentCode;
import com.vantiv.triposmobilesdk.enums.CardInputCode;
import com.vantiv.triposmobilesdk.enums.CardPresentCode;
import com.vantiv.triposmobilesdk.express.TokenProvider;
import com.vantiv.triposmobilesdk.requests.RefundWithTokenRequest;
import com.vantiv.triposmobilesdk.responses.RefundWithTokenResponse;

import java.math.BigDecimal;

public class RefundWithTokenFragment extends WithTokenBaseFragment implements RefundWithTokenRequestListener
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

        try
        {
            RefundWithTokenRequest request = setUpRefundWithTokenRequest();
            sharedVtp.processRefundWithTokenRequest(request, this);
        }
        catch (Exception e)
        {
            handleResponse(e.getMessage());
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public void onRefundWithTokenRequestCompleted(RefundWithTokenResponse refundWithTokenResponse)
    {
        try
        {
            handleResponse(ReflectionUtils.recursiveToString(refundWithTokenResponse));
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onRefundWithTokenRequestError(Exception exception)
    {
        handleResponse(exception.getMessage());
    }

    private RefundWithTokenRequest setUpRefundWithTokenRequest()
    {
        RefundWithTokenRequest request = new RefundWithTokenRequest();
        request.setTokenId(this.tokenEditText.getText().toString());
        request.setTokenProvider(TokenProvider.OmniToken);
        request.setCardLogo(cardLogoSpinner.getSelectedItem().toString());
        request.setVaultId(triPOSConfig.getSharedConfig().getHostConfiguration().getVaultId());
        request.setTransactionAmount(new BigDecimal("10.00"));
        request.setLaneNumber("1");
        request.setReferenceNumber("12345678");
        request.setCardInputCode(CardInputCode.ManualKeyed);
        request.setCardPresentCode(CardPresentCode.NotPresent);
        request.setCardholderPresentCode(CardHolderPresentCode.NotPresent);

        return request;
    }
}
