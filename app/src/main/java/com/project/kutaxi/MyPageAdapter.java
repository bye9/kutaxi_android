package com.project.kutaxi;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class MyPageAdapter extends BaseAdapter {

    private ArrayList<MyPageItem> mItems = new ArrayList<MyPageItem>();

    public MyPageAdapter() {

    }


    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position) ;
    }

    @Override
    public long getItemId(int position) {
        return position ;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Context context = parent.getContext();

        /* 'listview_item' Layout을 inflate하여 convertView 참조 획득 */
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.mypage_listitem, parent, false);
        }

        TextView settingItem = (TextView) convertView.findViewById(R.id.settingItem);
        ImageView settingImage = (ImageView) convertView.findViewById(R.id.settingImage);

        MyPageItem myItem = mItems.get(position);

        settingItem.setText(myItem.getSettingItem());
        settingImage.setImageDrawable(myItem.getSettingImage());


        return convertView;
    }


    public void addItem(Drawable settingImage, String settingItem) {

        MyPageItem mItem = new MyPageItem();

        /* MyItem에 아이템을 setting한다. */
        mItem.setSettingItem(settingItem);
        mItem.setSettingImage(settingImage);

        /* mItems에 MyItem을 추가한다. */
        mItems.add(mItem);

    }


}