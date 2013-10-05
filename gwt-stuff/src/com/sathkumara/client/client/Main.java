package com.sathkumara.client.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;
import com.google.gwt.user.client.ui.RootPanel;

public class Main implements EntryPoint {

	@Override
	public void onModuleLoad() {
		GWT.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			
			@Override
			public void onUncaughtException(Throwable e) {
				GWT.log("ERROR", e);
				e.printStackTrace();
			}
		});
		
		RootPanel panel = RootPanel.get("container");
		
		panel.add(new Spinner());
		panel.add(new TimePicker());
		panel.add(new FavoriteCheckBox());
	}

}
