package core.ui;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import core.utilities.text.TextLine;

public class TextBox extends UIElement {

	private ArrayList<TextLine> text = new ArrayList<TextLine>();
	private String fontFace;
	
	public TextBox(String text, String fontFace, float x, float y, String image) {
		super(x, y, image);
		
		this.fontFace = fontFace;
		String[] temp = text.split(";");
		for(String t : temp)
			this.text.add(new TextLine(t));
		this.box = new Rectangle2D.Double(x, y, getWidth(), getHeight());
	}
	
	@Override
	public void draw() {
		if(frame != null)
			frame.draw(x, y, box);
		for(int i = 0; i<text.size(); i++)
			text.get(i).draw(x, y + getCurrentHeight(i), fontFace);
	}
	
	@Override
	public void draw(float x, float y) {
		if(frame != null)
			frame.draw(x, y, box);
		for(int i = 0; i<text.size(); i++)
			text.get(i).draw(x, y + getCurrentHeight(i), fontFace);
	}
	
	public float getWidth() {
		float width = 0;
		for(TextLine t : text) {
			if(t.getWidth() > width) {
				width = t.getWidth();
			}
		}
		
		return width;
	}
	
	public float getHeight() {
		float height = 0;
		for(TextLine t : text) {
			height += t.getHeight();
		}
		
		return height;
	}
	
	public float getCurrentHeight(int index) {
		float height = 0;
		for(int x = 0; x<index; x++) {
			height += text.get(x).getHeight();
		}
		
		return height;
	}
}
