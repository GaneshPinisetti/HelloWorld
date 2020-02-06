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
import org.apache.poi.ss.usermodel.ExcelStyleDateFormatter;

import com.documentum.com.DfClientX;
import com.documentum.com.IDfClientX;
import com.documentum.fc.client.DfQuery;
import com.documentum.fc.client.IDfClient;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfQuery;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSessionManager;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.IDfId;
import com.documentum.fc.common.IDfLoginInfo;
import com.invista.file.util.ReadPropertiesFile;

public class Testing {

	// final static Logger logger = Logger.getLogger(ReportWithObjectID.class);

	// below method for read the object ids form text file
	public static Set getObjectIDs(String fileName) {
		// logger.info("##### getObjectIDFromText is started");
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
		// logger.info("##### End of methode getObjectIDsFromText ");
		return objectIdSet;
	}

	// @SuppressWarnings("resource")
	public static void main(String[] args) throws Exception {
		BasicConfigurator.configure();

		// logger.info("#####generating reports");
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
		System.out.println(successpath);
		String failpath = appProperties.getProperty("failpath");

		Set objectids = new HashSet();
		IDfQuery reportquery = null;
		IDfQuery folderquery = null;
		IDfSessionManager sessionManager = null;
		IDfSession session = null;

		FileWriter successfile = null;
		FileWriter failfile = null;
		String objectid = null;
		try {
			successfile = new FileWriter(successpath);
			failfile = new FileWriter(failpath);
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
			// logger.info("#####session is created " + session);
			String filename = appProperties.getProperty("filename");
			objectids = getObjectIDs(filename);
			Iterator it = objectids.iterator();
			int x = 0;
			while (it.hasNext()) {
				x++;
				System.out.println("Object no:" + x);
				objectid = it.next().toString();
				System.out.println("Object ID:" + objectid);
				// logger.info("#####Object ID:"+objectid);
				try {

					/*
					 * String
					 * query="select r_object_id,object_name,i_folder_id,r_content_size,r_object_type,r_creation_date,r_modify_date,r_version_label,r_authors,area,bpf_no,building,proj_no,rev_no,sbu,site,systems,contractor,eng_function,dwg_type,en_no from drawings(all) where r_object_id='"
					 * +objectid+"'";
					 */

					// successfile.write("\r\n");

					String folderrepoquery = "select r_object_id,r_folder_path from dm_folder where r_object_id='"
							+ objectid + "'";
					folderquery = new DfQuery();
					folderquery.setDQL(folderrepoquery);
					IDfCollection colfolderInfo = folderquery.execute(session, IDfQuery.DF_READ_QUERY);
					while (colfolderInfo.next()) {
						String folderpath = colfolderInfo.getString("r_folder_path");
						successfile.write(folderpath + "|");
						// successfile.write("\r\n");
						System.out.println(folderpath);

					}

				} catch (Exception e) {

				}

			}
		} catch (Exception e) {
			// logger.error("objectid does not exist");
			failfile.write(objectid);
			failfile.write("\r\n");
			e.printStackTrace();
		}

	}
}
