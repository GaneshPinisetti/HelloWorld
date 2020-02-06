package com.invista.dctm.session;


import com.documentum.com.DfClientX;
import com.documentum.com.IDfClientX;
import com.documentum.fc.client.IDfClient;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSessionManager;
import com.documentum.fc.common.IDfLoginInfo;

/**
 * Author : ravikishore karnam
 * @param args
 */
public class CreateSourceDCTMSession{
	
	
	public static IDfSessionManager smgr = null;
	public static IDfSession getSession(){
		IDfSession session = null;
		try{
		String username="dmadmin";
		String password="dmadmin";
		String repository="trainingrepo";
		IDfClientX clintx= new DfClientX();
		IDfClient clint= clintx.getLocalClient();
		smgr= clint.newSessionManager();
		IDfLoginInfo logininfo= clintx.getLoginInfo();
		logininfo.setUser(username);
		logininfo.setPassword(password);
		logininfo.setDomain("");
		smgr.setIdentity(repository, logininfo);
		
		session = smgr.getSession(repository);
         
		System.out.println("------------- session created-----------");
		

	}catch(Exception e){
		System.out.println(" "+e);
	
	}
		return session;
	}
	public static IDfSession getSession(String userName, String password, String repository){
		IDfSession session = null;
		try{
		IDfClientX clintx= new DfClientX();
		IDfClient clint= clintx.getLocalClient();
		smgr= clint.newSessionManager();
		IDfLoginInfo logininfo= clintx.getLoginInfo();
		logininfo.setUser(userName);
		logininfo.setPassword(password);
		logininfo.setDomain("");
		smgr.setIdentity(repository, logininfo);
		
		session = smgr.getSession(repository);
         
		System.out.println("-------------Source session created-----------");
		

	}catch(Exception e){
		System.out.println(" "+e);
	
	}
		return session;
	}
}