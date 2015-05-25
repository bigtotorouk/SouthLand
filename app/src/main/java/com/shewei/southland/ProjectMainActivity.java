package com.shewei.southland;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.shewei.southland.fragment.InvestManageSurveyFragment;
import com.shewei.southland.fragment.NavigationDrawerFragment;
import com.shewei.southland.fragment.WorkGuaranteeImpFragment;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.ArrayAdapter;
import android.widget.TextView;


public class ProjectMainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private static final int STATISTIC_FRAGMENT = 0;
    private static final int FBF_LIST_FRAGMENT = 1;
    private static final int CBF_LIST_FRAGMENT = 2;
    private static final int CBDK_LIST_FRAGMENT = 3;

    private Fragment[] mFragments = new Fragment[4];
    private int lastFragment = -1;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private String project_name;
    private String owners_db;
    private String parcel_map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_main);

        if (getIntent().getExtras() != null) {
            project_name = getIntent().getStringExtra("project_name");
            owners_db = getIntent().getStringExtra("owners_db");
            parcel_map = getIntent().getStringExtra("parcel_map");
        } else if (savedInstanceState != null) {
            mTitle = savedInstanceState.getString("title");
            project_name = savedInstanceState.getString("project_name");
            owners_db = savedInstanceState.getString("owners_db");
            parcel_map = savedInstanceState.getString("parcel_map");

            lastFragment = savedInstanceState.getInt("last_fragment");
            onNavigationDrawerItemSelected(lastFragment);
        }

        setTitle(project_name);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("last_fragment", lastFragment);
        outState.putString("title", mTitle.toString());
        outState.putString("project_name", project_name);
        outState.putString("owners_db", owners_db);
        outState.putString("parcel_map", parcel_map);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        FragmentManager fragmentManager = getSupportFragmentManager();

        if (mFragments[position] == null) {
            mFragments[position] = createFragment(position);
            fragmentManager.beginTransaction()
                    .add(R.id.container, mFragments[position])
                    .commit();
        }

        // update the main content by replacing fragments
        FragmentTransaction ft = fragmentManager.beginTransaction();
        if (lastFragment != -1) {
            ft.hide(mFragments[lastFragment]);
        }
        ft.show(mFragments[position]);
        ft.commit();

        lastFragment = position;
    }

    private Fragment createFragment(int position) {
        switch (position) {
            case STATISTIC_FRAGMENT:
                mFragments[position] = WorkGuaranteeImpFragment.newInstance(owners_db, parcel_map);
                break;
            case FBF_LIST_FRAGMENT:
                mFragments[position] = InvestManageSurveyFragment.newInstance(owners_db, parcel_map);
                break;
            case CBF_LIST_FRAGMENT:
                mFragments[position] = InvestManageSurveyFragment.newInstance(owners_db, parcel_map);
                break;
            case CBDK_LIST_FRAGMENT:
                mFragments[position] = InvestManageSurveyFragment.newInstance(owners_db, parcel_map);
                break;
        }
        return mFragments[position];
    }

    @Override
    public void onShowGlobalContextActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setTitle(project_name);

    }

    @Override
    public void onCloseProject() {
        Intent intent = new Intent(this, StartActivity.class);
        intent.putExtra("from_project", true);
        startActivity(intent);
        finish();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = project_name + ": " + getString(R.string.title_section1);
                break;
            case 2:
                mTitle = project_name + ": " + getString(R.string.title_section2);
                break;
            case 3:
                mTitle = project_name + ": " + getString(R.string.title_section3);
                break;
            case 4:
                mTitle = project_name + ": " + getString(R.string.title_section4);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.project_main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add) {
            Intent intent = new Intent(this, FBFListActivity.class);
            intent.putExtra("owners_db",owners_db);
            intent.putExtra("parcel_map",parcel_map);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
