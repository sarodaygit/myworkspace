package com.hp.automation.OAuthProj.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


import com.hp.automation.OAuthProj.dao.CpgDbDetails;
import com.hp.automation.OAuthProj.services.CpgDBServices;

@Controller
public class ShardController {

	
	private CpgDBServices cpgDBServices;
	
	@Autowired
	public void setCpgDBServices(CpgDBServices cpgDBServices) {
		this.cpgDBServices = cpgDBServices;
	}
	

	@RequestMapping("/shardurl")
	public String getShardUrl() {
		Object printeremail;
		
		
		return "shardurl";
	}




}
