package com.vantiv.sampleapp;

import java.math.BigDecimal;

public final class SharedData
{
    private static String lastTransactionId;
    private static BigDecimal lastTransactionAmount;

    public static String getLastTransactionId()
    {
        return lastTransactionId;
    }

    public static void setLastTransactionId(String transactionId)
    {
        lastTransactionId = transactionId;
    }

    public static BigDecimal getLastTransactionAmount()
    {
        return lastTransactionAmount;
    }

    public static void setLastTransactionAmount(BigDecimal transactionAmount)
    {
        lastTransactionAmount = transactionAmount;
    }
}
