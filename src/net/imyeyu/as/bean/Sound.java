package net.imyeyu.as.bean;

import java.applet.Applet;
import java.applet.AudioClip;
import java.util.HashMap;
import java.util.Map;

import net.imyeyu.as.AngelicSerenade;

/**
 * 音效
 * 
 * @author 夜雨
 * @createdAt 2021-01-20 22:02:27
 *
 */
public enum Sound {
	
	HOVER("HOVER"),
	CLICK("CLICK");

	private String typeName;
	
	private Sound(String typeName) {
        this.typeName = typeName;
    }
	
	public static Sound fromTypeName(String typeName) {
        for (Sound item : Sound.values()) {
            if (item.getTypeName().equals(typeName)) {
                return item;
            }
        }
        return null;
    }
	
	public static String getFileName(Sound typeName) {
		switch (typeName) {
			case HOVER: return "hover.wav";
			case CLICK: return "click.wav";
		}
		return null;
	}
	
	public static Map<Sound, AudioClip> getList() {
		Map<Sound, AudioClip> map = new HashMap<>();
        for (Sound type : Sound.values()) {
        	map.put(type, Applet.newAudioClip(Sound.class.getClassLoader().getResource(AngelicSerenade.RES_PATH.substring(1) + getFileName(type))));
        }
        return map;
	}
	
	public String getTypeName() {
        return this.typeName;
    }
}