package com.sathkumara.client;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.text.shared.Parser;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.client.ui.ValueBoxBase;

public class TimePicker extends ValueBoxBase<Long> {
	
	/**
	 * The Renderer interface to convert a Long -> String
	 */
	static Renderer<Long> renderer = new Renderer<Long>() {
		@Override public String render(Long milliseconds) {
			if (milliseconds == null) {
				return "";
			}
			int minInt = (int) ((milliseconds / (1000*60)) % 60),
			    hrInt  = (int) ((milliseconds / (1000*60*60)) % 24);
			String ampm = A.AM.name();
			
			if (milliseconds >= 43200000) {
				ampm = A.PM.name();
				if (milliseconds > 43200000) {
					hrInt -= 12;
				}
			}
			if (hrInt == 0) hrInt = 12;
			return hrInt+":"+ (minInt < 10 ? "0" + minInt : minInt) +" "+ampm;
		}
		
		@Override public void render(Long object, Appendable appendable) throws IOException {
			appendable.append(render(object));
		}
	};

	/**
	 * The Parser interface to convert a String -> Long
	 */
	static Parser<Long> parser = new Parser<Long>() {
		private static final String justHr = "([1-9]|1[0-2])";
		private static final String hrAMPM = "([1-9]|1[0-2])(AM|am|PM|pm|a|p|A|P)";
		private static final String justHr24 = "([01]?[0-9]|2[0-3])";
		private static final String hrMin12 = "([1-9]|1[0-2])([0-5][0-9])";
		private static final String hrMin24 = "([01]?[0-9]|2[0-3])([0-5][0-9])";
		private static final String hrMinAMPM = "([1-9]|1[0-2])([0-5][0-9])(AM|am|PM|pm|a|p|A|P)";
		private final A defaultAmPm = A.AM;
		
		private int hrInt, minInt;//24hr time-used for value
		private Long theValue = 0L;
		

		/**
		 * Uses the internal hour, minute, and AM/PM values 
		 * to calculate a Long millisecond value.
		 */
		private void processValueChange() {
			theValue = (long) ((1000*60)* (minInt + (60*hrInt)));
		}
		
		/**
		 * Given a string with only digits, this gets the minutes from it.
		 */
		private String chopMinutesFrom(String val) {
			return val.substring(Math.max(0, val.length() - 2));
		}
		
		/**
		 * Given a string with only digits, this gets the hours from it.
		 */
		private String chopHrsFrom(String val) {
			return val.substring(0, val.length() - 2);
		}
		
		/**
		 * @return AM or PM from given String.
		 */
		private A getAMOrPMFrom(String value) {
			return (value.indexOf('a') > 0) ? A.AM : A.PM;
		}
		
		/**
		 * Update internal reference of the minutes
		 */
		private void setMinute(String minute) {
			minInt = Integer.parseInt(minute);
		}
		
		/**
		 * Updates the internal hour and AM/PM from given 
		 * 12-hour and AM/PM value.
		 */
		private void set12Hour(String hour, A val) {
			hrInt = Integer.parseInt(hour);
			if (val == A.AM && hrInt == 12) {
				hrInt = 0;//12 AM = 00:00
			}
			if (val == A.PM && hrInt < 12) {
				hrInt+= 12;
			}
		}
		
		/**
		 * Updates the internal hour and AM/PM from given
		 * 24-hour value
		 */
		private void set24Hour(String value) {
			hrInt = Integer.parseInt(value);
			if (hrInt > 0 && hrInt < 12) {
				set12Hour(value, A.AM);
				return;
			}
		}
		
		/**
		 * Updates the internal hour, minute, and AM/PM 
		 * from passed values.
		 */
		private void setHourMin12Hr(String hr, String min) {
			set12Hour(hr, defaultAmPm);
			setMinute(min);
		}
		
		private void clear() {
			minInt = 0; hrInt = 0;
			processValueChange();
		}
		
		@Override public Long parse(CharSequence text) throws ParseException {
			String value = text.toString().replaceAll("\\s+","").replaceAll(":", "").toLowerCase();
			
			if (value.isEmpty()) {
				clear();
			}
			if (value.matches(justHr)) { //1-12, just hrs
				set12Hour(value, defaultAmPm);
				minInt = 0;
				
			} else if (value.matches(hrAMPM)) { //Hour 1-12 and AM/PM
				set12Hour(value.replaceAll("\\D+", ""), getAMOrPMFrom(value));
				minInt = 0;
				
			} else if (value.matches(justHr24)) { //24-format Hour only
				set24Hour(value);
				minInt = 0;
				
			} else if (value.matches(hrMin12)) { //Hour and minute (12hr)
				setHourMin12Hr(chopHrsFrom(value), chopMinutesFrom(value));
				
			} else if (value.matches(hrMin24)) { //Hour and minute (24hr)
				set24Hour(chopHrsFrom(value));
				setMinute(chopMinutesFrom(value));
				
			} else if (value.matches(hrMinAMPM)) {
				String hrmin = value.replaceAll("\\D+", "");
				set12Hour(chopHrsFrom(hrmin), getAMOrPMFrom(value));
				setMinute(chopMinutesFrom(hrmin));
				
			} else {
				clear();
			}
			
			processValueChange();
			return theValue;
		}
	};
	
	public static enum A { AM, PM };
	
	
	public TimePicker() {
		this(Document.get().createElement("input"));//just like TextBox does it
		getElement().setAttribute("type", "time");
		getElement().setAttribute("placeholder", "Enter time");
		setStyleName("TimePicker");
	}
	
	/**
	 * Protected constructor to initialize super class.
	 */
	protected TimePicker(Element el) {
		super(el, renderer, parser);
	}
	
	
	@Override
	protected void onLoad() {
		super.onLoad();//need dis.
		
		if (!("time".equals(getElement().getPropertyString("type")))) {
			addValueChangeHandler(new ValueChangeHandler<Long>() {
				@Override public void onValueChange(ValueChangeEvent<Long> event) {
					setText(renderer.render(event.getValue()));
				}
			});
			
			addFocusHandler(new FocusHandler() {
				@Override public void onFocus(FocusEvent event) {
					//onFocus, select all for easy replacement of text
					setSelectionRange(0, getText().length());
				}
			});
		}
	}
	
	@Override
	protected void onUnload() {
		getElement().removeFromParent(); //Cleanup the InputElement we made.
	}
	
	/**
	 * Strips the time from a Date object, leaving just the date
	 */
	@SuppressWarnings("deprecation")
	private static void resetTime(Date date) {
		long msec = date.getTime();
		msec = (msec / 1000) * 1000;
		date.setTime(msec);
		// Daylight savings time occurs at midnight in some time zones, so we
		// reset
		// the time to noon instead.
		date.setHours(12);
		date.setMinutes(0);
		date.setSeconds(0);
	}
	
	/**
	 * @return The Value (time entered in widget) + Today's date.
	 */
	public Date getValueCombinedWithToday() {
		Date d = new Date();
		resetTime(d);
		return getValueCombinedWith(d);
	}
	
	/**
	 * @return The Value (time entered in widget) + given date.
	 */
	public Date getValueCombinedWith(Date d) {
		if (getValue() == null || getValue() == 0L) return d;
		if (d != null) 
			return new Date(d.getTime() + getValue());
		return getValueCombinedWithToday();
	}
	
}
