package com.vantiv.sampleapp.singlebuttoncomponent;

public interface StatusTextViewCallbacks
{
    void setSdkStatusTextView(boolean flag);

    void setDeviceStatusTextView(boolean flag);

    void setDeviceStatusTextView(String text);

    void setDeviceStatusError(Exception e);

    void hideOutputTextView();
}
