package com.sathkumara.client;

import java.util.Date;

import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.TextBox;

/**
 * This widget creates a native HTML5 <code>&lt;input type='time' /&gt;</code>
 * element that falls back to a regular expression based time parser if 
 * the HTML5 time picker isn't supported on the client's browser. This widget 
 * lets the user enter a time (hours, minutes, AM/PM) value and parses it as 
 * a Long millisecond value. Also included are methods to combine the time 
 * with a given Date object.
 * <br /><br />
 * Accepted formats are: <ul><li>12:59 AM</li><li>5 - assumed to be 5 
 * AM</li><li>5p - 5:00 PM</li><li>13 - 1 PM</li><li>345 - 3:45 AM</li>
 * <li>1534 - 3:34 PM</li><li>111p - 1:11 PM</li></ul>
 * Spaces and colons will be disregarded, and time will parsed correctly.
 * <br /><br />
 * I'm sure there's a better way to implement this widget, but... 
 * deal with it.
 * 
 * @author Churro
 */
public class TimePickerOLD extends Composite implements HasValue<Long>, HasEnabled {
	public static enum A { AM, PM };
	
	//private static final String idealFormat = "([1-9]|1[0-2]):([0-5][0-9]) (AM|am|PM|pm)";
	private static final String justHr = "([1-9]|1[0-2])";
	private static final String hrAMPM = "([1-9]|1[0-2])(AM|am|PM|pm|a|p|A|P)";
	private static final String justHr24 = "([01]?[0-9]|2[0-3])";
	private static final String hrMin12 = "([1-9]|1[0-2])([0-5][0-9])";
	private static final String hrMin24 = "([01]?[0-9]|2[0-3])([0-5][0-9])";
	private static final String hrMinAMPM = "([1-9]|1[0-2])([0-5][0-9])(AM|am|PM|pm|a|p|A|P)";
	private static final A defaultAmPm = A.AM;
	
	
	private final TextBox theBox;
	private String hr, min, ampm;//display strings
	private int hrInt, minInt;//24hr time-used for value
	private Long theValue = 0L;
	
	
	public TimePickerOLD() {
		theBox = new TextBox();
		theBox.getElement().setAttribute("type", "time");
		initWidget(theBox);
		setStyleName("TimePicker");
		theBox.getElement().setAttribute("placeholder", "Enter time");
	}
	
	@Override
	protected void onLoad() {
		if (!("time".equals(theBox.getElement().getPropertyString("type")))) {
			//doesn't support HTML5 <input type="time"/>

			setTitle("Enter 24Hr time or 12Hr time with AM/PM");
			
			theBox.addValueChangeHandler(new ValueChangeHandler<String>() {
				@Override public void onValueChange(ValueChangeEvent<String> event) {
					String value = event.getValue().replaceAll("\\s+","")
							.replaceAll(":", "").toLowerCase();
					
					if (value.isEmpty()) {
						clear();
						return;
					}
					
					if (value.matches(justHr)) { //1-12, just hrs
						set12Hour(value, defaultAmPm);
						setMinute("00");
						
					} else if (value.matches(hrAMPM)) { //Hour 1-12 and AM/PM
						set12Hour(value.replaceAll("\\D+", ""), getAMOrPMFrom(value));
						setMinute("00");
						
					} else if (value.matches(justHr24)) { //24-format Hour only
						set24Hour(value);
						setMinute("00");
						
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
						return;
					}
					
					setTheText();
					processValueChange();
				}
			});
			
		} else {
			theBox.addValueChangeHandler(new ValueChangeHandler<String>() {
				@Override public void onValueChange(ValueChangeEvent<String> event) {
					String value = event.getValue();
					if (value.isEmpty()) {
						clear();
						return;
					}
					set24Hour(value.substring(0, value.indexOf(':')));
					setMinute(chopMinutesFrom(value));
					processValueChange();
				}
			});
		}
		
		theBox.addFocusHandler(new FocusHandler() {
			@Override public void onFocus(FocusEvent event) {
				//onFocus, select all for easy replacement of text
				theBox.setSelectionRange(0, theBox.getText().length());
			}
		});
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
		min = minute;
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
		hr = hour;
		if (val == A.PM && hrInt < 12) {
			hrInt+= 12;
		}
		ampm = val.name();
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
		} else {
			hr = "12";
			if (hrInt == 0) {
				ampm = A.AM.name();
			} else if (hrInt == 12) {
				ampm = A.PM.name();
			} else {
				hr = Integer.toString(hrInt-12);
				ampm = A.PM.name();
			}
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
	
	/**
	 * Uses the internal hour, minute, and AM/PM values 
	 * to calculate a Long millisecond value.
	 */
	private void processValueChange() {
		setValue((long) ((1000*60)* (minInt + (60*hrInt))), true, false);
		
		System.out.println("Display: " + hr+":"+min+" "+ampm 
			  + " int: " + hrInt + ":" + minInt + " Long: " + getValue());
	}
	
	/** Update display with passed value. */
	private void render(Long milliseconds) {
		if (milliseconds == null) {
			clear();
			return;
		}
		minInt = (int) ((milliseconds / (1000*60)) % 60);
		hrInt   = (int) ((milliseconds / (1000*60*60)) % 24);
		ampm = minInt >= 43200000 ? A.PM.name() : A.AM.name();
		setTheText();
	}
	
	/** Sets the text in the widget, formatted properly */
	private void setTheText() {
		theBox.setText(hr+":"+min+" "+ampm);
	}
	
	private void clear() {
		minInt = 0; hrInt = 0;
		processValueChange();
		theBox.setText("");
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
	
	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Long> handler) {
		return addHandler(handler, ValueChangeEvent.getType());
	}
	
	@Override
	public Long getValue() {
		return theValue;
	}
	
	@Override
	public void setValue(Long value) {
		setValue(value, false);
	}
	
	@Override
	public void setValue(Long value, boolean fireEvents) {
		setValue(value, fireEvents, true);
	}
	
	/**
	 * Set value internally. Fires a value change event, 
	 * but doesn't re-render (when render is false).
	 */
	private void setValue(Long value, boolean fireEvents, boolean render) {
		this.theValue = value == null ? 0L : value;
		if (render) {
			render(value);
		}
		if (fireEvents) {
			ValueChangeEvent.fireIfNotEqual(this, getValue(), value);
		}
	}
	
	@Override
	public boolean isEnabled() {
		return theBox.isEnabled();
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		theBox.setEnabled(enabled);
	}
	
}
