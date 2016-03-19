package com.hp.automation.OAuthProj.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hp.automation.OAuthProj.dao.CpgDBData;
import com.hp.automation.OAuthProj.dao.CpgDbDetails;

@Service("cpgDBServices")
public class CpgDBServices {
	
	private CpgDbDetails cpgDBdetails;
	public Object getShardUrl;
	
	
	@Autowired
	public void setCpgDBdetails(CpgDbDetails cpgDBdetails) {
		this.cpgDBdetails = cpgDBdetails;
	}



	CpgDBData getcpgdb(String printeremailaddress) {
		
		
		return cpgDBdetails.getShardUrl(printeremailaddress);
		
	}

}
