package com.project.kutaxi;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;

import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;

public class PopupDelete extends FragmentActivity {

    private String room_number, host, hostgender, guest1, guest2, room_host;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.popup_delete);

        room_number = getIntent().getStringExtra("room_number"); //방 번호
        room_host = getIntent().getStringExtra("room_host");
        guest1 = getIntent().getStringExtra("guest1");
        guest2 = getIntent().getStringExtra("guest2");
        host = getIntent().getStringExtra("host"); //로그인 한 사람 학번
        hostgender = getIntent().getStringExtra("hostgender");

    }

    //취소 버튼 클릭
    public void onCancel(View v){
        finish();
    }

    //확인 버튼 클릭
    public void onClick(View v){
        if (host.equals(room_host))
            deleteHost();
        else if (host.equals(guest1))
            deleteMember();
        else if (host.equals(guest2))
            deleteMember2();

        Toast.makeText(getApplicationContext(), "대화방 나가기가 완료되었습니다.", Toast.LENGTH_SHORT).show();
        FirebaseMessaging.getInstance().unsubscribeFromTopic(room_number);
        Intent intent = new Intent(getApplicationContext(), ChatList.class);
        intent.addFlags (Intent.FLAG_ACTIVITY_NO_ANIMATION); // 화면전환 애니메이션 없애기
        intent.putExtra("host", host);
        intent.putExtra("hostgender", hostgender);
        startActivity(intent);

    }

    /*
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }*/

    @Override
    public void onBackPressed() {
        //안드로이드 백버튼 막기
    }

    //나=room_host
    public void deleteHost() {
        String url = "http://kutaxi.dothome.co.kr/delete_host.php";
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
                params.put("room_number", room_number);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(PopupDelete.this);
        queue.add(stringRequest);
    }

    //나=guest1
    public void deleteMember() {
        String url = "http://kutaxi.dothome.co.kr/delete_member.php";
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
                params.put("room_number", room_number);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(PopupDelete.this);
        queue.add(stringRequest);
    }

    //나=guest2
    public void deleteMember2() {
        String url = "http://kutaxi.dothome.co.kr/delete_member2.php";
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
                params.put("room_number", room_number);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(PopupDelete.this);
        queue.add(stringRequest);
    }
}