package com.hp.automation.tutorials.itxt;

import java.io.File;

public class InfosheetRepository {
	
	private static String Base_Location;
	private static String Master_Location;
	private static String Output_Location;
	
	InfosheetRepository() {
		ClassLoader classLoader = InfosheetRepository.class.getClassLoader();
        File classpathRoot = new File(classLoader.getResource("").getPath());
        Base_Location = classpathRoot.getPath();
        File Masterlocation = new File(classLoader.getResource("Master/pdfs").getPath());
        Master_Location = classpathRoot.getPath();
        
        File Outputlocation = new File(classLoader.getResource("Output").getPath());
        Output_Location = classpathRoot.getPath();
	}

	public static String getBase_Location() {
		return Base_Location;
	}

	public static void setBase_Location(String base_Location) {
		Base_Location = base_Location;
	}

	public static String getMaster_Location() {
		
		return Master_Location;
	}

	public static void setMaster_Location(String master_Location) {
		Master_Location = master_Location;
	}

	public static String getOutput_Location() {
		return Output_Location;
	}

	public static void setOutput_Location(String output_Location) {
		Output_Location = output_Location;
	}

	
	

}
