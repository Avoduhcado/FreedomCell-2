package core.render;

import java.util.HashMap;

import core.render.textured.Sprite;

public class SpriteIndex {

	private static HashMap<String, Sprite> sprites = new HashMap<String, Sprite>();
	
	public static Sprite getSprite(String ref) {
		if(sprites.get(ref) != null) {
			return sprites.get(ref);
		}
		
		if(ref != null)
			sprites.put(ref, new Sprite(ref));
		
		return sprites.get(ref);
	}
	
}
