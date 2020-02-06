package com.invista.dctm.extraction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class GetMetaDataFromTextFile {
	
	public static Map getMetaDataMap(String metaDataFilePath, String attrNames){
		
		Map metaDataMap = new HashMap();;
		try{
			
			File file = new File(metaDataFilePath);
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			StringBuffer stringBuffer = new StringBuffer();
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				stringBuffer.append(line);
			}
			bufferedReader.close();
			fileReader.close();
			
			String lines[]=stringBuffer.toString().split("Exported");
			for(int i=0;i< lines.length; i++){
				//metaDataMap.put(splitMataDataByDelimiter(lines[i],"|")[0], lines[i]);
				//System.out.println("line:"+lines[i]);
				String temp = lines[i];
				//System.out.println("Temp:"+temp);
				StringTokenizer st = new StringTokenizer(temp, "|");
				String arr[] = new String[65];
				String attributeArr[] = attrNames.split(","); 
				int j=0;
				Map attrNameValueMap = new HashMap();
				while(st.hasMoreTokens()){
					arr[j]= st.nextToken();
					attrNameValueMap.put(attributeArr[j],arr[j]);
					System.out.println("Attribute ="+attributeArr[j]+":"+arr[j]);
					j++;
				}				
				System.out.println("Object ID:"+arr[0]+" No of Attr:"+attrNameValueMap.size());
				metaDataMap.put(arr[0], attrNameValueMap);
				System.out.println("Input Attributes:"+attributeArr.length);
			}
			System.out.println("Size"+metaDataMap.size());
			
			return metaDataMap;
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{			
			return metaDataMap;
		}
		
	}
	
 
public static String getObjectMetadataFromTextFile(String objectId,String objectType, String metadataFolderPath){
	StringBuffer stringBuffer = new StringBuffer();
	String metadata = "";
	try{
		File file = new File(metadataFolderPath+"/"+objectType+"/"+objectId+"_metadata.txt");
		
		if(file.exists()){
		FileReader fileReader = new FileReader(file);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		
		String line;
		while ((line = bufferedReader.readLine()) != null) {
			stringBuffer.append(line);
		}
		metadata = stringBuffer.toString();
		bufferedReader.close();
		fileReader.close();
		
		}
		
		else{
			metadata ="NO METADATA";
			System.out.println("File Not Available:"+metadataFolderPath+"/"+objectType+"/"+objectId+"_metadata.txt");
		}
		return metadata;
	}
	catch(Exception e){
		e.printStackTrace();
	}
	finally{
		//return stringBuffer.toString();
	}
	return stringBuffer.toString();
}
 public static void main(String ar[]){
	 String attrNames = "r_object_id,object_name,i_folder_id,i_cabinet_id,r_content_size,r_version_label,title,subject,authors,keywords,resolution_label,owner_name,owner_permit,group_name,group_permit,world_permit,log_entry,acl_domain,acl_name,language_code,orig_date,rev_date,rev_no,sbu,security_level,contractor,attribute_status,migrated,original_location,physical_status,psm,status_date,transfer_to_orgnztn,under_litigation,borrower,borrower_date,borrower_location,bpf_sheet_status,dwg_status,media_type,cae_system,intergraph_id,func_folder,r_object_type,r_creation_date,r_modify_date,a_content_type,migration,area,bpf_no,building,en_no,keyterm,physical_location,po_no,proj_no,sbu,site,subsystem,systems,eng_function,departments,proj_title,ref_bpf_no,ref_spec_no,dwg_type,ref_dwg_no,superceded_dwg_no";
	// getMetaDataMap("C:/Invista Extraction/metadata/test.txt",attrNames);
	 
	String metadata = getObjectMetadataFromTextFile("090008868005bb01","dm_document","C:/Invista Extraction/metadata");
	
	StringTokenizer st =new StringTokenizer(metadata,"|");
	System.out.println("Tokens :"+st.countTokens());
	while(st.hasMoreTokens()){
		System.out.println(st.nextToken());
	}
 }
}

