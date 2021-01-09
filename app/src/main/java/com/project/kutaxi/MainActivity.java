package com.project.kutaxi;

import android.content.Intent;
import android.graphics.Color;

import android.os.Bundle;
import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.project.kutaxi.decorators.Saturday;
import com.project.kutaxi.decorators.Sunday;
import com.project.kutaxi.decorators.Today;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


// 달력 화면 액티비티

public class MainActivity extends AppCompatActivity {

    private final Today today = new Today();
    String host, hostgender;
    String select_date = new SimpleDateFormat("yyyy.M.d", Locale.getDefault()).format(new Date()); //기본 오늘날짜 선택
    MaterialCalendarView materialCalendarView;
    private long backKeyPressedTime;
    Date target_date, today_date;

    @Override
    //메인 메소드
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar); //xml 파일 지정
        host = getIntent().getStringExtra("host"); //로그인 한 사람 학번
        hostgender = getIntent().getStringExtra("hostgender"); //성별

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar); //툴바 지정
        setSupportActionBar(toolbar); //툴바 설정
        toolbar.inflateMenu(R.menu.menu_nav); //메뉴 추가

        toolbar.setTitleTextColor(Color.BLACK); //툴바의 텍스트 색상 지정
        getSupportActionBar().setDisplayShowTitleEnabled(false); //기본 액션바를 사용하지 않고 커스텀 액션바 사용

        materialCalendarView = (MaterialCalendarView) findViewById(R.id.calendarView); //MaterialCalendarView의 id를 찾음

        materialCalendarView.state().edit() //달력 세부 설정
                .setFirstDayOfWeek(Calendar.SUNDAY) // 주의 시작 요일을 일요일로 지정
                .setMinimumDate(CalendarDay.from(2019, 12, 1)) // 달력의 시작
                .setMaximumDate(CalendarDay.from(2050, 12, 31)) // 달력의 끝
                .setCalendarDisplayMode(CalendarMode.MONTHS) //캘린더 모드를 월 단위로 지정
                .commit();

        materialCalendarView.addDecorators( //표시 추가 (오늘날짜, 일요일, 토요일 효과 지정)
                new Sunday(),
                new Saturday(),
                today);



        // 날짜변경 설정을 해주는 리스너
        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {

            @Override
            //날짜를 클릭하였을 때 동작시켜주는 메소드
            public void onDateSelected(MaterialCalendarView widget, CalendarDay date, boolean selected) {

                int Year = date.getYear(); // 해당 날짜를 클릭하였을 때 date 정보에서 연도만 추출하여 Year에 저장
                int Month = date.getMonth() + 1; // 해당 날짜를 클릭하였을 때 date 정보에서 달만 추출하여 +1한 결과를 Month에 저장 (하지 않으면 1달 차이 발생)
                int Day = date.getDay(); // 해당 날짜를 클릭하였을 때 date 정보에서 일만 추출하여 Day에 저장

                select_date = Year + "." + Month + "." + Day;
            }
        });

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView_Bar);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);

        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {


            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.ic_chat:
                        Intent intent2 = new Intent(MainActivity.this, ChatList.class);
                        intent2.addFlags (Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        intent2.putExtra("host", host);
                        intent2.putExtra("hostgender", hostgender);
                        startActivity(intent2);
                        break;

                    case R.id.ic_mypage:
                        Intent intent3 = new Intent(MainActivity.this, MyPageCarpool.class);
                        intent3.addFlags (Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        intent3.putExtra("host", host);
                        intent3.putExtra("hostgender", hostgender);
                        startActivity(intent3);
                        break;
                }
                return false;

            }

        });
    }

    public void onNextButtonClicked(View view) throws ParseException { //다음 버튼 클릭

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.M.d"); //날짜 데이터 형식을 지정

        target_date = simpleDateFormat.parse(select_date); //지정한 데이터 형식에 값을 넣음 (선택된 날짜)
        today_date = simpleDateFormat.parse(new SimpleDateFormat("yyyy.M.d", Locale.getDefault()).format(new Date())); //지정한 데이터 형식에 값을 넣음 (오늘 날짜)

        if (today_date.compareTo(target_date)<= 0) { //오늘 날짜가 선택된 날짜보다 이전이거나 같은 경우

            Intent intent = new Intent(MainActivity.this, JoinCarpool.class);
            intent.putExtra("select_date", select_date);
            intent.putExtra("host", host);
            intent.putExtra("hostgender", hostgender);
            startActivity(intent);

        }

        else // 오늘날짜가 선택된 날짜보다 이후인 경우
            Toast.makeText(MainActivity.this, "참여할 수 없는 날짜입니다.", Toast.LENGTH_SHORT).show();
    }

    //두번 누르면 앱꺼지게
    @Override
    public void onBackPressed() {
        if(System.currentTimeMillis() > backKeyPressedTime + 2000){
            backKeyPressedTime = System.currentTimeMillis();
            Toast.makeText(getApplicationContext(),"한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
        }else {
            finishAffinity();
        }
    }

}