package org.fabmazz.locdemo;

import android.content.Context;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.mylocation.IMyLocationConsumer;
import org.osmdroid.views.overlay.mylocation.IMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;
import org.owm.libwlocate.WLocListener;
import org.owm.libwlocate.WLocate;



public class MainActivity extends AppCompatActivity {
    protected MapView  osmMap;
    private MapController mapController;
    private WLocate mwlocate;
    private WLocListener mwlocListener;
    private MyLocationNewOverlay mlocOverlay;
    private WlocLocationProvider provider;
    private double mlat,mlon;
    private float maccu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        osmMap = (MapView) findViewById(R.id.mapView);
        osmMap.setBuiltInZoomControls(true);
        osmMap.setMultiTouchControls(true);
        osmMap.setMinZoomLevel(4);
        GeoPoint portaSusa = new GeoPoint(45.070401, 7.664702);
        osmMap.showContextMenu();
        mapController = (MapController) osmMap.getController();
        /**
         * Wlocate object and listener go together
         */
        mwlocate = new WLocate(getApplicationContext(), WLocate.LOC_SERVER_OPENWIFISU,false);
        mwlocListener = new WLocListener() {
            @Override
            public void onLocationReceived(double lat, double lon, float radius) {
                Log.d("LocDemo", "Location received from libwlocate: "+lat+", "+lon+", accu: "+radius);
                mlat=lat;
                mlon=lon;
                maccu=radius;
                mapController.setZoom(15);
                mlocOverlay.enableFollowLocation();
            }

            @Override
            public void onLocationError(int code) {
                switch (code){
                    case WLocate.WIFI_DISABLED:
                        Log.w("LocDemo", "Location was started but wifi is disabled");
                        makeToast(R.string.enable_wifi_message,true);
                        break;
                    case WLocate.THREAD_ALREADY_RUNNING:
                        Log.w("LocDemo", "Location was started but thread wasn't finished");
                        break;
                    case WLocate.IO_ERROR:
                        makeToast(R.string.error_io_error,false);
                        break;
                    case WLocate.PARSING_RESPONSE_ERROR:
                        makeToast(R.string.error_parsing_response,false);
                        break;
                    case WLocate.WLOC_CONNECTION_ERROR:
                        makeToast(R.string.error_connection_problem,true);
                        break;
                    case WLocate.WLOC_SERVER_ERROR:
                        makeToast(R.string.error_server_comunication,true);
                    default:
                        makeToast(R.string.error_generic,false);
                        break;
                    //As precaution, check if there are any remaining cases
                }
            }
        };
        mwlocate.setLocListener(mwlocListener);
        provider = new WlocLocationProvider();
        mlocOverlay = new MyLocationNewOverlay(getApplicationContext(),provider, osmMap);
        mlocOverlay.enableMyLocation();
        osmMap.getOverlays().add(mlocOverlay);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_map, menu);
        return true;
    }

    public void startLocation(View view){
        Log.d("LocDemo", "Location started manually");
        mwlocate.wlocRequestPosition(0);
    }
    private void makeToast(int resourceID, boolean durationLong){
        Context context = getApplicationContext();
        int duration;
        if(durationLong) duration=Toast.LENGTH_LONG;
        else duration=Toast.LENGTH_SHORT;
        Toast.makeText(context,resourceID,duration).show();
    }

    /**
     *
     * This class is a personal "trick" to make the osmdroid library accept location updates from libwlocate
     * @author fab4mazz
     */
    class WlocLocationProvider implements IMyLocationProvider{
        @Override
        public boolean startLocationProvider(IMyLocationConsumer myLocationConsumer) {
            Log.d("LocDemo", "Location requested");
            if(mwlocate!=null) {
                mwlocate.wlocRequestPosition(0);
                return true;
            }else {
                return false;
            }

        }

        @Override
        public void stopLocationProvider() {

        }

        @Override
        public Location getLastKnownLocation() {
            Location loc = new Location("");
            if(mlat>0. && mlon>0.0) {
                loc.setLatitude(mlat);
                loc.setLongitude(mlon);
                loc.setAccuracy(maccu);
                Log.d("LocDemo", "Libwlocate location read");
                return loc;
            }
            Log.d("LocDemo", "No libwlocate location");
            return null;
        }
    }
}
