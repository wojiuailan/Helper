package xin.shengnan.helper.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Administrator on 2018/1/31.
 */

public class GuideViewPagerAdapter extends PagerAdapter {

    private List<View> mViewList;
    private Context mContext;

    public GuideViewPagerAdapter(List<View> viewList, Context context) {
        this.mViewList = viewList;
        this.mContext = context;
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(mViewList.get(position));
        return mViewList.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(mViewList.get(position));
    }

    @Override
    public int getCount() {
        return mViewList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
