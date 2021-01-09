package com.project.kutaxi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

public class ChatCarpool extends AppCompatActivity {

    private String room_number, host, nickname, hostgender, guest1, guest2, room_host;
    private ListView lv_chating;
    private EditText et_send;
    private ImageButton btn_send;
    private String message, time;
    private ChatAdapter chatAdapter;
    private Calendar calendar;
    private DatabaseReference reference;

    String receive_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_carpool);
        room_number = getIntent().getStringExtra("room_number"); //방 번호
        host = getIntent().getStringExtra("host"); //로그인 한 사람 학번
        nickname = getIntent().getStringExtra("nickname"); //닉네임
        hostgender = getIntent().getStringExtra("hostgender");
        room_host = getIntent().getStringExtra("room_host");

        //Toast.makeText(getApplicationContext(), room_number, Toast.LENGTH_SHORT).show();
        FirebaseMessaging.getInstance().subscribeToTopic(room_number);

        SharedPreferences chatInformation = getSharedPreferences("chat", MODE_PRIVATE);
        SharedPreferences.Editor editor = chatInformation.edit();
        editor.putString("receive_number", room_number);
        editor.apply();

        receive_number = chatInformation.getString("receive_number", "null");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_chat_carpool);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.BLACK);
        toolbar.inflateMenu(R.menu.menu_nav); //메뉴 추가
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView_Bar);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);

        final Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.ic_home:
                        SharedPreferences chatInformation = getSharedPreferences("chat", MODE_PRIVATE); //사용 선언
                        SharedPreferences.Editor editor = chatInformation.edit(); //데이터 저장
                        editor.putString("receive_number", "null");
                        editor.apply();

                        Intent intent1 = new Intent(ChatCarpool.this, MainActivity.class);
                        intent1.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        intent1.putExtra("host", host);
                        intent1.putExtra("hostgender", hostgender);
                        startActivity(intent1);
                        break;

                    case R.id.ic_mypage:
                        SharedPreferences chatInformation2 = getSharedPreferences("chat", MODE_PRIVATE); //사용 선언
                        SharedPreferences.Editor editor2 = chatInformation2.edit(); //데이터 저장
                        editor2.putString("receive_number", "null");
                        editor2.apply();

                        Intent intent3 = new Intent(ChatCarpool.this, MyPageCarpool.class);
                        intent3.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        intent3.putExtra("host", host);
                        intent3.putExtra("hostgender", hostgender);
                        startActivity(intent3);
                        break;
                }
                return false;

            }

        });

        btn_send = (ImageButton) findViewById(R.id.btn_send);   //전송 버튼에 대한 아이디를 찾음.
        et_send = (EditText) findViewById(R.id.et_send);    //edittext에 대한 아이디를 찾음.
        lv_chating = (ListView) findViewById(R.id.lv_chating);  // 리스트에 대한 아이디를 찾음.

        btn_send.setOnClickListener(new View.OnClickListener() {    //전송버튼에 대한 리스너
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

        chatAdapter = new ChatAdapter(host); //ChatAdapter에 대한 새로운 객체 생성
        lv_chating.setAdapter(chatAdapter); //리스트뷰와 어댑터 연결

        reference = FirebaseDatabase.getInstance().getReference().child("chat").child(room_number);
        reference.addChildEventListener(new ChildEventListener() {
            //자식이 추가되었을 때 실행하는 메소드
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                chatConversation(dataSnapshot);
                lv_chating.setSelection(chatAdapter.getCount() - 1);  //항상 맨 밑으로
            }

            //자식이 변경되었을 때 실행하는 메소드
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            //자식이 삭제되었을 때 실행하는 메소드
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            //자식이 이동되었을 때 실행하는 메소드
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        setListViewHeight(lv_chating);
    }

    //https://m.blog.naver.com/PostView.nhn?blogId=thdeodls85&logNo=150173868905&proxyReferer=https:%2F%2Fwww.google.co.kr%2F
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

        for (int i = 0; i < listAdapter.getCount(); i++) {
            View view = listAdapter.getView(i, null, listView);
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

    // addChildEventListener를 통해 실제 데이터베이스에 변경된 값이 있으면,
    // 화면에 보여지고 있는 Listview의 값을 갱신함
    // 이벤트 발생 시점에 데이터베이스에서 지정된 위치에 있던 데이터를 포함하는 DataSnapshot
    private void chatConversation(DataSnapshot dataSnapshot) {
        Iterator i = dataSnapshot.getChildren().iterator();
        //읽어 올 요소가 남아있는지 확인하는 메소드
        while (i.hasNext()) {
            String chat_host = (String) ((DataSnapshot) i.next()).getValue();
            String chat_message = (String) ((DataSnapshot) i.next()).getValue();
            String chat_nickname = (String) ((DataSnapshot) i.next()).getValue();
            String chat_time = (String) ((DataSnapshot) i.next()).getValue();

            //chatAdapter.addItem(chat_nickname, chat_message, chat_time, chat_host);// 채팅방 화면에 nickname, message, time 값을 불러옴.

            if (host.equals(chat_host)) // 학번 동일 (나)
                chatAdapter.addItem(nickname, chat_message, chat_time, chat_host);// 채팅방 화면에 nickname, message, time 값을 불러옴.

            else // 학번 다름 (상대방)
                chatAdapter.addItem(chat_nickname, chat_message, chat_time, chat_host);
        }
        chatAdapter.notifyDataSetChanged();
    }

    //뒤로 가기시 대화방 목록 화면
    public void onBackPressed() {
        SharedPreferences chatInformation = getSharedPreferences("chat", MODE_PRIVATE); //사용 선언
        SharedPreferences.Editor editor = chatInformation.edit(); //데이터 저장
        editor.putString("receive_number", "null");
        editor.apply();

        Intent intent = new Intent(this, ChatList.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION); // 화면전환 애니메이션 없애기
        intent.putExtra("host", host);
        intent.putExtra("hostgender", hostgender);
        startActivity(intent);
    }


    //홈버튼 클릭 시 대화방 변수 초기화
    @Override
    public void onUserLeaveHint() {
        SharedPreferences chatInformation = getSharedPreferences("chat", MODE_PRIVATE); //사용 선언
        SharedPreferences.Editor editor = chatInformation.edit(); //데이터 저장
        editor.putString("receive_number", "null");
        editor.apply();
    }

    //guest1, guest2 받아오고 메시지 firebase넘겨주기 + 알림
    public void sendMessage() {
        String target = "http://kutaxi.dothome.co.kr/get_member.php";

        //https://kkangsnote.tistory.com/45 (volley api)
        RequestQueue requestQueue = Volley.newRequestQueue(ChatCarpool.this);
        //요청방식지정, 웹서버의 url정보전달, 응답을 성공적으로 받았을 때 이 리스너의 onResponse메소드를 자동으로 호출해주는 리스너 객체
        StringRequest stringRequest = new StringRequest(Request.Method.POST, target, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    //https://www.it-swarm-ko.tech/ko/android/android-%ed%8c%8c%ec%8b%b1-jsonobject/1043469052/
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("response");
                    jsonObject = jsonArray.getJSONObject(0);
                    guest1 = jsonObject.getString("guest1");
                    guest2 = jsonObject.getString("guest2");

                    calendar = Calendar.getInstance();
                    //현재 시간
                    time = String.format(Locale.getDefault(), "%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
                    message = et_send.getText().toString();

                    //Map<String, Object> map = new HashMap<String, Object>();
                    String key = reference.push().getKey(); //push()는 리스트 형태로 데이터 추가, Firebase에서 생성해주는 Key를 사용해서 해당 데이터베이스를 열어준다.
                    //reference.updateChildren(map);

                    if (message.equals("")) {
                        Toast.makeText(getApplicationContext(), "내용을 입력해주세요", Toast.LENGTH_SHORT).show();
                    } else {
                        sendNotification();

                        DatabaseReference root = reference.child(key);
                        //알파벳 순서로 가지가 들어감
                        Map<String, Object> objectMap = new HashMap<String, Object>();
                        objectMap.put("host", host);
                        objectMap.put("nickname", nickname); //put을 이용하여 HashMap 안에 데이터를 삽입 (첫 번째 인자 - Key 값, 두 번째 인자 - 실제 값)
                        objectMap.put("message", message);
                        objectMap.put("time", time);

                        root.updateChildren(objectMap); //updateChildren를 호출하여 database 최종 업데이트
                    }
                    et_send.setText("");

                } catch (JSONException e) {
                    //e.printStackTrace();
                }
            }
            //에러가 발생했을 때 호출될 리스너 객체
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(ChatCarpool.this, "에러 -> " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            //서버에 전송할 데이터가 있을 경우 포함
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("room_number", room_number);
                return params;
            }
        };
        stringRequest.setShouldCache(false);    //이전 결과 있어도 새로 요청하여 응답을 보여준다
        requestQueue.add(stringRequest);
    }

    //서버를 통한 알림 전송
    public void sendNotification() {
        String url = "http://kutaxi.dothome.co.kr/send_notification.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("nickname", nickname);
                params.put("message", message);
                params.put("send_number", room_number);
                params.put("guest1", guest1);
                params.put("guest2", guest2);
                params.put("room_host", room_host);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(ChatCarpool.this);
        queue.add(stringRequest);
    }

}