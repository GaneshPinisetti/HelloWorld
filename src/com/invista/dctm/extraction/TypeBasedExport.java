package com.invista.dctm.extraction;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;

import com.documentum.com.DfClientX;
import com.documentum.com.IDfClientX;
import com.documentum.fc.client.DfQuery;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfQuery;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfId;
import com.documentum.fc.common.IDfId;
import com.documentum.operations.IDfExportNode;
import com.documentum.operations.IDfExportOperation;
import com.invista.file.util.FileUtil;

public class TypeBasedExport {

	private static Logger log = Logger.getLogger(TypeBasedExport.class);

	public static Set getObjectIdSetByDateRange(IDfSession session, String startDate, String endDate,
			String objectType) {
		IDfQuery qrySelectObjectIdByDateRange = null;
		Set objectIds = null;

		// String strSelectObjectIdByDateRangeQry ="SELECT r_object_id FROM
		// dm_document(all) where r_creation_date >= DATE('04/09/2018
		// 00:00:00','mm/dd/yyyy hh:mm:ss') and r_creation_date <=
		// DATE('04/09/2018 23:59:59','mm/dd/yyyy hh:mm:ss')";
		/*
		 * String strSelectObjectIdByDateRangeQry = "SELECT r_object_id FROM " +
		 * objectType + "(all) where r_object_type='" + objectType +
		 * "' and r_creation_date >= DATE('" + startDate +
		 * " 00:00:00','mm/dd/yyyy hh:mm:ss') and r_creation_date <= DATE('" + endDate +
		 * " 23:59:59','mm/dd/yyyy hh:mm:ss') and NOT FOLDER ('/System','/Temp', DESCEND)"
		 * ;
		 */

		String strSelectObjectIdByDateRangeQry = "SELECT r_object_id from " + objectType
				+ " (all) WHERE r_modify_date between date('03/16/2018') and date('04/28/2018') and r_object_type='"
				+ objectType + "'";

		try {
			objectIds = new HashSet();
			qrySelectObjectIdByDateRange = new DfQuery();
			qrySelectObjectIdByDateRange.setDQL(strSelectObjectIdByDateRangeQry);

			IDfCollection colObjectId = qrySelectObjectIdByDateRange.execute(session, IDfQuery.DF_READ_QUERY);
			while (colObjectId.next()) {

				objectIds.add(colObjectId.getString("r_object_id"));
			}
			colObjectId.close();
			System.out.println("No Of Objects: " + objectIds.size());
			return objectIds;
		} catch (Exception e) {
			e.printStackTrace();
			return objectIds;
		}
	}

	static int c = 0;

	public static void exportListOfObjects(Set objectIds, String objecType, String attrNames,
			String destinationFolderPath, IDfSession session)   {
		
		try {
			FileWriter failFile = new FileWriter("E:/Wilton/failfile_Exception.txt",true);
			String objectName = null;
			String objectId = null;
			Iterator itrObjIds = objectIds.iterator();
			// Create Text file
			// FileWriter writer = new
			// FileWriter("E:/Export/Inputfiles/"+objecType+"_metadata.txt", true);
			FileWriter writer = new FileWriter("E:/Wilton/" + objecType + "_metadata.txt", true);

			while (itrObjIds.hasNext()) {
				c++;
				System.out.println("object no ----------------------------:" + c);
				objectId = itrObjIds.next().toString();
				try {

					System.out.println(objectId);

					StringBuffer fileName = new StringBuffer();
					IDfSysObject object = (IDfSysObject) session.getObject(new DfId(objectId));
					
					/* this line added by rajkumar */
					objectName = object.getFile(object.getObjectName());
					StringBuffer metaData = new StringBuffer();
					// metaData.append(objectId);
					// metaData.append("|"+object.getObjectName());
					// object.getTypeName();
					String attrNamesArr[] = attrNames.split(",");
					// isCheckedOut
					/*
					 * if (object.isCheckedOut()) { object.cancelCheckout(); }
					 */
					//

					for (int i = 0; i < attrNamesArr.length; i++) {
						// metaData.append(object.getString(attrNamesArr[i].trim())+"|");
						writer.write(object.getString(attrNamesArr[i].trim()) + "|");

					}
					// metaData.append(objecType+"|");
					writer.write(objecType + "|");
					// metaData.append(object.getFormat().getDOSExtension()+"|");
					// writer.write(object.getFormat().getDOSExtension()+"|");
					IDfId contentId = object.getContentsId();

					//
					boolean flag = false;
					if (!contentId.toString().equals("0000000000000000")) {

						String folderPath = destinationFolderPath + "/";
						// Create Folders if does not exists
						FileUtil.createDirStructure(folderPath);

						// export content using IDfExportOperation
						IDfClientX clientx = new DfClientX();
						IDfExportOperation operation = clientx.getExportOperation();
						operation.setDestinationDirectory(folderPath);
						// fileName.append(objectId +"."+
						fileName.append(objectName + "." + object.getFormat().getDOSExtension());
						// IDfExportOperation operation = clientx.getExportOperation();
						// object.setObjectName(fileName.toString());
						// metaData.append(folderPath+fileName.toString()+"|");
						// metaData.append("CONTENT AVAILABLE");
						writer.write(folderPath + fileName.toString() + "|");
						writer.write("CONTENT AVAILABLE");
						IDfExportNode node = (IDfExportNode) operation.add(object);
						// node.setFilePath(folderPath + fileName);

						flag = operation.execute();

					} else {
						// metaData.append("NO CONTENT");
						writer.write("NO CONTENT");
					}
					if (flag) {
						// exported
						System.out.println("Exported");
						// metaData.append("|Exported");
						writer.write("|Exported");
					} else {
						// Not exported
						System.out.println("Not Exported");
						// metaData.append("|Not Exported");
						writer.write("|Not Exported");
					}
					// System.out.println("Meta Data:"+metaData.toString());
					// writer.write(metaData.toString()+"");
					writer.write("\r\n");
				} catch (Exception ex) {
					System.out.println("catcing or not" + objectId);
					try {
						failFile.write(objectId.toString() + "|"+ex.getMessage()+"\n");
					} catch (Exception ee) {
						ee.printStackTrace();
					}

					writer.write("ERROR");
					writer.write("\r\n");
					
					ex.printStackTrace();
					continue;
				}
			}
			failFile.close();
			//writer.close();
		} catch (Exception e) {
			System.out.println("here we found");
			e.printStackTrace();
		} finally {

		}
	}
}
