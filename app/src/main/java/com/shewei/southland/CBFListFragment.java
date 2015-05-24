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
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CBFListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CBFListFragment extends Fragment {
    private static final String ARG_OWNERS_DB = "owners_db";
    private static final String ARG_PARCEL_MAP = "parcel_map";
    private static final int SECTION_NUMBER = 3;

    private String mOwnersDB;
    private String mParcelMap;
    private ListView lVi_CBF;
    private SQLiteDatabase dbOwners;
    private SimpleCursorAdapter adapter;


    public static CBFListFragment newInstance(String owners_db, String parcel_map) {
        CBFListFragment fragment = new CBFListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_OWNERS_DB, owners_db);
        args.putString(ARG_PARCEL_MAP, parcel_map);
        fragment.setArguments(args);
        return fragment;
    }

    public CBFListFragment() {
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
        View view = inflater.inflate(R.layout.fragment_cbflist, container, false);
        lVi_CBF = (ListView) view.findViewById(R.id.lVi_CBF);
        lVi_CBF.setOnItemClickListener(onCBFListItemClicked);

        Cursor c = dbOwners.rawQuery("SELECT [CBFBM] as [_id], * FROM [CBF]", null);
        adapter = new SimpleCursorAdapter(getActivity(),
                R.layout.cbf_item, c,
                new String[] {"CBFBM", "CBFMC", "CBFZJHM", "LXDH"},
                new int[] {R.id.txt_cbf_bm, R.id.txt_cbf_mc, R.id.txt_cbf_zjhm, R.id.txt_cbf_lxdh});
        lVi_CBF.setAdapter(adapter);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((ProjectMainActivity) activity).onSectionAttached(SECTION_NUMBER);
    }

    private AdapterView.OnItemClickListener onCBFListItemClicked = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Cursor c = adapter.getCursor();
            if (c.moveToPosition(position)) {
                Intent intent = new Intent(getActivity(), CBFDetailActivity.class);
                intent.putExtra("owners_db", mOwnersDB);
                intent.putExtra("parcel_map", mParcelMap);
                intent.putExtra("CBFBM", c.getString(c.getColumnIndex("CBFBM")));
                getActivity().startActivity(intent);
            }
        }
    };
}
