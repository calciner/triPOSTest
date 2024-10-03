package com.vantiv.sampleapp;

import com.vantiv.triposmobilesdk.ApplicationConfiguration;
import com.vantiv.triposmobilesdk.Configuration;
import com.vantiv.triposmobilesdk.DeviceConfiguration;
import com.vantiv.triposmobilesdk.HostConfiguration;
import com.vantiv.triposmobilesdk.StoreAndForwardConfiguration;
import com.vantiv.triposmobilesdk.TransactionConfiguration;
import com.vantiv.triposmobilesdk.enums.AddressVerificationCondition;
import com.vantiv.triposmobilesdk.enums.ApplicationMode;
import com.vantiv.triposmobilesdk.enums.CurrencyCode;
import com.vantiv.triposmobilesdk.enums.DeviceType;
import com.vantiv.triposmobilesdk.enums.PaymentProcessor;
import com.vantiv.triposmobilesdk.enums.TerminalType;
import com.vantiv.triposmobilesdk.enums.TipSelectionType;
import com.vantiv.triposmobilesdk.express.Application;
import com.vantiv.triposmobilesdk.express.Credentials;

import java.math.BigDecimal;

public class triPOSConfig
{

    private triPOSConfig()
    {
    }

    private static Configuration sharedConfig = null;

    private static Credentials credentials = null;

    private static Application app = null;

    private static DeviceConfiguration deviceConfig = null;

    private static TransactionConfiguration transactionConfig = null;

    private static StoreAndForwardConfiguration storeAndForwardConfiguration = null;

    public static int timeout = 10000;

    public static Configuration getSharedConfig()
    {
        if (sharedConfig == null)
        {
            sharedConfig = new Configuration();
            setupApplicationConfiguration();
            setupHostConfiguration();
            setupDeviceConfiguration();
            setupTransactionConfiguration();
            setupStoreAndForwardConfiguration();
        }

        return sharedConfig;
    }

    private static void setupApplicationConfiguration()
    {
        ApplicationConfiguration appConfig = new ApplicationConfiguration();

        appConfig.setIdlePrompt("triPOS Samples");
        appConfig.setApplicationMode(ApplicationMode.TestCertification);

        sharedConfig.setApplicationConfiguration(appConfig);
    }

    private static void setupHostConfiguration()
    {
        HostConfiguration hostConfig = new HostConfiguration();

        // Express test credentials, see http://www.elementps.com/Create-a-Test-Account
//        credentials = new Credentials("1016486", "85092AAA1C94D9F775A286594F55DDA69478C96B05B9DACC31B1EAD723A61AA62AADE101", "874767052");
        credentials = new Credentials("1287095", "1F182238A00EC5EEF53BA0D444B328CCBEA5025DEF986A0F09DD6B7B3B5948E6CE0BEC01", "364805779");

        hostConfig.setAcceptorId(credentials.getAcceptorID());
        hostConfig.setAccountId(credentials.getAccountID());
        hostConfig.setAccountToken(credentials.getAccountToken());
        hostConfig.setStoreCardId("631522003");
        hostConfig.setStoreCardPassword("xyz");

        // Application config
        app = new Application("19352", "triPOS SampleApp", "0.0.0.0");

        hostConfig.setApplicationId(app.getApplicationID());
        hostConfig.setApplicationName(app.getApplicationName());
        hostConfig.setApplicationVersion(app.getApplicationVersion());
        hostConfig.setVaultId("174");

        hostConfig.setPaymentProcessor(PaymentProcessor.Worldpay);

        sharedConfig.setHostConfiguration(hostConfig);
    }

    private static void setupDeviceConfiguration()
    {
        deviceConfig = new DeviceConfiguration();

        deviceConfig.setContactlessAllowed(true);
        deviceConfig.setDeviceType(DeviceType.IngenicoRuaBluetooth);
        deviceConfig.setKeyedEntryAllowed(true);
        deviceConfig.setTerminalId("1234");
        deviceConfig.setTerminalType(TerminalType.Mobile);

        // Bluetooth configuration
        deviceConfig.setIdentifier("MOB55-K0187468");

        // TCP/IP configuration
        //TcpIpConfiguration tcpIpConfiguration = new TcpIpConfiguration();
//        tcpIpConfiguration.setIpAddress("10.0.1.16");
//        tcpIpConfiguration.setPort(12000);
        //deviceConfig.setTcpIpConfiguration(tcpIpConfiguration);
        sharedConfig.setDeviceConfiguration(deviceConfig);
    }

    private static void setupTransactionConfiguration()
    {
        transactionConfig = new TransactionConfiguration();

        transactionConfig.setAddressVerificationCondition(AddressVerificationCondition.Keyed);
        transactionConfig.setAmountConfirmationEnabled(true);
        transactionConfig.setConvenienceFeeConfirmationEnabled(false);
        transactionConfig.setEmvAllowed(true);
        transactionConfig.setTipAllowed(true);
        transactionConfig.setTipEntryAllowed(true);
        transactionConfig.setTipSelectionType(TipSelectionType.Amount);
        transactionConfig.setTipOptions(new BigDecimal[]{new BigDecimal(1.00), new BigDecimal(2.00), new BigDecimal(3.00)});
        transactionConfig.setDebitAllowed(true);
        transactionConfig.setCashbackAllowed(true);
        transactionConfig.setCashbackEntryAllowed(true);
        transactionConfig.setCashbackEntryIncrement(5);
        transactionConfig.setCashbackEntryMaximum(100);
        transactionConfig.setCashbackOptions(new BigDecimal[]{new BigDecimal(5.00), new BigDecimal(10.00), new BigDecimal(15.00)});
        transactionConfig.setGiftCardAllowed(true);
        transactionConfig.setQuickChipAllowed(true);
        transactionConfig.setPreReadQuickChipPlaceHolderAmount(BigDecimal.ONE);
        transactionConfig.setHealthcareSupported(false);
        transactionConfig.setDynamicCurrencyConversionEnabled(true);
        transactionConfig.setCustomAidSelectionEnabled(true);
        transactionConfig.setDuplicateTransactionsAllowed(true);
        transactionConfig.setEmvCardInputFailureRetriesEnabled(true);
        transactionConfig.setCardInputRetries(3);
        transactionConfig.setEbtCashBenefitsAllowed(false);
        transactionConfig.setEbtFoodStampsAllowed(false);
        transactionConfig.setShouldConfirmSurchargeAmount(true);
        transactionConfig.setPartialApprovalAllowed(false);

        transactionConfig.setCurrencyCode(CurrencyCode.USD);

        sharedConfig.setTransactionConfiguration(transactionConfig);
    }

    private static void setupStoreAndForwardConfiguration()
    {
        storeAndForwardConfiguration = new StoreAndForwardConfiguration();

        storeAndForwardConfiguration.setNumberOfDaysToRetainProcessedTransactions(1);
        storeAndForwardConfiguration.setShouldTransactionsBeAutomaticallyForwarded(false);
        storeAndForwardConfiguration.setStoringTransactionsAllowed(false);
        storeAndForwardConfiguration.setTransactionAmountLimit(50);
        storeAndForwardConfiguration.setUnprocessedTotalAmountLimit(100);

        sharedConfig.setStoreAndForwardConfiguration(storeAndForwardConfiguration);
    }

    public static void clearConfig()
    {
        sharedConfig = null;
        credentials = null;
        app = null;
        deviceConfig = null;
    }

    public static Credentials getCredentials()
    {
        Credentials credentials = new Credentials();

        credentials.setAcceptorID(getSharedConfig().getHostConfiguration().getAcceptorId());
        credentials.setAccountID(getSharedConfig().getHostConfiguration().getAccountId());
        credentials.setAccountToken(getSharedConfig().getHostConfiguration().getAccountToken());

        return credentials;
    }

    public static Application getApp()
    {
        return app;
    }
}
