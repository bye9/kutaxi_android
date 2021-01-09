package com.project.kutaxi;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

//대화방 생성 정보를 PHP 서버에 보내서 DB에 저장시킨다.
public class CreateRequest extends StringRequest {

    //서버 URL 설정 (PHP 파일 연동)
    final static private String URL="http://kutaxi.dothome.co.kr/create_carpool.php";
    private Map<String, String> map;

    //생성자, 네트워크의 응답은 listener에 담아서 처리(volley라이브러리에서 요청에 대한 응답처리의 콜백)
    public CreateRequest(String select_date, String start_time, String end_time, String route,
                         int current_member, int member, String content, int gender, String host, String hostgender, Response.Listener<String> listener) {
        //GET(멱등하다)은 해당 요청을 몇번을 수행해도  해당 요청에 대한 결과가 계속 동일하게 돌아오는 것을 의미하며,
        //POST는 해당 요청이 수행되면 서버에서 무언가 바뀌고, 동일한 결과가 돌아오는 것을 보장할 수 없다는 것을 의미한다.
        super(Method.POST, URL, listener, null);//위 url에 post방식으로 값을 전송
        //부모클래스의 생성자를 호출하기 위한 메소드

        map = new HashMap<>();
        map.put("select_date", select_date);
        map.put("start_time", start_time);
        map.put("end_time", end_time);
        map.put("route", route);
        map.put("current_member", current_member + "");
        map.put("member", member + "");
        map.put("content", content);
        map.put("gender", gender + "");
        map.put("host", host);
        map.put("hostgender", hostgender);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}