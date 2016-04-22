package org.fabmazz.locdemo;

import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import org.osmdroid.api.IGeoPoint;
import org.osmdroid.events.MapListener;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.mylocation.IMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;
import org.owm.libwlocate.WLocListener;
import org.owm.libwlocate.WLocate;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    protected MapView  osmMap;
    protected MapListener mapListener;
    MapController mapController;
    WLocate mwlocate;
    WLocListener mwlocListener;
    ArrayList<OverlayItem> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        osmMap = (MapView) findViewById(R.id.mapView);
        osmMap.setBuiltInZoomControls(true);
        osmMap.setMultiTouchControls(true);
        osmMap.setMinZoomLevel(3);
        osmMap.getController().animateTo(new GeoPoint(45.0472,7.6524));
        osmMap.showContextMenu();
        mapController = (MapController) osmMap.getController();
        GeoPoint portasusa = new GeoPoint(45.070401, 7.664702);


        items = new ArrayList<OverlayItem>();
        items.add(new OverlayItem("Porta Susa", "Stazione ferroviaria", portasusa)); // Lat/Lon decimal degrees

//the overlay
        ItemizedOverlayWithFocus<OverlayItem> mOverlay = new ItemizedOverlayWithFocus<OverlayItem>(items,
                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                    @Override
                    public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                        //do something
                        return true;
                    }
                    @Override
                    public boolean onItemLongPress(final int index, final OverlayItem item) {
                        return false;
                    }
                },osmMap.getResourceProxy());
        mOverlay.setFocusItemsOnTap(true);

        osmMap.getOverlays().add(mOverlay);


        mwlocate = new WLocate(getApplicationContext(), WLocate.LOC_SERVER_OPENWIFISU,false);
        mwlocListener = new WLocListener() {
            @Override
            public void onLocationReceived(double lat, double lon, float radius) {
                osmMap.getController().animateTo(new GeoPoint(lat,lon));
            }

            @Override
            public void onLocationError(int code) {

            }
        };
        mwlocate.setLocListener(mwlocListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_map, menu);
        return true;
    }
    public void showExample(){
        GeoPoint portasusa = new GeoPoint(45.070401, 7.664702);
        mapController.setZoom(20);
        mapController.animateTo(portasusa);
    }
}
