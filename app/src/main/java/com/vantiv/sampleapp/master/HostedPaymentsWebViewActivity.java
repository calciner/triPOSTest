package com.vantiv.sampleapp.master;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.vantiv.sampleapp.R;
import com.vantiv.triposmobilesdk.VTP;
import com.vantiv.triposmobilesdk.responses.AuthorizationResponse;
import com.vantiv.triposmobilesdk.responses.SaleResponse;
import com.vantiv.triposmobilesdk.triPOSMobileSDK;

import java.net.URL;
import android.view.Window;
import android.view.WindowManager;

public class HostedPaymentsWebViewActivity extends AppCompatActivity {

    private static final String TAG = HostedPaymentsWebViewActivity.class.getSimpleName();
    public static final String HOSTED_PAYMENTS_TITLE = "hostedPaymentsTitle";
    public static final String RESPONSE_DATA_HOSTED_PAYMENTS = "responseDataHostedPayments";
    public static final String HOSTED_PAYMENTS_URL = "hostedPaymentsURL";

    private final String HOSTED_PAYMENT_URL_DOMAIN_NAME = "https://certtransaction.hostedpayments.com";
    private String hostedPaymentsURL;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);

        setContentView(R.layout.activity_webview_hosted_payments);

        String title = getIntent().getStringExtra(HOSTED_PAYMENTS_TITLE);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        WebView browser = findViewById(R.id.webView_browser);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            hostedPaymentsURL = extras.getString(HOSTED_PAYMENTS_URL);
        }

        browser.getSettings().setJavaScriptEnabled(true);
        if(hostedPaymentsURL != null && hostedPaymentsURL.contains(HOSTED_PAYMENT_URL_DOMAIN_NAME))
        {
            browser.loadUrl(hostedPaymentsURL);
        }

        browser.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String responseUrl, Bitmap favicon) {
                Log.d(TAG, responseUrl);

                if (triPOSMobileSDK.getSharedVtp().isHostedPaymentsResponse(responseUrl)) {
                    try {
                        if (responseUrl.contains(VTP.hostedPaymentsResponseReturnUrlSale)) {
                            SaleResponse saleResponse = triPOSMobileSDK.getSharedVtp().processHostedPaymentsSaleResponse(new URL(responseUrl));
                            String response = new Gson().toJson(saleResponse);
                            Intent intent = new Intent();
                            intent.putExtra(RESPONSE_DATA_HOSTED_PAYMENTS, response);
                            setResult(RESULT_OK, intent);
                        }

                        if (responseUrl.contains(VTP.hostedPaymentsResponseReturnUrlAuthorization)) {
                            AuthorizationResponse authorizationResponse = triPOSMobileSDK.getSharedVtp().processHostedPaymentsAuthorizationResponse(new URL(responseUrl));
                            String response = new Gson().toJson(authorizationResponse);
                            Intent intent = new Intent();
                            intent.putExtra(RESPONSE_DATA_HOSTED_PAYMENTS, response);
                            setResult(RESULT_OK, intent);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error in process hosted payments response:" + e.getMessage());
                    }

                    // finish webview Activity after hosted payments any response received.
                    finish();
                }

                super.onPageStarted(view, responseUrl, favicon);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) // Press Back Icon
        {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

}