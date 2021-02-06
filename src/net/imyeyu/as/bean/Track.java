package net.imyeyu.as.bean;

import javafx.scene.paint.Paint;

/**
 * 轨道属性
 * 
 * @author 夜雨
 * @createdAt 2021-01-25 16:16:28
 *
 */
public enum Track {

	A("A"),
	S("S"),
	D("D"),
	J("J"),
	K("K"),
	L("L");

	private String typeName;
	
	private Track(String typeName) {
        this.typeName = typeName;
    }
	
	public static Track fromTypeName(String typeName) {
        for (Track item : Track.values()) {
            if (item.getTypeName().equals(typeName)) {
                return item;
            }
        }
        return null;
    }

	/**
	 * 轨道颜色
	 * 
	 * @param tick
	 * @return
	 */
	public static Paint getColor(Track tick) {
		switch (tick) {
			case A: return Paint.valueOf("#C96");
			case S: return Paint.valueOf("#666");
			case D: return Paint.valueOf("#C99");
			case J: return Paint.valueOf("#FF7A9B");
			case K: return Paint.valueOf("#09C");
			case L: return Paint.valueOf("#9C6");
		}
		return null;
	}
	
	/**
	 * 轨道纵轴偏移
	 * 
	 * @param tick
	 * @return
	 */
	public static double getOffset(Track tick) {
		switch (tick) {
			case A: return 146;
			case S: return 125;
			case D: return 104;
			case J: return 62;
			case K: return 40;
			case L: return 20;
		}
		return 0;
	}
	
	public String getTypeName() {
        return this.typeName;
    }
}