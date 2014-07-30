/**
 * 
 */
package farom.astroidserver;

import java.net.Socket;

import laazotea.indi.INDIException;
import laazotea.indi.server.DefaultINDIServer;
import farom.astroiddriver.aoa.INDIAstroidDriverAOA;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * @author farom
 * 
 */
public class INDIServerService extends Service implements Runnable{

	/**
	 * 
	 */
	public INDIServerService() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate() {
		Thread t = new Thread(this);
		t.run();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_STICKY;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void run() {
		MinimumINDIServer s = new MinimumINDIServer(); 
		
	}
	
	public class MinimumINDIServer extends DefaultINDIServer {

		  /**
		   * Just loads the available driver.
		   */
		  public MinimumINDIServer() {
		    super();

		    // Loads the Java Driver. Please note that its class must be in the classpath.
		    try {
		      loadJavaDriver(INDIAstroidDriverAOA.class);
		    } catch (INDIException e) {
		      Log.e("MinimumINDIServer","error while loading the driver");
		    }
		  }

		  
		  /**
		   * Accepts the client  (localhost).
		   *
		   * @param socket
		   * @return <code>true</code> 
		   */
		  @Override
		  protected boolean acceptClient(Socket socket) {
		      return true;
		  }
	}

}
