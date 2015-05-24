package com.shewei.southland;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Date;


public class CreateProjectActivity extends ActionBarActivity {

    private static final int SELECT_OWNERS_DB = 1;
    private static final int SELECT_PARCEL_MAP = 2;
    private EditText edt_project_name;
    private Button btn_browser_owners_db;
    private Button btn_browser_parcel_map;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_project);
        overridePendingTransition(R.animator.slide_in_right, 0);

        edt_project_name = (EditText) findViewById(R.id.edt_project_name);
        btn_browser_owners_db = (Button) findViewById(R.id.btn_browser_owners_db);
        btn_browser_parcel_map = (Button) findViewById(R.id.btn_browser_parcel_map);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_project, menu);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case SELECT_OWNERS_DB:
                if (resultCode == RESULT_OK) {
                    String path = FileUtils.getPath(this, data.getData());
                    btn_browser_owners_db.setText(path);
                }
                break;

            case SELECT_PARCEL_MAP:
                if (resultCode == RESULT_OK) {
                    String path = FileUtils.getPath(this, data.getData());
                    btn_browser_parcel_map.setText(path);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void onBrowserOwnersDBButtonClicked(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(Intent.createChooser(intent,
                    "选择权属数据库"), SELECT_OWNERS_DB);
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(this, "需要安装文件管理器后才能执行该功能", Toast.LENGTH_SHORT).show();
        }
    }

    public void onBrowserParcelMapButtonClicked(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(Intent.createChooser(intent,
                    "选择宗地矢量图"), SELECT_PARCEL_MAP);
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(this, "需要安装文件管理器后才能执行该功能", Toast.LENGTH_SHORT).show();
        }
    }

    public void onCreateProjectButtonClicked(View view) {
        String project_name = edt_project_name.getText().toString();
        String owners_db = btn_browser_owners_db.getText().toString();
        String parcel_map = btn_browser_parcel_map.getText().toString();

        if (project_name.isEmpty() || owners_db.isEmpty() || parcel_map.isEmpty()) {
            Toast.makeText(this, "输入信息有误!", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent();
        intent.putExtra("project_name", project_name);
        intent.putExtra("owners_db", owners_db);
        intent.putExtra("parcel_map", parcel_map);
        intent.putExtra("creation_time", DateFormat.getDateTimeInstance().format(new Date()));
        setResult(RESULT_OK, intent);
        finish();
    }

    public static class FileUtils {
        public static String getPath(Context context, Uri uri) {
            if ("content".equalsIgnoreCase(uri.getScheme())) {
                String[] projection = { "_data" };
                Cursor cursor = null;
                try {
                    cursor = context.getContentResolver().query(uri, projection,null, null, null);
                    int column_index = cursor.getColumnIndexOrThrow("_data");
                    if (cursor.moveToFirst()) {
                        return cursor.getString(column_index);
                    }
                } catch (Exception e) {
                    // Eat it
                }
            } else if ("file".equalsIgnoreCase(uri.getScheme())) {
                return uri.getPath();
            }
            return null;
        }
    }
}
