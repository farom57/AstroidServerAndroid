/*
 *
 * This file is part of INDIserver.
 *
 * INDIserver is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as published by
 * the Free Software Foundation.
 *
 * INDIserver is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with INDIserver.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright (C) 2012 Alexander Tuschen <atuschen75 at gmail dot com>
 *
 */
 
package de.hallenbeck.indiserver.server;

import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.IBinder;

/**
 * Android Background-Service for INDI-Server
 * @author Alexander Tuschen <atuschen75 at gmail dot com>
 *
 */
public class INDIservice extends Service {
	
	private AndroidINDIServer server;
	private static INDIservice instance = null;
	

	/**
	 * Main Code 
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		instance = this;
		
		// just start the server, no parameters are given at the moment
		server = new AndroidINDIServer(getApplicationContext());
		
		return super.onStartCommand(intent, flags, startId);
	}

	// Only in Android 4.0
	  
	/*@Override
	public void onTrimMemory(int level) {
		super.onTrimMemory(level);
	} */

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		server.stopServer();
		
		super.onDestroy();
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}
	
	public static INDIservice getInstance(){
		return instance;
	}
	
}
