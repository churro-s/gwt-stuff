package com.sathkumara.client.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ToggleButton;

/**
 * This "CheckBox" is a Boolean field that indicates its current value with a 
 * star, highlighted for true, grayed out for false.
 * @author Churro
 */
public class FavoriteCheckBox extends ToggleButton {
	
	
	//The public_html dir compiles out to here.
	public static final String imagesDir = GWT.getModuleBaseURL();
	
	public FavoriteCheckBox() {
		super(new Image(imagesDir + "fave-inactive.png"), new Image(imagesDir + "fave.png"));
		setSize("19px", "19px");
		setStyleName("ehr-FavoriteCheckBox"); //Remove default ToggleButton style
	}
	
}
