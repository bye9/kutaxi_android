package com.project.kutaxi;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ChatList extends AppCompatActivity {

    String host, hostgender, nickname;
    private ChatListAdapter adapter;
    private ListView listView;
    String select_date, room_number, room_host, room_guest1, room_guest2, room_start;

    String month, day, roomNum, roomDate, starttime, endtime, route, currentmember, maxmember, content, roomhost, roomgender, guest1, guest2;
    int genderNum;
    Drawable gender;

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.M.d HH:mm");
    Date now = new Date();
    Date today, target;
    Date today_date, room_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatlist_carpool);

        host = getIntent().getStringExtra("host"); //로그인 한 사람 학번
        hostgender = getIntent().getStringExtra("hostgender"); //성별

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_chatlist_carpool);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.BLACK);
        toolbar.inflateMenu(R.menu.menu_nav); //메뉴 추가
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView_Bar);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);

        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.ic_home:
                        Intent intent1 = new Intent(ChatList.this, MainActivity.class);
                        intent1.addFlags (Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        intent1.putExtra("host", host);
                        intent1.putExtra("hostgender", hostgender);
                        startActivity(intent1);
                        break;

                    case R.id.ic_mypage:
                        Intent intent3 = new Intent(ChatList.this, MyPageCarpool.class);
                        intent3.addFlags (Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        intent3.putExtra("host", host);
                        intent3.putExtra("hostgender", hostgender);
                        startActivity(intent3);
                        break;
                }
                return false;

            }

        });


        getNickname();  //닉네임 얻어오기
        sendRequest();  //내 대화방만 출력


        listView = (ListView) findViewById(R.id.lv_chatinglist);
        // 리스트뷰의 채팅방을 클릭했을 때 반응
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                ChatListItem item = (ChatListItem) parent.getItemAtPosition(position);
                room_number = item.getIdString();
                room_host = item.getHostString();
                room_guest1 = item.getGuest1String();
                room_guest2 = item.getGuest2String();
                room_start = item.getStartString();
                roomDate = item.getDateString();

                String today_date = simpleDateFormat.format(now);
                String room_date = roomDate + " " + room_start;

                try {
                    today = simpleDateFormat.parse(today_date);
                    target = simpleDateFormat.parse(room_date);
                } catch (ParseException e) {
                    //e.printStackTrace();
                }

                if (today.compareTo(target) < 0 ) {
                    Intent intent = new Intent(getApplicationContext(), ChatCarpool.class);
                    //intent.putExtra("select_date", select_date);
                    intent.putExtra("room_number", room_number);
                    intent.putExtra("host", host);
                    intent.putExtra("nickname", nickname);
                    intent.putExtra("hostgender", hostgender);
                    //intent.putExtra("guest1", room_guest1);
                    //intent.putExtra("guest2", room_guest2);
                    intent.putExtra("room_host", room_host);
                    startActivity(intent);
                } else {
                    Toast.makeText(ChatList.this, "더이상 참여할 수 없는 방입니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ChatListItem item = (ChatListItem) parent.getItemAtPosition(position);
                room_number = item.getIdString();
                room_host = item.getHostString();
                room_guest1 = item.getGuest1String();
                room_guest2 = item.getGuest2String();

                Intent delete = new Intent (ChatList.this, PopupDelete.class);
                delete.putExtra("room_number", room_number);
                delete.putExtra("room_host", room_host);
                delete.putExtra("guest1", room_guest1);
                delete.putExtra("guest2", room_guest2);
                delete.putExtra("host", host);
                delete.putExtra("hostgender", hostgender);
                startActivity(delete);

                return true;
            }
        });

        setListViewHeight(listView);

    }

    //뒤로가기 시 달력화면
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags (Intent.FLAG_ACTIVITY_NO_ANIMATION); // 화면전환 애니메이션 없애기
        intent.putExtra("host", host);
        intent.putExtra("hostgender", hostgender);
        startActivity(intent);
    }

    //내 대화방 출력
    public void sendRequest() {
        String target = "http://kutaxi.dothome.co.kr/get_mylist.php";

        RequestQueue requestQueue = Volley.newRequestQueue(ChatList.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, target, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("response");

                    adapter = new ChatListAdapter();
                    //리스트뷰 참조 및 adapter 달기
                    listView = (ListView) findViewById(R.id.lv_chatinglist);

                    if (jsonArray.length() == 0) {
                        TextView textView = (TextView)findViewById(R.id.empty);
                        textView.setText("참여한 대화방이 없습니다.");
                    } else {
                        int count = 0;
                        for(int i=0; i<jsonArray.length(); i++) {
                            jsonObject = jsonArray.getJSONObject(i);
                            roomNum = jsonObject.getString("roomNum");
                            roomDate = jsonObject.getString("roomDate");
                            starttime = jsonObject.getString("starttime");
                            endtime = jsonObject.getString("endtime");
                            route = jsonObject.getString("route");
                            currentmember = jsonObject.getString("currentmember");
                            maxmember = jsonObject.getString("maxmember");
                            content = jsonObject.getString("content");
                            genderNum = jsonObject.getInt("gender");
                            roomhost = jsonObject.getString("host");
                            roomgender = jsonObject.getString("hostgender");
                            guest1 = jsonObject.getString("guest1");
                            guest2 = jsonObject.getString("guest2");

                            //날짜 문자열에서 월, 일 추출
                            String[] date = roomDate.split("\\.");
                            month = date[1];
                            day = date[2];

                            //https://codechacha.com/ko/java-compare-date-and-time/
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.M.d"); //날짜 데이터 형식을 지정

                            today_date = simpleDateFormat.parse(new SimpleDateFormat("yyyy.M.d", Locale.getDefault()).format(new Date())); // 지정한 데이터 형식에 값을 넣음 (오늘 날짜)
                            room_date = simpleDateFormat.parse(roomDate); //지정한 데이터 형식에 값을 넣음 (방 날짜)
                            StringBuffer origin = new StringBuffer(starttime);
                            String start_time = String.valueOf(origin.insert(2, ":"));
                            StringBuffer origin2 = new StringBuffer(endtime);
                            String end_time = String.valueOf(origin2.insert(2, ":"));


                            if (today_date.compareTo(room_date) <= 0) { // 오늘날짜가 방날짜보다 이전이거나 같은 경우
                                //성별에 따른 대화방 목록호출
                                if (genderNum == 0) {
                                    gender = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_human);
                                    adapter.addItem(month, day, start_time, end_time, route, currentmember, maxmember, content, gender, roomNum, roomhost, guest1, guest2, roomDate);
                                    count += 1;
                                    gender = null;
                                } else if (genderNum == 1) { //동성 체크 시 로그인 한 사용자의 성별과 대화방 생성자의 성별 비교
                                    if (hostgender.equals(roomgender)) {
                                        if (hostgender.equals("남자"))
                                            gender = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_man);
                                        else if (hostgender.equals("여자"))
                                            gender = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_woman);
                                        adapter.addItem(month, day, start_time, end_time, route, currentmember, maxmember, content, gender, roomNum, roomhost, guest1, guest2, roomDate);
                                        count += 1;
                                        gender = null;
                                    }
                                }
                                listView.setAdapter(adapter);
                            }
                                /*
                                ArrayList<String> message = new ArrayList<String>();
                                message.add("참여한 대화방이 없습니다!");
                                ArrayAdapter<String> message_adapter = new ArrayAdapter<String>(ChatList.this, android.R.layout.simple_list_item_1, message);
                                listView.setAdapter(message_adapter);*/
                            } if (count == 0) { // 어뎁터에 저장된 값이 없는 경우 (0인 경우) (즉, 오늘 날짜 및 시간보다 이전인 날짜 및 시간인 경우)
                            TextView textView = (TextView)findViewById(R.id.empty);
                            textView.setText("참여한 대화방이 없습니다.");
                        }
                    }

                }   catch (JSONException | ParseException e) {
                    //e.printStackTrace();
                }
            }
            //에러가 발생했을 때 호출될 리스너 객체
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(ChatList.this, "에러 -> " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            //서버에 전송할 데이터가 있을 경우 포함
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("host", host);
                return params;
            }
        };
        stringRequest.setShouldCache(false);    //이전 결과 있어도 새로 요청하여 응답을 보여준다
        requestQueue.add(stringRequest);

    }

    //닉네임 얻기
    public void getNickname() {
        String target = "http://kutaxi.dothome.co.kr/mypage_carpool.php";

        RequestQueue requestQueue = Volley.newRequestQueue(ChatList.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, target, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("response");
                    jsonObject = jsonArray.getJSONObject(0);
                    nickname = jsonObject.getString("nickname");
                } catch (JSONException e) {
                    //e.printStackTrace();
                }
            }
            //에러가 발생했을 때 호출될 리스너 객체
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(ChatList.this, "에러 -> " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            //서버에 전송할 데이터가 있을 경우 포함
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("host", host);
                return params;
            }
        };
        stringRequest.setShouldCache(false);    //이전 결과 있어도 새로 요청하여 응답을 보여준다
        requestQueue.add(stringRequest);
    }

    //리스트뷰 크기 자동 조정
    public static void setListViewHeight(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        // https://tech.burt.pe.kr/android/view/measure-android-custom-view
        // https://m.blog.naver.com/PostView.nhn?blogId=zoomen1004&logNo=220227606331&proxyReferer=https:%2F%2Fwww.google.co.kr%2F
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);
        //MeasureSpec.AT_MOST - 최대 측정값을 설정하여 그 범위 내에서 값을 지정
        for (int i = 0; i < listAdapter.getCount(); i++ ) {
            View view = listAdapter.getView(i,null,listView);
            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            //MeasureSpec.UNSPECIFIED - mode가 셋팅되지 않은 크기가 넘어올 때 사용
            totalHeight += view.getMeasuredHeight();
        }
        // https://mrgamza.tistory.com/202
        ViewGroup.LayoutParams params = listView.getLayoutParams(); // 현재 레이아웃 요소의 속성객체를 얻어옴
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params); // 해당 속성객체의 값을 변경
        listView.requestLayout(); // requestLayout - 레이아웃을 갱신함.
    }
}
