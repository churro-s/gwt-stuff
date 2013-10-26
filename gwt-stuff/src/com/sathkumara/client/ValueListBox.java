package com.sathkumara.client;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.ListBox;

/**
 * Simple ListBox that can store a Generic value
 * @author Churro
 *
 * @param <T> Type of value.
 */
public class ValueListBox<T> extends ListBox implements HasValue<T> {
	
	/**
	 * Custom interface for the purpose of getting a 
	 * human-readable String and an id for the generic type.
	 * This is required in the constructor.
	 * @author Churro
	 */
	public interface ItemDisplay<T> {
		/**
		 * Gets the user human-readable text for given item
		 */
		String getDisplay(T item);
		
		/**
		 * Gets the unique identifier for reference of the given item.
		 */
		String getValue(T item);
	}
	
	
	/**
	 * Custom interface to allow filtering of the values that get 
	 * added to the ListBox.
	 * @author Churro
	 */
	public interface ItemFilter<T> {
		
		/**
		 * The items that return <code>true</code> will be added 
		 * to the ListBox.
		 */
		boolean okayToAdd(T item);
	}
	
	private T value;
	private List<T> items = new LinkedList<T>();
	private ItemDisplay<T> provider;
	
	private static final String nullSelection = "Please select...";
	private static final String nullSelectionValue = "-999";
	private boolean addNullSelection = false;
	
	public ValueListBox(ItemDisplay<T> provider) {
		super();
		this.provider = provider;
		setStyleName("ValueListBox");
	}
	
	public ValueListBox(ItemDisplay<T> provider, boolean addNullSelection) {
		this(provider);
		this.addNullSelection = addNullSelection;
	}
	
	/*public ValueListBox(ItemDisplay<T> provider, Collection<T> items) {
		this(provider);
		addItems(items);
	}*/
	
	@Override
	protected void onLoad() {
		addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				updateValue(true);
			}
		});
		setSelectedIndex(0);
	}

	/**
	 * Update the internal reference list of possible selection items.
	 */
	private void updateItemsList(Collection<T> newItems) {
		if (!items.isEmpty()) {
			items.clear();
		}
		items.addAll(newItems);
	}
	
	/**
	 * Use in place of {@link #addItem(String, String)}.
	 */
	private void addItem(T item) {
		addItem(provider.getDisplay(item), provider.getValue(item));
	}
	
	public void addItems(Collection<T> items) {
		addItems(items, null);
	}
	
	
	public void addItems(Collection<T> items, ItemFilter<T> filter) {
		if (items == null) {
			throw new IllegalArgumentException();
		}
		if (addNullSelection) {
			addItem(nullSelection, nullSelectionValue);
		}
		if (filter == null) {
			for (T item : items) {
				addItem(item);
			}
		} else {
			for (T item : items) {
				if (filter.okayToAdd(item)) {
					addItem(item);
				}
			}
		}
		updateItemsList(items);
	}
	
	/**
	 * Sets the ListBox's selection to the given text.
	 * @param text The text of the item to be selected.
	 */
	public void setItemSelectedByText(String text, boolean selected) {
		for (int i = 0; i < getItemCount(); i++) {
			if (getItemText(i).equals(text)) {
				setItemSelected(i, selected);
				break;
			}
		}
	}
	
	public String getSelectedItemText() {
		return getItemText(getSelectedIndex());
	}
	
	/**
	 * Sets the ListBox's selection to the given value.
	 * @param value The value of the item to be selected.
	 */
	public void setItemSelectedByValue(String value, boolean selected) {
		for (int i = 0; i < getItemCount(); i++) {
			if (getValue(i).equals(value)) {
				setItemSelected(i, selected);
				break;
			}
		}
	}
	
	public String getSelectedItemValue() {
		return getValue(getSelectedIndex());
	}
	
	/**
	 * Called to sync the Value object from a ChangeEvent
	 * @param fireValueChangeEvent
	 */
	public void updateValue(boolean fireValueChangeEvent) {
		String selectedValue = getSelectedItemValue();
		if (nullSelectionValue.equals(selectedValue)) {
			this.value = null;
			
			if (fireValueChangeEvent) {
				fireValueChangeEvent(null);
			}
			return;
		}
		for (T item : items) {
			if (selectedValue.equals(provider.getValue(item))) {
				this.value = item;
				
				if (fireValueChangeEvent) {
					fireValueChangeEvent(item);
				}
				break;
			}
		}
	}
	
	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<T> handler) {
		return addHandler(handler, ValueChangeEvent.getType());
	}
	
	@Override
	public T getValue() {
		return value;
	}
	
	/**
	 * Set the ListBox's selection to this item.
	 */
	@Override
	public void setValue(T value) {
		if (value == null) {
			setItemSelectedByValue(nullSelectionValue, true);
			return;
		}
		this.value = value;
		setItemSelectedByValue(provider.getValue(value), true);
	}
	
	/**
	 * Set the ListBox's selection to this item, then fires a {@link ValueChangeEvent}.
	 */
	@Override
	public void setValue(T value, boolean fireValueChangeEvent) {
		setValue(value);
		if (fireValueChangeEvent) {
			fireValueChangeEvent(value);
		}
	}
	
	private void fireValueChangeEvent(T value) {
		ValueChangeEvent.fire(this, value);
	}
	
}
