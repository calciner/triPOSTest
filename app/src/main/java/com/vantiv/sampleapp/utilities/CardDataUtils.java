package com.vantiv.sampleapp.utilities;

import com.vantiv.triposmobilesdk.CardData;
import com.vantiv.triposmobilesdk.EncryptedKeyedCardData;
import com.vantiv.triposmobilesdk.EncryptedSwipedCardData;
import com.vantiv.triposmobilesdk.enums.CardDataEncryption;
import com.vantiv.triposmobilesdk.express.Card;
import com.vantiv.triposmobilesdk.express.Terminal;

import org.apache.commons.lang3.StringUtils;

public class CardDataUtils
{
    public static Card makeCardFromCardData(CardData cardData)
    {
        Card card = new Card();

        if (cardData instanceof EncryptedSwipedCardData)
        {
            String encryptedTrack2 = ((EncryptedSwipedCardData)cardData).getEncryptedTrack2();
            String encryptedTrack1 = ((EncryptedSwipedCardData)cardData).getEncryptedTrack1();

            if (StringUtils.isNotBlank(encryptedTrack2))
            {
                card.setEncryptedTrack2Data(encryptedTrack2);
                card.setCardDataKeySerialNumber(((EncryptedSwipedCardData)cardData).getEncryptedTrack2KeySerialNumber());
            }
            else if (StringUtils.isNotBlank(encryptedTrack1))
            {
                card.setEncryptedTrack1Data(encryptedTrack1);
                card.setCardDataKeySerialNumber(((EncryptedSwipedCardData)cardData).getEncryptedTrack1KeySerialNumber());
            }

            card.setEncryptedFormat(getCardEncryptedFormatWithCardDataEncryption(((EncryptedSwipedCardData)cardData).getEncryption()));

            return card;
        }

        if (cardData instanceof EncryptedKeyedCardData)
        {
            card.setEncryptedCardData(((EncryptedKeyedCardData)cardData).getEncryptedData());
            card.setCardDataKeySerialNumber(((EncryptedKeyedCardData)cardData).getEncryptedDataKeySerialNumber());
            card.setEncryptedFormat(getCardEncryptedFormatWithCardDataEncryption(((EncryptedKeyedCardData)cardData).getEncryption()));

            return card;
        }

        return card;
    }

    private static Card.EncryptedFormat getCardEncryptedFormatWithCardDataEncryption(CardDataEncryption cardDataEncryption)
    {
        switch (cardDataEncryption)
        {
            case DataVariantEpsFormat:
                return Card.EncryptedFormat.Format4;
            case PinVariantMercuryFormat:
                return Card.EncryptedFormat.Format5;
            case DataVariantEpsMxFormat:
                return Card.EncryptedFormat.Format7;
            default:
            case PinVariantEpsFormat:
                return Card.EncryptedFormat.Default;
        }
    }

    public static Terminal makeTerminal()
    {
        Terminal terminal = new Terminal();

        terminal.setTerminalID("1");
        terminal.setLaneNumber("1");
        terminal.setTerminalType(Terminal.Type.Mobile);
        terminal.setCardPresentCode(Terminal.CardPresentCode.Present);
        terminal.setCardholderPresentCode(Terminal.CardHolderPresentCode.Present);
        terminal.setCVVPresenceCode(Terminal.CVVPresenceCode.Default);
        terminal.setTerminalCapabilityCode(Terminal.CapabilityCode.Default);
        terminal.setTerminalEnvironmentCode(Terminal.EnvironmentCode.Default);
        terminal.setMotoECICode(Terminal.MotoECICode.NotUsed);
        terminal.setCardInputCode(Terminal.CardInputCode.MagstripeRead);

        return terminal;
    }
}
