package com.project.kutaxi;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

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

import java.util.HashMap;
import java.util.Map;


public class MyPageCarpool extends AppCompatActivity {

    String host, hostgender;
    String name, nickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mypage_carpool);
        getIntent();
        host = getIntent().getStringExtra("host"); //로그인 한 사람 학번
        hostgender = getIntent().getStringExtra("hostgender"); //성별

        SharedPreferences chatInformation = getSharedPreferences("chat", MODE_PRIVATE);
        String receive_number = chatInformation.getString("receive_number", "null");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_mypage_carpool);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.BLACK);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView_Bar);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(2);
        menuItem.setChecked(true);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.ic_home:
                        Intent intent1 = new Intent(MyPageCarpool.this, MainActivity.class);
                        intent1.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION); // 화면전환 애니메이션 없애기
                        intent1.putExtra("host", host);
                        intent1.putExtra("hostgender", hostgender);
                        startActivity(intent1);
                        break;

                    case R.id.ic_chat:
                        Intent intent2 = new Intent(MyPageCarpool.this, ChatList.class);
                        intent2.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION); // 화면전환 애니메이션 없애기
                        intent2.putExtra("host", host);
                        intent2.putExtra("hostgender", hostgender);
                        startActivity(intent2);
                        break;



                }
                return true;

            }

        });

        //마이페이지 정보 업데이트
        sendRequest();
        TextView studentIdTextView = (TextView) findViewById(R.id.studentId);
        studentIdTextView.setText(host);


        MyPageAdapter adapter = new MyPageAdapter();
        ListView listview = (ListView) findViewById(R.id.settingList); // 위젯과 멤버변수 참조 획득

        listview.setAdapter(adapter);

        //adapter.addItem(ContextCompat.getDrawable(this, R.drawable.ic_information),"공지사항");
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.ic_nickname),"닉네임 설정");
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.ic_alarm),"알림 설정");
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.ic_help),"문의사항");
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.ic_lock),"로그아웃");

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) { //각각의 리스트를 클릭하였을 때의 이벤트 실행
                /*if (position == 0) {
                    Intent information = new Intent(MyPageCarpool.this, JoinCarpool.class);
                    startActivity(information);
                }*/
                if (position == 0) {
                    Intent nickname = new Intent (MyPageCarpool.this, PopupNickname.class);
                    nickname.putExtra("host", host);
                    nickname.putExtra("hostgender", hostgender);
                    startActivity(nickname);
                }
                else if (position == 1) {
                    Intent alarm = new Intent (MyPageCarpool.this, PopupAlarm.class);
                    startActivity(alarm);
                }
                else if (position == 2) {
                    //주소 하이퍼링크로 바꾸기
                    AlertDialog.Builder builder = new AlertDialog.Builder(MyPageCarpool.this);
                    builder.setTitle("문의사항").setMessage(R.string.kakaolink);
                    builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            return;
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    //주소 바로가기 가능하게 하기
                    ((TextView) alertDialog.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
                }
                else if (position == 3) {
                    Intent logout = new Intent (MyPageCarpool.this, LoginActivity.class);
                    logout.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    SharedPreferences loginInformation = getSharedPreferences("setting", 0); //사용 선언
                    SharedPreferences.Editor editor = loginInformation.edit(); //데이터 저장
                    editor.clear(); //자동로그인 기능 초기화
                    editor.commit();
                    startActivity(logout);
                }
            }

        });

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

    public void sendRequest() {
        String target = "http://kutaxi.dothome.co.kr/mypage_carpool.php";

        RequestQueue requestQueue = Volley.newRequestQueue(MyPageCarpool.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, target, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("response");

                    TextView nameTextView = (TextView) findViewById(R.id.name);
                    TextView nicknameTextView = (TextView) findViewById(R.id.nickname);

                    jsonObject = jsonArray.getJSONObject(0);
                    name = jsonObject.getString("name");
                    nickname = jsonObject.getString("nickname");
                    nameTextView.setText(name);
                    nicknameTextView.setText(nickname);

                } catch (JSONException e) {
                    //e.printStackTrace();
                }
            }
            //에러가 발생했을 때 호출될 리스너 객체
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(MyPageCarpool.this, "에러 -> " + error.getMessage(), Toast.LENGTH_SHORT).show();
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
}