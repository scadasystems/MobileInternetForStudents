package com.example.mobileinternetforstudent.exampleQR;

import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.mobileinternetforstudent.R;
import com.google.android.gms.vision.barcode.Barcode;

import java.util.List;

import info.androidhive.barcode.BarcodeReader;

public class QRscannerFragment extends Fragment implements BarcodeReader.BarcodeReaderListener {
    private BarcodeReader barcodeReader;

    public static QRscannerFragment newInstance() {
        return new QRscannerFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qrscanner, container, false);
        // 바코드 리더기를 qrcode_fragment 에 연결
        barcodeReader = (BarcodeReader) getChildFragmentManager().findFragmentById(R.id.qrcode_fragment);
        barcodeReader.setListener(this);    // 생성
        return view;
    }

    /* 단일 스캔 */
    @Override
    public void onScanned(Barcode barcode) {
        barcodeReader.playBeep();   // 스캔 성공 했을 때 삡 소리 내기
        getActivity().runOnUiThread(() ->    // 데이터값이 있으면
                Toast.makeText(getActivity(), barcode.displayValue, Toast.LENGTH_SHORT).show()
        );
    }
    /* 다중 스캔 */
    @Override
    public void onScannedMultiple(List<Barcode> barcodes) {
        String codes = "";
        for (Barcode barcode : barcodes) {
            codes += barcode.displayValue + ", ";
        }
        final String finalCodes = codes;
        getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "QRcodes: " + finalCodes, Toast.LENGTH_SHORT).show()
        );
    }
    /* 비트맵 스캔 */
    @Override
    public void onBitmapScanned(SparseArray<Barcode> sparseArray) {

    }

    /* 스캔 에러 */
    @Override
    public void onScanError(String errorMessage) {
        Toast.makeText(getActivity(), "onScanError: " + errorMessage, Toast.LENGTH_SHORT).show();
    }

    /* 퍼미션 없을 때 */
    @Override
    public void onCameraPermissionDenied() {
        Toast.makeText(getActivity(), "카메라 권한을 허용해주세요.", Toast.LENGTH_SHORT).show();
    }
}