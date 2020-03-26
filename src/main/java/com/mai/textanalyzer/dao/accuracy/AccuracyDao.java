/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mai.textanalyzer.dao.accuracy;

import com.mai.textanalyzer.classifier.common.ClassifierEnum;
import com.mai.textanalyzer.indexing.common.IndexerEnum;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

/**
 *
 * @author Sergey
 */
public class AccuracyDao implements IAccuracyDao {
    private static final Logger log = LoggerFactory.getLogger(AccuracyDao.class.getName()); 
    private NamedParameterJdbcTemplate jdbcTemplate;

    public void setJdbcTemplate(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void deleteAllDataFromAccuracy() {
        String sql = "DELETE FROM accuracy";
        jdbcTemplate.update(sql, new MapSqlParameterSource());
        jdbcTemplate.query("Select * FROM accuracy ", new ResultSetExtractor<Object>() {
            @Override
            public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
                return null;
            }
        });
    }

    @Override
    public List<Accuracy> getListAccuracy(ClassifierEnum classifierEnum, IndexerEnum indexerEnum) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ACC.* ,TPK.NAME as NAME_TOPIC  FROM ( ")
                .append("SELECT * FROM ACCURACY WHERE ")
                .append(" VECTORIZATION_ID = :vectorization_id ")
                .append(" AND CLASSIFIER_ID = :classifier_id ) ACC ")
                .append(" LEFT OUTER JOIN TOPIC as TPK ON TPK.TOPIC_ID=ACC.TOPIC_ID ");
        log.info("sql.toString() = " + sql.toString());
        Map<String, Object> paramМap = new HashMap<>();
        paramМap.put("classifier_id", classifierEnum.getId());
        paramМap.put("vectorization_id", indexerEnum.getId());
        List<Accuracy> accuracys = jdbcTemplate.query(sql.toString(), paramМap, new RowMapper<Accuracy>() {
            @Override
            public Accuracy mapRow(ResultSet rs, int i) throws SQLException {
                return new Accuracy(ClassifierEnum.getClassifierEnumById(rs.getInt("CLASSIFIER_ID")), IndexerEnum.getIndexerEnumById(rs.getInt("VECTORIZATION_ID")), rs.getString("NAME_TOPIC"), rs.getDouble("ACCURACY"), rs.getInt("DOC_COUNT"));
            }
        });
        return accuracys;
    }

    @Override
    public void inserOrUpdateAccyracy(ClassifierEnum classifierEnum, IndexerEnum indexerEnum, int topic_id, int docCount, double accuracy) {
        StringBuilder sql = new StringBuilder();
        sql.append(" MERGE INTO ACCURACY ")
                .append(" (TOPIC_ID,VECTORIZATION_ID,CLASSIFIER_ID,DOC_COUNT,ACCURACY) ")
                .append(" KEY(TOPIC_ID,VECTORIZATION_ID,CLASSIFIER_ID) ")
                .append(" VALUES(:topic_id,:vectorization_id,:classifier_id,:doc_count,:accuracy) ");
        log.info("sql.toString() = " + sql.toString());
        Map<String, Object> paramМap = new HashMap<>();
        paramМap.put("topic_id", topic_id);
        paramМap.put("vectorization_id", indexerEnum.getId());
        paramМap.put("classifier_id", classifierEnum.getId());
        paramМap.put("doc_count", docCount);
        paramМap.put("accuracy", accuracy);
        jdbcTemplate.update(sql.toString(), paramМap);
    }

}
