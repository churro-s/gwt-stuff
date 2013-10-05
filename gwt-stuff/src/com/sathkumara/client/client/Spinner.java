package com.sathkumara.client.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.LabelBase;

/**
 * A class that encapsulates a CSS loading image / AJAX spinner
 * generated at http://cssload.net/
 * 
 * @author Churro
 */
public class Spinner extends LabelBase<Void> {

	private static SpinnerUiBinder uiBinder = GWT.create(SpinnerUiBinder.class);
	interface SpinnerUiBinder extends UiBinder<Element, Spinner> {}

	public Spinner() {
		super(Document.get().createDivElement());
		getElement().appendChild(uiBinder.createAndBindUi(this));
	}
	
	@Override
	protected void onUnload() {
		getElement().removeFromParent();
	}
}
