package com.vantiv.sampleapp.singlebuttoncomponent;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;

import com.vantiv.triposmobilesdk.Device;
import com.vantiv.triposmobilesdk.VTP;
import com.vantiv.triposmobilesdk.triPOSMobileSDK;
import com.vantiv.sampleapp.R;

public class SingleButtonActivity extends AppCompatActivity
        implements SingleButtonFragment.StatusListener
{
    public VTP sharedVtp;

    public static final String ARG_ITEM_NAME = "item_name";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        sharedVtp = triPOSMobileSDK.getSharedVtp();

        super.onCreate(savedInstanceState);
    }

    private Fragment getFragment()
    {
        return getSupportFragmentManager().findFragmentById(R.id.item_detail_container);
    }

    public void setFragmentStatusTextViews()
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                Fragment fragment = getFragment();

                if (fragment != null)
                {
                    ((StatusTextViewCallbacks) fragment).setSdkStatusTextView(sharedVtp.getIsInitialized());

                    Device device = sharedVtp.getDevice();

                    if (device == null)
                    {
                        ((StatusTextViewCallbacks) fragment).setDeviceStatusTextView(false);
                    }
                    else
                    {
                        ((StatusTextViewCallbacks) fragment).setDeviceStatusTextView(device.getIsConnected());
                    }

                    ((StatusTextViewCallbacks) fragment).hideOutputTextView();
                }
            }
        });
    }


    public void setFragmentStatusTextViews(final String  text)
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                Fragment fragment = getFragment();

                if (fragment != null)
                {
                    ((StatusTextViewCallbacks) fragment).setSdkStatusTextView(sharedVtp.getIsInitialized());


                    ((StatusTextViewCallbacks) fragment).setDeviceStatusTextView(text);


                    ((StatusTextViewCallbacks) fragment).hideOutputTextView();
                }
            }
        });
    }
}
