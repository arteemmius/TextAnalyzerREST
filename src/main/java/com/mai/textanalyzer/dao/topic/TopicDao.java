/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mai.textanalyzer.dao.topic;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

/**
 *
 * @author Sergey
 */
public class TopicDao implements ITopicDao {

    private NamedParameterJdbcTemplate jdbcTemplate;

    public void setJdbcTemplate(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Topic getTopicByName(String name) {
        String sql = "Select * from TOPIC where name = :name";
        Map<String, Object> paramМap = new HashMap<>();
        paramМap.put("name", name);
        try {
            return jdbcTemplate.queryForObject(sql, paramМap, rowMapper);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public Topic getTopicByID(int id) {
        String sql = "Select * from TOPIC where TOPIC_ID = :id";
        Map<String, Object> paramМap = new HashMap<>();
        paramМap.put("id", id);
        try {
            return jdbcTemplate.queryForObject(sql, paramМap, rowMapper);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public int insertTopic(String name) {
        String sql = "INSERT INTO TOPIC (NAME) VALUES(:name)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, new MapSqlParameterSource().addValue("name", name), keyHolder);
        Number key = keyHolder.getKey();
        if (key == null) {
            throw new RuntimeException();
        }
        return key.intValue();
    }

    private final RowMapper<Topic> rowMapper = (ResultSet rs, int i) -> {
        int id = rs.getInt("topic_id");
        String name = rs.getString("name");
        return new Topic(id, name);
    };
}
