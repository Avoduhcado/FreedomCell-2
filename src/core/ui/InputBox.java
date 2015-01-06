package core.ui;

import java.awt.Color;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.geom.Rectangle2D;
import java.io.IOException;

import org.lwjgl.input.Keyboard;

import core.Theater;
import core.utilities.keyboard.Keybinds;
import core.utilities.text.Text;

public class InputBox extends UIElement {

	private int style; // 0 = plain text; 1 = Integers; -1 = Keybinds;
	private String text;
	private int textLimit = 100;
	private float flash = 0.0f;
	private boolean centered = true;
	
	/**
	 * @param x coordinate of box
	 * @param y coordinate of box
	 * @param image Background frame
	 * @param style Type of input accepted
	 * @param text Preset text
	 * @param textLimit Total number of characters accepted
	 */
	public InputBox(float x, float y, String image, int style, String text, int textLimit) {
		super(x, y, image);
		
		Keybinds.clear();
		Keyboard.enableRepeatEvents(true);
		
		this.style = style;
		this.text = text != null ? text : "";
		if(textLimit != 0)
			this.textLimit = textLimit;
		
		this.box = new Rectangle2D.Double(x, y, Text.getWidth(this.text, "SYSTEM"), Text.getHeight(this.text, "SYSTEM"));
	}
	
	@Override
	public void update() {
		if(isClicked()) {
			enabled = !enabled;
			Keybinds.clear();
			if(!enabled)
				flash = 0f;
		}
		if(enabled) {
			// Display flashing cursor
			if(flash < 1.0f)
				flash += Theater.getDeltaSpeed(0.025f);
			else
				flash = 0;
		}
	}
	
	@Override
	public void draw() {
		super.draw();
		
		if(centered)
			Text.drawCenteredString(flash > 0.5f ? text + "|" : text, x, y,
					enabled ? Color.white : (this.isHovering() ? Color.gray : Color.darkGray), "SYSTEM");
		else
			Text.drawString(flash > 0.5f ? text + "|" : text, x, y,
					enabled ? Color.white : (this.isHovering() ? Color.gray : Color.darkGray), "SYSTEM");
	}
	
	/**
	 * Collect input from user.
	 * @return null if Confirm is not pressed. Text if Confirm is pressed.
	 */
	public String input() {
		// Loop through all existing key presses
		while(Keyboard.next()) {
			// If a key was both pressed and isn't a modifier
			if(Keyboard.getEventKeyState()) {
				switch(style) {
				case 0:
				case 1:
					if(!isModifierKey()) {
						// Check for backspace and if text can be removed
						if(Keyboard.getEventKey() == Keyboard.KEY_BACK && text.length() > 0) {
							// Remove the last character from text
							text = (String)text.subSequence(0, text.length() - 1);
						} else if(text.length() <= textLimit) {
							// If the user is pasting text
							if(Keyboard.getEventKey() == Keyboard.KEY_V && (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL))) {
								paste();
							} else if(Keyboard.getEventCharacter() != Keyboard.CHAR_NONE && Keyboard.getEventKey() != Keyboard.KEY_RETURN) {
								// Add a regular character
								addCharacter();
							}
							// Make sure text is still inside limit
							trimText();
						}
						updateBox();
					}
					break;
				case -1:
					text = Keyboard.getKeyName(Keyboard.getEventKey());
					Keyboard.enableRepeatEvents(false);
					updateBox();
					return text;
				}
			}
		}
		
		if(Keybinds.CONFIRM.clicked()) {
			Keyboard.enableRepeatEvents(false);
			return text;
		}
		
		return null;
	}
	
	/**
	 * Paste contents of clipboard into text.
	 */
	public void paste() {
		try {
			switch(style) {
			case 1: 
				text += Integer.parseInt((String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor));
				break;
			default: 
				text += (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
				break;
			}
		} catch (NumberFormatException e) {
		} catch (IOException | HeadlessException | UnsupportedFlavorException  e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Add the next character from the keyboard to text.
	 */
	public void addCharacter() {
		switch(style) {
		case 0: 
			text = text + Keyboard.getEventCharacter();
			break;
		case 1:
			try {
				Integer.parseInt(text + Keyboard.getEventCharacter());
				text = text + Keyboard.getEventCharacter();
			} catch (NumberFormatException e) {}
			break;
		}
	}
	
	/**
	 * Trim the text to fit limit constraints.
	 */
	public void trimText() {
		if(text.length() > textLimit)
			text = text.substring(0, textLimit);
	}
	
	/**
	 * @return true if the next key is a modifier
	 */
	public boolean isModifierKey() {
		if(Keyboard.getEventKey() == Keyboard.KEY_LSHIFT || Keyboard.getEventKey() == Keyboard.KEY_RSHIFT || Keyboard.getEventKey() == Keyboard.KEY_RIGHT
				|| Keyboard.getEventKey() == Keyboard.KEY_LEFT || Keyboard.getEventKey() == Keyboard.KEY_DOWN || Keyboard.getEventKey() == Keyboard.KEY_UP
				|| Keyboard.getEventKey() == Keyboard.KEY_LCONTROL || Keyboard.getEventKey() == Keyboard.KEY_RCONTROL) {
			return true;
		}
		
		return false;
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		Keybinds.clear();
		if(!enabled)
			flash = 0f;
	}
	
	/**
	 * Center the text in this input.
	 * @param centered
	 */
	public void setCentered(boolean centered) {
		this.centered = centered;
	}
	
	/**
	 * @return text from this input.
	 */
	public String getText() {
		return text;
	}
	
	@Override
	public void updateBox() {
		if(text.length() > 0)
			box = new Rectangle2D.Double(x, y, Text.getWidth(text, "SYSTEM"), Text.getHeight(text, "SYSTEM"));
		else
			box = new Rectangle2D.Double(x, y, 15f, 15f);
	}
	
}
