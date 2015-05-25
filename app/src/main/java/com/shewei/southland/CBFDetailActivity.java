package com.shewei.southland;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;


public class CBFDetailActivity extends ActionBarActivity {

    private String mOwnersDB;
    private String mParcelMap;
    private String CBFBM;

    private SQLiteDatabase dbOwners;

    private EditText edt_cbf_bm;
    private EditText edt_cbf_mc;
    private EditText edt_cbf_dz;
    private EditText edt_cbf_zjlx;
    private EditText edt_cbf_zjhm;
    private EditText edt_cbf_cysl;
    private ListView lVi_CBF_JTCY;

    private SimpleCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cbfdetail);
        overridePendingTransition(R.animator.slide_in_right, 0);

        edt_cbf_bm = (EditText) findViewById(R.id.edt_cbf_bm);
        edt_cbf_mc = (EditText) findViewById(R.id.edt_cbf_mc);
        edt_cbf_dz = (EditText) findViewById(R.id.edt_cbf_dz);
        edt_cbf_zjlx = (EditText) findViewById(R.id.edt_cbf_zjlx);
        edt_cbf_zjhm = (EditText) findViewById(R.id.edt_cbf_zjhm);
        edt_cbf_cysl = (EditText) findViewById(R.id.edt_cbf_cysl);
        lVi_CBF_JTCY = (ListView) findViewById(R.id.lVi_CBF_JTCY);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setLogo(R.drawable.ic_logo);

        if (getIntent().getExtras() != null) {
            mOwnersDB = getIntent().getStringExtra("owners_db");
            mParcelMap = getIntent().getStringExtra("parcel_map");
            CBFBM = getIntent().getStringExtra("CBFBM");
        }

        dbOwners = SQLiteDatabase.openDatabase(mOwnersDB, null, SQLiteDatabase.OPEN_READWRITE);
        Cursor c = dbOwners.rawQuery("SELECT * FROM [CBF] WHERE [CBFBM] = ?", new String[] {CBFBM});
        if (c.moveToFirst()) {
            edt_cbf_bm.setText(c.getString(c.getColumnIndex("CBFBM")));
            edt_cbf_mc.setText(c.getString(c.getColumnIndex("CBFMC")));
            edt_cbf_dz.setText(c.getString(c.getColumnIndex("CBFDZ")) +
                    ", " + c.getString(c.getColumnIndex("YZBM")));
            String zjlx = c.getString(c.getColumnIndex("CBFZJLX"));
            if (zjlx.equals("1")) {
                edt_cbf_zjlx.setText("身份证");
            }

            edt_cbf_zjhm.setText(c.getString(c.getColumnIndex("CBFZJHM")));
            edt_cbf_cysl.setText(c.getString(c.getColumnIndex("CBFCYSL")));

            setTitle(edt_cbf_mc.getText());
        }

        c = dbOwners.rawQuery("SELECT [CBFBM] as [_id], * FROM [CBF_JTCY] WHERE [CBFBM] = ?", new String[] {CBFBM});
        adapter = new SimpleCursorAdapter(this, R.layout.cbf_jtcy_item, c,
                new String[] { "CYXM", "CYXB", "CYZJHM", "YHZGX", "SFGYR" },
                new int[] {R.id.txt_jtcy_cyxm, R.id.txt_jtcy_cyxb, R.id.txt_jtcy_cyzjhm, R.id.txt_jtcy_yhzgx, R.id.txt_jtcy_sfgyr});
        lVi_CBF_JTCY.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbOwners.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_cbfdetail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
