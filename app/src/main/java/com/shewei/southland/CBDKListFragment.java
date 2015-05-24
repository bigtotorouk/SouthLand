package com.shewei.southland;


import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.Layer;
import com.esri.android.map.MapView;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.android.runtime.ArcGISRuntime;
import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.MapGeometry;
import com.esri.core.geometry.Polygon;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.SimpleFillSymbol;
import com.esri.core.symbol.Symbol;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;
import org.json.JSONException;

import java.io.IOException;
import java.lang.Exception;
import java.util.ArrayList;

import jsqlite.*;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CBDKListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CBDKListFragment extends Fragment {
    private static final String ARG_OWNERS_DB = "owners_db";
    private static final String ARG_PARCEL_MAP = "parcel_map";
    private static final int SECTION_NUMBER = 4;

    private String mOwnersDB;
    private String mParcelMap;
    private MapView mapView;
    private Database parcel_map;
    private ProgressBar proBar_loading_layer;
    private int layerIndex;

    private static class LayerInfo {
        public String layer_name;
        public String SRID;
        public String geometry_type;
    }
    private ArrayList<LayerInfo> layerInfos = new ArrayList<>();

    public static CBDKListFragment newInstance(String owners_db, String parcel_map) {
        CBDKListFragment fragment = new CBDKListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_OWNERS_DB, owners_db);
        args.putString(ARG_PARCEL_MAP, parcel_map);
        fragment.setArguments(args);
        return fragment;
    }

    public CBDKListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ArcGISRuntime.setClientId("bf2Vxv1bkmNyfPNj");

        if (getArguments() != null) {
            mOwnersDB = getArguments().getString(ARG_OWNERS_DB);
            mParcelMap = getArguments().getString(ARG_PARCEL_MAP);
        }

        parcel_map = new Database();
        try {
            parcel_map.open(mParcelMap, Constants.SQLITE_OPEN_READWRITE);
        } catch (jsqlite.Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            parcel_map.close();
        } catch (jsqlite.Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cbdklist, container, false);
        proBar_loading_layer = (ProgressBar) view.findViewById(R.id.proBar_loading_layer);

        // Setting up MapView
        mapView = (MapView) view.findViewById(R.id.mapView);
        mapView.enableWrapAround(true);
        mapView.setOnStatusChangedListener(new OnStatusChangedListener() {
            @Override
            public void onStatusChanged(Object o, STATUS status) {
                if (status == STATUS.LAYER_LOADED && o instanceof Layer) {
                    Layer layer = (Layer) o;
                    Polygon extent = layer.getExtent();
                    mapView.setExtent(extent);
                }
            }
        });

        // 获取图层列表
        if (layerInfos.isEmpty()) {
            loadLayerList();
        }

        // 创建图层
        ArrayList<GraphicsLayer> layers = new ArrayList<>();
        for (int i = 0; i < layerInfos.size(); i++) {
            SpatialReference sr = SpatialReference.create(Integer.valueOf(layerInfos.get(i).SRID));
            Envelope env = queryLayerExtent(layerInfos.get(i).layer_name);

            GraphicsLayer layer = new GraphicsLayer(sr, env, GraphicsLayer.RenderingMode.STATIC);
            layers.add(layer);

            mapView.addLayer(layer);
            mapView.centerAt(env.getCenter(), true);
        }

        // 创建加载图层的异步任务
        SimpleFillSymbol sfs = new SimpleFillSymbol(Color.GREEN);
        proBar_loading_layer.setVisibility(View.VISIBLE);
        layerIndex = 0;
        for (int i = 0; i < layers.size(); i++) {
            LayerInfo info = layerInfos.get(i);
            LoadLayerContentAsyncTask task = new LoadLayerContentAsyncTask(parcel_map, layers.get(i), sfs);
            task.execute(info.layer_name, info.geometry_type, info.SRID);
        }

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((ProjectMainActivity) activity).onSectionAttached(SECTION_NUMBER);
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.pause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.unpause();
    }

    private void loadLayerList() {
        try {
            parcel_map.exec("SELECT * FROM geometry_columns;", new Callback() {
                public void columns(String[] strings) {
                }

                public void types(String[] strings) {
                }

                @Override
                public boolean newrow(String[] cols) {
                    LayerInfo info = new LayerInfo();
                    info.layer_name = cols[0]; // f_table_name;
                    info.geometry_type = cols[2]; // geometry_type
                    info.SRID = cols[4]; // srid
                    layerInfos.add(info);
                    return false;
                }
            });
        } catch (jsqlite.Exception e) {
            e.printStackTrace();
        }
    }

    private Envelope queryLayerExtent(String layer_name) {
        String sql_query_envelop = "SELECT MbrMinX(Extent(Geometry)), MbrMinY(Extent(Geometry)), " +
                "MbrMaxX(Extent(Geometry)), MbrMaxY(Extent(Geometry)) FROM ";
        final Envelope env = new Envelope();

        try {
            parcel_map.exec(sql_query_envelop + layer_name, new Callback() {
                @Override public void columns(String[] strings) { }
                @Override public void types(String[] strings) { }

                @Override
                public boolean newrow(String[] cols) {
                    env.setXMin(Double.valueOf(cols[0]));
                    env.setYMin(Double.valueOf(cols[1]));
                    env.setXMax(Double.valueOf(cols[2]));
                    env.setYMax(Double.valueOf(cols[3]));
                    return false;
                }
            });
        } catch (jsqlite.Exception e) {
            e.printStackTrace();
        }

        return env;
    }

    public class LoadLayerContentAsyncTask extends AsyncTask<String, Integer, Void> {
        private static final String sql_query_content = "SELECT Dkbm, Scmj, Dkmc, AsText(Geometry) FROM ";

        private final Database mGDB;
        private final GraphicsLayer mLayer;
        private final JsonFactory jsonFactory = new JsonFactory();
        private final Symbol mSymbol;
        private final ArrayList<Graphic> mGraphics = new ArrayList<>();

        public LoadLayerContentAsyncTask(Database gdb, GraphicsLayer layer, Symbol symbol) {
            mGDB = gdb;
            mLayer = layer;
            mSymbol = symbol;
        }

        @Override
        protected Void doInBackground(String... params) {
            String table_name = params[0];
            final String geometry_type = params[1];
            final int SRID = Integer.valueOf(params[2]);

            try {
                mGDB.exec(sql_query_content + table_name, new Callback() {
                    @Override public void columns(String[] strings) { }
                    @Override public void types(String[] strings) { }

                    @Override
                    public boolean newrow(String[] cols) {
                        String geometry = null;
                        // WKT -> JSON
                        try {
                            if (geometry_type.equals("3")) {
                                geometry = WKT.writePolygon(cols[3], SRID);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        JsonParser parser = null;
                        try {
                            parser = jsonFactory.createJsonParser(geometry);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        // 创建ArcGIS geometry
                        MapGeometry mapGeometry = GeometryEngine.jsonToGeometry(parser);

                        // 创建Graphic
                        Graphic g = new Graphic(mapGeometry.getGeometry(), mSymbol);
                        mGraphics.add(g);
                        return false;
                    }
                });
            } catch (jsqlite.Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mLayer.addGraphics(mGraphics.toArray(new Graphic[mGraphics.size()]));
            ++layerIndex;
            if (layerIndex == layerInfos.size()) {
                proBar_loading_layer.setVisibility(View.GONE);
            }
        }
    }
}
