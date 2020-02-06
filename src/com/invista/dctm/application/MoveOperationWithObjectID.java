package com.invista.dctm.application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import com.documentum.com.DfClientX;
import com.documentum.com.IDfClientX;
import com.documentum.fc.client.IDfClient;
import com.documentum.fc.client.IDfEnumeration;
import com.documentum.fc.client.IDfFolder;
import com.documentum.fc.client.IDfQuery;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSessionManager;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.IDfId;
import com.documentum.fc.common.IDfLoginInfo;
import com.documentum.operations.IDfMoveNode;
import com.documentum.operations.IDfMoveOperation;
import com.invista.file.util.ReadPropertiesFile;

public class MoveOperationWithObjectID {
	final static Logger logger = Logger.getLogger(MoveOperationWithObjectID.class);

	// below method for read the object ids form text file
	public static Set getObjectIDs(String fileName) {
		logger.info("##### getObjectIDFromText is started");
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
		logger.info("##### End of methode getObjectIDsFromText ");
		return objectIdSet;
	}

	public static void main(String[] args) throws Exception {
		BasicConfigurator.configure();

		logger.info("#####generating reports");
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
		String lockreportpath= appProperties.getProperty("lockpath");
		Set objectids = new HashSet();
		IDfSessionManager sessionManager = null;
		IDfSession session = null;

		FileWriter successfile = null;
		FileWriter failfile = null;
		FileWriter lockedobjreport = null;
		try {
			successfile = new FileWriter(successpath);
			lockedobjreport = new FileWriter(lockreportpath);
			failfile = new FileWriter(failpath);
			// creating the IDFClientx object
			IDfClientX clientx = new DfClientX();
			// Getting local client from IDFClientx object
			IDfClient localClient = clientx.getLocalClient();
			// creating SessionManager from IDFClient
			sessionManager = localClient.newSessionManager();
			// getting IDFLoginInfo object from IDFClientX
			IDfLoginInfo logininfo = clientx.getLoginInfo();

			logininfo.setUser(userName);
			logininfo.setPassword(password);
			logininfo.setDomain("");
			sessionManager.setIdentity(repository, logininfo);
			// Getting session from sessionManager
			session = sessionManager.getSession(repository);
			System.out.println("session is created");
			logger.info("#####session is created " + session);
			String filename = appProperties.getProperty("filename");
			objectids = getObjectIDs(filename);
			Iterator it = objectids.iterator();
			int i=0;
			while (it.hasNext()) {
				System.out.println("object no"+i);
				// geting the line from the text file and then saparating the objectid and path
				String objectid = it.next().toString();
				StringTokenizer st = new StringTokenizer(objectid, "|");
				int arrtCount = 0;
				String data[] = new String[st.countTokens()];
				// assigning objectid and destination path to data array
				while (st.hasMoreTokens()) {
					data[arrtCount] = st.nextToken();
					arrtCount++;
				}

				System.out.println("Object ID:" + data[0]);
				logger.info("#####Object ID:" + data[0]);
				logger.error("objectd does not exist");
				try {
					IDfId id = clientx.getId(data[0]);
					// getting the object path by using object id
					IDfEnumeration paths = session.getObjectPaths(id);
					// getting the sysobject by objectid
					IDfFolder sysobject = (IDfFolder) session.getObject(id);
					System.out.println("my path"+sysobject.getFolderPath(0));
					
					String lockOwner = sysobject.getLockOwner();
					System.out.println(lockOwner);
					if(lockOwner.length() >0) {
						lockedobjreport.write(data[0]+"|");
						lockedobjreport.write(lockOwner+"|");
						lockedobjreport.write("\r\n");
						continue;
					}
					Object path = paths.nextElement();
					// converting path to string
					String srcpath = path.toString();
					System.out.println(srcpath + "*****src path");
					logger.info("#####source folder:" + srcpath);
					String despath = data[1];
					System.out.println(despath + "*****des  path");
					logger.info("#####destination folder:" + despath);
					// getting idffolder by path
					IDfFolder destFolder = session.getFolderByPath(despath);
					if (destFolder == null) {
						System.out.println("folder doesnot exist");
						continue;
					}
					// getting idffolder by path
					IDfFolder srcFolder = session.getFolderByPath(srcpath);
					// getting move operation
					IDfMoveOperation operation = clientx.getMoveOperation();
					// setting source folder path to move operation
					operation.setSourceFolderId(srcFolder.getObjectId());
					// setting destination folder path
					operation.setDestinationFolderId(destFolder.getObjectId());
					// adding sysobject to moveoperation
					IDfMoveNode node = (IDfMoveNode) operation.add(sysobject);
					// executing moveoperation
					operation.execute();
					successfile.write(data[0] + "|");
					successfile.write(data[1] + "|");
					successfile.write("\r\n");
					System.out.println("move operation is completed");
					i++;
					

				} catch (Exception e) {
					failfile.write(data[0] + "|");
					failfile.write("\r\n");
					e.printStackTrace();
					System.out.println("folder doesnot exist");
					logger.error("folder doesnot exist");
					continue;
				}

				// arrtCount++;
				// }

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			successfile.close();
			failfile.close();
			lockedobjreport.close();
			sessionManager.release(session);
		}
	}
}