package com.project.kutaxi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;

import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class PopupNickname extends FragmentActivity {

    String host, hostgender, nickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.popup_nickname);

        //데이터 가져오기
        host = getIntent().getStringExtra("host"); //로그인 한 사람 학번
        hostgender = getIntent().getStringExtra("hostgender"); //성별
    }

    //확인 버튼 클릭
    public void onClick(View v){

        TextView nicknameTextView = (TextView) findViewById(R.id.nicknameText);
        nickname = nicknameTextView.getText().toString().replaceAll("\\s","");

        if (nickname.length() > 6) {
            Toast.makeText(this, "닉네임은 최대 6글자까지만 가능합니다.", Toast.LENGTH_SHORT).show();
        } else if (nickname.equals("")) {
            Toast.makeText(this, "닉네임을 입력해주세요!", Toast.LENGTH_SHORT).show();
        } else {
            String url = "http://kutaxi.dothome.co.kr/change_nickname.php";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    SharedPreferences loginInformation = getSharedPreferences("setting", MODE_PRIVATE); //사용 선언
                    SharedPreferences.Editor editor = loginInformation.edit(); //데이터 저장
                    editor.putString("nickname", nickname);
                    editor.commit();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("host", host);
                    params.put("nickname", nickname);
                    return params;
                }
            };
            RequestQueue queue = Volley.newRequestQueue(PopupNickname.this);
            queue.add(stringRequest);

            Intent intent = new Intent(this, MyPageCarpool.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            intent.putExtra("host", host);
            intent.putExtra("hostgender", hostgender);
            startActivity(intent);
            //액티비티(팝업) 닫기
            finish();
        }

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
}