package com.example.mobileinternetforstudent.ExampleGoogleMap;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.mobileinternetforstudent.MainActivity;
import com.example.mobileinternetforstudent.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GoogleMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_map);
        ButterKnife.bind(this);

        // custom toolbar 사용
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  // toolbar에 뒤로가기 버튼 생성

        // fragment 을 연결
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_map);      // 레이아웃에 추가했던 프래그먼트의 핸들을 가져옴.
        mapFragment.getMapAsync(this);  // 메인스레드에서 onMapReady 콜백이 실행

    }

    // 맵이 사용할 준비가 되었을 때 호출되는 메소드
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng daelim = new LatLng(37.403822, 126.930361);  // 대림대학교에 대한 좌표값

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(daelim);                             // 마커가 표시될 위치
        markerOptions.title("대림대학교");                           // 마커에 표시될 타이틀
        markerOptions.snippet("모바일 인터넷학과");         // 마커 클릭시 보여주는 간단한 설명
        mMap.addMarker(markerOptions);                        // 지도에 표시

        mMap.moveCamera(CameraUpdateFactory.newLatLng(daelim));     // 카메라에 지정한 경도, 위도로 이동
        mMap.animateCamera(CameraUpdateFactory.zoomTo(17));       // 지정한 단계로 카메라 줌 조정.
        // 1단계로 지정하면 세계지도 수준, 숫자가 커질수록 상세지도로 보여진다.
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
}
