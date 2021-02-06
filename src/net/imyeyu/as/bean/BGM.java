package net.imyeyu.as.bean;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javafx.scene.media.Media;
import net.imyeyu.as.AngelicSerenade;

public enum BGM {

	MENU("MENU");

	private String typeName;
	
	private BGM(String typeName) {
        this.typeName = typeName;
    }
	
	public static BGM fromTypeName(String typeName) {
        for (BGM item : BGM.values()) {
            if (item.getTypeName().equals(typeName)) {
                return item;
            }
        }
        return null;
    }
	
	public static String getFileName(BGM typeName) {
		switch (typeName) {
			case MENU: return "menu.mp3";
		}
		return null;
	}
	
	public static Map<BGM, Media> getList() throws URISyntaxException {
		Map<BGM, Media> map = new HashMap<>();
		URI uri;
        for (BGM type : BGM.values()) {
        	uri = BGM.class.getClassLoader().getResource(AngelicSerenade.RES_PATH.substring(1) + getFileName(type)).toURI();
        	map.put(type, new Media(uri.toString()));
        }
        return map;
	}
	
	public String getTypeName() {
        return this.typeName;
    }
}