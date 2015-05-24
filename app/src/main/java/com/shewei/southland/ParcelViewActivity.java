package com.shewei.southland;

import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.Layer;
import com.esri.android.map.MapView;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.android.runtime.ArcGISRuntime;
import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.MapGeometry;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.SimpleFillSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol;
import com.esri.core.symbol.Symbol;
import com.southgnss.location.SouthGnssManager;
import com.southgnss.server.CoordTransformManager;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;
import org.json.JSONException;

import java.io.IOException;
import java.lang.Exception;
import java.util.ArrayList;

import jsqlite.*;


public class ParcelViewActivity extends ActionBarActivity {

    private MapView mapView_parcel;
    private Database parcel_map;

    private String mOwnersDB;
    private String mParcelMap;
    private String FBFBM;
    private String CBFBM;
    private SouthGnssManager mGPSManager;
    private CoordTransformManager mCoordTransformManager;


    private static class LayerInfo {
        public String layer_name;
        public String SRID;
        public String geometry_type;
    }
    private ArrayList<LayerInfo> layerInfos = new ArrayList<>();

    private GraphicsLayer gpsLayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parcel_view);
        ArcGISRuntime.setClientId("bf2Vxv1bkmNyfPNj");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);

        mapView_parcel = (MapView) findViewById(R.id.mapView_parcel);
        mapView_parcel.enableWrapAround(true);
        mapView_parcel.setOnStatusChangedListener(new OnStatusChangedListener() {
            @Override
            public void onStatusChanged(Object o, STATUS status) {
                if (status == STATUS.LAYER_LOADED && o instanceof Layer) {
                    Layer layer = (Layer) o;
                    Polygon extent = layer.getExtent();
                    Polygon ext2 = mapView_parcel.getExtent();
                    Polygon g = (Polygon) GeometryEngine.union(new Geometry[] {ext2, extent}, mapView_parcel.getSpatialReference());
                    mapView_parcel.setExtent(g);
                }
            }
        });

        if (getIntent().getExtras() != null) {
            mOwnersDB = getIntent().getStringExtra("owners_db");
            mParcelMap = getIntent().getStringExtra("parcel_map");
            FBFBM = getIntent().getStringExtra("FBFBM");
            CBFBM = getIntent().getStringExtra("CBFBM");
            setTitle(getIntent().getStringExtra("title"));
        }

        parcel_map = new Database();
        try {
            parcel_map.open(mParcelMap, Constants.SQLITE_OPEN_READWRITE);
        } catch (jsqlite.Exception e) {
            e.printStackTrace();
        }

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

            mapView_parcel.addLayer(layer);
            mapView_parcel.centerAt(env.getCenter(), true);
        }

        // 创建加载图层的异步任务
        SimpleFillSymbol sfs = new SimpleFillSymbol(Color.GREEN);

        for (int i = 0; i < layers.size(); i++) {
            LayerInfo info = layerInfos.get(i);
            LoadLayerContentAsyncTask task = new LoadLayerContentAsyncTask(parcel_map, layers.get(i), sfs);
            task.execute(info.layer_name, info.geometry_type, info.SRID);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            parcel_map.close();
        } catch (jsqlite.Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView_parcel.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView_parcel.unpause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_parcel_view, menu);
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
        } else if (id == R.id.action_gps) {
            mGPSManager = SouthGnssManager.getInstence(this);
            mGPSManager.initialize();

            mCoordTransformManager = new CoordTransformManager();
            mCoordTransformManager.bindService(this);

            double dA = 6378245.0;
            double dF = 298.3;
            double[] projPar = new double[6];
            projPar[0] = 0;//北坐标
            projPar[1] = 500000;//东坐标
            projPar[2] = 114;//中央子午线
            projPar[3] = 0;//基准纬度
            projPar[4] = 1;//比较尺
            projPar[5] = 0;//投影高
            mCoordTransformManager.setProjectionTransformPar(1, dA, dF, projPar);

            mGPSManager.startGnss();

            mGPSManager.addListener(mLocationListener);

            gpsLayer = new GraphicsLayer();
            mapView_parcel.addLayer(gpsLayer);
            return true;
        }

        return super.onOptionsItemSelected(item);
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
        private static final String sql_query_content = "SELECT AsText(Geometry)， * FROM ";

        private final Database mGDB;
        private final GraphicsLayer mLayer;
        private final JsonFactory jsonFactory = new JsonFactory();
        private final Symbol mSymbol;
        private final ArrayList<Graphic> mGraphics = new ArrayList<>();
        private int layerIndex;

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
                String tmp = "SELECT AsText(Geometry), * FROM " + table_name + " WHERE Dkbm LIKE \"" + FBFBM + "%\"";
                mGDB.exec(tmp, new Callback() {
                    @Override public void columns(String[] strings) { }
                    @Override public void types(String[] strings) { }

                    @Override
                    public boolean newrow(String[] cols) {
                        String geometry = null;
                        // WKT -> JSON
                        try {
                            if (geometry_type.equals("3")) {
                                geometry = WKT.writePolygon(cols[0], SRID);
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
        }
    }

    private LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            double[] dx = new double[1];
            double[] dy = new double[1];
            double[] dh = new double[1];

            mCoordTransformManager.BLHtoxyh(
                    location.getLatitude(), location.getLongitude(), location.getAltitude(),
                    dx, dy, dh);
            SimpleMarkerSymbol simpleMarkerSymbol = new SimpleMarkerSymbol(Color.RED, 10, SimpleMarkerSymbol.STYLE.CIRCLE);
            Point gps = new Point(dx[0], dy[0]);
            Graphic g = new Graphic(gps, simpleMarkerSymbol);
            gpsLayer.removeAll();
            gpsLayer.addGraphic(g);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };
}
