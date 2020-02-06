package com.invista.dctm.application;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import com.documentum.com.DfClientX;
import com.documentum.com.IDfClientX;
import com.documentum.fc.client.IDfClient;
import com.documentum.fc.client.IDfFolder;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSessionManager;
import com.documentum.fc.common.IDfLoginInfo;
import com.invista.file.util.ReadPropertiesFile;

public class CreatingFolders {
	//final static Logger logger = Logger.getLogger(CreatingFolders.class);
	// below method for read the get folder paths form text file
	public static Set getObjectIDs(String fileName) {
		//logger.info("##### creating folders");
		Set objectIdSet = null;
		try {
			objectIdSet = new HashSet();
			File file = new File(fileName);
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			int i = 0;
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				i++;
				// System.out.println(line);
				objectIdSet.add(line);
			}
			System.out.println(i);
			System.out.println(objectIdSet.size());
			bufferedReader.close();
			fileReader.close();

			// return objectIdSet;
		} catch (Exception e) {
			e.printStackTrace();

		}
		//logger.info("##### End of methode get folder paths ");
		return objectIdSet;
	}
	
public static void main(String[] args) throws Exception {
	
	
	BasicConfigurator.configure();

	//logger.info("#####generating reports");
	String repository = "";
	String userName = "";
	String password = "";
	String appPropFileName = "application.properties";

	// The below code is used to read data form the application.properties file and
	// supply properties file name as parameter
	Properties appProperties = ReadPropertiesFile.readProperties(appPropFileName);
	repository = appProperties.getProperty("repository");
	userName = appProperties.getProperty("username");
	password = appProperties.getProperty("password");
	String successpath = appProperties.getProperty("successpath");
	String failpath = appProperties.getProperty("failpath");

	Set objectids = new HashSet();
	IDfSessionManager sessionManager = null;
	IDfSession session = null;
	String path=null;
	FileWriter successfile = null;
	FileWriter failfile = null;
	
	try {
		successfile = new FileWriter(successpath,true);
		failfile = new FileWriter(failpath,true);
		// creating the IDFClientx object
		IDfClientX clientx = new DfClientX();
		// Getting local client from IDFClientx object
		IDfClient localClient = clientx.getLocalClient();
		System.out.println("before session manager");
		// creating SessionManager from IDFClient
		sessionManager = localClient.newSessionManager();
		// getting IDFLoginInfo object from IDFClientX
		IDfLoginInfo logininfo = clientx.getLoginInfo();

		logininfo.setUser(userName);
		logininfo.setPassword(password);
		logininfo.setDomain("");
		sessionManager.setIdentity(repository, logininfo);
		System.out.println("before session");
		// Getting session from sessionManager
		session = sessionManager.getSession(repository);
		System.out.println("session is created");
		System.out.println("session is created " + session);
		//logger.info("#####session is created " + session);
		String filename = appProperties.getProperty("filename");
		objectids = getObjectIDs(filename);
		Iterator it = objectids.iterator();
		
		while (it.hasNext()) {
			try{
			// geting the folderpath
			 path = it.next().toString();
			
			
	IDfFolder folder = session.getFolderByPath(path);
	if(folder == null) {
	String[] dirs=path.split("/");
    // loop through path folders and build
    String dm_path = "";
    for (int i=0; i<dirs.length; i++) {

        if (dirs[i].length() > 0) {

            // build up path
            dm_path = dm_path + "/" + dirs[i];

            // see if this path exists
            IDfFolder testFolder = (IDfFolder) session.getObjectByQualification("dm_folder where any r_folder_path='" + dm_path + "'");
            if (null == testFolder) {

                // check if a cabinet need to be made
                if (dm_path.equalsIgnoreCase("/" + dirs[i])) {
                    IDfFolder cab = (IDfFolder) session.newObject("dm_cabinet");
                    cab.setObjectName(dirs[i]);
                    cab.save();
                 // else make a folder
                 } else {
                     folder = (IDfFolder) session.newObject("dm_folder");
                     folder.setObjectName(dirs[i]);

                     // link it to parent
                     String parent_path = "";
                     for (int j=0; j < i; j++) {
                        if (dirs[j].length() > 0) {
                            parent_path = parent_path + "/" + dirs[j];
                        }
                     }
                 folder.link(parent_path);
                 folder.save();
		
	
                }
            }
         }
    
	}
	
	System.out.println("folder is created");
	
	}
	successfile.write(path+"|");
	successfile.write("\r\n");
	}catch(Exception e){
	failfile.write(path+"|");
	System.out.println("folder is not created");
	failfile.write("\r\n");
	continue;
	}
	
	}
		//logger.info("folder created");
	}catch (Exception e) {
	e.printStackTrace();
	failfile.write(path+"|");
	//logger.info("folder not created");
	}finally{
		successfile.close();
		failfile.close();
		sessionManager.release(session);
	}
	
}
}
