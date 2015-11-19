package application.util;

import java.io.File;
import java.io.FileInputStream;
import java.util.concurrent.Callable;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public abstract class SystemUpdater implements Callable<Boolean> {

	private static final int SSH_PORT = 22;
	private static final int TIMEOUT = 2000;
	private static final String HOME = "/home/";
	private static final String WORKSPACE = "/workspace/";

	private File updateSystemFile;

	private Session session;	
	private String sysPath;

	public SystemUpdater(String host, String user, String passwd, File updateSystemFile) {
		super();
		this.updateSystemFile = updateSystemFile;
		this.sysPath = HOME + user + WORKSPACE;

		try {
			session = new JSch().getSession(user, host, SSH_PORT);
			session.setPassword(passwd);
			session.setConfig("StrictHostKeyChecking", "no");
		} catch (JSchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public Boolean call() throws Exception {
		// TODO Auto-generated method stub
		session.connect();
		Channel channel = session.openChannel("sftp");
		ChannelSftp channelSftp = (ChannelSftp) channel;
		channelSftp.connect(TIMEOUT);

		channelSftp.put(new FileInputStream(updateSystemFile), sysPath + updateSystemFile.getName(), ChannelSftp.OVERWRITE);

		channelSftp.disconnect();
		session.disconnect();
		succeed();
		
		return true;
	}
	
	abstract public void succeed();

}
