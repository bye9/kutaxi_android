package com.project.kutaxi;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class JoinCarpool extends AppCompatActivity {

    String select_date, host, hostgender; //기본 값 관련
    String select_route, nickname;
    String room_number, room_host, room_current, room_max, room_guest1, room_guest2, room_start;
    String month, day;

    String roomNum, roomDate, starttime, endtime, route, currentmember, maxmember, content, roomhost, roomgender, guest1, guest2;
    int genderNum;
    Drawable gender;

    private ListViewAdapter adapter;
    private ListView list;

    Date today_date, room_date;
    int countRoute = 0;
    /*SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.M.d HH:mm");
    Date now = new Date();
    Date today, target;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join_carpool);

        select_date = getIntent().getStringExtra("select_date"); //오늘날짜 받아오기
        host = getIntent().getStringExtra("host"); //로그인 한 사람 학번
        hostgender = getIntent().getStringExtra("hostgender"); //성별

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_join_carpool);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.BLACK);
        toolbar.inflateMenu(R.menu.menu_nav); //메뉴 추가
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //adapter로 구현한 스피너 값 설정
        Spinner route_spinner = (Spinner)findViewById(R.id.route);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.route, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        route_spinner.setAdapter(adapter);

        //날짜 문자열에서 월, 일 추출
        TextView date_textView = (TextView) findViewById(R.id.date);
        String[] date = select_date.split("\\.");
        month = date[1];
        day = date[2];
        String s_date = month + " " + "/" + " " + day;
        date_textView.setText(s_date);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView_Bar);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);

        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.ic_home:
                        Intent intent1 = new Intent(JoinCarpool.this, MainActivity.class);
                        intent1.addFlags (Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        intent1.putExtra("host", host);
                        intent1.putExtra("hostgender", hostgender);
                        startActivity(intent1);
                        break;

                    case R.id.ic_chat:
                        Intent intent2 = new Intent(JoinCarpool.this, ChatList.class);
                        intent2.addFlags (Intent.FLAG_ACTIVITY_NO_ANIMATION); // 화면전환 애니메이션 없애기
                        intent2.putExtra("host", host);
                        intent2.putExtra("hostgender", hostgender);
                        startActivity(intent2);
                        break;

                    case R.id.ic_mypage:
                        Intent intent3 = new Intent(JoinCarpool.this, MyPageCarpool.class);
                        intent3.addFlags (Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        intent3.putExtra("host", host);
                        intent3.putExtra("hostgender", hostgender);
                        startActivity(intent3);
                        break;
                }
                return false;

            }

        });

        getNickname(); //닉네임 얻어오기

        sendRequest(); //전체 대화방 목록 출력

        route_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position!=0) {
                    select_route = (String) parent.getItemAtPosition(position);
                    routefilter(); //방향만 선택시 필터링
                } else if (position==0)
                    select_route = "";
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        CheckBox gender_checkbox = (CheckBox)findViewById(R.id.gender);
        gender_checkbox.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((CheckBox)view).isChecked()) {
                    genderfilterChecked(); //동성여부 체크시 필터링
                    countRoute += 1;
                } else {
                    if (select_route.length()>0 && countRoute != 0)
                        routefilter(); //방향만 선택시 필터링
                    else if (select_route.length()==0 && countRoute != 0)
                        sendRequest(); //최초상태
                }
            }
        });

        list = (ListView) findViewById(R.id.list);
        // 리스트뷰의 채팅방을 클릭했을 때 반응
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                ListViewItem item = (ListViewItem) parent.getItemAtPosition(position);
                room_number = item.getIdString();
                room_host = item.getHostString();
                room_current = item.getCurrentString();
                room_max = item.getMaxString();
                room_guest1 = item.getGuest1String();
                room_guest2 = item.getGuest2String();
                room_start = item.getStartString();

                /*
                String today_date = simpleDateFormat.format(now);
                String room_date = roomDate + " " + room_start;
                try {
                    today = simpleDateFormat.parse(today_date);
                    target = simpleDateFormat.parse(room_date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (today.compareTo(target) < 0 ) */

                //내가 참여한 대화방은 바로 입장
                if (host.equals(room_host) || (host.equals(room_guest1) || host.equals(room_guest2))) {
                    Intent intent = new Intent(getApplicationContext(), ChatCarpool.class);
                    intent.putExtra("room_number", room_number);
                    intent.putExtra("host", host);
                    intent.putExtra("nickname", nickname);
                    intent.putExtra("hostgender", hostgender);
                    intent.putExtra("room_host", room_host);
                    startActivity(intent);
                } else {
                    if (room_current.equals(room_max))  //풀방은 못들어가게
                        Toast.makeText(JoinCarpool.this, "인원수가 초과된 방입니다.", Toast.LENGTH_SHORT).show();
                    else {
                        if (room_current.equals("1"))
                            addMember();
                        else if (room_current.equals("2"))
                            addMember2();
                        Intent intent = new Intent(getApplicationContext(), ChatCarpool.class);
                        intent.putExtra("room_number", room_number);
                        intent.putExtra("host", host);
                        intent.putExtra("nickname", nickname);
                        intent.putExtra("hostgender", hostgender);
                        intent.putExtra("room_host", room_host);
                        startActivity(intent);
                    }
                }

            }
        });
        setListViewHeight(list);
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

    public void onPlusButtonClicked(View view) { //플러스 버튼 클릭
        Intent intent = new Intent(this, CreateCarpool.class);
        intent.putExtra("select_date", select_date);
        intent.putExtra("host", host);
        intent.putExtra("hostgender", hostgender);
        startActivity(intent);
    }

    public void onRefreshClicked(View view) {
        Spinner route_spinner = (Spinner)findViewById(R.id.route);
        route_spinner.setSelection(0);
        CheckBox gender_checkbox = (CheckBox)findViewById(R.id.gender);
        gender_checkbox.setChecked(false);
        sendRequest();
    }

    //최초 전체 목록 출력
    public void sendRequest() {
        //1) RequestQueue를 생성한다.
        //2) Request Object를 생성한다.
        //3) 생성한 Object를 RequestQueue로 넘긴다.
        String target = "http://kutaxi.dothome.co.kr/join_carpool.php";

        TextView textView = (TextView)findViewById(R.id.empty);
        textView.setText("");
        //https://kkangsnote.tistory.com/45 (volley api)
        RequestQueue requestQueue = Volley.newRequestQueue(JoinCarpool.this);
        //요청방식지정, 웹서버의 url정보전달, 응답을 성공적으로 받았을 때 이 리스너의 onResponse메소드를 자동으로 호출해주는 리스너 객체
        StringRequest stringRequest = new StringRequest(Request.Method.POST, target, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    //https://www.it-swarm-ko.tech/ko/android/android-%ed%8c%8c%ec%8b%b1-jsonobject/1043469052/
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("response");

                    adapter = new ListViewAdapter();
                    //리스트뷰 참조 및 adapter 달기
                    list = (ListView) findViewById(R.id.list);

                    if (jsonArray.length() == 0) {
                        TextView textView = (TextView)findViewById(R.id.empty);
                        textView.setText("해당하는 날짜에 대화방이 없습니다!");
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

                            StringBuffer origin = new StringBuffer(starttime);
                            String start_time = String.valueOf(origin.insert(2, ":"));
                            StringBuffer origin2 = new StringBuffer(endtime);
                            String end_time = String.valueOf(origin2.insert(2, ":"));

                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.M.d HH:mm"); //날짜 데이터 형식을 지정
                            room_date = simpleDateFormat.parse(select_date + " " + start_time); //지정한 데이터 형식에 값을 넣음 (선택된 날짜 및 시간)
                            today_date = simpleDateFormat.parse(new SimpleDateFormat("yyyy.M.d HH:mm", Locale.getDefault()).format(new Date())); //지정한 데이터 형식에 값을 넣음 (오늘 날짜 및 시간)


                            if (today_date.compareTo(room_date) <= 0) { //오늘 날짜 및 시간이 선택된 날짜 및 시간보다 이전이거나 같은 경우

                                //성별에 따른 대화방 목록호출
                                if (genderNum == 0) {
                                    gender = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_human);
                                    adapter.addItem(start_time, end_time, route, currentmember, maxmember, content, gender, roomNum, roomhost, guest1, guest2);
                                    count += 1;
                                    gender = null;
                                } else if (genderNum == 1) { //동성 체크 시 로그인 한 사용자의 성별과 대화방 생성자의 성별 비교
                                    if (hostgender.equals(roomgender)) {
                                        if (hostgender.equals("남자"))
                                            gender = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_man);
                                        else if (hostgender.equals("여자"))
                                            gender = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_woman);
                                        adapter.addItem(start_time, end_time, route, currentmember, maxmember, content, gender, roomNum, roomhost, guest1, guest2);
                                        count += 1;
                                        gender = null;
                                    }
                                }
                                list.setAdapter(adapter);
                            }
                        } if (count == 0) { // 어뎁터에 저장된 값이 없는 경우 (0인 경우) (즉, 오늘 날짜 및 시간보다 이전인 날짜 및 시간인 경우)
                            textView.setText("해당하는 날짜에 대화방이 없습니다!");

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
                //Toast.makeText(JoinCarpool.this, "에러 -> " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        })
        {
            //서버에 전송할 데이터가 있을 경우 포함
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String,String>();
                params.put("select_date", select_date);
                params.put("hostgender", hostgender);
                return params;
            }
        };
        stringRequest.setShouldCache(false);    //이전 결과 있어도 새로 요청하여 응답을 보여준다
        requestQueue.add(stringRequest);

    }

    //방향만 선택시 필터링
    public void routefilter() {
        //1) RequestQueue를 생성한다.
        //2) Request Object를 생성한다.
        //3) 생성한 Object를 RequestQueue로 넘긴다.
        String target = "http://kutaxi.dothome.co.kr/join_carpool.php";

        TextView textView = (TextView)findViewById(R.id.empty);
        textView.setText("");
        //https://kkangsnote.tistory.com/45 (volley api)
        RequestQueue requestQueue = Volley.newRequestQueue(JoinCarpool.this);
        //요청방식지정, 웹서버의 url정보전달, 응답을 성공적으로 받았을 때 이 리스너의 onResponse메소드를 자동으로 호출해주는 리스너 객체
        StringRequest stringRequest = new StringRequest(Request.Method.POST, target, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    //https://www.it-swarm-ko.tech/ko/android/android-%ed%8c%8c%ec%8b%b1-jsonobject/1043469052/
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("response");

                    adapter = new ListViewAdapter();
                    //리스트뷰 참조 및 adapter 달기
                    list = (ListView) findViewById(R.id.list);

                    if (jsonArray.length() == 0) {
                        TextView textView = (TextView)findViewById(R.id.empty);
                        textView.setText("해당하는 옵션의 대화방이 없습니다!");
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

                            StringBuffer origin = new StringBuffer(starttime);
                            String start_time = String.valueOf(origin.insert(2, ":"));
                            StringBuffer origin2 = new StringBuffer(endtime);
                            String end_time = String.valueOf(origin2.insert(2, ":"));

                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.M.d HH:mm"); //날짜 데이터 형식을 지정
                            room_date = simpleDateFormat.parse(select_date + " " + start_time); //지정한 데이터 형식에 값을 넣음 (선택된 날짜 및 시간)
                            today_date = simpleDateFormat.parse(new SimpleDateFormat("yyyy.M.d HH:mm", Locale.getDefault()).format(new Date())); //지정한 데이터 형식에 값을 넣음 (오늘 날짜 및 시간)

                            if (select_route.equals(route) && today_date.compareTo(room_date) <= 0) { // db에서 불러온 경로와 선택된 경로가 같고 오늘 날짜 및 시간이 방날짜 및 시간보다 이전이거나 같은 경우
                                //성별에 따른 대화방 목록호출
                                if (genderNum == 0) {
                                    gender = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_human);
                                    adapter.addItem(start_time, end_time, route, currentmember, maxmember, content, gender, roomNum, roomhost, guest1, guest2);
                                    count+=1;
                                    gender = null;
                                } else if (genderNum == 1) { //동성 체크 시 로그인 한 사용자의 성별과 대화방 생성자의 성별 비교
                                    if (hostgender.equals(roomgender)) {
                                        if (hostgender.equals("남자"))
                                            gender = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_man);
                                        else if (hostgender.equals("여자"))
                                            gender = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_woman);
                                        adapter.addItem(start_time, end_time, route, currentmember, maxmember, content, gender, roomNum, roomhost, guest1, guest2);
                                        count+=1;
                                        gender = null;
                                    }
                                }
                                list.setAdapter(adapter);
                                 
                            }
                        } if (count ==0){
                            adapter.clearItem();
                            list.setAdapter(adapter);
                            textView.setText("해당하는 옵션의 대화방이 없습니다!");
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
                //Toast.makeText(JoinCarpool.this, "에러 -> " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        })
        {
            //서버에 전송할 데이터가 있을 경우 포함
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String,String>();
                params.put("select_date", select_date);
                params.put("hostgender", hostgender);
                return params;
            }
        };
        stringRequest.setShouldCache(false);    //이전 결과 있어도 새로 요청하여 응답을 보여준다
        requestQueue.add(stringRequest);

    }

    //동성여부 체크시 필터링
    public void genderfilterChecked() {
        //1) RequestQueue를 생성한다.
        //2) Request Object를 생성한다.
        //3) 생성한 Object를 RequestQueue로 넘긴다.
        String target = "http://kutaxi.dothome.co.kr/join_carpool.php";

        TextView textView = (TextView)findViewById(R.id.empty);
        textView.setText("");
        //https://kkangsnote.tistory.com/45 (volley api)
        RequestQueue requestQueue = Volley.newRequestQueue(JoinCarpool.this);
        //요청방식지정, 웹서버의 url정보전달, 응답을 성공적으로 받았을 때 이 리스너의 onResponse메소드를 자동으로 호출해주는 리스너 객체
        StringRequest stringRequest = new StringRequest(Request.Method.POST, target, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    //https://www.it-swarm-ko.tech/ko/android/android-%ed%8c%8c%ec%8b%b1-jsonobject/1043469052/
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("response");

                    adapter = new ListViewAdapter();
                    //리스트뷰 참조 및 adapter 달기
                    list = (ListView) findViewById(R.id.list);

                    if (jsonArray.length() == 0) {
                        TextView textView = (TextView)findViewById(R.id.empty);
                        textView.setText("해당하는 옵션의 대화방이 없습니다!");
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

                            StringBuffer origin = new StringBuffer(starttime);
                            String start_time = String.valueOf(origin.insert(2, ":"));
                            StringBuffer origin2 = new StringBuffer(endtime);
                            String end_time = String.valueOf(origin2.insert(2, ":"));

                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.M.d HH:mm"); //날짜 데이터 형식을 지정
                            room_date = simpleDateFormat.parse(select_date + " " + start_time); //지정한 데이터 형식에 값을 넣음 (선택된 날짜 및 시간)
                            today_date = simpleDateFormat.parse(new SimpleDateFormat("yyyy.M.d HH:mm", Locale.getDefault()).format(new Date())); //지정한 데이터 형식에 값을 넣음 (오늘 날짜 및 시간)

                            if (select_route.length() > 0 && today_date.compareTo(room_date) <= 0) {  //방향 선택 + 동성 체크 + 시간 체크
                                if (select_route.equals(route)) {
                                    if (genderNum == 1) { //동성 체크 시 로그인 한 사용자의 성별과 대화방 생성자의 성별 비교
                                        if (hostgender.equals(roomgender)) {
                                            if (hostgender.equals("남자"))
                                                gender = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_man);
                                            else if (hostgender.equals("여자"))
                                                gender = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_woman);
                                            adapter.addItem(start_time, end_time, route, currentmember, maxmember, content, gender, roomNum, roomhost, guest1, guest2);
                                            count += 1;
                                            gender = null;
                                        }
                                    }
                                    list.setAdapter(adapter);
                                }
                            } else if (select_route.length() == 0 && today_date.compareTo(room_date) <= 0) { //방향 미선택 + 동성 체크 + 시간 체크
                                if (genderNum == 1) { //동성 체크 시 로그인 한 사용자의 성별과 대화방 생성자의 성별 비교

                                    if (hostgender.equals(roomgender)) {
                                        if (hostgender.equals("남자"))
                                            gender = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_man);
                                        else if (hostgender.equals("여자"))
                                            gender = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_woman);
                                        adapter.addItem(start_time, end_time, route, currentmember, maxmember, content, gender, roomNum, roomhost, guest1, guest2);
                                        count += 1;
                                        gender = null;
                                    }
                                }
                                list.setAdapter(adapter);
                            }
                        } if (count==0) {
                            textView.setText("해당하는 옵션의 대화방이 없습니다!");

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
                //Toast.makeText(JoinCarpool.this, "에러 -> " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        })
        {
            //서버에 전송할 데이터가 있을 경우 포함
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String,String>();
                params.put("select_date", select_date);
                params.put("hostgender", hostgender);
                return params;
            }
        };
        stringRequest.setShouldCache(false);    //이전 결과 있어도 새로 요청하여 응답을 보여준다
        requestQueue.add(stringRequest);

    }

    //닉네임 얻기
    public void getNickname() {
        String target = "http://kutaxi.dothome.co.kr/mypage_carpool.php";

        RequestQueue requestQueue = Volley.newRequestQueue(JoinCarpool.this);
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
                //Toast.makeText(JoinCarpool.this, "에러 -> " + error.getMessage(), Toast.LENGTH_SHORT).show();
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

    //현재 인원 수 추가
    public void addMember() {
        String url = "http://kutaxi.dothome.co.kr/add_member.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
            }}, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("host", host);
                params.put("room_number", room_number);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(JoinCarpool.this);
        queue.add(stringRequest);

    }

    //현재 인원 수 추가(현재인원수가 2명일때)
    public void addMember2() {
        String url = "http://kutaxi.dothome.co.kr/add_member2.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
            }}, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("host", host);
                params.put("room_number", room_number);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(JoinCarpool.this);
        queue.add(stringRequest);

    }

    //리스트뷰 크기 자동 조정
    public static void setListViewHeight(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);
        //MeasureSpec.AT_MOST - 최대 측정값을 설정하여 그 범위 내에서 값을 지정

        for (int i = 0; i < listAdapter.getCount(); i++ ) {
            View view = listAdapter.getView(i,null,listView);
            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            //MeasureSpec.UNSPECIFIED - mode가 셋팅되지 않은 크기가 넘어올 때 사용
            totalHeight += view.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams(); // 현재 레이아웃 요소의 속성객체를 얻어옴
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params); // 해당 속성객체의 값을 변경
        listView.requestLayout(); // requestLayout - 레이아웃을 갱신함.

    }
}

