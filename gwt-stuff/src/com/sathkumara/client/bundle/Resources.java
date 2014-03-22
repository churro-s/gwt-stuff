package com.sathkumara.client.bundle;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

public interface Resources extends ClientBundle {
	
	public static final Resources INSTANCE =  GWT.create(Resources.class);
	
	@Source("styles.css")
	public CssResource styles();
	
	@Source("fave_inactive.png")
	public ImageResource fave_inactive();
	
	@Source("fave.png")
	public ImageResource fave();
	
}
