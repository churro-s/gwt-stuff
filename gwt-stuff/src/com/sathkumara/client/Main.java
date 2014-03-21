package com.sathkumara.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.HeadElement;
import com.google.gwt.dom.client.LinkElement;

public class Main implements EntryPoint {
	
	private HeadElement head;
	private HeadElement getHead() {
		if (head == null) {
			Element elt = Document.get().getElementsByTagName("head").getItem(0);
			assert elt != null : "ERROR: <head> element not found!";
			head = HeadElement.as(elt);
		}
		return head;
	}
	
	/**
	 * Attach the necessary stylesheet to the host page.
	 */
	private void initCSS() {
		LinkElement style = Document.get().createLinkElement();
		style.setAttribute("rel", "stylesheet");
		style.setAttribute("href", GWT.getModuleBaseURL() + "styles.css");
		getHead().appendChild(style);
	}
	
	@Override
	public void onModuleLoad() {
		initCSS();
	}
	
}
