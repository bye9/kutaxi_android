package com.project.kutaxi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

// 인트로 화면

public class IntroActivity extends AppCompatActivity {

    Boolean checkAuto;
    String host, hostgender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences chatInformation = getSharedPreferences("chat", MODE_PRIVATE); //사용 선언
        SharedPreferences.Editor editor = chatInformation.edit(); //데이터 저장
        editor.clear(); //방 번호 초기화
        editor.commit();

        //최초로그인인지 체크
        SharedPreferences loginInformation = getSharedPreferences("setting", MODE_PRIVATE);
        checkAuto = loginInformation.getBoolean("check", false);

        if (checkAuto) {
            //로그인 한 사람의 학번, 성별 받아오기
            host = loginInformation.getString("host", "200000000");
            hostgender = loginInformation.getString("hostgender", "성별");

            final Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("host", host);
            intent.putExtra("hostgender", hostgender);

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {

                    startActivity(intent);
                    finish();
                }
            }, 1500);
        } else {
            final Intent intent = new Intent(this, LoginActivity.class);

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {

                    startActivity(intent);
                    finish();
                }
            }, 1500);
        }
    }
}
