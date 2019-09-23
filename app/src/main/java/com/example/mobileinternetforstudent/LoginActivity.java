package com.example.mobileinternetforstudent;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;


public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private TextInputEditText edtId, edtPw;
    private MaterialButton btnSignIn, btnLogin;
    private SignInButton btnGoogleSignIn;

    // firebase
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    // Google sign in
    private static final int RC_SIGN_IN = 100;
    private GoogleSignInClient mGoogleSignInClient;
    // progressDialog
    private ProgressDialog progressDialog;

    // 자동 로그인
    @Override
    protected void onStart() {
        super.onStart();
        mUser = mAuth.getCurrentUser();
        if (!(mUser == null)) { // 유저 정보가 있다면 바로 MainActivity로 이동
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtId = findViewById(R.id.edt_id);
        edtPw = findViewById(R.id.edt_pw);
        btnSignIn = findViewById(R.id.btn_sign_in);
        btnLogin = findViewById(R.id.btn_login);
        btnGoogleSignIn = findViewById(R.id.btn_google_sign_in);

        btnGoogleSignIn.setSize(SignInButton.SIZE_WIDE);    // 구글 로그인 버튼 테마

        // configure google sign in
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        btnSignIn.setOnClickListener(btnClickListener);
        btnLogin.setOnClickListener(btnClickListener);
        btnGoogleSignIn.setOnClickListener(btnClickListener);
    }

    // 회원가입 logic
    private void registerAccount(String email, String password) {
        if (TextUtils.isEmpty(email)) {     // 이메일 입력 후 확인 이벤트 처리
            Toast.makeText(this, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "이메일 형식이 아닙니다.", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {   // 패스워드 입력 후 확인 이벤트 처리
            Toast.makeText(this, "패스워드를 입력해주세요..", Toast.LENGTH_SHORT).show();
        } else if (password.length() < 6) {
            Toast.makeText(this, "패스워드를 6자리 이상으로 해주세요.", Toast.LENGTH_SHORT).show();
        } else if (password.length() > 15) {
            Toast.makeText(this, "패스워드를 15자리 이하로 해주세요.", Toast.LENGTH_SHORT).show();
        } else {
            progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setMessage("회원가입 중입니다...");
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(false);

            // 이메일/패스워드 회원가입
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();   // 완료 되었을때 progressDialog 제거
                            Toast.makeText(this, "회원가입이 완료 되었습니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(this, "회원가입이 실패했습니다.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    // 로그인 logic
    private void loginAccount(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "이메일 형식이 아닙니다.", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "패스워드를 입력해주세요..", Toast.LENGTH_SHORT).show();
        } else if (password.length() < 6) {
            Toast.makeText(this, "패스워드를 6자리 이상으로 해주세요.", Toast.LENGTH_SHORT).show();
        } else if (password.length() > 15) {
            Toast.makeText(this, "패스워드를 15자리 이하로 해주세요.", Toast.LENGTH_SHORT).show();
        } else {
            progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setMessage("로그인 중입니다...");
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(false);

            // 이메일/패스워드 로그인
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent i = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(i);
                            finish();
                        } else {
                            Log.w(TAG, "로그인 실패 : ", task.getException());
                            progressDialog.dismiss();
                            Toast.makeText(this, "로그인이 실패했습니다.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    // 구글 로그인
    private void googleSignIn() {
        Intent intent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(intent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                assert account != null;
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Log.w(TAG, "구글 로그인 실패 : ", e);
            }
        }
    }

    // 구글 로그인 logic
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle : " + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage("로그인 중입니다...");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);
        // 구글 로그
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "구글 로그인 성공", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(this, MainActivity.class);
                        startActivity(i);
                        finish();
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "구글 로그인 실패 : " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
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

    private Button.OnClickListener btnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            // 이메일, 비밀번호 텍스트 값
            String email = edtId.getText().toString();             // 이메일 값 가져오기
            String password = edtPw.getText().toString();   // 패스워드 값 가져오기

            switch (view.getId()) {
                case R.id.btn_sign_in:  // 회원가입 버튼
                    registerAccount(email, password);
                    break;
                case R.id.btn_login:    // 로그인 버튼
                    loginAccount(email, password);
                    break;
                case R.id.btn_google_sign_in:   // 구글 로그인 버튼
                    googleSignIn();
                    break;
            }
        }
    };

}
