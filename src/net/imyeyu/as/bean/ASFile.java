package net.imyeyu.as.bean;

import javafx.beans.property.SimpleDoubleProperty;

/**
 * <br>AS 文件对象，包含名称，WAV 音频，曲谱轨道数据
 * <br>music 和 opacity 不序列化，仅在运行时用到
 * 
 * @author 夜雨
 * @createdAt 2021-01-23 19:53:06
 *
 */
public class ASFile {

	private String name;
	private String wav;
	private Tracks tracks;

	private transient Music music;

	// 透明度，用于渐变列表
	private transient SimpleDoubleProperty opacity = new SimpleDoubleProperty(.2);

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getWav() {
		return wav;
	}

	public void setWav(String wav) {
		this.wav = wav;
	}

	public Music getMusic() {
		return music;
	}

	public void setMusic(Music music) {
		this.music = music;
	}

	public double getOpacity() {
		return opacity.get();
	}

	public void setOpacity(double opacity) {
		this.opacity.set(opacity);
	}

	public SimpleDoubleProperty opacityProperty() {
		return opacity;
	}

	public Tracks getTracks() {
		return tracks;
	}

	public void setTracks(Tracks tracks) {
		this.tracks = tracks;
	}
}