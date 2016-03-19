package com.hp.automation.tutorials.fileops;

import java.io.File;
import java.io.IOException;

public class Relpath {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		/*File tempfile = new File("user.dir/tmp", "tempfile.txt");
		
		 try {
			if (tempfile.createNewFile()){
			        System.out.println("File is created!");
			      }else{
			        System.out.println("File already exists.");
			      }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
//		File dir = new File("tmp/test");
		File dir = new File("tmp");
		
		if(dir.exists()) {
			System.out.println("directory exists");
		} else {
			dir.mkdirs();
			
		}
		
		File tmp = new File(dir, "tmp.txt");
		try {
			tmp.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
