package com.vantiv.sampleapp.master;

import android.content.Intent;
import android.os.Bundle;
import androidx.core.app.NavUtils;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;

import com.vantiv.sampleapp.R;
import com.vantiv.sampleapp.singlebuttoncomponent.SingleButtonActivity;
import com.vantiv.sampleapp.singlebuttoncomponent.SingleButtonFragment;

public class DetailActivity extends SingleButtonActivity
{
    int itemId;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        String itemName = getIntent().getStringExtra(SingleButtonActivity.ARG_ITEM_NAME);

        if (itemName != null)
        {
            if (getSupportActionBar() != null)
            {
                getSupportActionBar().setTitle(itemName);
            }
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null)
        {
            showSingleButtonFragment();
        }
    }

    void showSingleButtonFragment()
    {
        itemId = getIntent().getIntExtra(SingleButtonFragment.ARG_ITEM_ID, 0);

        Action.ActionItem item = Action.ITEMS.get(itemId);

        Bundle arguments = new Bundle();
        arguments.putInt(SingleButtonFragment.ARG_ITEM_ID, itemId);

        SingleButtonFragment fragment = (SingleButtonFragment) MasterActivity.getFragmentForAction(item);

        fragment.setArguments(arguments);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.item_detail_container, fragment)
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        if (id == android.R.id.home)
        {
            NavUtils.navigateUpTo(this, new Intent(this, MasterActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
