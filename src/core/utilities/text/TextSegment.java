package core.utilities.text;

import java.awt.Color;
import java.lang.reflect.Field;

public class TextSegment {

	private Color color = Color.darkGray;
	private float size = GameFont.defaultSize;
	private String fontFace;
	
	private String segment;
	
	public TextSegment(String segment) {
		if(segment.contains(">")) {
			String[] temp = segment.substring(0, segment.indexOf('>')).split(",");
			this.segment = segment.substring(segment.indexOf('>') + 1, segment.length());
			
			for(int x = 0; x<temp.length; x++) {
				if(temp[x].startsWith("s")) {
					size = Float.parseFloat(temp[x].substring(1));
				} else if(temp[x].startsWith("c")) {
					try {
						if(temp[x].contains("#")) {
							color = Color.decode(temp[x].substring(1));
						} else {
							Field f = Color.class.getField(temp[x].substring(1));
							color = (Color) f.get(null);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if(temp[x].startsWith("f")) {
					fontFace = temp[x].substring(1);
				}
			}
		} else {
			this.segment = segment;
		}
	}
	
	public void draw(float x, float y, String font) {
		Text.getFont(fontFace != null ? fontFace : font).setSize(size);
		Text.drawString(segment, x, y, color, fontFace != null ? fontFace : font);
	}
	
	public String getSegment() {
		return segment;
	}
	
	public float getWidth() {
		return Text.getWidth(segment, fontFace);
	}
	
	public float getHeight() {
		return Text.getHeight(segment, fontFace);
	}
	
}
