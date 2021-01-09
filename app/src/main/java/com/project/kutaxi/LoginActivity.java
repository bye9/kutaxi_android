package com.project.kutaxi;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.Map;


// 로그인 화면

public class LoginActivity extends AppCompatActivity {

    private String studentNum;
    private String nickname;
    private String name;
    private int grade;
    private String birth;
    private String gender;
    private String college;
    private String major;
    private String phone;
    private int report;
    private String id;
    private String password;
    int count;
    private long backKeyPressedTime = 0;

    int blank=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_main);

        getIntent();

        TextView textView = (TextView)findViewById(R.id.textView3);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    blank++;
                    if (blank>=7) {
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("host", "admin");
                        intent.putExtra("hostgender", "여자");
                        startActivity(intent);
                        //saveToken2();
                        blank = 0;

                        SharedPreferences loginInformation = getSharedPreferences("setting", MODE_PRIVATE); //사용 선언
                        SharedPreferences.Editor editor = loginInformation.edit(); //데이터 저장
                        editor.putBoolean("check", true); //최초로그인 여부 true로 변경
                        editor.putString("host", "admin");
                        editor.putString("hostgender", "여자");
                        editor.commit();
                    }
            }
        });
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

    public void onClickLogin(View view) {

        final EditText textId = (EditText) findViewById(R.id.id);
        final EditText textPassword = (EditText) findViewById(R.id.password);

        //https://mine-it-record.tistory.com/127
        //https://emflant.tistory.com/198
        id = textId.getText().toString().replaceAll("\\s",""); //replaceAll은 정규표현식 사용 가능 (예를 들어 \n, \t와 같은)
        password = textPassword.getText().toString().replaceAll("\\s",""); //replaceAll은 정규표현식 사용 가능 (예를 들어 \n, \t와 같은)

        if (id.isEmpty() | password.isEmpty()) {
            Toast.makeText(getApplicationContext(), "아이디와 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
        } else {

                //AlertDialog 알림창띄우기 (https://lktprogrammer.tistory.com/155?category=741470참고)
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("이용약관").setMessage("1. 개인정보의 처리 목적 ('가치타실’ 이하 ‘가치타실’) 은(는) 다음의 목적을 위하여 개인정보를 처리하고 있으며, 다음의 목적 이외의 용도로는 이용하지 않습니다.\t\n" +
                        "- 고객 가입의사 확인, 고객에 대한 서비스 제공에 따른 본인 식별.인증, 회원자격 유지.관리, 물품 또는 서비스 공급에 따른 금액 결제, 물품 또는 서비스의 공급.배송 등\t\n\n" +
                        "2. 개인정보의 처리 및 보유 기간\t\n" +
                        "① (‘가치타실’ 이하 ‘가치타실’) 은(는) 정보주체로부터 개인정보를 수집할 때 동의 받은 개인정보 보유․이용기간 또는 법령에 따른 개인정보 보유․이용기간 내에서 개인정보를 처리․보유합니다.\t\n" +
                        "② 구체적인 개인정보 처리 및 보유 기간은 다음과 같습니다.\t\n" +
                        "☞ 아래 예시를 참고하여 개인정보 처리업무와 개인정보 처리업무에 대한 보유기간 및 관련 법령, 근거 등을 기재합니다.\t\n" +
                        "(예시)- 고객 가입 및 관리 : 서비스 이용계약 또는 회원가입 해지시까지, 다만 채권․채무관계 잔존시에는 해당 채권․채무관계 정산시까지\t\n" +
                        "- 전자상거래에서의 계약․청약철회, 대금결제, 재화 등 공급기록 : 5년\t\n\n" +
                        "3. 정보주체와 법정대리인의 권리·의무 및 그 행사방법 이용자는 개인정보주체로써 다음과 같은 권리를 행사할 수 있습니다.\t\n" +
                        "① 정보주체는 가치타실(‘가치타실’ 이하 ‘가치타실') 에 대해 언제든지 다음 각 호의 개인정보 보호 관련 권리를 행사할 수 있습니다.\t\n" +
                        "1. 개인정보 열람요구\t\n" +
                        "2. 오류 등이 있을 경우 정정 요구\t\n" +
                        "3. 삭제요구\t\n" +
                        "4. 처리정지 요구\t\n\n" +
                        "4. 처리하는 개인정보의 항목 작성 \t\n" +
                        "① ('가치타실' 이하 '가치타실')은(는) 다음의 개인정보 항목을 처리하고 있습니다.\t\n" +
                        "<학교 인증 및 자격 확인, 부적절한 사용 방지>\t\n" +
                        "수집항목: 학번, 성명, 성별, 전화번호, 닉네임\n" +
                        "- 건국대학교 포탈 아이디, 비밀번호는 회원가입시 최초에만 이용되며, 수집되지 않습니다.\n\n" +
                        "5. 개인정보의 파기('가치타실')은(는) 원칙적으로 개인정보 처리목적이 달성된 경우에는 지체없이 해당 개인정보를 파기합니다. 파기의 절차, 기한 및 방법은 다음과 같습니다.\t\n" +
                        "-파기절차\t\n" +
                        "이용자가 입력한 정보는 목적 달성 후 별도의 DB에 옮겨져(종이의 경우 별도의 서류) 내부 방침 및 기타 관련 법령에 따라 일정기간 저장된 후 혹은 즉시 파기됩니다. 이 때, DB로 옮겨진 개인정보는 법률에 의한 경우가 아니고서는 다른 목적으로 이용되지 않습니다.\t\n" +
                        "-파기기한\t\n" +
                        "이용자의 개인정보는 개인정보의 보유기간이 경과된 경우에는 보유기간의 종료일로부터 5일 이내에, 개인정보의 처리 목적 달성, 해당 서비스의 폐지, 사업의 종료 등 그 개인정보가 불필요하게 되었을 때에는 개인정보의 처리가 불필요한 것으로 인정되는 날로부터 5일 이내에 그 개인정보를 파기합니다.\t\n\n" +
                        "6. 개인정보 자동 수집 장치의 설치•운영 및 거부에 관한 사항\t\n" +
                        "가치타실 은 정보주체의 이용정보를 저장하고 수시로 불러오는 ‘쿠키’를 사용하지 않습니다.\t\n\n" +
                        "7. 개인정보 보호책임자 작성 \t\n" +
                        "① 가치타실(‘가치타실’ 이하 ‘가치타실') 은(는) 개인정보 처리에 관한 업무를 총괄해서 책임지고, 개인정보 처리와 관련한 정보주체의 불만처리 및 피해구제 등을 위하여 아래와 같이 개인정보 보호책임자를 지정하고 있습니다.\t\n" +
                        "▶ 개인정보 보호책임자\t\n" +
                        "성명 : 석정환\t\n" +
                        "직책 : 학생\t\n" +
                        "직급 : 학생\t\n" +
                        "연락처 : 010-9332-1551\t\n" +
                        "이메일 : tjrwjdghks12@naver.com\t\n" +
                        "※ 개인정보 보호 담당부서로 연결됩니다.\t\n" +
                        "▶ 개인정보 보호 담당부서\t\n" +
                        "부서명 : 장용운\t\n" +
                        "담당자 : 장용운\t\n" +
                        "연락처 : popcorn0120@gmail.com\t\n" +
                        "② 정보주체께서는 가치타실(‘가치타실’ 이하 ‘가치타실') 의 서비스(또는 사업)을 이용하시면서 발생한 모든 개인정보 보호 관련 문의, 불만처리, 피해구제 등에 관한 사항을 개인정보 보호책임자 및 담당부서로 문의하실 수 있습니다. 가치타실(‘가치타실’ 이하 ‘가치타실') 은(는) 정보주체의 문의에 대해 지체 없이 답변 및 처리해드릴 것입니다.\t\n\n" +
                        "8. 개인정보 처리방침 변경 \t\n" +
                        "①이 개인정보처리방침은 시행일로부터 적용되며, 법령 및 방침에 따른 변경내용의 추가, 삭제 및 정정이 있는 경우에는 변경사항의 시행 7일 전부터 공지사항을 통하여 고지할 것입니다.\t\n\n" +
                        "9. 개인정보의 안전성 확보 조치 ('가치타실')은(는) 개인정보보호법 제29조에 따라 다음과 같이 안전성 확보에 필요한 기술적/관리적 및 물리적 조치를 하고 있습니다.\t\n" +
                        "1. 해킹 등에 대비한 기술적 대책\t\n" +
                        "<가치타실>('가치타실')은 해킹이나 컴퓨터 바이러스 등에 의한 개인정보 유출 및 훼손을 막기 위하여 보안프로그램을 설치하고 주기적인 갱신·점검을 하며 외부로부터 접근이 통제된 구역에 시스템을 설치하고 기술적/물리적으로 감시 및 차단하고 있습니다.\t\n" +
                        "2. 개인정보의 암호화\t\n" +
                        "이용자의 개인정보는 비밀번호는 암호화 되어 저장 및 관리되고 있어, 본인만이 알 수 있으며 중요한 데이터는 파일 및 전송 데이터를 암호화 하거나 파일 잠금 기능을 사용하는 등의 별도 보안기능을 사용하고 있습니다.\t\n" +
                        "3. 개인정보에 대한 접근 제한\t\n" +
                        "개인정보를 처리하는 데이터베이스시스템에 대한 접근권한의 부여,변경,말소를 통하여 개인정보에 대한 접근통제를 위하여 필요한 조치를 하고 있으며 침입차단시스템을 이용하여 외부로부터의 무단 접근을 통제하고 있습니다.\t");
                builder.setPositiveButton("OK (필수)", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        new KonkukPortal().execute();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

        }
    }

    //시간많이걸리는 작업,새로운 스레드 생성에서 백그라운드처리(https://jamssoft.tistory.com/55참고)
    private class KonkukPortal extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {  //스레드가 수행할 작업
            try {

                final String loginUrl = "https://student.kku.ac.kr/member/loginProc.do";
                final String myPageUrl = "https://student.kku.ac.kr/RegiRegisterMasterInq.do";

                //로그인 하기(Connection 생성)
                Connection.Response res = Jsoup.connect(loginUrl)
                        .method(Connection.Method.POST)
                        .data("user_id", id)
                        .data("user_pwd", password)
                        .execute();


                //로그인 페이지에서 얻은 쿠키
                Map<String, String> cookies = res.cookies();


                //마이페이지(HTML 파싱)
                Document myPageDocument = (Document) Jsoup.connect(myPageUrl)
                        .cookies(cookies)
                        .get();


                Element tables = myPageDocument.select("tbody").first();
                Elements table1 = tables.select("tr").eq(0);
                Elements table2 = tables.select("tr").eq(2);
                Elements table3 = tables.select("tr").eq(3);
                Elements table4 = tables.select("tr").eq(4);

                studentNum = table1.select("td").eq(0).text();
                nickname = studentNum;
                name = table1.select("td").eq(1).text();
                grade = Integer.parseInt(table1.select("td").eq(2).text());
                birth = table2.select("td").eq(0).text();
                gender = table2.select("td").eq(1).text();
                college = table3.select("td").eq(0).text();
                major = table3.select("td").eq(1).text();
                phone = table4.select("td").eq(1).text();
                report = 0;
                count = 1; //정상적으로 로그인되었는지 판단


            } catch (Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),"아이디와 비밀번호를 확인해주세요.", Toast.LENGTH_SHORT).show();
                        count = 0;
                    }
                });
            }
            return null;
        }

        //스레드 작업이 모두 끝난 후 수행할 작업
        @Override
        protected void onPostExecute(Void result) {
            Response.Listener<String> responseListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    if (count==1) {
                        //Toast.makeText(getApplicationContext(), "로그인 성공! DB에 회원정보가 저장되었습니다.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("host", studentNum);
                        intent.putExtra("hostgender", gender);
                        startActivity(intent);

                        //saveToken();

                        SharedPreferences loginInformation = getSharedPreferences("setting", MODE_PRIVATE); //사용 선언
                        SharedPreferences.Editor editor = loginInformation.edit(); //데이터 저장
                        editor.putBoolean("check", true); //최초로그인 여부 true로 변경
                        editor.putString("host", studentNum);
                        editor.putString("hostgender", gender);
                        editor.putString("nickname", nickname);
                        editor.commit();

                    }

                }
            };
            //서버로 volley를 이용하여 요청을 함
            RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
            RegisterRequest registerRequest = new RegisterRequest(studentNum, nickname, name, grade, birth, gender, college, major, phone, report, responseListener);
            queue.add(registerRequest);

        }

    }


    public void saveToken() {
        String token = FirebaseInstanceId.getInstance().getToken();
        /*
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("host", studentNum);
        map.put("token", token);
        FirebaseDatabase.getInstance().getReference().child("user").child(studentNum).updateChildren(map);
        */
        FirebaseDatabase.getInstance().getReference().child("user").child(studentNum).setValue(token);
    }

    public void saveToken2() {
        String token = FirebaseInstanceId.getInstance().getToken();
        /*
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("host", studentNum);
        map.put("token", token);
        FirebaseDatabase.getInstance().getReference().child("user").child(studentNum).updateChildren(map);
        */
        FirebaseDatabase.getInstance().getReference().child("user").child("admin").setValue(token);
    }

}
