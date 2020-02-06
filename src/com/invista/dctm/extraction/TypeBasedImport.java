package com.invista.dctm.extraction;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;



import java.io.BufferedReader;
//import java.io.File;
import java.io.FileReader;
//import java.util.HashSet;
import java.util.*;
//import java.util.StringTokenizer;

import org.apache.log4j.Logger;


import com.documentum.com.DfClientX;
import com.documentum.com.IDfClientX;
import com.documentum.fc.client.IDfFolder;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.IDfList;
import com.documentum.operations.IDfFile;
import com.documentum.operations.IDfImportNode;
import com.documentum.operations.IDfImportOperation;

/** * 
 * Author : ravikishore karnam
 * 
 * @param args */
public class TypeBasedImport {
	private static Logger log = Logger.getLogger(TypeBasedImport.class);
//******************
public static Set getErrorObjects(String reportFilePath){
		Set objectIdSet = null;
		try{
			objectIdSet = new HashSet();
			File file = new File(reportFilePath);
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
				int i=0;
				String line=null;
				while ((line = bufferedReader.readLine()) != null) {
					i++;
					//System.out.println(line);
					objectIdSet.add(line);
					System.out.println("added input is "+line);
					
					
				}
				System.out.println("List of inputs added" +i);
				System.out.println("hashSet Size "+objectIdSet.size());
				bufferedReader.close();
				fileReader.close();
				
			
			return objectIdSet;
		}
		catch(Exception e){
			e.printStackTrace();
			return objectIdSet;
		}
		
	}
//*****************************	









	
	public static Set getObjectIdsFromContentFolder(String objectType, String contentFolderPath){
		Set objectIdSet = new HashSet();
		try{
				File typeContentFolder = new File(contentFolderPath+"/"+objectType);
				if(typeContentFolder.exists()){
					String arrObjestIds[] = typeContentFolder.list();
					
					for(int i=0;i<arrObjestIds.length; i++){						
						System.out.println(arrObjestIds[i].substring(0, arrObjestIds[i].indexOf(".")));						
						objectIdSet.add(arrObjestIds[i].substring(0, arrObjestIds[i].indexOf(".")));
					}					
				}
				System.out.println("Total Count:"+objectIdSet.size());
		}
		finally{
			
		}
		return objectIdSet;
	}
	public static Map getObjectIdFileNameMap(String objectType, String contentFolderPath){
		Map objectIdFileNameMap = new HashMap();
		try{
				File typeContentFolder = new File(contentFolderPath+"/"+objectType);
				if(typeContentFolder.exists()){
					String arrObjestIds[] = typeContentFolder.list();
					
					for(int i=0;i<arrObjestIds.length; i++){						
						//System.out.println(arrObjestIds[i].substring(0, arrObjestIds[i].indexOf(".")));	
						objectIdFileNameMap.put(arrObjestIds[i].substring(0, arrObjestIds[i].indexOf(".")), arrObjestIds[i]);
					}					
				}
				//System.out.println("Total Count:"+objectIdFileNameMap.size());
		}
		catch(Exception e){
			e.printStackTrace();
			
		}
		finally{
			
		}
		return objectIdFileNameMap;
	}
	public static Map getObjectIdFileNameMap(Set objectIds,String objectType, String contentFolderPath){
		Map objectIdFileNameMap = new HashMap();
		try{
				File typeContentFolder = new File(contentFolderPath+"/"+objectType);
				if(typeContentFolder.exists()){
					String arrObjestIds[] = typeContentFolder.list();
					
					for(int i=0;i<arrObjestIds.length; i++){						
						//System.out.println(arrObjestIds[i].substring(0, arrObjestIds[i].indexOf(".")));
						String objectId = arrObjestIds[i].substring(0, arrObjestIds[i].indexOf("."));
						if(objectIds.contains(objectId)){	
							objectIdFileNameMap.put(objectId, arrObjestIds[i]);
						}
					}					
				}
				//System.out.println("Total Count:"+objectIdFileNameMap.size());
		}
		catch(Exception e){
			e.printStackTrace();
			
		}
		finally{
			
		}
		return objectIdFileNameMap;
	}
	public static void doCustomImport(Map objectIdFileNameMap, String objectType,String attrNames,String setAttrNames,String dctmFolderPath, IDfSession session, String contentFolderPath, String metadataFolderPath ){
		
		try{
			IDfFolder folder = session.getFolderByPath(dctmFolderPath);
			if(folder != null){
				FileWriter writerReport = new FileWriter(metadataFolderPath+"/"+objectType+"REPORT.txt");
				System.out.println(folder.getObjectName());				
				
				Set keysSet = objectIdFileNameMap.keySet();
				String attrNamesArr [] = attrNames.split(",");
				String setAttrNamesArr [] = setAttrNames.split(",");
				Iterator it = keysSet.iterator();
				
				// Importing
				IDfClientX clientx = new DfClientX();	        
				String dateAttrs[] ={"status_date","orig_date","rev_date"};
				while(it.hasNext()){
					String objId =  it.next().toString();
					IDfSysObject newObject = (IDfSysObject) session.newObject(objectType);
					try{
					
					//newObject.set
					
					System.out.println(objId+" File Name:"+objectIdFileNameMap.get(objId).toString());
					String metadata = GetMetaDataFromTextFile.getObjectMetadataFromTextFile(objId,objectType,metadataFolderPath);
					
					StringTokenizer st =new StringTokenizer(metadata,"|");
					System.out.println("Tokens :"+st.countTokens());
					Map metaDataMap = new HashMap();
					int attrCounter =0;
					while(st.hasMoreTokens()){
						//System.out.println(st.nextToken());
						if(attrNamesArr.length == attrCounter ){
							break;
						}
						String str = st.nextToken();
						if(dateAttrs[0].equals(attrNamesArr[attrCounter]) || dateAttrs[1].equals(attrNamesArr[attrCounter]) || dateAttrs[2].equals(attrNamesArr[attrCounter])){
						try{
							//str = convertStringToDate(str,"mm/dd/yyyy");
						}
						catch(Exception ex){
						metaDataMap.put(attrNamesArr[attrCounter], str);						
						attrCounter++;
						 continue;
						}
						}
						metaDataMap.put(attrNamesArr[attrCounter], str);						
						attrCounter++;
					}
					//newObject.setId("i_folder_id", folder.getObjectId());
					//newObject.setContentType(metaDataMap.get("a_content_type").toString());
					newObject.setContentType("ustn");
					
					File newFile = new File(contentFolderPath+"/"+objectType+"/"+objectIdFileNameMap.get(objId).toString());
					if(newFile.exists()){
						newObject.setFile(contentFolderPath+"/"+objectType+"/"+objectIdFileNameMap.get(objId).toString());
					}
					else{
						System.out.println("NO CONTENT");
					}
					System.out.println("AFTER CONTENT");
					int setAttrCounter = 0;
					for(int i=0;i<setAttrNamesArr.length-1; i++){
						try{
						if(newObject.isAttrRepeating(setAttrNamesArr[i])){

							System.out.println("REPEAT:"+setAttrNamesArr[i]+"="+ metaDataMap.get(setAttrNamesArr[i]).toString());
							
							newObject.setRepeatingString(setAttrNamesArr[i], 0, metaDataMap.get(setAttrNamesArr[i]).toString());
						}
						else{
							System.out.println("SINGLE:"+setAttrNamesArr[i]);
							newObject.setString(setAttrNamesArr[i], metaDataMap.get(setAttrNamesArr[i]).toString());
						}
						}
						catch(Exception ex){
							ex.printStackTrace();
						 continue;
						}
						
					}
					//IDfFolder destFolder = session.getFolderByPath(metaDataMap.get("r_folder_path"));
					//System.out.println("PATH:"+metaDataMap.get("r_folder_path").toString());
					newObject.link("/Sites/Site  SINGAPORE TUAS/Drawings/13I - Instrumentation");


					newObject.save();	
					System.out.println(objId+"|"+newObject.getObjectId()+"|"+metaDataMap.get("r_folder_path"));
					writerReport.write(objId+"|"+newObject.getObjectId()+"|"+metaDataMap.get("r_folder_path"));
					writerReport.write("\r\n");
					}
					catch(Exception ex){
						ex.printStackTrace();
						writerReport.write(objId+":ERROR");						
						writerReport.write(":"+newObject.getObjectId());
						writerReport.write("\r\n");
						continue;
					}
					
				}
				writerReport.close();
			}
			
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			
		}
	}
public static String convertStringToDate(String stringDate,String dateFormat){
	 
	try{
		if(stringDate !=null && stringDate !="" && ! stringDate.equalsIgnoreCase("nulldate")){
			if(dateFormat.equalsIgnoreCase("mm/dd/yyyy")){
				String temp = stringDate.substring(0, 5);
				String day = stringDate.substring(0, 2);
				String month = stringDate.substring(3, 5);
				System.out.println("temp=========="+month+"-"+day+"-"+stringDate.substring(6, stringDate.length()));
				stringDate = month+"-"+day+"-"+stringDate.substring(6, stringDate.length());
				
			}
		}
		return stringDate;
	}

	catch(Exception e){
		e.printStackTrace();
		return stringDate;
	}
}
	public static void main(String ar[]){
		Map idFileNameMap = getObjectIdFileNameMap("dm_document","C:/Invista Extraction/content");
		Set keysSet = idFileNameMap.keySet();
		
		Iterator it = keysSet.iterator();
		while(it.hasNext()){
			String objId =  it.next().toString();
			System.out.println(objId+" File Name:"+idFileNameMap.get(objId).toString());
			
		}
		//doCustomImport(idFileNameMap,"dm_document","/abc/TestImport",)
		
		
	}
}
