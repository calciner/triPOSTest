package com.vantiv.sampleapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.vantiv.sampleapp.R;
import com.vantiv.sampleapp.singlebuttoncomponent.SingleButtonFragment;
import com.vantiv.sampleapp.triPOSConfig;
import com.vantiv.triposmobilesdk.express.Card;
import com.vantiv.triposmobilesdk.express.Express;
import com.vantiv.triposmobilesdk.express.Terminal;
import com.vantiv.triposmobilesdk.express.Token;
import com.vantiv.triposmobilesdk.express.TokenProvider;
import com.vantiv.triposmobilesdk.express.Transaction;

import java.math.BigDecimal;

public class WithTokenBaseFragment extends SingleButtonFragment implements Express.Listener
{
    EditText tokenEditText;

    Spinner cardLogoSpinner;

    enum CardLogo
    {
        Unknown,
        Visa,
        MasterCard,
        Amex,
        Discover,
        Diners,
        StoredValue,
        Other,
        JCB,
        CarteBlanche,
        Interac
    }

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

    public boolean sendAction()
    {
        return super.sendAction();
    }

    void addInputOptions(View view)
    {
        LinearLayout addedStuffLinearLayout = (LinearLayout)view.findViewById(R.id.addedStuffLinearLayout);

        LinearLayout.LayoutParams addedStuffLinearLayoutParams = (LinearLayout.LayoutParams)addedStuffLinearLayout.getLayoutParams();
        addedStuffLinearLayoutParams.setMargins(0, 20, 0, 0);

        addedStuffLinearLayout.setLayoutParams(addedStuffLinearLayoutParams);

        TextView prompt = new TextView(getActivity());
        prompt.setText("Enter token");

        addedStuffLinearLayout.addView(prompt);

        tokenEditText = new EditText(getActivity());

        addedStuffLinearLayout.addView(tokenEditText);

        ArrayAdapter<CardLogo> cardLogosAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, CardLogo.values());
        cardLogoSpinner = new Spinner(getActivity());
        cardLogoSpinner.setAdapter(cardLogosAdapter);

        addedStuffLinearLayout.addView(cardLogoSpinner);
    }

    Token makeToken()
    {
        Token token = new Token();
        token.setTokenProvider(TokenProvider.OmniToken);
        token.setVaultId(triPOSConfig.getSharedConfig().getHostConfiguration().getVaultId());
        token.setTokenId(this.tokenEditText.getText().toString());

        return token;
    }

    Transaction makeTransaction()
    {
        Transaction transaction = new Transaction();
        transaction.setTransactionAmount(new BigDecimal("10.00"));
        transaction.setReferenceNumber("12345678");
        transaction.setMarketCode(Transaction.MarketCode.Retail);
        transaction.setDuplicateCheckDisableFlag(Express.BooleanType.True);

        return transaction;
    }

    Terminal makeTerminal()
    {
        Terminal terminal = new Terminal();
        terminal.setTerminalID("1");
        terminal.setTerminalType(Terminal.Type.Mobile);
        terminal.setCardPresentCode(Terminal.CardPresentCode.NotPresent);
        terminal.setCardholderPresentCode(Terminal.CardHolderPresentCode.NotPresent);
        terminal.setCardInputCode(Terminal.CardInputCode.ManualKeyed);
        terminal.setCVVPresenceCode(Terminal.CVVPresenceCode.Default);
        terminal.setTerminalCapabilityCode(Terminal.CapabilityCode.Default);
        terminal.setTerminalEnvironmentCode(Terminal.EnvironmentCode.Default);
        terminal.setMotoECICode(Terminal.MotoECICode.NotUsed);

        return terminal;
    }

    Card makeCard()
    {
        Card card = new Card();
        card.setCardLogo(Integer.toString(cardLogoSpinner.getSelectedItemPosition()));

        return card;
    }

    String cardLogoLabelForSelectedItem()
    {
        return cardLogoSpinner.getSelectedItem().toString();
    }

    /**
     * Express Callbacks
     **/
    @Override
    public void onCompleted(Express.ExpressCompletedEvent e)
    {
        handleResponse(e.getRawXml());
    }

    @Override
    public void onError(Express.ExpressErrorEvent e)
    {
        handleResponse(e.getDescription());
    }
}
