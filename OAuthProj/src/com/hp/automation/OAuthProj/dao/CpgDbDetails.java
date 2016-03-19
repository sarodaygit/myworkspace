package com.hp.automation.OAuthProj.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;


@Component("cpgDBDetails")
public class CpgDbDetails {
	
	private NamedParameterJdbcTemplate jdbc;
	
	public void setDataSource(DataSource jdbc) {
		this.jdbc = new NamedParameterJdbcTemplate(jdbc);
	}
	
	public CpgDBData getShardUrl(String printeremailaddress) {
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("printeremailaddress", printeremailaddress);
		String sql = "Select s.jdbcUrl from Shard s, Device d where d.code = s.shardId and d.deviceEmailId=:printeremailaddress";
		
		return  jdbc.queryForObject(sql, params,
				
				new RowMapper<CpgDBData>() {

			@Override
			public CpgDBData mapRow(ResultSet rs, int rowNum)
					throws SQLException {
				CpgDBData cpgdbdata = new CpgDBData();
				
				return cpgdbdata;
			}
			
		}); 		
	}

}
