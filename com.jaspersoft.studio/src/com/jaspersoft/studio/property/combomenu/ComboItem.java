/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.property.combomenu;

import org.eclipse.swt.graphics.Image;

import com.jaspersoft.studio.messages.MessagesByKeys;

/**
 * Class that describe the content of a Combo-popup action
 * @author Orlandin Marco
 *
 */
public class ComboItem {
	
		/**
		 * The action image
		 */
		private Image image;
		
		/**
		 * The action textual value
		 */
		private String label;
		
		/**
		 * True if the textual value should be printed directly or must be translated using the Messages class
		 */
		private boolean printDirectly;
		
		/**
		 * Order of the item in the list
		 */
		private int order;
		
		/**
		 * Item that this action represent
		 */
		private Object item;
		
		/**
		 * Value to return when this item is selected
		 */
		private Object value;
		
		/**
		 * 
		 * @param label The action textual value
		 * @param printDirectly True if the textual value should be printed directly or must be translated using the Messages class
		 * @param order Order of the item in the list
		 * @param item Item that this action represent
		 * @param value Value to return when this item is selected
		 */
		public ComboItem(String label, boolean printDirectly, int order, Object item, Object value){
			this.label = label;
			this.printDirectly = printDirectly;
			this.order = order;
			this.item = item;
			this.value = value;
			image = null;
		}
		
		/**
		 * @param label The action textual value
		 * @param printDirectly True if the textual value should be printed directly or must be translated using the Messages class
		 * @param image The image of the item
		 * @param order Order of the item in the list
		 * @param item Item that this action represent
		 * @param value Value to return when this item is selected
		 */
		public ComboItem(String label, boolean printDirectly, Image image, int order, Object item, Object value){
			this(label, printDirectly, order, item, value);
			this.image = image;
		}
		
		public String getText(){
			if (printDirectly) return label;
			else return MessagesByKeys.getString(label);
		}
		
		public  int getOrder(){
			return order;
		}
		
		public Image getImage(){
			return image;
		}
		
		public Object getItem(){
			return item;
		}
		
		public Object getValue(){
			return value;
		}
		
		public void setOrder(int newPosition){
			order = newPosition;
		}
		
		public boolean isSeparator(){
			return false;
		}
}
