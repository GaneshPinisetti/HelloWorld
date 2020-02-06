package com.invista.file.util;

import java.io.File;

public class FileUtil {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public static void createDirStructure(String path){
		File file = null;		
		 try{
			 file = new File(path);
			 if(!file.exists()){
				 file.mkdirs();
				 System.out.println(file.getAbsolutePath()+ " Created");
			 }
			 else{
				 System.out.println("Directory Exist");
			 }
			 
		 }
		 catch(Exception e){
			 e.printStackTrace();
		 }
		 finally{
			 if(file!=null){
				 file = null;
			 }
		 }
	}

}
