package core.utilities.text;

import java.awt.Color;
import java.util.HashMap;

public class Text {

	private static HashMap<String, GameFont> fonts = new HashMap<String, GameFont>();
	
	/**
	 * Loads a font to be used. If no other fonts exist, will load given font under "DEFAULT" tag.
	 * @param key to identify given font. eg. "DEFAULT", "SYSTEM", "DIALOGUE"
	 * @param ref the name of the font to load
	 */
	public static void loadFont(String key, String ref) {
		if(fonts.get(ref) == null) {
			if(fonts.isEmpty())
				fonts.put("DEFAULT", new GameFont(ref));
			
			if(!key.matches("DEFAULT")) {
				fonts.put(key, new GameFont(ref));
			}
		}
	}
	
	/**
	 * Get a specific GameFont defined by font
	 * @param font key name to search for
	 * @return GameFont "font"
	 */
	public static GameFont getFont(String font) {
		if(fonts.containsKey(font))
			return fonts.get(font);
		
		return fonts.get("DEFAULT");
	}
	
	/**
	 * Draw a centered String.
	 * @param text
	 * @param x
	 * @param y
	 * @param color
	 */
	public static void drawCenteredString(String text, float x, float y, Color color) {
		fonts.get("DEFAULT").drawCenteredString(text, x, y, color);
	}
	
	/**
	 * Draw a centered String.
	 * @param text
	 * @param x
	 * @param y
	 * @param color
	 * @param font
	 */
	public static void drawCenteredString(String text, float x, float y, Color color, String font) {
		if(fonts.containsKey(font))
			fonts.get(font).drawCenteredString(text, x, y, color);
		else
			drawCenteredString(text, x, y, color);
	}
	
	/**
	 * Draw a String.
	 * @param text
	 * @param x
	 * @param y
	 * @param color
	 */
	public static void drawString(String text, float x, float y, Color color) {
		fonts.get("DEFAULT").drawString(text, x, y, color);
	}
	
	/**
	 * Draw a String.
	 * @param text
	 * @param x
	 * @param y
	 * @param color
	 * @param font
	 */
	public static void drawString(String text, float x, float y, Color color, String font) {
		if(fonts.containsKey(font))
			fonts.get(font).drawString(text, x, y, color);
		else
			drawString(text, x, y, color);
	}
	
	/**
	 * Draw a segment of a String.
	 * @param text
	 * @param x
	 * @param y
	 * @param color
	 * @param start
	 * @param end
	 */
	public static void drawString(String text, float x, float y, Color color, int start, int end) {
		fonts.get("DEFAULT").drawString(text, x, y, color, start, end);
	}
	
	/**
	 * Draw a segment of a String.
	 * @param text
	 * @param x
	 * @param y
	 * @param color
	 * @param font
	 * @param start
	 * @param end
	 */
	public static void drawString(String text, float x, float y, Color color, String font, int start, int end) {
		if(fonts.containsKey(font))
			fonts.get(font).drawString(text, x, y, color, start, end);
		else
			drawString(text, x, y, color, start, end);
	}

	/** 
	 * Get the width of a line of text.
	 * @param text to check the width of
	 * @return The width of the text line
	 */
	public static float getWidth(String text) {
		return fonts.get("DEFAULT").getWidth(text);
	}
	
	/**
	 * Get the width of a line of text in a specific font face.
	 * @param text to check the width of
	 * @param font to use to find width
	 * @return The width of a text line in a given font face
	 */
	public static float getWidth(String text, String font) {
		if(fonts.containsKey(font))
			return fonts.get(font).getWidth(text);
		
		return fonts.get("DEFAULT").getWidth(text);
	}
	
	/** 
	 * Get the height of a line of text.
	 * @param text to check the height of
	 * @return The height of the text line
	 */
	public static float getHeight(String text) {
		return fonts.get("DEFAULT").getHeight(text);
	}
	
	/**
	 * Get the height of a line of text in a specific font face.
	 * @param text to check the height of
	 * @param font to use to find height
	 * @return The height of a text line in a given font face
	 */
	public static float getHeight(String text, String font) {
		if(fonts.containsKey(font)) {
			return fonts.get(font).getHeight(text);
		}
		
		return fonts.get("DEFAULT").getHeight(text);
	}
	
}
