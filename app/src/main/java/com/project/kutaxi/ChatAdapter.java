package com.project.kutaxi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;


public class ChatAdapter extends BaseAdapter {

    private ArrayList<ChatItem> mItems = new ArrayList<>();
    String host;
    LayoutInflater layoutInflater;

    public ChatAdapter(String host) {
        this.host = host;

    }


    @Override
    public int getCount() {
        return mItems.size();
    } // 어뎁터가 관리할 데이터의 갯수를 설정

    @Override
    public ChatItem getItem(int position) {
        return mItems.get(position);
    } // 어뎁터가 관리하는 데이터 아이템의 position을 객체 형태로 얻어 옴

    @Override
    public long getItemId(int position) {
        return position; } //어뎁터가 관리하는 데이터 아이템의 position 값의 id를 얻어 옴.

    @Override
    public View getView(int position, View convertView, ViewGroup parent) { // 어뎁터가 가지고 있는 데이터를 어떻게 보여줄 것인지 정의 하는데 쓰임.

        //현재 보여줄 번째의(position)의 데이터로 뷰를 생성
        final Context context = parent.getContext();

        ChatItem myItem = mItems.get(position);

        convertView = null;

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //inflate얻어오기
        if (this.host.equals(myItem.getHost()))
            convertView = layoutInflater.inflate(R.layout.my_msgbox, parent,false);
        else
            convertView = layoutInflater.inflate(R.layout.other_msgbox, parent,false);

        // 클릭 비활성화
        // https://jamssoft.tistory.com/161 (setOnTouchListener에 대한 설명)
        convertView.setOnTouchListener(new View.OnTouchListener() { // 터치를 하였을 때 실행되는 리스너

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
                /* 리턴값에 따라 달라지는데 리턴값이 true인 경우에는 onTouchEvent 함수는 호출되지 않고 추가 작업은 실행되지 않기 때문에 터치를 막을 수 있음. */

                /* 리턴값이 false인 경우에는 onTouchEvent함수가 호출되는데 기본적으로 setOnTouchListener를 선언하지 않았을 경우
                리턴값은 false가 되어 onTouchEvent함수를 호출하게 되고 터치 이벤트가 발생하게 됨. */

            }});

        //화면에 표시될 View객체로부터 위젯에 대한 참조 획득
        TextView nickname = (TextView) convertView.findViewById(R.id.nickname);
        TextView message = (TextView) convertView.findViewById(R.id.message);
        TextView time = (TextView) convertView.findViewById(R.id.time);

        nickname.setText(myItem.getNickname());
        message.setText(myItem.getMessage());
        time.setText(myItem.getTime());

        return convertView;
    }

    public void addItem(String nickname, String message, String time, String host) {
        ChatItem item = new ChatItem();

        item.setNickName(nickname);
        item.setMessage(message);
        item.setTime(time);
        item.setHost(host);

        mItems.add(item);
    }




}