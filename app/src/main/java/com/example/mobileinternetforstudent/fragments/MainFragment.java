package com.example.mobileinternetforstudent.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.mobileinternetforstudent.R;
import com.google.android.material.button.MaterialButton;

import butterknife.BindView;

public class MainFragment extends Fragment {

    @BindView(R.id.btn_qr)
    MaterialButton btnQr;

    private OnFragmentInteractionListener mListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    // 버튼을 클릭하여 uri 있는 곳으로 이동
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            // 리스너를 액티비티에 연결 안했을 때 등록하라고 에러 남.
            throw new RuntimeException(context.toString() + "리스너 등록해야함");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // btnQr이 무엇인지
        btnQr = view.findViewById(R.id.btn_qr);

        Bundle bundle = new Bundle();
        bundle.putString("qrcode", "from main"); // key,value
        /** 네비게이션 연결
         *   action_mainFragment_to_QRFragment 은 nav_graph.xml에서 메인 프래그먼트에서
         *   qr 프래그먼트로 drag 해서 선을 연결했을 때 생기는 action 이름이다.
         * */
        View.OnClickListener onClickListener = Navigation.createNavigateOnClickListener(R.id.action_mainFragment_to_QRFragment, bundle);
        // 클릭리스너 생성
        btnQr.setOnClickListener(onClickListener);
    }
}
