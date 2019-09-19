package com.example.mobileinternetforstudent;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.example.mobileinternetforstudent.exampleGoogleMap.GoogleMapActivity;
import com.example.mobileinternetforstudent.exampleJson.JsonActivity;
import com.example.mobileinternetforstudent.exampleWebView.WebviewActivity;
import com.example.mobileinternetforstudent.fragments.MainFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, MainFragment.OnFragmentInteractionListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.navigationView)
    NavigationView navigationView;
    // firebase 정보
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    // controller
    public NavController navController;

    @Override
    public void onFragmentInteraction(Uri uri) {
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setupNavigation();

        mAuth = FirebaseAuth.getInstance();     // firebase 인스턴스 가져오기

        Logger.addLogAdapter(new AndroidLogAdapter() {
            @Override
            public boolean isLoggable(int priority, @Nullable String tag) {
                return BuildConfig.DEBUG;
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(Navigation.findNavController(this, R.id.nav_host_fragment), drawerLayout);
    }

    // DrawerLayout 로직
    private void setupNavigation() {
        setSupportActionBar(toolbar);   // 기본 액션바를 custom toolbar 로 사용한다.
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);  // 툴바에 아이콘이 정상적으로 표시된다.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  // 다른 프레그먼트로 넘어갈 때 back 버튼이 툴바에 생김.

        // controller를 사용하여 main fragment 랑 연결
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        // navigation ui 를 이용하여 메인과 이어진 프레그먼트들의 툴바를 연결한다. 다른 프레그먼트는 툴바가 필요 없음.
        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout);
        NavigationUI.setupWithNavController(navigationView, navController);

        // drawerlayout 안에 아이템을 클릭 시 이벤트
        navigationView.setNavigationItemSelectedListener(this);

        // Drawerloyout header 리소스 캐스트
        View header = navigationView.getHeaderView(0);
        CircleImageView img_user_profile = header.findViewById(R.id.img_user_profile);  // 사용자 사진
        AppCompatTextView tv_email_draw = header.findViewById(R.id.tv_email);          // 사용자 이메일
        MaterialButton btn_logout_draw = header.findViewById(R.id.btn_logout);            // 로그아웃 버튼

        // 사용자 정보 가져오기
        currentUser = FirebaseAuth.getInstance().getCurrentUser();  // 현재 로그인 되어있는 사용자의 정보 가져오기

        // 사용자 프로필 사진 가져오기
        Glide.with(header.getContext())
                .load(currentUser.getPhotoUrl())
                .centerCrop()
                .placeholder(R.mipmap.ic_launcher_round)
                .into(img_user_profile);

        // 사용자 이메일 가져오기
        tv_email_draw.setText(currentUser.getEmail());

        // 로그아웃 클릭 이벤트
        btn_logout_draw.setOnClickListener(view -> {
            final AlertDialog.Builder alt_logout = new AlertDialog.Builder(MainActivity.this);  // alterDialog 생성
            alt_logout.setMessage("로그아웃 하시겠습니까?")   // 다이얼로그안에 들어갈 문구 설정
                    .setCancelable(false)   // cancel 안되게 막기
                    // 확인 이벤트
                    .setPositiveButton("네",
                            (dialogInterface, i) -> signOut())
                    // 취소 이벤트
                    .setNegativeButton("아니요",
                            (dialogInterface, i) -> dialogInterface.cancel());
            AlertDialog alertDialog = alt_logout.create();
            // 다이얼로그 띄울 때 배경 어둡게 하는거 막기
//            alertDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            alertDialog.setTitle("로그아웃");   // 다이얼로그 제목 설정
            // 대화창 아이콘 설정
//            alertDialog.setIcon(R.drawable.ic_launcher_foreground);
            alertDialog.show(); // 다이얼로그를 보여준다.
            drawerLayout.closeDrawer(GravityCompat.START);  // 다이얼로그를 보여줄 때 drawerlayout은 닫는다.
        });
    }
    // 로그아웃 로직
    private void signOut() {
        mAuth.signOut();    // 현재 로그인 상태을 로그아웃 시키기.
        Intent intent_login = new Intent(this, LoginActivity.class);
        startActivity(intent_login);
        finish();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        item.setChecked(true);  // 아이템 클릭에 대해 허용

        drawerLayout.closeDrawers();    // 클릭 후 drawer 닫기

        int id = item.getItemId();  // 아이템별 id

        switch (id) {
            case R.id.first:        // firebase 업로드 프레그먼트
                navController.navigate(R.id.firebaseFragment);
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
        Intent intent_json = new Intent(this, JsonActivity.class);
        startActivity(intent_json);
    }

    public void tvMap(View view) {
        Intent intent_map = new Intent(this, GoogleMapActivity.class);
        startActivity(intent_map);

    }

    public void btnWebview(View view) {
        Intent intent_webview = new Intent(this, WebviewActivity.class);
        startActivity(intent_webview);

    }
}
