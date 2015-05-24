package com.shewei.southland;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;


public class StartActivity extends ActionBarActivity {

    private static final int CREATE_PROJECT_DIALOG = 1;
    public static final String SQL_QUERY_PROJECTS = "SELECT * FROM [projects] ORDER BY [creation_time] DESC;";
    private ConfigDbOpenHelper dbOpenHelper;
    private ListView lVi_projects;
    private SimpleCursorAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        boolean from_project = getIntent().getBooleanExtra("from_project", false);
        if (from_project) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
            sp.edit().putInt("project_position", -1).apply();
        }

        lVi_projects = (ListView) findViewById(R.id.lVi_projects);
        lVi_projects.setOnItemClickListener(onProjecsListItemClicked);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setLogo(R.drawable.ic_logo);
        actionBar.setDisplayUseLogoEnabled(true);

        dbOpenHelper = new ConfigDbOpenHelper(this);
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();

        Cursor c = db.rawQuery(SQL_QUERY_PROJECTS, null);
        adapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_1,
                c,
                new String[] { "project_name" },
                new int[] { android.R.id.text1 });
        lVi_projects.setAdapter(adapter);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        int project_position = sp.getInt("project_position", -1);
        if (project_position != -1) {
            onProjecsListItemClicked.onItemClick(null, null, project_position, -1);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbOpenHelper.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_start, menu);
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
            case CREATE_PROJECT_DIALOG:
                if (resultCode == RESULT_OK) {
                    createNewProject(data.getStringExtra("project_name"),
                            data.getStringExtra("owners_db"),
                            data.getStringExtra("parcel_map"),
                            data.getStringExtra("creation_time"));
                }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void createNewProject(String project_name, String owners_db, String parcel_map, String creation_time) {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("project_name", project_name);
        cv.put("owners_db", owners_db);
        cv.put("parcel_map", parcel_map);
        cv.put("creation_time", creation_time);
        if (-1 == db.insert("projects", null, cv)) {
            Toast.makeText(this, "新建工程失败", Toast.LENGTH_SHORT).show();
        } else {
            Cursor c = db.rawQuery(SQL_QUERY_PROJECTS, null);
            adapter.changeCursor(c);
            adapter.notifyDataSetChanged();
        }
    }

    public void onNewProjectButtonClicked(View view) {
        Intent intent = new Intent(this, CreateProjectActivity.class);
        startActivityForResult(intent, CREATE_PROJECT_DIALOG);
    }

    // Event Listener: lVi_project: onItemClickListener
    private AdapterView.OnItemClickListener onProjecsListItemClicked = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Cursor c = adapter.getCursor();
            if (c.moveToPosition(position)) {
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(StartActivity.this);
                sp.edit().putInt("project_position", position).apply();

                Intent intent = new Intent(StartActivity.this, ProjectMainActivity.class);
                intent.putExtra("project_name", c.getString(c.getColumnIndex("project_name")));
                intent.putExtra("owners_db", c.getString(c.getColumnIndex("owners_db")));
                intent.putExtra("parcel_map", c.getString(c.getColumnIndex("parcel_map")));
                startActivity(intent);
                finish();
            }
        }
    };
}
