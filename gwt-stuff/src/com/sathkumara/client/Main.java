package com.sathkumara.client;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.HeadElement;
import com.google.gwt.dom.client.LinkElement;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.RootPanel;
import com.sathkumara.client.ProgressBar.Type;
import com.sathkumara.client.ValueListBox.ItemDisplay;

public class Main implements EntryPoint {

	final Logger rootLogger = Logger.getLogger("");
	
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
		
		initCSS();
		
		
		panel.add(new Spinner());
		
		TimePicker picker = new TimePicker();
		panel.add(picker);
		
		FavoriteCheckBox favbox = new FavoriteCheckBox();
		panel.add(favbox);
		
		final ProgressBar bar = new ProgressBar(Type.DYNAMIC);
		panel.add(bar);
		
		final List<String> items = Arrays.asList("Widget", "GWT", "WebApp", "AJAX");
		ValueListBox<String> listbox = new ValueListBox<String>(new ItemDisplay<String>() {
			@Override public String getDisplay(String item) {
				return item;
			}
			@Override public String getValue(String item) {
				return "item#" + items.indexOf(item);
			}
		}, true);
		listbox.addItems(items);
		panel.add(listbox);
		
		
		////Value change logging
		
		picker.addValueChangeHandler(new ValueChangeHandler<Long>() {
			@Override public void onValueChange(ValueChangeEvent<Long> event) {
				rootLogger.info("Timepicker picked: " + event.getValue());
				bar.setPercentage((int) (100*event.getValue()/86400000L));
			}
		});
		favbox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override public void onValueChange(ValueChangeEvent<Boolean> event) {
				rootLogger.info("FavoriteCheckBox picked: " + event.getValue());
			}
		});
		listbox.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override public void onValueChange(ValueChangeEvent<String> event) {
				rootLogger.info("ValueListBox picked: " + event.getValue());
				
			}
		});
	}
	
	private void initCSS() {
		LinkElement style = Document.get().createLinkElement();
		style.setAttribute("rel", "stylesheet");
		style.setAttribute("href", GWT.getModuleBaseURL() + "styles.css");
		getHead().appendChild(style);
	}
	
	private HeadElement head;
	private HeadElement getHead() {
		if (head == null) {
			Element elt = Document.get().getElementsByTagName("head").getItem(0);
			assert elt != null : "ERROR: <head> element not found!";
			head = HeadElement.as(elt);
		}
		return head;
	}
}
