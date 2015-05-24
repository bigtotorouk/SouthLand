package com.shewei.southland;


import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FBFListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FBFListFragment extends Fragment {
    private static final String ARG_OWNERS_DB = "owners_db";
    private static final String ARG_PARCEL_MAP = "parcel_map";
    private static final int SECTION_NUMBER = 2;

    private static final String SQL_QUERY_FBF = "SELECT [FBFBM] AS [_id], * FROM [FBF] WHERE [FBFBM] LIKE ? ORDER BY [FBFBM] ASC;";
    public static final String SQL_QUERY_XJXZQ = "SELECT [BSM] as [_id], [XZQMC] FROM [XJXZQ] ORDER BY [BSM] ASC;";
    public static final String SQL_EXACT_QUERY_XJQY = "SELECT [BSM] as [_id], [XJQYDM] FROM [XJQY] WHERE [XJQYMC] = ?";
    public static final String SQL_QUERY_CJQY_BY_XJQYDM = "SELECT [BSM] as [_id], [CJQYMC] FROM [CJQY] WHERE [CJQYDM] LIKE ?";
    public static final String SQL_QUERY_XJQY_BY_XZQDM = "SELECT [BSM] as [_id], [XJQYMC] FROM [XJQY] WHERE [XJQYDM] LIKE ?";
    public static final String SQL_EXACT_QUERY_XJXZQ = "SELECT [BSM] as [_id], [XZQDM] FROM [XJXZQ] WHERE [XZQMC] = ?";
    public static final String SQL_EXACT_QUERY_CJQY = "SELECT [BSM] as [_id], [CJQYDM] FROM [CJQY] WHERE [CJQYMC] = ?";

    private String mOwnersDB;
    private String mParcelMap;

    private SQLiteDatabase dbOwners;
    private ListView lVi_FBF;
    private SimpleCursorAdapter adapter;

    private Spinner spn_xian;
    private Spinner spn_xiang;
    private Spinner spn_cun;

    private ArrayList<String> strXian = new ArrayList<>();
    private ArrayList<String> strXiang = new ArrayList<>();
    private ArrayList<String> strCun = new ArrayList<>();

    private String filterCode = "%";

    public static FBFListFragment newInstance(String owners_db, String parcel_map) {
        FBFListFragment fragment = new FBFListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_OWNERS_DB, owners_db);
        args.putString(ARG_PARCEL_MAP, parcel_map);
        fragment.setArguments(args);
        return fragment;
    }

    public FBFListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mOwnersDB = getArguments().getString(ARG_OWNERS_DB);
            mParcelMap = getArguments().getString(ARG_PARCEL_MAP);
        }

        dbOwners = SQLiteDatabase.openDatabase(mOwnersDB, null, SQLiteDatabase.OPEN_READWRITE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dbOwners.close();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fbflist, container, false);
        lVi_FBF = (ListView) view.findViewById(R.id.lVi_FBF);
        lVi_FBF.setOnItemClickListener(onFBFListItemClicked);

        spn_xian = (Spinner) view.findViewById(R.id.spn_xian);
        spn_xiang = (Spinner) view.findViewById(R.id.spn_xiang);
        spn_cun = (Spinner) view.findViewById(R.id.spn_cun);

        spn_xian.setOnItemSelectedListener(onXianSpinnerItemSelected);
        spn_xiang.setOnItemSelectedListener(onXiangSpinnerItemSelected);
        spn_cun.setOnItemSelectedListener(onCunSpinnerItemSelected);

        // 填充县级筛选器
        strXian.add("不限");
        Cursor c = dbOwners.rawQuery(SQL_QUERY_XJXZQ, null);
        while (c.moveToNext()) {
            strXian.add(c.getString(1));
        }
        spn_xian.setAdapter(new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item, strXian));

        // 禁用乡和村级筛选器
        spn_xiang.setEnabled(false);
        spn_cun.setEnabled(false);

        c = dbOwners.rawQuery(SQL_QUERY_FBF, new String[] {filterCode});
        adapter = new SimpleCursorAdapter(getActivity(),
                android.R.layout.simple_list_item_2,
                c,
                new String[] { "FBFBM", "FBFMC" },
                new int[] { android.R.id.text1, android.R.id.text2 });
        lVi_FBF.setAdapter(adapter);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((ProjectMainActivity) activity).onSectionAttached(SECTION_NUMBER);
    }

    private AdapterView.OnItemClickListener onFBFListItemClicked = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Cursor c = adapter.getCursor();
            if (c.moveToPosition(position)) {
                Intent intent = new Intent(getActivity(), FBFDetailActivity.class);
                intent.putExtra("FBFBM", c.getString(c.getColumnIndex("FBFBM")));
                intent.putExtra("owners_db", mOwnersDB);
                intent.putExtra("parcel_map", mParcelMap);
                getActivity().startActivity(intent);
            }
        }
    };

    private AdapterView.OnItemSelectedListener onXianSpinnerItemSelected = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            strXiang.clear();
            strXiang.add("不限");
            String localFilterCode = null;

            if (position > 0) {
                String XZQDM = null;
                Cursor c = dbOwners.rawQuery(SQL_EXACT_QUERY_XJXZQ, new String[] {strXian.get(position)});
                if (c.moveToFirst()) {
                    XZQDM = c.getString(1);
                }

                localFilterCode = XZQDM + "%";

                c = dbOwners.rawQuery(SQL_QUERY_XJQY_BY_XZQDM, new String[] {localFilterCode});
                while (c.moveToNext()) {
                    strXiang.add(c.getString(1));
                }

                spn_xiang.setEnabled(true);
            } else if (position == 0) {
                spn_xiang.setEnabled(false);
                localFilterCode = "%";
            }

            spn_xiang.setAdapter(new ArrayAdapter<>(getActivity(),
                    android.R.layout.simple_spinner_dropdown_item, strXiang));

            if (!localFilterCode.equals(filterCode)) {
                filterCode = localFilterCode;
                onFilterCodeChanged();
            }
        }

        @Override public void onNothingSelected(AdapterView<?> parent) { }
    };

    private AdapterView.OnItemSelectedListener onXiangSpinnerItemSelected = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            strCun.clear();
            strCun.add("不限");

            String localFilterCode = null;

            if (position > 0) {
                String XJQYDM = null;
                Cursor c = dbOwners.rawQuery(SQL_EXACT_QUERY_XJQY, new String[]{strXiang.get(position) });
                if (c.moveToFirst()) {
                    XJQYDM = c.getString(1);
                }

                localFilterCode = XJQYDM + "%";

                c = dbOwners.rawQuery(SQL_QUERY_CJQY_BY_XJQYDM, new String[] {localFilterCode});
                while (c.moveToNext()) {
                    strCun.add(c.getString(1));
                }

                spn_cun.setEnabled(true);
            } else if (position == 0) {
                spn_cun.setEnabled(false);
                if (filterCode.length() > 1)
                    localFilterCode = filterCode.substring(0, 6) + "%";
                else
                    localFilterCode = filterCode;
            }

            spn_cun.setAdapter(new ArrayAdapter<>(getActivity(),
                    android.R.layout.simple_spinner_dropdown_item, strCun));

            if (!localFilterCode.equals(filterCode)) {
                filterCode = localFilterCode;
                onFilterCodeChanged();
            }
        }

        @Override public void onNothingSelected(AdapterView<?> parent) { }
    };

    private AdapterView.OnItemSelectedListener onCunSpinnerItemSelected = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String localFilterCode = null;

            if (position > 0) {
                String CJQYDM = null;
                Cursor c = dbOwners.rawQuery(SQL_EXACT_QUERY_CJQY, new String[] {strCun.get(position)});
                if (c.moveToFirst()) {
                    CJQYDM = c.getString(1);
                }

                 localFilterCode = CJQYDM + "%";
            } else if (position == 0) {
                if (filterCode.length() > 7)
                    localFilterCode = filterCode.substring(0, 9) + "%";
                else
                    localFilterCode = filterCode;
            }

            if (!localFilterCode.equals(filterCode)) {
                filterCode = localFilterCode;
                onFilterCodeChanged();
            }
        }

        @Override public void onNothingSelected(AdapterView<?> parent) { }
    };

    private void onFilterCodeChanged() {
        Cursor c = dbOwners.rawQuery(SQL_QUERY_FBF, new String[] {filterCode});
        adapter.changeCursor(c);
        adapter.notifyDataSetChanged();
    }
}
