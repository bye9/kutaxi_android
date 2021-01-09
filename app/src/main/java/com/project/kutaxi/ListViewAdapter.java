package com.project.kutaxi;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;


//https://recipes4dev.tistory.com/43?category=605791
//https://mixup.tistory.com/46?category=720995
public class ListViewAdapter extends BaseAdapter {
    //adapter에 추가된 데이터를 저장하기 위한 Arraylist
    private ArrayList<ListViewItem> listViewItemArrayList = new ArrayList<ListViewItem>();

    //생성자
    public ListViewAdapter() {

    }

    //adapter에 사용되는 데이터의 개수를 리턴
    @Override
    public int getCount() {
        return listViewItemArrayList.size();
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴
    @Override
    public long getItemId(int position) {
        return position;
    }

    // 지정한 위치(position)에 있는 데이터 리턴
    @Override
    public Object getItem(int position) {
        return listViewItemArrayList.get(position);
    }

    //position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //final int pos = position;
        final Context context = parent.getContext();

        //listview_item xml을 바탕으로 inflate(view객체생성)하여 convertView 참조 획득
        if (convertView == null) {
            //inflate얻어오기
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_item, parent, false);
        }

        //화면에 표시될 View객체로부터 위젯에 대한 참조 획득
        TextView startTextView = (TextView) convertView.findViewById(R.id.start_time);
        TextView endTextView = (TextView) convertView.findViewById(R.id.end_time);
        TextView routeTextView = (TextView) convertView.findViewById(R.id.route);
        TextView currentTextView = (TextView) convertView.findViewById(R.id.current_member);
        TextView maxTextView = (TextView) convertView.findViewById(R.id.max_member);
        TextView contentTextView = (TextView) convertView.findViewById(R.id.content);
        ImageView genderImageView = (ImageView) convertView.findViewById(R.id.gender);
        TextView idTextView = (TextView) convertView.findViewById(R.id.room_id);
        TextView hostTextView = (TextView) convertView.findViewById(R.id.room_host);
        TextView guest1TextView = (TextView) convertView.findViewById(R.id.guest1);
        TextView guest2TextView = (TextView) convertView.findViewById(R.id.guest2);

        // Data Set(listViewItemArrayList)에서 position에 위치한 데이터 참조 획득
        ListViewItem listViewItem = listViewItemArrayList.get(position);

        // 아이템 내 각 위젯에 데이터 반영
        startTextView.setText(listViewItem.getStartString());
        endTextView.setText(listViewItem.getEndString());
        routeTextView.setText(listViewItem.getRouteString());
        currentTextView.setText(listViewItem.getCurrentString());
        maxTextView.setText(listViewItem.getMaxString());
        contentTextView.setText(listViewItem.getContentString());
        genderImageView.setImageDrawable(listViewItem.getGenderDrawable());
        idTextView.setText(listViewItem.getIdString());
        hostTextView.setText(listViewItem.getHostString());
        guest1TextView.setText(listViewItem.getGuest1String());
        guest2TextView.setText(listViewItem.getGuest2String());

        String c_member = listViewItem.getCurrentString();
        String m_member = listViewItem.getMaxString();

        if (c_member.equals(m_member)) {
            currentTextView.setTextColor(Color.rgb(255,61,0));
            maxTextView.setTextColor(Color.rgb(255,61,0));
        } else {
            currentTextView.setTextColor(Color.rgb(0,184,212));
            maxTextView.setTextColor(Color.rgb(0,184,212));
        }



        return convertView;
    }

    // 아이템 데이터 추가를 위한 함수
    public void addItem(String starttime, String endtime, String route, String currentmember, String maxmember,
                        String content, Drawable gender, String room_id, String room_host, String guest1, String guest2) {
        ListViewItem item = new ListViewItem();

        item.setStartString(starttime);
        item.setEndString(endtime);
        item.setRouteString(route);
        item.setCurrentString(currentmember);
        item.setMaxString(maxmember);
        item.setContentString(content);
        item.setGenderDrawable(gender);
        item.setIdString(room_id);
        item.setHostString(room_host);
        item.setGuest1String(guest1);
        item.setGuest2String(guest2);

        listViewItemArrayList.add(item);
    }

    public void clearItem() {
        listViewItemArrayList.clear();
    }

}
