package com.vantiv.sampleapp.singlebuttoncomponent;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.vantiv.sampleapp.R;
import com.vantiv.sampleapp.master.Action;
import com.vantiv.triposmobilesdk.CardInputDevice;
import com.vantiv.triposmobilesdk.CurrencyConversionOffer;
import com.vantiv.triposmobilesdk.Device;
import com.vantiv.triposmobilesdk.DeviceInteractionListener;
import com.vantiv.triposmobilesdk.DeviceListener;
import com.vantiv.triposmobilesdk.DisplayDevice;
import com.vantiv.triposmobilesdk.VTP;
import com.vantiv.triposmobilesdk.VtpStatus;
import com.vantiv.triposmobilesdk.enums.AmountConfirmationType;
import com.vantiv.triposmobilesdk.enums.DeviceType;
import com.vantiv.triposmobilesdk.enums.NumericInputType;
import com.vantiv.triposmobilesdk.enums.SelectionType;
import com.vantiv.triposmobilesdk.exceptions.CardInputDisableException;
import com.vantiv.triposmobilesdk.exceptions.DeviceNotConnectedException;
import com.vantiv.triposmobilesdk.exceptions.DeviceNotInitializedException;
import com.vantiv.triposmobilesdk.triPOSMobileSDK;
import com.vantiv.triposmobilesdk.utilities.BigDecimalUtility;

import java.math.BigDecimal;
import java.util.concurrent.TimeoutException;

public class SingleButtonFragment extends Fragment implements DeviceListener, StatusTextViewCallbacks, DeviceInteractionListener
{
    private AlertDialog statusDialog;
    private AlertDialog interactiveDialog;

    @Override
    public void onAmountConfirmation(final AmountConfirmationType amountConfirmationType,final BigDecimal amount,final ConfirmAmountListener confirmAmountListener) {
        getActivity().runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {

                if (SingleButtonFragment.this.interactiveDialog != null)
                {
                    SingleButtonFragment.this.interactiveDialog.hide();
                    SingleButtonFragment.this.interactiveDialog = null;
                }

                if (SingleButtonFragment.this.statusDialog != null)
                {
                    SingleButtonFragment.this.statusDialog.hide();
                }


                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(SingleButtonFragment.this.getContext());

                String confirmAmountString =  null;

                switch (amountConfirmationType)
                {
                    case Tip:
                        confirmAmountString = String.format("Confirm tip amount %s?", BigDecimalUtility.formatAsCurrency(amount));
                        break;
                    case ConvenienceFee:
                        confirmAmountString = String.format("Confirm convenience fee %s?", BigDecimalUtility.formatAsCurrency(amount));
                        break;
                    case SurchargeAmount:
                        confirmAmountString = String.format("Confirm Surcharge Amount %s?", BigDecimalUtility.formatAsCurrency(amount));
                        break;
                    case Total:
                    default:
                        confirmAmountString = String.format("Confirm amount %s?", BigDecimalUtility.formatAsCurrency(amount));
                }



                dialogBuilder.setMessage(confirmAmountString);
                dialogBuilder.setTitle("Confirm Amount");

                dialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        if (SingleButtonFragment.this.statusDialog != null)
                        {
                            SingleButtonFragment.this.statusDialog.show();
                        }
                        confirmAmountListener.confirmAmount(false);

                    }
                });


                dialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        if (SingleButtonFragment.this.statusDialog != null)
                        {
                            SingleButtonFragment.this.statusDialog.show();
                        }

                        confirmAmountListener.confirmAmount(true);
                    }
                });

                SingleButtonFragment.this.interactiveDialog = dialogBuilder.create();
                SingleButtonFragment.this.interactiveDialog.show();

            }
        });
    }

    @Override
    public void onNumericInput(NumericInputType numericInputType,final NumericInputListener numericInputListener) {
        getActivity().runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {

                if (SingleButtonFragment.this.interactiveDialog != null)
                {
                    SingleButtonFragment.this.interactiveDialog.hide();
                    SingleButtonFragment.this.interactiveDialog = null;
                }

                if (SingleButtonFragment.this.statusDialog != null)
                {
                    SingleButtonFragment.this.statusDialog.hide();
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                // Get the layout inflater
                LayoutInflater inflater = getActivity().getLayoutInflater();

                // Inflate and set the layout for the dialog
                // Pass null as the parent view because its going in the dialog layout

                final View inputNumberView = inflater.inflate(R.layout.field_editnumber, null);

                final TextView textView1 = (TextView) inputNumberView.findViewById(R.id.field_name_textview);

                switch (numericInputType)
                {
                    case VTPNumericInputTypeTip:
                        textView1.setText("Enter Tip");
                        break;
                    case VTPNumericInputTypeCashback:
                        textView1.setText("Enter Cashback");
                        break;
                    case VTPNumericInputTypePostalCode:
                        textView1.setText("Enter Postal Code");
                        break;
                }

                final TextView textView2 = (TextView) inputNumberView.findViewById(R.id.class_type_textview);
                textView2.setText(" ");

                final TextView numericInputView = (TextView) inputNumberView.findViewById(R.id.settings_editnumber);

                builder.setView(inputNumberView)
                        // Add action buttons
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {

                                String tipAmount = numericInputView.getText().toString();

                                numericInputListener.enterNumericInput(tipAmount);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //Cancel;
                                if (numericInputType == NumericInputType.VTPNumericInputTypePostalCode)
                                {
                                    sharedVtp.cancelCurrentFlow();
                                }
                                else
                                {
                                    numericInputListener.enterNumericInput("0.0");
                                }
                            }
                        });

                SingleButtonFragment.this.interactiveDialog = builder.create();
                SingleButtonFragment.this.interactiveDialog.show();

            }
        });
    }

    @Override
    public void onSelectApplication(final String[] strings, final SelectChoiceListener selectChoiceListener) {
        getActivity().runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {

                if (SingleButtonFragment.this.interactiveDialog != null)
                {
                    SingleButtonFragment.this.interactiveDialog.hide();
                    SingleButtonFragment.this.interactiveDialog = null;
                }

                if (SingleButtonFragment.this.statusDialog != null)
                {
                    SingleButtonFragment.this.statusDialog.hide();
                }

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(SingleButtonFragment.this.getContext());

                String dialogTitle =  "Select Application";

                dialogBuilder.setTitle(dialogTitle);

                dialogBuilder.setItems(strings, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        selectChoiceListener.selectChoice(which);

                    }
                });

                SingleButtonFragment.this.interactiveDialog = dialogBuilder.create();
                SingleButtonFragment.this.interactiveDialog.show();

            }
        });
    }

    @Override
    public void onPromptUserForCard(final String prompt)
    {
        if (getActivity() == null)
        {
            return;
        }
        getActivity().runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {

                if (SingleButtonFragment.this.interactiveDialog != null)
                {
                    SingleButtonFragment.this.interactiveDialog.dismiss();
                    SingleButtonFragment.this.interactiveDialog = null;
                }

                if (SingleButtonFragment.this.statusDialog != null)
                {
                    SingleButtonFragment.this.statusDialog.dismiss();
                }

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(SingleButtonFragment.this.getContext());

                String promptString =  String.format("%s", prompt);

                dialogBuilder.setMessage("");
                dialogBuilder.setTitle(promptString);

                dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        sharedVtp.cancelCurrentFlow();
                    }
                });

                SingleButtonFragment.this.interactiveDialog = dialogBuilder.create();
                SingleButtonFragment.this.interactiveDialog.setCancelable(false);
                SingleButtonFragment.this.interactiveDialog.setCanceledOnTouchOutside(false);
                SingleButtonFragment.this.interactiveDialog.show();

            }
        });
    }

    @Override
    public void onChoiceSelections(final String[] choices, final SelectionType selectionType, final SelectChoiceListener selectChoice)
    {
        handleOnChoiceSelection(choices, selectionType, null, selectChoice);
    }

    @Override
    public void onChoiceSelections(final String[] choices,final SelectionType selectionType, final String title, final SelectChoiceListener selectChoice)
    {
        handleOnChoiceSelection(choices, selectionType, title, selectChoice);
    }


    @Override
    public void onDisplayText(final String text)
    {
        getActivity().runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {

                Toast tst= Toast.makeText(getActivity().getApplicationContext(), text, Toast.LENGTH_SHORT);
                tst.setGravity(Gravity.BOTTOM, 5, 5);
                tst.show();
            }
        });
    }

    @Override
    public void onRemoveCard() {
        getActivity().runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {

                if (SingleButtonFragment.this.interactiveDialog != null)
                {
                    SingleButtonFragment.this.interactiveDialog.hide();
                    SingleButtonFragment.this.interactiveDialog = null;
                }

                if (SingleButtonFragment.this.statusDialog != null)
                {
                    SingleButtonFragment.this.statusDialog.hide();
                }


                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(SingleButtonFragment.this.getContext());

                String promptString =  String.format("%s", "Remove Card");

                dialogBuilder.setMessage("");
                dialogBuilder.setTitle(promptString);

                dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {

                        if (SingleButtonFragment.this.statusDialog != null)
                        {
                            SingleButtonFragment.this.statusDialog.show();
                        }


                        try
                        {
                            ((CardInputDevice)device).disableCardInput();
                        }
                        catch (DeviceNotInitializedException e)
                        {
                            e.printStackTrace();
                        }
                        catch (DeviceNotConnectedException e)
                        {
                            e.printStackTrace();
                        }
                        catch (CardInputDisableException e)
                        {
                            e.printStackTrace();
                        }
                    }
                });

                SingleButtonFragment.this.interactiveDialog = dialogBuilder.create();
                SingleButtonFragment.this.interactiveDialog.show();

            }
        });
    }

    @Override
    public void onCardRemoved() {
        getActivity().runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {

                if (SingleButtonFragment.this.interactiveDialog != null)
                {
                    SingleButtonFragment.this.interactiveDialog.hide();
                    SingleButtonFragment.this.interactiveDialog = null;
                }

            }
        });
    }

    public interface StatusListener
    {
        void setFragmentStatusTextViews();
    }

    public SingleButtonFragment.StatusListener statusListener;

    public VTP sharedVtp;
    public Device device;

    public static final String ARG_ITEM_ID = "item_id";

    private Action.ActionItem mItem;

    TextView sdkStatusTextView;
    TextView deviceStatusTextView;
    public TextView transactionStatusTextView;
    public Button actionButton;
    public TextView outputTextView;
    ProgressBar progressBar;
    View rootView;

    static final String outputTextViewStringTag = "outputTextViewString";

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        sharedVtp = triPOSMobileSDK.getSharedVtp();

        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID))
        {
            mItem = Action.ITEMS.get(getArguments().getInt(ARG_ITEM_ID));

            Activity activity = this.getActivity();

            if (activity instanceof SingleButtonFragment.StatusListener)
            {
                statusListener = (SingleButtonFragment.StatusListener) activity;
            }

            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null)
            {
                appBarLayout.setTitle(mItem.displayText);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        String outputTextViewString = outputTextView.getText().toString();

        if (outputTextView != null && !outputTextViewString.isEmpty())
        {
            outState.putString(outputTextViewStringTag, outputTextViewString);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        rootView = inflater.inflate(R.layout.action_detail, container, false);

        sdkStatusTextView = (TextView) rootView.findViewById(R.id.sdkStatusTextView);
        deviceStatusTextView = (TextView) rootView.findViewById(R.id.deviceStatusTextView);
        transactionStatusTextView = (TextView) rootView.findViewById(R.id.transactionStatusTextView);

        if (statusListener != null)
        {
            statusListener.setFragmentStatusTextViews();
        }

        actionButton = (Button) rootView.findViewById(R.id.actionButton);
        actionButton.setOnClickListener(actionButtonOnClickListener);

        // Make action button easier to tap
        rootView.post(new Runnable() {
            @Override
            public void run()
            {
                Rect r = new Rect();
                actionButton.getHitRect(r);
                r.top -= 10;
                r.bottom += 10;
                ((View) actionButton.getParent()).setTouchDelegate(new TouchDelegate(r, actionButton));
            }
        });

        outputTextView = (TextView) rootView.findViewById(R.id.outputTextView);

        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);

        if (savedInstanceState != null)
        {
            outputTextView.setText(savedInstanceState.getString(outputTextViewStringTag));

            if (!outputTextView.getText().toString().isEmpty())
            {
                showOutputTextView();
            }
        }

        if (mItem != null)
        {
            ((TextView) rootView.findViewById(R.id.actionButton)).setText(mItem.displayText.toUpperCase());
        }

        return rootView;
    }

    public void setSdkStatusTextView(boolean flag)
    {
        if (sdkStatusTextView != null)
        {
            sdkStatusTextView.setText(flag ? "SDK initialized" : "SDK not initialized");
            sdkStatusTextView.setTextColor(flag ? getResources().getColor(R.color.colorAccent) : getResources().getColor(R.color.black));
            sdkStatusTextView.setTypeface(null, (flag ? Typeface.BOLD_ITALIC : Typeface.ITALIC));
        }
    }

    public void setDeviceStatusTextView(boolean flag)
    {
        if (deviceStatusTextView != null)
        {
            deviceStatusTextView.setText(flag ? "Device connected" : "Device not connected");
            deviceStatusTextView.setTextColor(flag ? getResources().getColor(R.color.colorAccent) : getResources().getColor(R.color.black));
            deviceStatusTextView.setTypeface(null, (flag ? Typeface.BOLD_ITALIC : Typeface.ITALIC));
        }
    }

    @Override
    public void setDeviceStatusTextView(String text)
    {
        if (deviceStatusTextView != null)
        {
            deviceStatusTextView.setText(text);
        }
    }

    public void setDeviceStatusError(Exception e)
    {
        if (deviceStatusTextView != null)
        {
            deviceStatusTextView.setText(e.getMessage());
            deviceStatusTextView.setTextColor(!e.getMessage().isEmpty() ? getResources().getColor(android.R.color.holo_red_light) : getResources().getColor(R.color.black));
            deviceStatusTextView.setTypeface(null, (!e.getMessage().isEmpty() ? Typeface.BOLD_ITALIC : Typeface.ITALIC));
        }
    }

    public boolean sendAction()
    {

        if (sharedVtp == null)
        {
            handleResponse("sharedVtp is null");
            return false;
        }

        if (!sharedVtp.getIsInitialized())
        {
            handleResponse(getResources().getString(R.string.sdk_not_initialized));
            return false;
        }

        device = sharedVtp.getDevice();

        return true;
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

    public void showOutputTextView()
    {
        if (outputTextView != null)
        {
            outputTextView.setVisibility(View.VISIBLE);
        }
    }

    public void hideOutputTextView()
    {
        if (outputTextView != null)
        {
            outputTextView.setVisibility(View.INVISIBLE);
        }
    }

    public void showProgressBarSpinner()
    {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgressBarSpinner()
    {
        progressBar.setVisibility(View.GONE);
    }

    public static final String notImplementedMessage = "Not implemented yet";

    public void handleResponse(final String response)
    {
        if (outputTextView != null)
        {
            getActivity().runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    dismissInteractiveDialogs();
                    manageKeyedUI(true);
                    transactionStatusTextView.setVisibility(View.GONE);
                    outputTextView.setText(response);

                    showOutputTextView();
                    hideProgressBarSpinner();

                    if (statusDialog != null)
                    {
                        statusDialog.hide();
                        statusDialog = null;
                    }

                    if (interactiveDialog != null)
                    {
                        interactiveDialog.hide();
                        interactiveDialog = null;
                    }
                }
            });
        }
    }

    @Override
    public void onInputTimeout(TimeoutException exception)
    {
        handleResponse(exception.getMessage());
    }

    @Override
    public void onWait(String message)
    {
        if (getActivity() == null)
        {
            return;
        }

        getActivity().runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {

                if (SingleButtonFragment.this.interactiveDialog != null)
                {
                    SingleButtonFragment.this.interactiveDialog.dismiss();
                    SingleButtonFragment.this.interactiveDialog = null;
                }

                if (SingleButtonFragment.this.statusDialog != null)
                {
                    SingleButtonFragment.this.statusDialog.dismiss();
                }

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(SingleButtonFragment.this.getContext());

                dialogBuilder.setMessage("");
                dialogBuilder.setTitle(message);

                SingleButtonFragment.this.interactiveDialog = dialogBuilder.create();
                SingleButtonFragment.this.interactiveDialog.setCancelable(false);
                SingleButtonFragment.this.interactiveDialog.setCanceledOnTouchOutside(false);
                SingleButtonFragment.this.interactiveDialog.show();

            }
        });
    }

    public void dismissInteractiveDialogs()
    {
        if (interactiveDialog != null)
        {
            interactiveDialog.dismiss();
            interactiveDialog = null;
        }

        if (statusDialog != null)
        {
            statusDialog.dismiss();
            statusDialog = null;
        }
    }

    public void manageInteractiveDialogs(boolean show){
        if (statusDialog != null)
        {
            statusDialog.hide();
            statusDialog = null;
        }
    }

    private void handleOnChoiceSelection(final String[] choices, final SelectionType selectionType, String title, final SelectChoiceListener selectChoice)
    {
        getActivity().runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {

                if (SingleButtonFragment.this.interactiveDialog != null)
                {
                    SingleButtonFragment.this.interactiveDialog.dismiss();
                    SingleButtonFragment.this.interactiveDialog = null;
                }

                if (SingleButtonFragment.this.statusDialog != null)
                {
                    SingleButtonFragment.this.statusDialog.dismiss();
                }


                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(SingleButtonFragment.this.getContext());
                String dialogTitle =  "";

                if (title == null)
                {
                    switch (selectionType)
                    {
                        case Payment:
                            dialogTitle = "Payment Type";
                            break;
                        case EBT:
                            dialogTitle = "EBT Type";
                            break;
                        case TipAmount:
                            dialogTitle = "Select Tip Amount";
                            break;
                        case TipConfirm:
                            dialogTitle = "Leave a tip?";
                            break;
                        case CashbackConfirm:
                            dialogTitle = "Cashback?";
                        default:
                            break;
                    }
                }
                else
                {
                    dialogTitle = title;
                }

                dialogBuilder.setTitle(dialogTitle);

                dialogBuilder.setItems(choices, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        selectChoice.selectChoice(which);

                    }
                });

                SingleButtonFragment.this.interactiveDialog = dialogBuilder.create();
                SingleButtonFragment.this.interactiveDialog.setCancelable(false);
                SingleButtonFragment.this.interactiveDialog.setCanceledOnTouchOutside(false);
                SingleButtonFragment.this.interactiveDialog.show();

            }
        });
    }

    @Override
    public void onCurrencyConversionOffer(@NonNull CurrencyConversionOffer currencyConversionOffer, @NonNull CurrencyConversionOfferListener currencyConversionOfferListener)
    {
        try
        {
            getActivity().runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {

                    if (SingleButtonFragment.this.interactiveDialog != null)
                    {
                        SingleButtonFragment.this.interactiveDialog.hide();
                        SingleButtonFragment.this.interactiveDialog = null;
                    }

                    if (SingleButtonFragment.this.statusDialog != null)
                    {
                        SingleButtonFragment.this.statusDialog.hide();
                    }


                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(SingleButtonFragment.this.getContext());

                    String promptString = String.format("%s", "Select Your Currency");

                    dialogBuilder.setMessage("1 " + currencyConversionOffer.getOriginalCurrency() + " = " + currencyConversionOffer.getRate() + " " + currencyConversionOffer.getConvertedCurrency());
                    dialogBuilder.setTitle(promptString);

                    dialogBuilder.setNegativeButton(currencyConversionOffer.getOriginalCurrency() + "\n" + currencyConversionOffer.getOriginalAmount(), new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            if (SingleButtonFragment.this.statusDialog != null)
                            {
                                SingleButtonFragment.this.statusDialog.dismiss();
                            }
                            if (SingleButtonFragment.this.interactiveDialog != null)
                            {
                                SingleButtonFragment.this.interactiveDialog.dismiss();
                            }
                            currencyConversionOfferListener.onOfferDeclined();

                        }
                    });


                    dialogBuilder.setPositiveButton(currencyConversionOffer.getConvertedCurrency() + "\n" + currencyConversionOffer.getConvertedAmount(), new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            if (SingleButtonFragment.this.statusDialog != null)
                            {
                                SingleButtonFragment.this.statusDialog.dismiss();
                            }
                            if (SingleButtonFragment.this.interactiveDialog != null)
                            {
                                SingleButtonFragment.this.interactiveDialog.dismiss();
                            }
                            currencyConversionOfferListener.onOfferAccepted();
                        }
                    });

                    SingleButtonFragment.this.interactiveDialog = dialogBuilder.create();
                    SingleButtonFragment.this.interactiveDialog.show();

                }
            });
        }
        catch (Exception e)
        {
            if (SingleButtonFragment.this.statusDialog != null)
            {
                SingleButtonFragment.this.statusDialog.dismiss();
            }
            if (SingleButtonFragment.this.interactiveDialog != null)
            {
                SingleButtonFragment.this.interactiveDialog.dismiss();
            }
        }
    }

    public void setStatusView(VtpStatus status)
    {
        if (status == VtpStatus.EnableCardKeyedOnlyInput) {
            manageKeyedUI(false);
        } else {
            if (this.statusDialog == null) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this.getContext());

            dialogBuilder.setMessage(status.name());
            dialogBuilder.setTitle("Transaction Status");
            dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    // We this listener here so the builder will create the button but the real work
                    // is done in the listener below because we only want to dismiss the dialog when
                    // the flow is actually cancelled
                }
            });

            this.statusDialog = dialogBuilder.create();
            this.statusDialog.setCancelable(false);
            this.statusDialog.setCanceledOnTouchOutside(false);
            this.statusDialog.show();

            this.statusDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    // If the flow is cancelled successfully then we dismiss the dialog
                    if (sharedVtp.cancelCurrentFlow())
                    {
                        statusDialog.dismiss();
                    }
                }
            });
        }

        if(!this.statusDialog.isShowing()){
            this.statusDialog.show();
        }
        this.statusDialog.setMessage(status.name());
        }
    }

    protected void manageKeyedUI(boolean show){
        if(sharedVtp != null && sharedVtp.getConfiguration() != null &&
            sharedVtp.getConfiguration().getDeviceConfiguration() != null &&
            !sharedVtp.getConfiguration().getDeviceConfiguration().getDeviceType().equals(DeviceType.IngenicoAxium))
        {
            return;
        }
        if(show){
            rootView.setVisibility(View.VISIBLE);
        } else {
            rootView.setVisibility(View.INVISIBLE);
            manageInteractiveDialogs(false);
        }
    }

    public void setStatusListener(){
        if (!(sharedVtp.getDevice() instanceof DisplayDevice))
        {
            transactionStatusTextView.setVisibility(View.VISIBLE);
        }

        sharedVtp.setStatusListener(status -> {
            if (getActivity() != null)
            {
                getActivity().runOnUiThread(() -> {
                    transactionStatusTextView.setText(status.name());
                    setStatusView(status);
                    Log.i(this.getClass().getSimpleName(), "VtpStatus: " + status.name());
                    if(status == VtpStatus.CardRemoved || status == VtpStatus.TransactionCancelled){
                        new Handler().postDelayed(() -> {
                            if (transactionStatusTextView.getVisibility() == View.GONE)
                            {
                                manageInteractiveDialogs(true);
                            }
                        }, 500);
                    }
                });
            }
        });
    }
}