package com.example.mobileinternetforstudent;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.navigationView)
    NavigationView navigationView;
    // controller
    public NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setupNavigation();

    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(Navigation.findNavController(this, R.id.nav_host_fragment), drawerLayout);
    }

    private void setupNavigation() {
        setSupportActionBar(toolbar);   // 기본 액션바를 custom toolbar 로 사용한다.
        getSupportActionBar().setDisplayShowHomeEnabled(true);  // 툴바에 아이콘이 정상적으로 표시된다.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  // 다른 프레그먼트로 넘어갈 때 back 버튼이 툴바에 생김.
        // controller를 사용하여 main fragment 랑 연결
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        // navigation ui 를 이용하여 메인과 이어진 프레그먼트들의 툴바를 연결한다. 다른 프레그먼트는 툴바가 필요 없음.
        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout);
        NavigationUI.setupWithNavController(navigationView, navController);
        // drawerlayout 안에 아이템을 클릭 시 이벤트
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        item.setChecked(true);  // 아이템 클릭에 대해 허용

        drawerLayout.closeDrawers();    // 클릭 후 drawer 닫기

        int id = item.getItemId();  // 아이템별 id

        switch (id) {
            case R.id.first:        // firebase 업로드 프레그먼트
                navController.navigate(R.id.firebaseUploadFragment);
                break;

            case R.id.second:   // 업데이트 예정
                Toast.makeText(this, "두번째", Toast.LENGTH_SHORT).show();
                break;

            case R.id.third:        // 업데이트 예정
                Toast.makeText(this, "세번째", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    /**
     * back버튼 이벤트
     */
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {        // 만약 drawer가 열려있다면
            drawerLayout.closeDrawer(GravityCompat.START);        // back 버튼 눌러 닫는다.
        } else {
            super.onBackPressed();        // 아니면 앱을 종료한다.
        }
    }

    public void tvJson(View view) {

    }

    public void tvMap(View view) {

    }

    public void btnWebview(View view) {

    }

    public void btnQR(View view) {

    }
}
