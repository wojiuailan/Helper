package xin.shengnan.helper.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import xin.shengnan.helper.R;
import xin.shengnan.helper.adapter.GuideViewPagerAdapter;
import xin.shengnan.helper.aty.ActivityLogin;
import xin.shengnan.helper.aty.ActivityUser;
import xin.shengnan.helper.local.Config;


public class FragmentGuide extends Fragment {

    private Context context;
    private List<View> viewList;

    public FragmentGuide(){}

    @SuppressLint("ValidFragment")
    public FragmentGuide(Context c) {
        this();
        this.context = c;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_guide, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button btnSkip = view.findViewById(R.id.btn_guid_skip);
        ViewPager vpGuide = view.findViewById(R.id.vp_guide);

        if(viewList == null) {
            viewList = new ArrayList<View>();
        }

        ImageView first = new ImageView(context);
        first.setImageResource(R.drawable.loginpage);
        viewList.add(first);

        ImageView seconded = new ImageView(context);
        seconded.setImageResource(R.drawable.notepage);
        viewList.add(seconded);

        ImageView third = new ImageView(context);
        third.setImageResource(R.drawable.userpage);
        viewList.add(third);

        GuideViewPagerAdapter guideVPAdapter = new GuideViewPagerAdapter(viewList, context);

        vpGuide.setAdapter(guideVPAdapter);

        btnSkip.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(context, ActivityLogin.class));
                getActivity().finish();
            }
        });
    }
}
