package com.vantiv.sampleapp.master;

import com.vantiv.sampleapp.fragments.AuthorizationCompletionFragment;
import com.vantiv.sampleapp.fragments.AuthorizationFragment;
import com.vantiv.sampleapp.fragments.AuthorizationWithTokenFragment;
import com.vantiv.sampleapp.fragments.CardInputFragment;
import com.vantiv.sampleapp.fragments.ChoiceInputFragment;
import com.vantiv.sampleapp.fragments.CreateTokenFragment;
import com.vantiv.sampleapp.fragments.CreditCardAdjustmentFragment;
import com.vantiv.sampleapp.fragments.DeleteStoredTransactionWithStateStoredFragment;
import com.vantiv.sampleapp.fragments.GetAllStoredTransactionsFragment;
import com.vantiv.sampleapp.fragments.GetAllStoredTransactionsFromIndexFragment;
import com.vantiv.sampleapp.fragments.GetStoredTransactionByTpIdFragment;
import com.vantiv.sampleapp.fragments.GetStoredTransactionsWithStateFragment;
import com.vantiv.sampleapp.fragments.GiftCardActivateFragment;
import com.vantiv.sampleapp.fragments.GiftCardCloseFragment;
import com.vantiv.sampleapp.fragments.GiftCardUnloadFragment;
import com.vantiv.sampleapp.fragments.GiftCardBalanceInquiryFragment;
import com.vantiv.sampleapp.fragments.GiftCardBalanceTransferFragment;
import com.vantiv.sampleapp.fragments.GiftCardReloadFragment;
import com.vantiv.sampleapp.fragments.GiftCardReturnFragment;
import com.vantiv.sampleapp.fragments.GiftCardReversalFragment;
import com.vantiv.sampleapp.fragments.HealthCheckFragment;
import com.vantiv.sampleapp.fragments.HostedPaymentsAuthorizationFragment;
import com.vantiv.sampleapp.fragments.HostedPaymentsSaleFragment;
import com.vantiv.sampleapp.fragments.IncrementalAuthorizationFragment;
import com.vantiv.sampleapp.fragments.KeyboardNumericInputFragment;
import com.vantiv.sampleapp.fragments.ManuallyForwardFragment;
import com.vantiv.sampleapp.fragments.PinInputFragment;
import com.vantiv.sampleapp.fragments.PreReadFragment;
import com.vantiv.sampleapp.fragments.RefundFragment;
import com.vantiv.sampleapp.fragments.RefundWithTokenFragment;
import com.vantiv.sampleapp.fragments.SaleFragment;
import com.vantiv.sampleapp.fragments.SaleWithTokenFragment;
import com.vantiv.sampleapp.fragments.ScanBtDevicesFragment;
import com.vantiv.sampleapp.fragments.TokenCreateWithTransactionIdFragment;
import com.vantiv.sampleapp.fragments.VoidFragment;
import com.vantiv.sampleapp.fragments.YesNoInputFragment;
import com.vantiv.sampleapp.fragments.CreditCardReturnFragment;
import com.vantiv.sampleapp.fragments.CreditCardReversalFragment;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;

public class Action
{
    public static final List<ActionItem> ITEMS;

    static
    {
        ITEMS = new ArrayList<ActionItem>()
        {
            {
                add(new ActionItem("Scan for BT Devices", ScanBtDevicesFragment.class));
                add(new ActionItem("Health Check", HealthCheckFragment.class));
                add(new ActionItem("Card Input", CardInputFragment.class));
                add(new ActionItem("Choice Input", ChoiceInputFragment.class));
                add(new ActionItem("Numeric Input", KeyboardNumericInputFragment.class));
                add(new ActionItem("PIN Input", PinInputFragment.class));
                add(new ActionItem("Yes/No Input", YesNoInputFragment.class));
                add(new ActionItem("Preread Card", PreReadFragment.class));
                add(new ActionItem("Sale", SaleFragment.class));
                add(new ActionItem("Hosted Payments Sale", HostedPaymentsSaleFragment.class));
                add(new ActionItem("Void", VoidFragment.class));
                add(new ActionItem("Refund", RefundFragment.class));
                add(new ActionItem("Authorization", AuthorizationFragment.class));
                add(new ActionItem("Hosted Payments Authorization", HostedPaymentsAuthorizationFragment.class));
                add(new ActionItem("Incremental Authorization", IncrementalAuthorizationFragment.class));
                add(new ActionItem("Authorization Completion", AuthorizationCompletionFragment.class));
                add(new ActionItem("Create Token", CreateTokenFragment.class));
                add(new ActionItem("Create Token with Transaction ID", TokenCreateWithTransactionIdFragment.class));
                add(new ActionItem("Sale with Token", SaleWithTokenFragment.class));
                add(new ActionItem("Refund with Token", RefundWithTokenFragment.class));
                add(new ActionItem("Authorization with Token", AuthorizationWithTokenFragment.class));
                add(new ActionItem("Credit Card Adjustment", CreditCardAdjustmentFragment.class));
                add(new ActionItem("Credit Card Return", CreditCardReturnFragment.class));
                add(new ActionItem("Credit Card Reversal", CreditCardReversalFragment.class));
                add(new ActionItem("Gift Card Activate", GiftCardActivateFragment.class));
                add(new ActionItem("Gift Card Close", GiftCardCloseFragment.class));
                add(new ActionItem("Gift Card Unload", GiftCardUnloadFragment.class));
                add(new ActionItem("Gift Card Balance Inquiry", GiftCardBalanceInquiryFragment.class));
                add(new ActionItem("Gift Card Reload", GiftCardReloadFragment.class));
                add(new ActionItem("Gift Card Return", GiftCardReturnFragment.class));
                add(new ActionItem("Gift Card Reversal", GiftCardReversalFragment.class));
                add(new ActionItem("Gift Card Balance Transfer", GiftCardBalanceTransferFragment.class));
                add(new ActionItem("Manually Forward Stored Transaction", ManuallyForwardFragment.class));
                add(new ActionItem("Get All Stored Transactions", GetAllStoredTransactionsFragment.class));
                add(new ActionItem("Get All Stored Transactions From Index For Count", GetAllStoredTransactionsFromIndexFragment.class));
                add(new ActionItem("Get All Stored Transactions With State", GetStoredTransactionsWithStateFragment.class));
                add(new ActionItem("Delete a Transaction With State Stored", DeleteStoredTransactionWithStateStoredFragment.class));
                add(new ActionItem("Get Stored Transaction By TpId", GetStoredTransactionByTpIdFragment.class));
            }
        };
    }

    public static class ActionItem
    {
        public final String displayText;
        public final Class<? extends Fragment> fragmentClass;

        public ActionItem(String displayText, Class<? extends Fragment> fragmentClass)
        {
            this.displayText = displayText;
            this.fragmentClass = fragmentClass;
        }

        @Override
        public String toString()
        {
            return displayText;
        }
    }
}
