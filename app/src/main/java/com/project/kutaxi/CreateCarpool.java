package com.project.kutaxi;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

// 플러스 버튼 눌렀을 때 대화방 생성 화면

public class CreateCarpool extends AppCompatActivity {

    String startampm, starthour, startminute, endampm, endhour, endminute;
    String select_date, host, hostgender, start_time, end_time, route;
    int member;
    int current_member = 1;
    int gender = 0; //tinyint(1) 기본값=이성허용 (0이면 false)
    String room_number;
    private EditText et_content;
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference().getRoot();
    Map<String, Object> map = new HashMap<String, Object>();
    Date s_time, today_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_carpool);
        getIntent();
        select_date = getIntent().getStringExtra("select_date"); //오늘날짜 받아오기
        host = getIntent().getStringExtra("host"); //로그인 한 사람 학번
        hostgender = getIntent().getStringExtra("hostgender"); //성별

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_carpool);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.BLACK);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        final Spinner s_spinner = (Spinner)findViewById(R.id.start_hour);
        final Spinner e_spinner = (Spinner)findViewById(R.id.end_hour);

        try {
            Field popup = Spinner.class.getDeclaredField("mPopup");
            popup.setAccessible(true);

            // Get private mPopup member variable and try cast to ListPopupWindow
            android.widget.ListPopupWindow s_popupWindow = (android.widget.ListPopupWindow) popup.get(s_spinner);
            android.widget.ListPopupWindow e_popupWindow = (android.widget.ListPopupWindow) popup.get(e_spinner);

            // Set popupWindow height to 500px
            assert s_popupWindow != null;
            s_popupWindow.setHeight(370);

            assert e_popupWindow != null;
            e_popupWindow.setHeight(370);
        }
        catch (NoClassDefFoundError | ClassCastException | NoSuchFieldException | IllegalAccessException e) {
            // silently fail...
        }


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
                        Intent intent1 = new Intent(CreateCarpool.this, MainActivity.class);
                        intent1.addFlags (Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        intent1.putExtra("host", host);
                        intent1.putExtra("hostgender", hostgender);
                        startActivity(intent1);
                        break;

                    case R.id.ic_chat:
                        Intent intent2 = new Intent(CreateCarpool.this, ChatList.class);
                        intent2.addFlags (Intent.FLAG_ACTIVITY_NO_ANIMATION); // 화면전환 애니메이션 없애기
                        intent2.putExtra("host", host);
                        intent2.putExtra("hostgender", hostgender);
                        startActivity(intent2);
                        break;

                    case R.id.ic_mypage:
                        Intent intent3 = new Intent(CreateCarpool.this, MyPageCarpool.class);
                        intent3.addFlags (Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        intent3.putExtra("host", host);
                        intent3.putExtra("hostgender", hostgender);
                        startActivity(intent3);
                        break;
                }
                return false;
            }

        });

        create_process();

    }

    public void onCreateNextButtonClicked(View view) throws ParseException { //다음 버튼 클릭

        final String content = et_content.getText().toString();

            if (startampm.equals("AM") && starthour.equals("12"))
                starthour = "00";

            else if (startampm.equals("PM") && !starthour.equals("12"))
                starthour = String.valueOf(Integer.parseInt(starthour)+12);

            if (endampm.equals("AM") && endhour.equals("12") && endminute.equals("00"))
                endhour = "24";
            else if (endampm.equals("AM") && endhour.equals("12"))
                endhour = "00";
            else if (endampm.equals("PM") && !endhour.equals("12"))
                endhour = String.valueOf(Integer.parseInt(endhour)+12);

        start_time = starthour + startminute;
        end_time = endhour + endminute;

        Integer temp_start = Integer.parseInt(starthour+startminute);
        Integer temp_end = Integer.parseInt(endhour+endminute);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.M.d HHmm"); //날짜 데이터 형식을 지정

        s_time = simpleDateFormat.parse(select_date + " " + start_time); //지정한 데이터 형식에 값을 넣음 (선택된 시간 및 날짜)
        today_time = simpleDateFormat.parse(new SimpleDateFormat("yyyy.M.d HHmm", Locale.getDefault()).format(new Date())); //지정한 데이터 형식에 값을 넣음 (오늘 날짜 및 시간)

        if (temp_end-temp_start==30 || temp_end-temp_start==70) {
            if (today_time.compareTo(s_time) <= 0) { //현재 날짜 및 시간이 선택된 날짜 및 시간보다 이전이거나 같은 경우
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Intent intent = new Intent(CreateCarpool.this, ChatList.class);
                        intent.putExtra("host", host);
                        intent.putExtra("hostgender", hostgender);
                        startActivity(intent);
                    }
                };

                RequestQueue queue = Volley.newRequestQueue(CreateCarpool.this);

                if (endhour.equals("24")) {
                     end_time = "00" + endminute;
                }

                CreateRequest createRequest = new CreateRequest(select_date, start_time, end_time, route, current_member, member, content, gender, host, hostgender, responseListener);
                queue.add(createRequest);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        create_firebase(); //방 번호로 firebase 대화방 생성
                    }
                }, 500);

            } else {
                Toast.makeText(getApplicationContext(), "현재 시간 이후로 입력해주세요!", Toast.LENGTH_SHORT).show();
                if (startampm.equals("PM") && !starthour.equals("12")) {
                    starthour = String.valueOf(Integer.parseInt(starthour)-12);
                }
                if (endampm.equals("PM") && !endhour.equals("12")) {
                    endhour = String.valueOf(Integer.parseInt(endhour)-12);
                }

            }

        } else {
            Toast.makeText(getApplicationContext(), "30분 단위로 입력해주세요!", Toast.LENGTH_SHORT).show();
            //Toast.makeText(getApplicationContext(), start_time+end_time, Toast.LENGTH_SHORT).show();
            if (startampm.equals("PM") && !starthour.equals("12")) {
                starthour = String.valueOf(Integer.parseInt(starthour)-12);
            }
            if (endampm.equals("PM") && !endhour.equals("12")) {
                endhour = String.valueOf(Integer.parseInt(endhour)-12);
            }

        }
    }

    //대화방 생성 시 정보 입력받는 과정
    public void create_process() {
        Spinner startampm_spinner = (Spinner)findViewById(R.id.start_ampm);
        Spinner starthour_spinner = (Spinner)findViewById(R.id.start_hour);
        Spinner startminute_spinner = (Spinner)findViewById(R.id.start_minute);
        Spinner endampm_spinner = (Spinner)findViewById(R.id.end_ampm);
        Spinner endhour_spinner = (Spinner)findViewById(R.id.end_hour);
        Spinner endminute_spinner = (Spinner)findViewById(R.id.end_minute);
        Spinner route_spinner = (Spinner)findViewById(R.id.route);
        Spinner member_spinner = (Spinner)findViewById(R.id.member);
        CheckBox gender_checkbox = (CheckBox)findViewById(R.id.gender);
        et_content = (EditText)findViewById(R.id.content);

        startampm_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                startampm = (String)parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(CreateCarpool.this, "AM 혹은 PM을 선택해주세요.", Toast.LENGTH_SHORT).show();
            }
        });
        starthour_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                starthour = (String)parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(CreateCarpool.this, "시작하는 시간을 선택해주세요.", Toast.LENGTH_SHORT).show();
            }
        });
        startminute_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                startminute = (String)parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(CreateCarpool.this, "시작하는 시간을 선택해주세요.", Toast.LENGTH_SHORT).show();
            }
        });
        endampm_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                endampm = (String)parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(CreateCarpool.this, "AM 혹은 PM을 선택해주세요.", Toast.LENGTH_SHORT).show();
            }
        });
        endhour_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                endhour = (String)parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(CreateCarpool.this, "끝나는 시간을 선택해주세요.", Toast.LENGTH_SHORT).show();
            }
        });
        endminute_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                endminute = (String)parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(CreateCarpool.this, "끝나는 시간을 선택해주세요.", Toast.LENGTH_SHORT).show();
            }
        });
        route_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                route = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(CreateCarpool.this, "방향을 선택해주세요.", Toast.LENGTH_SHORT).show();
            }
        });
        member_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                member = Integer.parseInt((String)parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(CreateCarpool.this, "인원수(본인포함)를 선택해주세요.", Toast.LENGTH_SHORT).show();
            }
        });
        gender_checkbox.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((CheckBox)view).isChecked()) {
                    gender = 1;
                }
            }
        });
    }

    //방 번호로 firebase 대화방 생성
    public void create_firebase() {
        String target = "http://kutaxi.dothome.co.kr/create_firebase.php";

        RequestQueue requestQueue = Volley.newRequestQueue(CreateCarpool.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, target, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("response");
                    jsonObject = jsonArray.getJSONObject(0);
                    room_number = jsonObject.getString("room_number");
                    //Toast.makeText(getApplicationContext(), room_number, Toast.LENGTH_SHORT).show();
                    FirebaseMessaging.getInstance().subscribeToTopic(room_number);
                } catch (JSONException e) {
                    //e.printStackTrace();
                }
            }
            //에러가 발생했을 때 호출될 리스너 객체
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(CreateCarpool.this, "에러 -> " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        stringRequest.setShouldCache(false);    //이전 결과 있어도 새로 요청하여 응답을 보여준다
        requestQueue.add(stringRequest);

        map.put(String.valueOf(room_number), "");
        reference.updateChildren(map);

    }

    //뒤로가기 시 대화방 참여 화면
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, JoinCarpool.class);
        intent.addFlags (Intent.FLAG_ACTIVITY_NO_ANIMATION); // 화면전환 애니메이션 없애기
        intent.putExtra("host", host);
        intent.putExtra("hostgender", hostgender);
        intent.putExtra("select_date", select_date);
        startActivity(intent);
    }
}
