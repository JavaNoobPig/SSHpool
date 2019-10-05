package com.javanoob.util;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.javanoob.model.OutputdataSecVer;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

@Service
public class JschCommandExecutor {

	@Value("${ssh.max.concurrenyuser}")
	private Integer maxConCurrenyUser;

	private Integer conCurrenyUser = 0;

	@Value("${ssh.waiting.time}")
	private Integer waitingTime;

	@Value("${ssh.retry.count}")
	private Integer retryCount;

	@Autowired
	private SessionFactory sessionFactory;

	private boolean checkCurrentProcessCount() {
		if (conCurrenyUser < maxConCurrenyUser) {
			return true;
		}
		return false;
	}

	private void retryexecute(int count) {

		if (count >= retryCount) {
			System.out.println("Inside retryexecute Method:count>= retryCount");
			throw new RuntimeException("ERROR: retryexecute over retry count ");
		} else {
			System.out.println("Inside retryexecute Method: Doing " + count + " th retry");
			try {
				Thread.sleep(waitingTime);
			} catch (InterruptedException e) {
				System.out.println("Inside retryexecute Method:InterruptedException");
				throw new RuntimeException("ERROR: retryexecute waiting failed " + e.getMessage(), e);
			}

			if (this.checkCurrentProcessCount()) {
				return;
			}
			this.retryexecute(++count);
		}
	}

	private void connectCheckAndRetryConnection(ChannelExec channel, int count) throws JSchException {

		if (!channel.isConnected()) {

			try {
				channel.connect(2000); // TIMEOUT
			} catch (JSchException e) {
				System.out.println("Inside connectCheckAndRetryConnection Method:!channel.isConnected() JSchException:"
						+ e.getMessage());
				if (count >= retryCount) {
					throw e;
				}
				this.connectCheckAndRetryConnection(channel, ++count);
			}
		}
	}

	private synchronized Session getAndRetrySession(String timer) throws RuntimeException {

		Session session = sessionFactory.getSession();
		if (!session.isConnected()) {
			try {
				System.out.println(timer + "=> Session isConnected status:" + session.isConnected());
				session.connect();
			} catch (JSchException e) {
				System.out.println(timer + "=> Inside retrySession Method:catch (JSchException e):" + e.getMessage()
						+ ". It will do destroySession");
				sessionFactory.destroySession();
				sessionFactory.makeSession(timer);
				session = sessionFactory.getSession();
			} catch (Exception e) {
				System.out.println(timer + "=> Inside retrySession Method:catch (Exception e) Msg:" + e.getMessage()
						+ ". And Class type:" + e.getClass());
				e.printStackTrace();
				sessionFactory.destroySession();
				sessionFactory.makeSession(timer);
				session = sessionFactory.getSession();
			} 
		}
		return session;
	}

	public OutputdataSecVer execute(String command) {
		// 1.確認目前允許的SSH工作數量未滿載
		if (!this.checkCurrentProcessCount()) {
			try {
				// 滿載時進入retrye機制
				this.retryexecute(0);
			} catch (Exception e) {
				throw e;
			}
		}

		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:SSS");
		String timer = sdf.format(date);

		// Session session = sessionFactory.getSession();
		Session session = this.getAndRetrySession(timer);

		ChannelExec channel = null;
		OutputdataSecVer outdata = new OutputdataSecVer();

		StringBuffer result = new StringBuffer();

		// 先把拿到Channel才把工作數量+1並且取用Session連線池
		conCurrenyUser++;

		try {
			// H9000 JOB緩衝時間0.2秒，不要說maxConCurrenyUser的JOB數量同時灌進H9000
			Thread.sleep(200);
			// 由Session連線池開通Channel(通道)開始執行工作
			channel = (ChannelExec) session.openChannel("exec");
			// channel.setPty(true); //default false
			channel.setCommand(command);
			channel.setErrStream(System.err);
			// 執行SSH command前再次確認Channel狀態，並決定重連與否
			this.connectCheckAndRetryConnection(channel, 0);
			// 取得SSH command結果的InputStream
			InputStream in = channel.getInputStream();

			byte[] tmp = new byte[1024];

			while (true) {
				while (in.available() > 0) {
					int i = in.read(tmp, 0, 1024);
					if (i < 0) {
						break;
					}
					// System.out.print(new String(tmp, 0, i));
//					result = result + ;
					result.append((new String(tmp, 0, i)));
				}
				if (channel.isClosed()) {
					break;
				}
				// InputStream.read的緩衝
				Thread.sleep(1000);
			}

		} catch (JSchException e) {
			// 錯誤第一步，確認Session連線池狀態
			if (session == null || !session.isConnected()) {
				System.out.println(timer + "=> session disconnect by JSchException");
				result.delete(0, result.length());
				result.append("連線異常，JSchException，內容:" + e.getMessage());
				outdata.setStatusCode("111");
				outdata.setStatusmsg("SSH執行失敗，JSchException !!");
				outdata.setResponseData(result.toString());
				return outdata;
			}

		} catch (RuntimeException e) {
			System.out.println(timer + "=> =====RuntimeException=====");
			System.out.println(timer + "=> Session  faild and throw RuntimeException:" + e.getMessage());
			throw e;

		} catch (Exception e) {
			// 其他Exception，Exception外拋
			System.out.println(timer + "=> =====Exception=====");
			throw new RuntimeException(
					timer + "=> ERROR: Unrecoverable error when performing remote command " + e.getMessage(), e);
		} finally {
			conCurrenyUser--;
			if (null != channel && channel.isConnected()) {
				try {
					// 為解決Informix一堆process died問題，多送一個類似ctrl+C的訊號過去
					channel.sendSignal("2");
					// 同上，再送它一個kill訊號，Unix代碼9
					channel.sendSignal("9");
					// 其他訊號參考:http://people.cs.pitt.edu/~alanjawi/cs449/code/shell/UnixSignals.htm
				} catch (Exception e) {
					e.printStackTrace();
				}
				// 因為已經執行結束，從session開的通道將其中斷
				channel.disconnect();

			}
			if (!result.equals("")) {
				outdata.setStatusCode("000");
				outdata.setStatusmsg("SSH執行成功!!");
				outdata.setResponseData(result.toString());
			} else {
				outdata.setStatusCode("001");
				outdata.setStatusmsg("SSH執行成功,但是回傳空白!!");
				outdata.setResponseData("");
			}
		}
		return outdata;
	}

}
