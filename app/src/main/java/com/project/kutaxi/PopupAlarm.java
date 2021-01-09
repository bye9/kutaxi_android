package com.project.kutaxi;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;

import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.Switch;


public class PopupAlarm extends FragmentActivity {

    Boolean checkAlarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.popup_alarm);

        SharedPreferences alarmInformation = getSharedPreferences("alarm", MODE_PRIVATE);
        checkAlarm = alarmInformation.getBoolean("check", true);

        Switch sw = (Switch) findViewById(R.id.message_alarm);
        if (checkAlarm)
            sw.setChecked(true);
        else
            sw.setChecked(false);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SharedPreferences loginInformation = getSharedPreferences("alarm", MODE_PRIVATE); //사용 선언
                    SharedPreferences.Editor editor = loginInformation.edit(); //데이터 저장
                    editor.putBoolean("check", true);
                    editor.commit();
                } else {
                    SharedPreferences loginInformation = getSharedPreferences("alarm", MODE_PRIVATE); //사용 선언
                    SharedPreferences.Editor editor = loginInformation.edit(); //데이터 저장
                    editor.putBoolean("check", false);
                    editor.commit();
                }
            }
        });


    }

    //확인 버튼 클릭
    public void onClick(View v){
        finish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if(event.getAction()== MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        //안드로이드 백버튼 막기
    }
}