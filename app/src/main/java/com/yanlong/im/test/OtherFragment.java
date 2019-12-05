package com.yanlong.im.test;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yanlong.im.R;
import com.yanlong.im.chat.ui.view.VoiceView;


public class OtherFragment extends Fragment {
    private View rootView;
private VoiceView voiceView;



    //自动寻找控件
    private void findViews(View rootView){
        voiceView = rootView.findViewById(R.id.voice);
    }

    public OtherFragment() {
        // Required empty public constructor
    }


    public static OtherFragment newInstance() {
        OtherFragment fragment = new OtherFragment();
        Bundle args = new Bundle();
    /*    args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);*/
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
/*            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);*/
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fgm_test_other, null);
        ViewGroup.LayoutParams layparm = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        rootView.setLayoutParams(layparm);
         findViews(rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

       initEvent();

    }

    private void initEvent() {
       // voiceView.init(false,70);

    }


    @Override
    public void onDetach() {
        super.onDetach();

    }
  /*
   private MainActivity getActivityMe() {
        return (MainActivity) getActivity();
    }

    */




}
