package com.example.contactame.fragments;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.contactame.R;
import com.example.contactame.models.Proveedor;
import com.example.contactame.models.Servicio;
import com.example.contactame.utils.PermissionUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import javax.annotation.Nonnull;
import java.util.ArrayList;


public class MapsFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener,
    GoogleApiClient.ConnectionCallbacks, LocationListener {

    private GoogleMap mMap;
    private MarkerOptions userMarker;
    private MapView mMapView;
    private GoogleApiClient mGoogleApiClient;
    private SupportMapFragment mMapFragment;
    private MapaProveedorDetalleFragment mapaProveedorDetalleFragment = new MapaProveedorDetalleFragment();

    private FirebaseFirestore mFirestore;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean mPermissionDenied = false;
    final int PERMISSION_LOCATION = 111;

    public MapsFragment() {
        // Required empty public constructor
    }

    public static MapsFragment newInstance() {
        MapsFragment fragment = new MapsFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        mMapView = view.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mMap.setMyLocationEnabled(true);
            }
        });
        return view;
    }

    public void startLocationServices() {
        Log.v("Wiwi", "Iniciando servicio de ubicacion");
        try {
            LocationRequest request = LocationRequest.create().setPriority(LocationRequest.PRIORITY_LOW_POWER);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, request, this);
            Log.v("Wiwi", "Ubicacion actualizada");
        } catch (SecurityException exception) {
            Log.v("Wiwi", exception.toString());
        }
    }

    public void setUserMarker(LatLng latLng) {
        if (userMarker == null) {
            userMarker = new MarkerOptions().position(latLng).title("Ubicacion actual");
            mMap.addMarker(userMarker);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            Log.v("Wiwi", "Latitud actual: " + latLng.latitude + " Long: " + latLng.longitude);
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        getProveedores();
    }

    private void updateMap(final ArrayList<Proveedor> proveedores) {
        int index = 0;
        for (Proveedor proveedor : proveedores) {

            if (proveedor.getServicio_ubicacion() != null) {
                MarkerOptions marker = new MarkerOptions().position(new LatLng(proveedor.getServicio_ubicacion().getLatitude(),
                    proveedor.getServicio_ubicacion().getLongitude()));
                marker.title(proveedor.getProveedor_nombre());
                marker.snippet(proveedor.getServicio_descripcion());

                setIconos(proveedor, marker);

                mMap.addMarker(marker).setTag(proveedores.indexOf(proveedor));
            }
        }

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker mark) {
                Integer selectedIndex = (Integer) mark.getTag();
                Bundle bundle  = new Bundle();
                bundle.putParcelable("proveedor", proveedores.get(selectedIndex));

                mapaProveedorDetalleFragment.show(getFragmentManager(), mapaProveedorDetalleFragment.getTag());
                mapaProveedorDetalleFragment.setArguments(bundle);

                return false;
            }
        });
    }

    protected void setIconos(Proveedor proveedor, MarkerOptions marker) {
        if (proveedor.getServicio_categoria().equals("Electricidad"))
            marker.icon(bitmapDescriptorFromVector(getContext(), R.drawable.ic_electricidad));
        if (proveedor.getServicio_categoria().equals("Carpintería"))
            marker.icon(bitmapDescriptorFromVector(getContext(), R.drawable.ic_carpinteria));
        if (proveedor.getServicio_categoria().equals("Cerrajería"))
            marker.icon(bitmapDescriptorFromVector(getContext(), R.drawable.ic_cerrajeria));
        if (proveedor.getServicio_categoria().equals("Mecánico"))
            marker.icon(bitmapDescriptorFromVector(getContext(), R.drawable.ic_mecanico));
        if (proveedor.getServicio_categoria().equals("Pintura"))
            marker.icon(bitmapDescriptorFromVector(getContext(), R.drawable.ic_pintura));
        if (proveedor.getServicio_categoria().equals("Albañilería"))
            marker.icon(bitmapDescriptorFromVector(getContext(), R.drawable.ic_albanil));
        if (proveedor.getServicio_categoria().equals("Limpieza"))
            marker.icon(bitmapDescriptorFromVector(getContext(), R.drawable.ic_limpieza));
    }

    protected BitmapDescriptor bitmapDescriptorFromVector(Context context, @DrawableRes int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void getProveedores() {
        mFirestore.collection("proveedores")
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    ArrayList<Proveedor> proveedores = new ArrayList<>();
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Proveedor proveedor = document.toObject(Proveedor.class);
                            if (proveedor.getServicio_ubicacion() != null) {
                                proveedores.add(proveedor);
                            }
                        }
                        updateMap(proveedores);
                    }
                }
            });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirestore = FirebaseFirestore.getInstance();
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
            .enableAutoManage(getActivity(), 1, this)
            .addConnectionCallbacks(this)
            .addApi(LocationServices.API)
            .build();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        if (mPermissionDenied) {
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mGoogleApiClient.stopAutoManage((FragmentActivity) getContext());
        mGoogleApiClient.disconnect();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.stopAutoManage((FragmentActivity) getContext());
        mGoogleApiClient.disconnect();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mGoogleApiClient = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mGoogleApiClient.stopAutoManage(getActivity());
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_LOCATION);
                Log.v("Wiwi", "Solicitando permisos");
            }
        } else {
            Log.v("Wiwi", "Servicio de Ubicacion conectado");
            startLocationServices();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        setUserMarker(new LatLng(location.getLatitude(), location.getLongitude()));
    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            PermissionUtils.requestPermission((AppCompatActivity) getActivity(), LOCATION_PERMISSION_REQUEST_CODE,
                Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }
        if (PermissionUtils.isPermissionGranted(permissions, grantResults, Manifest.permission.ACCESS_FINE_LOCATION)) {
            enableMyLocation();
        } else {
            mPermissionDenied = true;
        }
    }

    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog.newInstance(true).show(getFragmentManager(), "dialog");
    }
}
