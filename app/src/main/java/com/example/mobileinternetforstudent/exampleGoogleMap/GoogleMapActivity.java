package com.example.mobileinternetforstudent.exampleGoogleMap;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.mobileinternetforstudent.MainActivity;
import com.example.mobileinternetforstudent.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import noman.googleplaces.NRPlaces;
import noman.googleplaces.Place;
import noman.googleplaces.PlaceType;
import noman.googleplaces.PlacesException;
import noman.googleplaces.PlacesListener;

public class GoogleMapActivity extends AppCompatActivity implements OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback, PlacesListener {

    Toolbar toolbar;
    MaterialButton btnBus;
    ConstraintLayout layoutMain;

    private static final String TAG = GoogleMapActivity.class.getSimpleName();

    private GoogleMap mMap;
    private Marker currentMarker = null;

    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int UPDATE_INTERVAL_MS = 1000;  // 1초
    private static final int FASTEST_UPDATE_INTERVAL_MS = 999999; // 자동 업데이트 안하기 위함.

    // onRequestPermissionsResult에서 수신된 결과에서 ActivityCompat.requestPermissions를 사용한 퍼미션 요청을 구별하기 위해 사용된다.
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    boolean needRequest = false;

    // 앱을 실행하기 위해 필요한 퍼미션을 정의한다.
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};  // 외부 저장소

    Location mCurrentLocatiion;
    LatLng currentPosition;

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private Location location;

    // Places 에 필요한 변수
    List<Marker> previous_marker = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_map);

        toolbar = findViewById(R.id.toolbar);
        btnBus = findViewById(R.id.btn_bus);
        layoutMain = findViewById(R.id.layout_main);

        // 액티비티가 계속 켜짐 혹은 꺼짐 방지
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // custom toolbar 사용
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  // toolbar에 뒤로가기 버튼 생성

        locationRequest = new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL_MS)
                .setFastestInterval(FASTEST_UPDATE_INTERVAL_MS);

        // places
        previous_marker = new ArrayList<Marker>();
        btnBus.setOnClickListener(view -> showPlaceInformation(currentPosition));

        LocationSettingsRequest.Builder builder =
                new LocationSettingsRequest.Builder();

        builder.addLocationRequest(locationRequest);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_map);
        mapFragment.getMapAsync(this);
    }

    private void showPlaceInformation(LatLng currentPosition) {
        mMap.clear(); // 지도 클리어
        if (previous_marker != null) previous_marker.clear(); // 지역정보 마커 클리어

        new NRPlaces.Builder()
                .listener(GoogleMapActivity.this)
                .key("AIzaSyAn0zXuElUlcqVF4-XPVUndwnDOqJ0KNCk")
                .latlng(currentPosition.latitude, currentPosition.longitude) // 현재 위치
                .radius(1000) // 1000 미터 내에서 검색
                .type(PlaceType.BUS_STATION)    // 버스정류장
                .build()
                .execute();
    }

    /*  toolbar 뒤로가기 버튼 누를 시 메인으로 이동 */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent_home = new Intent(this, MainActivity.class);
                startActivity(intent_home);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: ");

        mMap = googleMap;

        //런타임 퍼미션 요청 대화상자나 GPS 활성 요청 대화상자 보이기전에 지도의 초기위치를 서울로 이동
        setDefaultLocation();

        /**
         * 런타임 퍼미션 처리
         */
        // 1. 위치 퍼미션을 가지고 있는지 체
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {
            // 2. 이미 퍼미션을 가지고 있다면
            // 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식한다
            startLocationUpdates(); // 3. 위치 업데이트 시작
        } else { //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요한다.

            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])) {
                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명
                Snackbar.make(layoutMain, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.",
                        Snackbar.LENGTH_INDEFINITE).setAction("확인", view -> {
                    // 3-3. 사용자에게 퍼미션 요청을 함, 요청 결과는 onRequestPermissionResult에서 수신한다.
                    ActivityCompat.requestPermissions(GoogleMapActivity.this, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
                }).show();
            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 한다.
                // 요청 결과는 onRequestPermissionResult에서 수신
                ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }
        }
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        mMap.setOnMapClickListener(latLng -> Log.d(TAG, "onMapClick :"));

    }

    private void setDefaultLocation() {
        // 지도 좌표 초기값 -> 서울
        LatLng DEFAULT_LOCATION = new LatLng(37.56, 126.97);
        String markerTitle = "위치정보 가져올 수 없음";
        String markerSnippet = "위치 퍼미션과 GPS 활성 요부 확인하세요.";

        if (currentMarker != null) currentMarker.remove();

        MarkerOptions markerOptions = new MarkerOptions();    // 마커 생성
        markerOptions.position(DEFAULT_LOCATION);            // 마커가 표시될 위치
        markerOptions.title(markerTitle);                        // 마커에 표시될 타이틀
        markerOptions.snippet(markerSnippet);                // 마커 클릭시 보여주는 간단한 설명
        markerOptions.draggable(true);                        // 출발 마커가 드래그 가능하도록 설정
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));    // 마커에 대한 아이콘 설정
        currentMarker = mMap.addMarker(markerOptions);        // 지도에 표시

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 15);    // 지정한 단계로 카메라 줌 조정.
        // 1단계로 지정하면 세계지도 수준, 숫자가 커질수록 상세지도로 보여진다.
        mMap.moveCamera(cameraUpdate);                    // 카메라에 지정한 경도, 위도로 이동
    }

    private void startLocationUpdates() {
        if (!checkLocationServicesStatus()) {
            showDialogForLocationServiceSetting();
        } else {
            int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION);
            int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION);

            if (hasFineLocationPermission != PackageManager.PERMISSION_GRANTED ||
                    hasCoarseLocationPermission != PackageManager.PERMISSION_GRANTED) {

                Log.d(TAG, "startLocationUpdates : 퍼미션 안가지고 있음");
                return;
            }

            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

            if (checkMapPermission())
                mMap.setMyLocationEnabled(true);
        }
    }

    private boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    // GPS 활성화가 되어 있는지 AlterDialog 를 띄운다.
    private void showDialogForLocationServiceSetting() {
        AlertDialog.Builder builder = new AlertDialog.Builder(GoogleMapActivity.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n" + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", (dialog, id) -> {
            Intent callGPSSettingIntent
                    = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
        });
        builder.setNegativeButton("취소", (dialog, id) -> dialog.cancel());
        builder.create().show();
    }

    // 런타임 퍼미션 처리를 위한 메소드들
    private boolean checkMapPermission() {
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {
            return true;
        }

        return false;
    }

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);

            List<Location> locationList = locationResult.getLocations();

            if (locationList.size() > 0) {
                location = locationList.get(locationList.size() - 1);

                currentPosition = new LatLng(location.getLatitude(), location.getLongitude());

                String markerTitle = getCurrentAddress(currentPosition);
                String markerSnippet = "위도:" + location.getLatitude() + " 경도:" + location.getLongitude();

                //현재 위치에 마커 생성하고 이동
                setCurrentLocation(location, markerTitle, markerSnippet);

                mCurrentLocatiion = location;
            }
        }
    };

    private String getCurrentAddress(LatLng currentPosition) {
        //Geocoder =>. GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocation(currentPosition.latitude, currentPosition.longitude, 1);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";
        }

        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";
        } else {
            Address address = addresses.get(0);
            return address.getAddressLine(0);
        }
    }

    private void setCurrentLocation(Location location, String markerTitle, String markerSnippet) {
        if (currentMarker != null) currentMarker.remove();

        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(currentLatLng);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);

        currentMarker = mMap.addMarker(markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(currentLatLng);
        mMap.moveCamera(cameraUpdate);
    }

    @Override
    public void onPlacesFailure(PlacesException e) {

    }

    @Override
    public void onPlacesStart() {

    }

    @Override
    public void onPlacesSuccess(List<Place> places) {
        runOnUiThread(() -> {
            for (Place place : places) {
                LatLng latLng = new LatLng(place.getLatitude(), place.getLongitude());

                String markerSnippet = getCurrentAddress(latLng);

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title(place.getName());
                markerOptions.snippet(markerSnippet);
                Marker item = mMap.addMarker(markerOptions);
                previous_marker.add(item);
            }
            // 중복 마커 제거
            HashSet<Marker> hashSet = new HashSet<>();
            hashSet.addAll(previous_marker);
            previous_marker.clear();
            previous_marker.addAll(hashSet);
        });
    }

    @Override
    public void onPlacesFinished() {

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (checkMapPermission()) {
            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);

            if (mMap != null)
                mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case GPS_ENABLE_REQUEST_CODE:
                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {
                        needRequest = true;
                        return;
                    }
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_CODE && grantResults.length == REQUIRED_PERMISSIONS.length) {
            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면
            boolean check_result = true;

            // 모든 퍼미션을 허용했는지 체크
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }

            if (check_result) {
                // 퍼미션을 허용했다면 위치 업데이트를 시작
                startLocationUpdates();
            } else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료한다.
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {

                    // 사용자가 거부만 선택한 경우에는 앱을 다시 실행하여 허용을 선택하면 앱을 사용할 수 있다.
                    Snackbar.make(layoutMain, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요. ",
                            Snackbar.LENGTH_INDEFINITE).setAction("확인", view -> finish()).show();
                } else {
                    // "다시 묻지 않음"을 사용자가 체크하고 거부를 선택한 경우에는 설정(앱 정보)에서 퍼미션을 허용해야 앱을 사용할 수 있다.
                    Snackbar.make(layoutMain, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ",
                            Snackbar.LENGTH_INDEFINITE).setAction("확인", view -> finish()).show();
                }
            }
        }
    }
}
