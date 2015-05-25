package com.shewei.southland;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.shewei.southland.util.StringUtil;

/**
 * 发包方编辑
 */
public class FBFEditActivity extends ActionBarActivity {

    private String mOwnersDB;
    private String mParcelMap;
    private String FBFBM;

    private SQLiteDatabase dbOwners;
    int rowId;
    private EditText edt_fbf_bm;
    private EditText edt_fbf_mc;
    private EditText edt_fbf_fzrxm;
    private EditText edt_fbf_lxdh;
    private EditText edt_fbf_fzrdz;
    private EditText edt_fbf_fzrzjlx;
    private EditText edt_fbf_fzrzjhm;
    private EditText edt_fbf_dcjs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fbfedit);

        edt_fbf_bm = (EditText) findViewById(R.id.edt_fbf_bm);
        edt_fbf_mc = (EditText) findViewById(R.id.edt_fbf_mc);
        edt_fbf_fzrxm = (EditText) findViewById(R.id.edt_fbf_fzrxm);
        edt_fbf_lxdh = (EditText) findViewById(R.id.edt_fbf_lxdh);
        edt_fbf_fzrdz = (EditText) findViewById(R.id.edt_fbf_fzrdz);
        edt_fbf_fzrzjlx = (EditText) findViewById(R.id.edt_fbf_fzrzjlx);
        edt_fbf_fzrzjhm = (EditText) findViewById(R.id.edt_fbf_fzrzjhm);
        edt_fbf_dcjs = (EditText) findViewById(R.id.edt_fbf_dcjs);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);



        if (getIntent().getExtras() != null) {
            mOwnersDB = getIntent().getStringExtra("owners_db");
            mParcelMap = getIntent().getStringExtra("parcel_map");
            FBFBM = getIntent().getStringExtra("FBFBM");
        }

        dbOwners = SQLiteDatabase.openDatabase(mOwnersDB, null, SQLiteDatabase.OPEN_READWRITE);
        Cursor c = dbOwners.rawQuery("SELECT [FBFBM] as [_id], * FROM [FBF] WHERE [FBFBM] = ?", new String[] { FBFBM });
        if (c.moveToFirst()) {
            //rowId = c.getInt(c.getColumnIndex("_id"));
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
        getMenuInflater().inflate(R.menu.menu_fbfedit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.dialog_title_tip);
            builder.setMessage(R.string.dialog_message_save_fbf);
            builder.setPositiveButton(R.string.dialog_btn_yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String error = verifyParamters();
                    if (StringUtil.isStrEmpty(error)) {
                        save();
                    } else {
                        showParamtersErrorDialog(error);
                    }
                }
            });
            builder.setNegativeButton(R.string.dialog_btn_no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.show();
            return true;
        }else if(id == android.R.id.home){
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showParamtersErrorDialog(String error) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_title_tip);
        builder.setMessage(error);
        builder.setPositiveButton(R.string.dialog_btn_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }

    private void save() {
        String fbf_bm = edt_fbf_bm.getText().toString();
        String fbf_mc = edt_fbf_mc.getText().toString();
        String fbf_fzrxm = edt_fbf_fzrxm.getText().toString();
        String fbf_lxdh = edt_fbf_lxdh.getText().toString();
        String fbf_fzrdz = edt_fbf_fzrdz.getText().toString();
        String fbf_fzrzjlx = edt_fbf_fzrzjlx.getText().toString();
        String fbf_fzrzjhm = edt_fbf_fzrzjhm.getText().toString();
        String fbf_dcjs = edt_fbf_dcjs.getText().toString();

        //dbOwners = SQLiteDatabase.openDatabase(mOwnersDB, null, SQLiteDatabase.OPEN_READWRITE);
        ContentValues values = new ContentValues();
        values.put("FBFBM", fbf_bm);
        values.put("FBFMC", fbf_mc);
        values.put("FBFFZRXM", fbf_fzrxm);
        values.put("LXDH", fbf_lxdh);
        values.put("FBFDZ", fbf_fzrdz);
        if(fbf_fzrzjlx.equals("身份证")){
            values.put("FZRZJLX", "1"); // ------------------- 不是身份证的情况呢
        }
        values.put("FZRZJHM", fbf_fzrzjhm);
        values.put("FBFDCJS", fbf_dcjs);

        String[] args = {String.valueOf(FBFBM)};
        dbOwners.update("FBF",values,"FBFBM=?", args);

        Toast.makeText(this,"保存数据成功",Toast.LENGTH_SHORT).show();;
        finish();
    }

    private String verifyParamters() {
        StringBuilder sb = new StringBuilder();
        if(StringUtil.isStrEmpty(edt_fbf_bm.getText().toString())){
            sb.append("发包方编码不能为空\n");
        }
        if(StringUtil.isStrEmpty(edt_fbf_mc.getText().toString())){
            sb.append("发包方名称不能为空\n");
        }
        if(StringUtil.isStrEmpty(edt_fbf_fzrxm.getText().toString())){
            sb.append("发包方姓名不能为空\n");
        }
        if(StringUtil.isStrEmpty(edt_fbf_lxdh.getText().toString())){
            sb.append("联系电话不能为空\n");
        }
        if(StringUtil.isStrEmpty(edt_fbf_fzrdz.getText().toString())){
            sb.append("发包方负责人地址不能为空\n");
        }
        if(StringUtil.isStrEmpty(edt_fbf_fzrzjlx.getText().toString())){
            sb.append("证件类型不能为空\n");
        }
        if(StringUtil.isStrEmpty(edt_fbf_fzrzjhm.getText().toString())){
            sb.append("证件号码不能为空\n");
        }
        if(StringUtil.isStrEmpty(edt_fbf_dcjs.getText().toString())){
            sb.append("调查记事不能为空\n");
        }
        return sb.toString();
    }

}
