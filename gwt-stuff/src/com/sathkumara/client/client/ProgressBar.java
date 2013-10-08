package com.sathkumara.client.client;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * A simple Progress bar implemented with two <code>&lt;div&gt;</code> blocks.
 * This is a GWT-fied version of the 
 * <a href="http://getbootstrap.com/2.3.2/components.html#progress">
 * Twitter Bootstrap v2.3 progress bar</a>.
 * There are 5 different styles to choose from, as specified by {@link Type}.
 * @author Churro
 */
public class ProgressBar extends SimplePanel {
	
	/** This enum is used to represent the type of {@link ProgressBar} to be 
	 * constructed. It will be used to determine the color and behavior.
	 */
	public static enum Type {
		/** The standard type. Represents a blue bar. */
		NORMAL("bar"), 
		
		/** A bar that changes color based on percentage. 
		 * Color will change from red to yellow to green. */
		DYNAMIC("bar"), 
		
		/** Represents a green bar. */
		GREEN("bar-success"), 
		
		/** Represents a yellow bar. */
		YELLOW("bar-warning"), 
		
		/** Represents a red bar. */
		RED("bar-danger");
		
		/** The CSS class name for this type */
		private String style;
		
		private Type(String style) {
			this.style = style;
		}
	}
	
	private HTML bar = new HTML();
	private Type type;
	private int[] dynamicColorRange = {30, 70};
	private int percentage;
	
	/**
	 * One and only constructor. Use the parameter to define what type of bar 
	 * this will be.
	 * @param type The {@link Type} enum that represents that style and behavior
	 *  of this bar. 
	 */
	public ProgressBar(Type type) {
		super();
		setStyleName("progress");
		setWidget(bar);
		setType(type);
	}
	
	/**
	 * Use this to specify which percentages will trigger the change in bar 
	 * color when using a {@link Type#DYNAMIC DYNAMIC} bar. If this bar is not
	 * {@link Type#DYNAMIC DYNAMIC}, this won't have any effect.
	 * @param yellow Anything greater than this integer will cause a yellow bar.
	 * @param green Anything greater than this integer will cause a green bar.
	 */
	public void setDynamicColorRange(int yellow, int green) {
		if (yellow > green || !isPercentage(yellow) || !isPercentage(green)) 
			return;
		dynamicColorRange = new int[]{yellow, green};
	}
	
	
	/**
	 * This is the sole control point of the bar. Setting a percentage will move
	 *  the bar.
	 * @param percent The integer to represent what percent of progress has 
	 * been made.
	 */
	public void setPercentage(int percent) {
		if (!isPercentage(percent)) 
			return;
		bar.setWidth(percent + "%");
		percentage = percent;
		if (Type.DYNAMIC == type) 
			updateDynamicStyle(percent);
	}
	
	/**
	 * Get the bar's current percentage
	 * @return What percentage of progress has been made.
	 */
	public int getPercentage() {
		return percentage;
	}
	
	/** Just a convenience method to check if the input is a valid percentage */
	public static boolean isPercentage(int percent) {
		return !(percent < 0 || percent > 100);
	}
	
	/**
	 * Find out what type of bar this is.
	 * @return The {@link Type} of bar this is.
	 */
	public Type getType() {
		return type;
	}
	
	/**
	 * Change what type of bar this is.
	 * @param type The new type you wish to set.
	 */
	public void setType(Type type) {
		this.type = type;
		updateStyle();
	}
	
	private void updateStyle() {
		bar.setStyleName(type.style);
	}
	
	private void updateDynamicStyle(int percent) {
		if (percent < dynamicColorRange[0]) 
			bar.setStyleName("bar " + Type.RED.style);
		else if (percent < dynamicColorRange[1]) 
			bar.setStyleName("bar " + Type.YELLOW.style);
		else 
			bar.setStyleName("bar " + Type.GREEN.style);
	}
	
}
