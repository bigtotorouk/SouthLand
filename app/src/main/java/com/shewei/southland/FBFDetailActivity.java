package com.shewei.southland;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


public class FBFDetailActivity extends ActionBarActivity implements View.OnClickListener {

    private String mOwnersDB;
    private String mParcelMap;
    private String FBFBM;

    private SQLiteDatabase dbOwners;
    private TextView edt_fbf_bm;
    private TextView edt_fbf_mc;
    private TextView edt_fbf_fzrxm;
    private TextView edt_fbf_lxdh;
    private TextView edt_fbf_fzrdz;
    private TextView edt_fbf_fzrzjlx;
    private TextView edt_fbf_fzrzjhm;
    private TextView edt_fbf_dcjs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fbfdetail);

        edt_fbf_bm = (TextView) findViewById(R.id.edt_fbf_bm);
        edt_fbf_mc = (TextView) findViewById(R.id.edt_fbf_mc);
        edt_fbf_fzrxm = (TextView) findViewById(R.id.edt_fbf_fzrxm);
        edt_fbf_lxdh = (TextView) findViewById(R.id.edt_fbf_lxdh);
        edt_fbf_fzrdz = (TextView) findViewById(R.id.edt_fbf_fzrdz);
        edt_fbf_fzrzjlx = (TextView) findViewById(R.id.edt_fbf_fzrzjlx);
        edt_fbf_fzrzjhm = (TextView) findViewById(R.id.edt_fbf_fzrzjhm);
        edt_fbf_dcjs = (TextView) findViewById(R.id.edt_fbf_dcjs);
        findViewById(R.id.fbf_map).setOnClickListener(this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setLogo(R.drawable.ic_logo);

        if (getIntent().getExtras() != null) {
            mOwnersDB = getIntent().getStringExtra("owners_db");
            mParcelMap = getIntent().getStringExtra("parcel_map");
            FBFBM = getIntent().getStringExtra("FBFBM");
        }

        dbOwners = SQLiteDatabase.openDatabase(mOwnersDB, null, SQLiteDatabase.OPEN_READWRITE);
        Cursor c = dbOwners.rawQuery("SELECT [FBFBM] as [_id], * FROM [FBF] WHERE [FBFBM] = ?", new String[] { FBFBM });
        if (c.moveToFirst()) {
            edt_fbf_bm.setText(c.getString(c.getColumnIndex("FBFBM")));
            edt_fbf_mc.setText(c.getString(c.getColumnIndex("FBFMC")));
            edt_fbf_fzrxm.setText(c.getString(c.getColumnIndex("FBFFZRXM")));
            edt_fbf_lxdh.setText(c.getString(c.getColumnIndex("LXDH")));
            edt_fbf_fzrdz.setText(c.getString(c.getColumnIndex("FBFDZ")));
            String zjlx = c.getString(c.getColumnIndex("FZRZJLX"));
            if (zjlx.equals("1")) {
                edt_fbf_fzrzjlx.setText("身份证");
            }
            edt_fbf_fzrzjhm.setText(c.getString(c.getColumnIndex("FZRZJHM")));
            edt_fbf_dcjs.setText(c.getString(c.getColumnIndex("FBFDCJS")));
        }

        setTitle(edt_fbf_mc.getText());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(dbOwners == null){
            dbOwners = SQLiteDatabase.openDatabase(mOwnersDB, null, SQLiteDatabase.OPEN_READWRITE);
        }
        Cursor c = dbOwners.rawQuery("SELECT [FBFBM] as [_id], * FROM [FBF] WHERE [FBFBM] = ?", new String[] { FBFBM });
        if (c.moveToFirst()) {
            edt_fbf_bm.setText(c.getString(c.getColumnIndex("FBFBM")));
            edt_fbf_mc.setText(c.getString(c.getColumnIndex("FBFMC")));
            edt_fbf_fzrxm.setText(c.getString(c.getColumnIndex("FBFFZRXM")));
            edt_fbf_lxdh.setText(c.getString(c.getColumnIndex("LXDH")));
            edt_fbf_fzrdz.setText(c.getString(c.getColumnIndex("FBFDZ")));
            String zjlx = c.getString(c.getColumnIndex("FZRZJLX"));
            if (zjlx.equals("1")) {
                edt_fbf_fzrzjlx.setText("身份证");
            }
            edt_fbf_fzrzjhm.setText(c.getString(c.getColumnIndex("FZRZJHM")));
            edt_fbf_dcjs.setText(c.getString(c.getColumnIndex("FBFDCJS")));
        }

        setTitle(edt_fbf_mc.getText());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbOwners.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_fbfdetail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_edit) {
            if(FBFBM!=null){
                Intent intent = new Intent(this, FBFEditActivity.class);
                intent.putExtra("FBFBM", FBFBM);
                intent.putExtra("owners_db", mOwnersDB);
                intent.putExtra("parcel_map", mParcelMap);
                startActivity(intent);
            }
            return true;
        } 
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fbf_map){
            Intent intent = new Intent(this, ParcelViewActivity.class);
            intent.putExtra("owners_db", mOwnersDB);
            intent.putExtra("parcel_map", mParcelMap);
            intent.putExtra("FBFBM", FBFBM);
            intent.putExtra("title", getTitle().toString());
            startActivity(intent);
        }
    }
}
