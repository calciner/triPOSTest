package com.vantiv.sampleapp.master;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.vantiv.sampleapp.R;
import com.vantiv.sampleapp.fragments.HealthCheckFragment;
import com.vantiv.sampleapp.singlebuttoncomponent.SingleButtonActivity;
import com.vantiv.sampleapp.singlebuttoncomponent.SingleButtonFragment;
import com.vantiv.sampleapp.triPOSConfig;
import com.vantiv.triposmobilesdk.BTPairingLedSequence;
import com.vantiv.triposmobilesdk.Device;
import com.vantiv.triposmobilesdk.DeviceConnectionListener;
import com.vantiv.triposmobilesdk.OTAUpdateListener;
import com.vantiv.triposmobilesdk.OTAUpdateType;
import com.vantiv.triposmobilesdk.triPOSMobileSDK;

import java.lang.reflect.Constructor;
import java.util.Hashtable;
import java.util.List;



import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.widget.Toast;

public class MasterActivity extends SingleButtonActivity implements DeviceConnectionListener, OTAUpdateListener
{

    Switch initializeSdkSwitch;

    private boolean mTwoPane;

    private static final int BLUETOOTH_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        checkBluetoothPermissions();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Sample App");
        toolbar.setSubtitle("triPOS Mobile SDK v" + triPOSMobileSDK.getVersion());
        setSupportActionBar(toolbar);

        View recyclerView = findViewById(R.id.item_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        if (findViewById(R.id.item_detail_container) != null)
        {
            mTwoPane = true;
        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void checkBluetoothPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {

                // Request the necessary Bluetooth permissions
                ActivityCompat.requestPermissions(this,
                        new String[]{
                                Manifest.permission.BLUETOOTH_CONNECT,
                                Manifest.permission.BLUETOOTH_SCAN
                        },
                        BLUETOOTH_PERMISSION_REQUEST_CODE);
            } else {
                // Permissions are already granted, proceed with Bluetooth operations
                startBluetoothOperations();
            }
        } else {
            // For Android versions below S, proceed directly with Bluetooth operations
            startBluetoothOperations();
        }
    }

    private void startBluetoothOperations() {
        // Code to start Bluetooth operations, e.g., device discovery or connection
        Toast.makeText(this, "Bluetooth permissions granted, starting Bluetooth operations!", Toast.LENGTH_SHORT).show();
    }

    // Override to handle the user's response to the permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == BLUETOOTH_PERMISSION_REQUEST_CODE) {
            // Check if the user granted the permissions
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permissions granted, proceed with Bluetooth operations
                startBluetoothOperations();
            } else {
                // Permissions denied, show a message to the user
                Toast.makeText(this, "Bluetooth permissions are required for this app!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem menuItem = menu.findItem(R.id.myswitch);

        View view = MenuItemCompat.getActionView(menuItem);

        initializeSdkSwitch = (Switch) view.findViewById(R.id.switchForActionBar);
        initializeSdkSwitch.setOnCheckedChangeListener(initializeSdkSwitchOnCheckChangeListener);
        initializeSdkSwitch.setChecked(sharedVtp.getIsInitialized());

        // Make initialize switch easier to tap
        view.post(new Runnable() {
            @Override
            public void run()
            {
                Rect r = new Rect();
                initializeSdkSwitch.getHitRect(r);
                r.top -= 10;
                r.left -= 10;
                r.right += 10;
                r.bottom += 10;
                ((View) initializeSdkSwitch.getParent()).setTouchDelegate(new TouchDelegate(r, initializeSdkSwitch));
            }
        });

        return super.onCreateOptionsMenu(menu);
    }



    private CompoundButton.OnCheckedChangeListener initializeSdkSwitchOnCheckChangeListener = new CompoundButton.OnCheckedChangeListener()
    {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
        {
            if (isChecked)
            {

                initializeSdk();
            }
            else
            {
                deinitializeSdk();
            }
        }
    };

    @Override
    public void onConfirmPairing(List<BTPairingLedSequence> ledSequence, String deviceName, ConfirmPairingListener confirmPairingListener) {
        this.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MasterActivity.this);
                String dialogTitle =  "Bluetooth Pairing";
                dialogBuilder.setTitle(dialogTitle);
                dialogBuilder.setMessage("Pair with "+ deviceName);
                dialogBuilder.setCancelable(true);
                dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        confirmPairingListener.cancelPairing();
                    }
                });

                dialogBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        confirmPairingListener.confirmPairing();
                    }
                });

                AlertDialog dialog = dialogBuilder.create();
                dialog.setCancelable(true);
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
            }
        });
    }


    @Override
    public void onConnected(Device device, String description, String model, String serialNumber)
    {
        Log.i("Device", "onConnected");

        runOnUiThread(new DeviceEventThread(this, String.format("Device connected: description: %s model: %s serial Number: %s", description, model, serialNumber)));
    }

    @Override
    public void onDisconnected(Device device)
    {
        Log.i("Device", "onDisconnected");

        runOnUiThread(new DeviceEventThread(this, "Device disconnected"));
    }

    @Override
    public void onError(Exception exception)
    {
        Log.e("Device onError", exception.getMessage());

        String errorString = null;

        if (exception.getCause() != null)
        {
            errorString = String.format("Device error: %s.  Cause: %s",
                    exception.getLocalizedMessage(),
                    exception.getCause().getLocalizedMessage());
        }
        else
        {
            errorString = String.format("Device error: %s.",
                    exception.getLocalizedMessage());
        }

        runOnUiThread(new DeviceEventThread(this, errorString));

    }

    @Override
    public void onBatteryLow()
    {
        Log.d("Battery", "onBatteryLow: Low battery indication received");
    }

    @Override
    public void onWarning(Exception e)
    {
        Log.e("Device onWarning", e.getMessage());

        runOnUiThread(new DeviceEventThread(this, String.format("Device error: %s", e.getLocalizedMessage())));
    }

    @Override
    public void onOverTheAirUpdateStarted(OTAUpdateType otaUpdateType, Hashtable hashtable)
    {
        MasterActivity.super.setFragmentStatusTextViews("Starting OTA update: " + otaUpdateType.toString());
    }

    @Override
    public void onOverTheAirUpdateFinished(OTAUpdateType otaUpdateType, Hashtable hashtable)
    {
        MasterActivity.super.setFragmentStatusTextViews("Completed OTA update: " + otaUpdateType.toString());
    }

    @Override
    public void onOverTheAirUpdateProgress(OTAUpdateType otaUpdateType, double currentProgress, Hashtable hashtable)
    {
        MasterActivity.super.setFragmentStatusTextViews("OTA update in progress: " + otaUpdateType.toString() + ".  Progress:" + currentProgress);
    }

    class DeviceEventThread implements Runnable
    {
        private SingleButtonActivity activity;

        private String message;

        DeviceEventThread(SingleButtonActivity activity, String message)
        {
            this.activity = activity;

            this.message = message;
        }

        @Override
        public void run()
        {
            if (mTwoPane)
            {
                this.activity.setFragmentStatusTextViews();
            }

            Toast.makeText(getApplicationContext(), this.message, Toast.LENGTH_SHORT).show();
        }
    }

    void createToastOnUiThread(final Context context, final String message, final int toastLength)
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                Toast.makeText(context, message, toastLength).show();
            }
        });
    }

    void initializeSdk()
    {
        final DeviceConnectionListener that = this;

        if (sharedVtp != null && !sharedVtp.getIsInitialized())
        {
            Log.d("TAG", "Debug message");
            new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        //The OTA Update Listener will provide OTA Firmware and Configuration updates for BBPos devices
                        sharedVtp.initialize(getApplicationContext(), triPOSConfig.getSharedConfig(), that, MasterActivity.this);

                        createToastOnUiThread(getApplicationContext(), "triPOS Mobile initialized", Toast.LENGTH_SHORT);

                        if (mTwoPane)
                        {
                            MasterActivity.super.setFragmentStatusTextViews();
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                        Log.d("TAG", e.getMessage());

                        toggleInitializeSdkSwitch();

                        createToastOnUiThread(getApplicationContext(), "Failed to initialize triPOS Mobile", Toast.LENGTH_SHORT);
                    }
                }
            }).start();
        }
    }

    @SuppressWarnings("SpellCheckingInspection")
    void deinitializeSdk()
    {
        if (sharedVtp != null && sharedVtp.getIsInitialized())
        {
            new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        sharedVtp.deinitialize();

                        triPOSConfig.clearConfig();

                        if (mTwoPane)
                        {
                            MasterActivity.super.setFragmentStatusTextViews();
                        }

                        createToastOnUiThread(getApplicationContext(), "triPOS Mobile deinitialized", Toast.LENGTH_SHORT);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();

                        toggleInitializeSdkSwitch();

                        createToastOnUiThread(getApplicationContext(), "Failed to deinitialize triPOS Mobile", Toast.LENGTH_SHORT);
                    }
                }
            }).start();
        }
    }

    void toggleInitializeSdkSwitch()
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                if (initializeSdkSwitch.isChecked())
                {
                    initializeSdkSwitch.setChecked(false);
                }
                else
                {
                    initializeSdkSwitch.setChecked(true);
                }
            }
        });
    }

    void navigateToItemDetail(View v, Action.ActionItem item)
    {
        if (mTwoPane)
        {
            navigateToItemDetailFragment(item);
        }
        else
        {
            navigateToItemDetailActivity(v, item);
        }
    }

    void navigateToItemDetailActivity(View v, Action.ActionItem action)
    {
        Context context = v.getContext();
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra(HealthCheckFragment.ARG_ITEM_ID, Action.ITEMS.indexOf(action));
        intent.putExtra(SingleButtonActivity.ARG_ITEM_NAME, action.displayText);

        context.startActivity(intent);
    }

    public void navigateToItemDetailFragment(Action.ActionItem action)
    {
        Bundle arguments = new Bundle();
        arguments.putInt(SingleButtonFragment.ARG_ITEM_ID, Action.ITEMS.indexOf(action));
        arguments.putString(SingleButtonActivity.ARG_ITEM_NAME, action.displayText);

        Fragment fragment = getFragmentForAction(action);

        fragment.setArguments(arguments);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        ft.replace(R.id.item_detail_container, fragment);
        ft.commit();
    }

    public static Fragment getFragmentForAction(Action.ActionItem action)
    {
        try
        {
            Constructor<? extends Fragment> constructor = action.fragmentClass.getConstructor();

            Fragment fragment = constructor.newInstance();

            return fragment;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();

            return new HealthCheckFragment();
        }
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView)
    {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(Action.ITEMS));
    }

    private Action.ActionItem activeItem;

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder>
    {

        private final List<Action.ActionItem> mValues;

        public SimpleItemRecyclerViewAdapter(List<Action.ActionItem> items)
        {
            mValues = items;
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.action_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position)
        {
            holder.mItem = mValues.get(position);
            holder.mContentView.setText(mValues.get(position).displayText);

            if (mTwoPane)
            {
                if (activeItem == null && position == 0)
                {
                    activeItem = holder.mItem;
                    navigateToItemDetail(holder.mView, activeItem);
                }
            }

            Typeface newTypeface;
            int newColor;
            int newVisibility;

            if (activeItem == holder.mItem)
            {
                newColor = getResources().getColor(R.color.colorPrimary);
                newTypeface = Typeface.DEFAULT.DEFAULT_BOLD;
                newVisibility = View.INVISIBLE;
            }
            else
            {
                newColor = getResources().getColor(R.color.black);
                newTypeface = Typeface.DEFAULT;
                newVisibility = View.VISIBLE;
            }

            holder.mContentView.setTypeface(newTypeface);
            holder.mContentView.setTextColor(newColor);

            ImageView chevron = (ImageView) holder.mView.findViewById(R.id.chevron);
            chevron.setVisibility(newVisibility);

            holder.mView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    activeItem = holder.mItem;
                    notifyDataSetChanged();
                    navigateToItemDetail(v, activeItem);
                }
            });
        }

        @Override
        public int getItemCount()
        {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder
        {
            public final View mView;
            public final TextView mContentView;
            public Action.ActionItem mItem;

            public ViewHolder(View view)
            {
                super(view);
                mView = view;
                mContentView = (TextView) view.findViewById(R.id.content);
            }

            @Override
            public String toString()
            {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }
}
