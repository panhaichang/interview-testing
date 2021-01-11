package com.example.demo.controller;

import java.io.IOException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@Controller
public class CallBackController {

    @RequestMapping(value = { "github-callback" }, method = RequestMethod.GET)
    public String Get(@RequestParam("code") String code, HttpServletResponse res) {

        String url = "https://github.com/login/oauth/access_token";
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();

        map.add("client_id", "1bb6114f3ee907e7a0bd");
        map.add("client_secret", "636db85346b7518feec1cb10049978e4222fbde0");
        map.add("code", code);
        map.add("redirect_uri", "http://49.235.68.199/github-callback");
        map.add("state", "1");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        ResponseEntity<String> response = null;
        String token = "";

        try {
            response = restTemplate.postForEntity(url, request, String.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (response != null) {
            String result = response.getBody();

            String[] split = result.split("&");
            String tokenstr = split[0];
            token = tokenstr.split("=")[1];
        }
        
        String username = githubUser(token);
        if(username != null && !username.isEmpty()){
            res.addCookie(new Cookie("username",username));
            return "Index";
        }

        return "Signin failed";
    }

    public String githubUser(String accessToken) {

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("https://api.github.com/user?")
                .header("Authorization", "token " + accessToken).build();
        try {
            Response response = client.newCall(request).execute();
            String string = response.body().string();
            return string.split("\"")[3];

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }
}
