package com.shewei.southland;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StatisticFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StatisticFragment extends Fragment {
    private static final String ARG_OWNERS_DB = "owners_db";
    private static final String ARG_PARCEL_MAP = "parcel_map";
    private static final int SECTION_NUMBER = 1;

    private String mOwnersDB;
    private String mParcelMap;

    public static StatisticFragment newInstance(String owners_db, String parcel_map) {
        StatisticFragment fragment = new StatisticFragment();
        Bundle args = new Bundle();
        args.putString(ARG_OWNERS_DB, owners_db);
        args.putString(ARG_PARCEL_MAP, parcel_map);
        fragment.setArguments(args);
        return fragment;
    }

    public StatisticFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mOwnersDB = getArguments().getString(ARG_OWNERS_DB);
            mParcelMap = getArguments().getString(ARG_PARCEL_MAP);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_statistic, container, false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((ProjectMainActivity) activity).onSectionAttached(SECTION_NUMBER);
    }
}
