package com.hp.automation.ha.data;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.hp.automation.commons.PrinterRegisterService;
import com.hp.automation.ha.appconfig.AppConfiguration;

public class PRData {
	
	private String printerEmailId;
	private String printerOauth;
	private String deRegisterUrl;
	private String listJobUrl;
	private String modelNumber;
	private String printerActiveTime;
	private String printerId;
	private String printerKey;
	private String printerSetUID;
	private String printerState;
	private String printerType;
	private String registrationPage;
	private String serialNumber;
	private String xmpp;
	private String registrationResponse;
	
	
	
	
	
	
	public PRData(AppConfiguration appConfigXml) {

		PrinterRegisterService printerRegisterService = new PrinterRegisterService(appConfigXml);
		this.registrationResponse = printerRegisterService.registerPrinter();
	}
	
	private String getRegistrationResponse() {
		return registrationResponse;
	}
	public void setRegistrationResponse(String registrationResponse) {
		this.registrationResponse = registrationResponse;
	}
	public String getPrinterEmailId() {
		return printerEmailId = getvalues("printerEmailId");
	}
	public void setPrinterEmailId(String printerEmailId) {
		this.printerEmailId = printerEmailId;
	}
	public String getPrinterOauth() {
		return printerOauth;
	}
	public void setPrinterOauth(String printerOauth) {
		this.printerOauth = printerOauth;
	}
	public String getDeRegisterUrl() {
		return deRegisterUrl;
	}
	public void setDeRegisterUrl(String deRegisterUrl) {
		this.deRegisterUrl = deRegisterUrl;
	}
	public String getListJobUrl() {
		return listJobUrl;
	}
	public void setListJobUrl(String listJobUrl) {
		this.listJobUrl = listJobUrl;
	}
	public String getModelNumber() {
		return modelNumber;
	}
	public void setModelNumber(String modelNumber) {
		this.modelNumber = modelNumber;
	}
	public String getPrinterActiveTime() {
		return printerActiveTime;
	}
	public void setPrinterActiveTime(String printerActiveTime) {
		this.printerActiveTime = printerActiveTime;
	}
	public String getPrinterId() {
		return printerId;
	}
	public void setPrinterId(String printerId) {
		this.printerId = printerId;
	}
	public String getPrinterKey() {
		return printerKey;
	}
	public void setPrinterKey(String printerKey) {
		this.printerKey = printerKey;
	}
	public String getPrinterSetUID() {
		return printerSetUID;
	}
	public void setPrinterSetUID(String printerSetUID) {
		this.printerSetUID = printerSetUID;
	}
	public String getPrinterState() {
		return printerState;
	}
	public void setPrinterState(String printerState) {
		this.printerState = printerState;
	}
	public String getPrinterType() {
		return printerType;
	}
	public void setPrinterType(String printerType) {
		this.printerType = printerType;
	}
	public String getRegistrationPage() {
		return registrationPage;
	}
	public void setRegistrationPage(String registrationPage) {
		this.registrationPage = registrationPage;
	}
	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	public String getXmpp() {
		return xmpp;
	}
	public void setXmpp(String xmpp) {
		this.xmpp = xmpp;
	}
	
	
	public String getvalues(String nodeTag) {
		SAXBuilder builder = new SAXBuilder();
		Reader reader;
		Document doc;
		String nodeValue =null;
		reader = new StringReader(registrationResponse);
		try {
			doc = builder.build(reader);
			Element root = doc.getRootElement();
			Element prn_element = root.getChild("printer");
			
			
			nodeValue = prn_element.getChildText(nodeTag);
			System.out.println("Printer Attribute requested "+ nodeTag +" = " + nodeValue);
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		System.out.println("Infosheet URL = "+infosheeturl);
		return nodeValue;
	}

	
}

// Sample output of printersimulator registration response xml
/*<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<registerPrinterResponse>
    <printer>
        <deRegisterUrl>http://localhost/PSRestUtils/v/1.0/printer/eknu95ervy224@emailinbound-test1.itcs.hp.com/unregister</deRegisterUrl>
        <listJobUrl>http://localhost/PSRestUtils/v/1.0/printer/eknu95ervy224@emailinbound-test1.itcs.hp.com/list/job</listJobUrl>
        <modelNumber>CN731A</modelNumber>
        <printerActiveTime>14400</printerActiveTime>
        <printerEmailId>eknu95ervy224@emailinbound-test1.itcs.hp.com</printerEmailId>
        <printerId>6e4bpwsxtb2hhfcnujiboa</printerId>
        <printerKey>fNjWrtWX1Xfz+H/5O0qDk9wpP/aFzArHoaW9dLTEQ28=</printerKey>
        <printerSetUID>bf5538bba5a743c79a94318125bb4eca</printerSetUID>
        <printerState>Connected</printerState>
        <printerType>VIRTUAL</printerType>
        <registrationPage>http://localhost/PSRestUtils/v/1.0/printer/eknu95ervy224@emailinbound-test1.itcs.hp.com/docid/01S65b3c7ed-70f3-4657-a587-c6d559603ab5</registrationPage>
        <responseTime>73</responseTime>
        <serialNumber>SIM11816742LZFLPK1MIS</serialNumber>
        <xmpp>xmpp1-ext-test1.itcs.hp.com</xmpp>
    </printer>
    <stack>dev2</stack>
</registerPrinterResponse>*/
