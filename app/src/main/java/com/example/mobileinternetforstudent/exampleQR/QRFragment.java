package com.example.mobileinternetforstudent.exampleQR;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.mobileinternetforstudent.BuildConfig;
import com.example.mobileinternetforstudent.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileOutputStream;


public class QRFragment extends Fragment implements View.OnClickListener {
    private static final int REQUEST_CODE_PERMISSIONS = 100;
    private FrameLayout flQrscanner;
    private TextView qrTitle, qrSubtitle;
    private ImageView qrImage;
    private ImageButton btnScanner, btnProfile, btnSave, btnShare;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 권한 체크
        // 23 버전 이전이면 이러한 절차가 필요없다.
        if (Build.VERSION.SDK_INT >= 23) {
            int permissionCheck = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
    }

    // 권한 체크 로직
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_PERMISSIONS:
                //  저장 권한 허용
                if (getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSIONS);
                    Toast.makeText(getActivity(), "권한이 필요합니다", Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qr, container, false);

        btnScanner = view.findViewById(R.id.btnScanner);
        btnScanner.setOnClickListener(this);
        btnProfile = view.findViewById(R.id.btnProfile);
        btnProfile.setOnClickListener(this);
        btnSave = view.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(this);
        btnShare = view.findViewById(R.id.btnShare);
        btnShare.setOnClickListener(this);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        qrImage = view.findViewById(R.id.qrImage);
        flQrscanner = view.findViewById(R.id.fl_qrscanner);
        qrTitle = view.findViewById(R.id.qr_title);
        qrSubtitle = view.findViewById(R.id.qr_subtitle);

        // 한글지원 x
        String text = "mobile Internet QR-code practice";
        // 멀티포멧 생성
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            // qr코드로 생성하고 가로x세로 는 600x600
            BitMatrix bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE, 600, 600);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            // qrImage 에 만든 QR코드를 셋팅한다
            qrImage.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 클릭 이벤트
    @Override
    public void onClick(View view) {
        // fragment_qr.xml 에 있는 fl_qrscanner 의 프레그먼트를 fragment_qrscanner.xml로 연동하기 위함.
        Fragment fragment;
        switch (view.getId()) {
             // QR 코드 스캔
            case R.id.btnScanner:
                qrTitle.setVisibility(View.INVISIBLE);
                qrImage.setVisibility(View.INVISIBLE);
                flQrscanner.setVisibility(View.VISIBLE);
                qrSubtitle.setText("QR코드를 스캔하여 친구 추가를 해보세요.");
                fragment = QRscannerFragment.newInstance();     // 프레그먼트 연결
                setQRScannerFragment(fragment);     // 프레그먼트 보여주기
                break;
             // 내프로필
            case R.id.btnProfile:
                qrTitle.setVisibility(View.VISIBLE);
                qrImage.setVisibility(View.VISIBLE);
                flQrscanner.setVisibility(View.GONE);
                qrSubtitle.setText("위 코드로 상대방이 나를 친구로 추가할 수 있습니다.");
                break;
             // 저장하기
            case R.id.btnSave:
                saveImageLocally(qrImage);  // 저장 로직
                break;
             // 공유하기
            case R.id.btnShare:
                shareImage();   // 공유 로직
                break;
        }
    }

    // QRscanner 프레그먼트 바꾸기
    private void setQRScannerFragment(Fragment child) {
        FragmentTransaction childFt = getChildFragmentManager().beginTransaction();

        if (!child.isAdded()) {     // activity에 fragment가 붙어 있지 않다면
            childFt.replace(R.id.fl_qrscanner, child);  // fl_qrscanner 에 대치한다.
            childFt.addToBackStack(null);   // 화면을 stack에 저장 안함.
            childFt.commit();   // 추가
        }
    }

    // QR 코드 저장 로직
    private String saveImageLocally(ImageView qrImage) {
        qrImage.buildDrawingCache();

        Bitmap bitmap = qrImage.getDrawingCache();

        File storageLocal = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);

        String qr_name = "myQR.png";

        File file = new File(storageLocal, qr_name);
        // DCIM 에다가 저장
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
        Toast.makeText(getActivity(), "저장을 성공했습니다: " + path + "/" + qr_name, Toast.LENGTH_SHORT).show();

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            // 파일 확장자는 png로 하고 퀄리티는 100 으로 한다.
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            // 저장하면 닫기.
            fileOutputStream.close();
            // 저장된 파일 스캔
            scanFile(getActivity(), Uri.fromFile(file));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 파일 스캔
    private void scanFile(Context context, Uri imageUri) {
        Intent intent_scan = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent_scan.setData(imageUri);
        context.sendBroadcast(intent_scan);
    }

    // qr코드 공유하기
    private void shareImage() {
        Intent intent = new Intent(Intent.ACTION_SEND);  // 공유 생성
        intent.setType("image/*");  // 공유할 타입은 image
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();  // 파일 경로
        File file = new File(filePath, "myQR.png"); // 파일 경로와 이름 지정
        if (file.exists()) { //  만약 QR 코드가 저장소에 있을 경우 바로 공유
            intent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID, file));
            // 공유할 때 타이틀 텍스트
            Intent chooser = Intent.createChooser(intent, "공유하기-모바일프로젝트");
            startActivity(chooser);
        } else { // 없다면 저장을 하라고 메세지 띄우기
            Toast.makeText(getActivity(), "QR코드를 다운로드 해주세요.", Toast.LENGTH_SHORT).show();
        }
    }
}
