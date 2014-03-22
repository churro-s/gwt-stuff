package com.sathkumara.client;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ToggleButton;
import com.sathkumara.client.bundle.Resources;

/**
 * This "CheckBox" is a Boolean field that indicates its current value with a 
 * star, highlighted for true, grayed out for false.
 * This can be used in conjunction with custom events to indicate a favorite 
 * was set or removed.
 * @author Churro
 */
public class FavoriteCheckBox extends ToggleButton {
	
	public FavoriteCheckBox() {
		super(new Image(Resources.INSTANCE.fave_inactive()), new Image(Resources.INSTANCE.fave()));
		setSize("19px", "19px");
		//Remove default ToggleButton style
		setStyleName("FavoriteCheckBox");
	}
	
}
