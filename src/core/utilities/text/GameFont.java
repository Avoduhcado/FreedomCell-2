package core.utilities.text;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import core.render.textured.Glyph;

public class GameFont {

	private HashMap<Character, Glyph> glyphs = new HashMap<Character, Glyph>();
	
	private String fontName;
	private float size;
	public static final float defaultSize = 0.45f;
	private Color color;
	private boolean dropShadow = true;
	
	public GameFont(String fontName) {
		this.fontName = fontName;
		this.color = Color.white;
		this.size = defaultSize;
		
		try (BufferedReader reader = new BufferedReader(new FileReader(System.getProperty("resources") + "/fonts/" + fontName + ".fnt"))) {
			String line;
			String image = null;
			while((line = reader.readLine()) != null) {
				//System.out.println(line);
				String[] temp = line.split(" +");
				
				switch(temp[0]) {
				case "info":
					break;
				case "common":
					break;
				case "page":
					image = temp[2].split("=")[1].replaceAll("\"", "").substring(0, temp[2].split("=")[1].lastIndexOf('_') - 1);
					break;
				case "char":
					//for(String t : temp)
						//System.out.println(t);
					//System.out.println();
					glyphs.put((char) Integer.parseInt(temp[1].split("=")[1]), 
							new Glyph(image, Integer.parseInt(temp[2].split("=")[1]), Integer.parseInt(temp[3].split("=")[1]),
									Integer.parseInt(temp[4].split("=")[1]), Integer.parseInt(temp[5].split("=")[1]),
									Integer.parseInt(temp[6].split("=")[1]), Integer.parseInt(temp[7].split("=")[1]),
									Integer.parseInt(temp[8].split("=")[1]), Integer.parseInt(temp[9].split("=")[1])));
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void drawText(String text, float x, float y, Color color) {
		float advance = 0;
		for(int i = 0; i<text.length(); i++) {
			// Apply text adjustments
			getChar(text.charAt(i)).setColor(color);
			getChar(text.charAt(i)).setSize(size);
			
			getChar(text.charAt(i)).draw(x + advance, y);
			advance += getChar(text.charAt(i)).getXAdvance();
		}
	}
	
	public void drawString(String text, float x, float y, Color color) {
		if(dropShadow)
			drawText(text, x + 2, y + 2, Color.black);
		drawText(text, x, y, (color == null ? this.color : color));
		
		setSize(defaultSize);
	}
	
	public void drawString(String text, float x, float y, Color color, int start, int end) {
		if(dropShadow)
			drawText(text.substring(start, end), x + 2, y + 2, Color.black);
		drawText(text.substring(start, end), x, y, (color == null ? this.color : color));
		
		setSize(defaultSize);
	}
	
	public void drawCenteredString(String text, float x, float y, Color color) {
		if(dropShadow)
			drawText(text, (x + 2) - (getWidth(text) / 2f), y + 2, Color.black);
		drawText(text, x - (getWidth(text) / 2f), y, (color == null ? this.color : color));
		
		setSize(defaultSize);
	}
	
	public Glyph getChar(Character c) {
		if(glyphs.containsKey(c)) {
			return glyphs.get(c);
		}
		
		return glyphs.get(' ');
	}
	
	public float getWidth(String text) {
		float width = 0;
		for(int i = 0; i<text.length(); i++) {
			width += getChar(text.charAt(i)).getXAdvance();
		}
		
		return width;
	}
	
	public float getHeight(String text) {
		float height = 0f;
		for(int i = 0; i<text.length(); i++) {
			if(getChar(text.charAt(i)).getLineHeight() > height) {
				height = getChar(text.charAt(i)).getLineHeight();
			}
		}
		
		return height;
	}
	
	public String getName() {
		return fontName;
	}
	
	public float getSize() {
		return size;
	}
	
	public void setSize(float size) {
		this.size = size;
	}

	public Color getColor() {
		return color;
	}
	
	public void setColor(Color color) {
		this.color = color;
	}
	
	public boolean hasDropShadow() {
		return dropShadow;
	}
	
	public void setDropShadow(boolean dropShadow) {
		this.dropShadow = dropShadow;
	}

}
