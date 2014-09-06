package farom.astroiddriver.aoa;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import laazotea.indi.Constants.SwitchStatus;
import laazotea.indi.INDIException;
import laazotea.indi.driver.INDITextElementAndValue;
import laazotea.indi.driver.INDITextProperty;
import com.android.future.usb.UsbAccessory;
import com.android.future.usb.UsbManager;

import android.os.ParcelFileDescriptor;
import android.util.Log;

import de.hallenbeck.indiserver.server.INDIservice;
import farom.astroiddriver.INDIAstroidDriver;
import farom.astroiddriver.StatusMessage;

/**
 * @author farom
 * 
 */
public class INDIAstroidDriverAOA extends INDIAstroidDriver implements Runnable {

	private FileInputStream inputStream;
	private FileOutputStream outputStream;
	private UsbManager manager;
	private UsbAccessory accessory;
	private ParcelFileDescriptor fileDescriptor;
	private Thread accessoryThread;
	private boolean continueAccessoryThread;
	


	/**
	 * @param inputStream
	 * @param outputStream
	 */
	public INDIAstroidDriverAOA(InputStream inputStream, OutputStream outputStream) {
		super(inputStream, outputStream);

	}

	/**
	 * Called when a new Text Vector message has been received from a Client.
	 * 
	 * @param property
	 *            The Text Property asked to change.
	 * @param timestamp
	 *            The timestamp of the received message
	 * @param elementsAndValues
	 *            An array of pairs of Text Elements and its requested values to
	 *            be parsed.
	 */
	@Override
	public void processNewTextValue(INDITextProperty property, Date date, INDITextElementAndValue[] elementsAndValues) {
		super.processNewTextValue(property, date, elementsAndValues);

	}

	@Override
	public void driverConnect(Date timestamp) throws INDIException {
		Log.i("accessory", "Connecting ...");

		try {
			manager = UsbManager.getInstance(INDIservice.getInstance());
			UsbAccessory[] accessoryList = manager.getAccessoryList();
			if (accessoryList == null) {
				Log.e("accessory", "accessory not found");
				throw new INDIException("The accessory is not connected");
			}
			accessory = accessoryList[0];

			Log.i("accessory", "");
			Log.i("accessory", "Accessory info:");
			Log.i("accessory", " - manufacturer: " + accessory.getManufacturer());
			Log.i("accessory", " - model: " + accessory.getModel());
			Log.i("accessory", " - version: " + accessory.getVersion());
			Log.i("accessory", " - description: " + accessory.getDescription());
			Log.i("accessory", " - serial: " + accessory.getSerial());
			Log.i("accessory", " - URL: " + accessory.getUri());
			Log.i("accessory", "");

			if (!manager.hasPermission(accessory)) {
				Log.i("accessory", "Acces denied");
				return;
			}

			Log.i("accessory", "Acces granted");

			fileDescriptor = manager.openAccessory(accessory);
			if(fileDescriptor!=null){
			FileDescriptor fd = fileDescriptor.getFileDescriptor();
			inputStream = new FileInputStream(fd);
			outputStream = new FileOutputStream(fd);
			accessoryThread = new Thread(null, this, "AccessoryThread");
			continueAccessoryThread = true;
			accessoryThread.start();
			

			onConnected();
			}else{
				Log.e("accessory", "fileDescriptor = null");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		

	}

	@Override
	public void driverDisconnect(Date timestamp) throws INDIException {
		try {
			continueAccessoryThread = false;
			if(fileDescriptor!=null){
				fileDescriptor.close();
			}
			accessory = null;
			fileDescriptor = null;
			inputStream = null;
			outputStream = null;
			
			Log.i("accessory", "Accessory disconected");
		} catch (IOException e) {
			e.printStackTrace();
		}
		onDisconnected();

	}

	/**
	 * Send the current command message to the device
	 */
	@Override
	protected void sendCommand() {
		try {
			outputStream.write(command.getBytes());
		} catch (IOException e) {
			Log.e("accessory", e.getMessage());
		}
	}

	@Override
	public void run() {
		int ret = 0;
		byte[] buffer = new byte[16384];
		int i=0;;
		while (ret >= 0 && continueAccessoryThread) {
			try {
				ret = inputStream.read(buffer);
			} catch (IOException e) {
				e.printStackTrace();
				break;
			}

			i = 0;

			while (i < ret) {
				if (i + StatusMessage.MESSAGE_SIZE < ret) {
					i++;
					break;
				}

				byte[] temp = new byte[StatusMessage.MESSAGE_SIZE];
				for (int j = 0; j < StatusMessage.MESSAGE_SIZE; j++) {
					temp[j] = buffer[i + j];
				}

				if (StatusMessage.verify(temp)) {
					lastStatusMessage = new StatusMessage(buffer);
					updateStatus();
				}

				i += StatusMessage.MESSAGE_SIZE;
			}

		}

		try {
			getConnectionProperty().getElement("DISCONNECT").setValue(SwitchStatus.ON);
			updateProperty(getConnectionProperty());
			driverDisconnect(null);
		} catch (INDIException e) {
			e.printStackTrace();
		}

	}
	



}