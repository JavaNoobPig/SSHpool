package com.javanoob.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;



@Component
public class SessionFactory {

	@Value("${ssh.host}")
	private String Host;

	@Value("${ssh.account}")
	private String User;
	
	// A.帶密碼
	@Value("${ssh.passwd}")
	private String Password;

	@Value("${ssh.port}")
	private Integer Port;

	//B.帶private key↓下面這兩行，id_rsa放在resources下
//	@Value("${ssh.rsa.file.name}")
//	private String rsaFileName;

	private Session session;

	private JSch jsch = null;
	private int makeSessionCount=0;
	
//	private boolean waitMakeSessionFlag=true;  //是否異常，待執行重建Session(true:等待重建，false:不需重建或等別的thread重建中)
//	private boolean sessionStatusOK=false;     //Session狀態(true:正常，false:當機中)

	public synchronized void makeSession(String timer) throws RuntimeException {
		// B.帶private key↓下面這兩行rsa放在resources下
//		InputStream is = this.getClass().getResourceAsStream("../../../"+rsaFileName);
//		byte[] bb = IOUtils.toByteArray(is);
		
//		sessionStatusOK=false;  //先將Session狀態掛當
//		waitMakeSessionFlag=false;  //已有一個Thead進入重建session程序，其他的就等待重建，所以掛false
		
		try {
			if (jsch == null) {
				jsch = new JSch();
			}
			// B.private key↓
//			jsch.addIdentity("id_rsaBA", bb, null, null);
			this.session = jsch.getSession(this.User, this.Host, this.Port);
			this.session.setConfig("StrictHostKeyChecking", "no");
			// A.使用帳密連線↓
			 this.session.setPassword(this.Password);
			this.session.setTimeout(60000);

			this.session.connect();
			//Session flag
			if(session.isConnected()) {
//				waitMakeSessionFlag=false;
//				sessionStatusOK=true;
			}else {
				
				
			}
			makeSessionCount++;
			System.out.println(timer+"=> Do makeSession count: "+makeSessionCount);
		} catch (Exception e) {
//			this.waitMakeSessionFlag=true;
			throw new RuntimeException(
					"ERROR: Unrecoverable error when trying to connect to Server via account :  " + this.User, e);
			
		}

	}

	public Session getSession() {
		return this.session;
	}
//	public boolean getWaitMakeSessionFlag() {
//		return this.waitMakeSessionFlag;
//	}
//	
//	public void setWaitMakeSessionFlag(boolean waitMakeSessionFlag) {
//		this.waitMakeSessionFlag=waitMakeSessionFlag;
//	}
//	
//	public boolean isSessionStatusOK() {
//		return sessionStatusOK;
//	}
//
//	public void setSessionStatusOK(boolean sessionStatusOK) {
//		this.sessionStatusOK = sessionStatusOK;
//	}


	public void destroySession() {
		System.out.println("Inside destroySession, and session isConnected :"+this.session.isConnected());
		if(this.session!=null) {
		this.session.disconnect();
		}
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		}
	
	

}
