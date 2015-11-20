package application.model;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class RaspberryPi implements Runnable {

	private static final String CHECK_SYSTEM_ALIVE_CMD = "ps aux | grep 'root' | grep 'java' | grep -v 'sudo' | awk '{print $2}'";
	private static final String START_SYSTEM_CMD = "~/workspace/DehumidSystem&";
	private static final String SHUT_DOWN_SYSTEM_CMD = "sudo kill ";

	private static final String CONNECTING = "連線中";
	private static final String RUNNING = "執行中";
	private static final String STOPPING = "停止";
	private static final String DISCONNECT = "斷線";

	private static final int SSH_PORT = 22;
	private static final int TIMEOUT = 20000;

	private static final TimeUnit SCAN_PERIOD_UNIT = TimeUnit.SECONDS;
	private static final int SCAN_PERIOD = 5;
	private static final int SCAN_DELAY = 0;
	
	private static final JSch jsch = new JSch();

	private StringProperty ip;
	private StringProperty status;
	private StringProperty pid;

	private Session session;

	public RaspberryPi(String host, String user, String passwd, ScheduledExecutorService executor) {
		super();
		try {
			setIP(host);
			setStatus(CONNECTING);

			session = jsch.getSession(user, host, SSH_PORT);
			session.setPassword(passwd);
			session.setConfig("StrictHostKeyChecking", "no");

			if (!executor.isShutdown())
				executor.scheduleAtFixedRate(this, SCAN_DELAY, SCAN_PERIOD, SCAN_PERIOD_UNIT);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public final void setIP(String IP) {
		Platform.runLater(() -> ipProperty().set(IP));
	}

	public final void setStatus(String status) {
		Platform.runLater(() -> statusProperty().set(status));
	}

	public final void setPid(String pid) {
		Platform.runLater(() -> pidProperty().set(pid));
	}

	public String getIP() {
		return ipProperty().get();
	}

	public String getStatus() {
		return statusProperty().get();
	}

	public String getPid() {
		return pidProperty().get();
	}

	public StringProperty ipProperty() {
		if (ip == null)
			ip = new SimpleStringProperty(this, "Invalid IP");
		return ip;
	}

	public StringProperty statusProperty() {
		if (status == null)
			status = new SimpleStringProperty(this, "Invalid Status");
		return status;
	}

	public StringProperty pidProperty() {
		if (pid == null)
			pid = new SimpleStringProperty(this, "none");
		return pid;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {			
			if (!session.isConnected())
				session.connect(TIMEOUT);
		} catch (JSchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(getIP() + " disconnect...");
			setPid("");
			setStatus(DISCONNECT);
			return;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String result = startCommand(CHECK_SYSTEM_ALIVE_CMD);
		if (result != null) {
			setPid(result);
			setStatus(RUNNING);
		} else {
			setPid("");
			setStatus(STOPPING);
		}
	}

	public void shutdown() {
		if (getPid().isEmpty())
			return;

		setCommand(SHUT_DOWN_SYSTEM_CMD + getPid());
	}

	public void restart() {
		// TODO Auto-generated method stub
		shutdown();

		if (getPid().isEmpty())
			setCommand(START_SYSTEM_CMD);
	}

	public synchronized String startCommand(String cmd) {

		byte[] buf = new byte[1024];
		String result = null;

		if (cmd == null || cmd.isEmpty())
			return result;

		try {
			Channel channel = session.openChannel("exec");

			((ChannelExec) channel).setCommand(cmd);
			channel.setInputStream(null);
			((ChannelExec) channel).setErrStream(System.err);
			InputStream in = channel.getInputStream();
			channel.connect(TIMEOUT);

			while (true) {
				while (in.available() > 0) {
					int i = in.read(buf, 0, 1024);
					if (i < 0)
						break;
					result = new String(buf, 0, i);
				}
				if (channel.isClosed()) {
					if (in.available() > 0)
						continue;
					break;
				}
				try {
					Thread.sleep(300);
				} catch (Exception ee) {
				}
			}
			channel.disconnect();

		} catch (JSchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	private synchronized void setCommand(String cmd) {
		if (cmd == null || cmd.isEmpty())
			return;

		try {
			Channel channel = session.openChannel("exec");

			((ChannelExec) channel).setCommand(cmd);
			channel.setInputStream(null);
			((ChannelExec) channel).setErrStream(System.err);
			channel.connect(TIMEOUT);
			channel.disconnect();
		} catch (JSchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
