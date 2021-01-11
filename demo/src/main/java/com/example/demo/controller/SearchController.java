package com.example.demo.controller;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.example.demo.util.SystemMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SearchController {

    @Resource
    private SystemMapper systemMapper;

    @Autowired
    ObjectMapper objectMapper;

    @RequestMapping(value = { "collectinfo/{username}/{keyword}" }, method = RequestMethod.GET)
    public void Get(@PathVariable String username, @PathVariable String keyword) {
        if (username != null && !username.isEmpty() && keyword != null && !keyword.isEmpty()) {

            StringBuffer buf = new StringBuffer();
            buf.append("insert into user (username,keywords) values (");
            buf.append("'");
            buf.append(username);
            buf.append("','");
            buf.append(keyword);
            buf.append("')");

            // List<Map<String,Object>> list = systemMapper.selectBySql("select * from
            // user");
            systemMapper.insertBySql(buf.toString());
        }
    }

    @RequestMapping(value = { "getlist" }, method = RequestMethod.GET)
    public String Get() {

        StringBuffer buf = new StringBuffer();
        buf.append("select username, keywords, murlnum as count from (select row_number() over(partition by username order by murlnum desc) as num,* from (select count(keywords) as murlnum,username,keywords from user group by username,keywords) as source ) a where a.num=1");

        // List<Map<String,Object>> list = systemMapper.selectBySql("select * from
        // user");
        List<Map<String, Object>> list = systemMapper.selectBySql(buf.toString());

        String json = "";
        try {
            json = objectMapper.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return json;
    }
}
