package com.example.mobileinternetforstudent.ExampleWebView;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;

import com.example.mobileinternetforstudent.MainActivity;
import com.example.mobileinternetforstudent.R;
import com.google.android.material.button.MaterialButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WebviewActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.edt_address)
    EditText edtAddress;
    @BindView(R.id.btn_webview)
    MaterialButton btnWebview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        ButterKnife.bind(this);

        // custom toolbar 사용
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  // toolbar에 뒤로가기 버튼 생성
        getSupportActionBar().setTitle("WebView 예제");

        edtAddress.setImeOptions(EditorInfo.IME_ACTION_SEARCH); // EditText의 엔터키 대신 찾기 버튼으로 바꿈.
        // 엔터키에 대한 이벤트 처리
        edtAddress.setOnEditorActionListener((textView, i, keyEvent) -> {
            switch (i) {
                case EditorInfo.IME_ACTION_SEARCH:
                    onViewClicked();
                    break;
            }
            return false;
        });
    }

    /**
     * CCT 로직
     */
    private void openChromeCustomTabs(String url) {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();    // CCT 생성

        CustomTabsIntent anotherTab = new CustomTabsIntent.Builder().build();    // 탭 생성

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);    // 비트맵 생성

        Intent intent = anotherTab.intent;    // 브라우저 열릴 때 탭 인텐트
        intent.setData(Uri.parse("https://www.gooogle.com"));    // 기본 주소는 구글

        /* PendingIntent 란?
         *  수행시킬 작업 및 인텐트와 그것을 수행하는 주체를 지정하기 위한 정보를 명시 할 수 있는 기능의 클래스
         *  (쉽게 말해 A한테 B인텐트를 C시점에 실행해라. 단 먼저 실행하지 말고 실행하라고 할 때)
         *  CCT에서는 액션 버튼 또는 메뉴 항목을 클릭 했을 때 시작되는 인텐트다.
         */
        int requestCode = 100; // 팬딩인텐트에 대한 요청코드
        PendingIntent pendingIntent = PendingIntent.getActivity(this, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT); // 이미 생성된 팬딩인텐트가 있다면 해당 인텐트의 Extra Data만 변경한다.

        builder.setShowTitle(true);    // 타이틀바 보여주기
        builder.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary));    // CCT 툴바 색상 변경
        builder.addDefaultShareMenuItem();    // CCT 메뉴버튼 생성
        builder.setActionButton(bitmap, "Android", pendingIntent, true);    // 액션 버튼 셋팅
        CustomTabsIntent customTabsIntent = builder.build();    // CCT 에 대해 인텐트 생성
        customTabsIntent.launchUrl(this, Uri.parse(url));   // CCT에 url 보내고 웹 열기
    }

    @OnClick(R.id.btn_webview)
    public void onViewClicked() {
        String web_address = edtAddress.getText().toString().trim(); // EditText에서 주소 값 가져오기

        openChromeCustomTabs(web_address);
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

    // editText clearFocus [화면 클릭시 키보드 숨기기]
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    assert imm != null;
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }
}
