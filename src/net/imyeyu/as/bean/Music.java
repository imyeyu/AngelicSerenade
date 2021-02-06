package net.imyeyu.as.bean;

import javafx.scene.image.Image;
import net.imyeyu.itools.EncodeUtils;

/**
 * 音乐对象，隶属于 ASFile
 * 
 * @author 夜雨
 * @createdAt 2021-01-23 19:52:15
 *
 */
public class Music {

	private String fileName;
	private String filePath;

	private String title;
	private String artist;
	private String album;
	private int year;

	private Image cover;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public String getAlbum() {
		return album;
	}

	public void setAlbum(String album) {
		this.album = album;
	}

	public int getYear() {
		return year;
	}

	public void setYear(String year) {
		if (year != null && EncodeUtils.isNumber(year)) {
			this.year = Integer.parseInt(year);
		} else {
			year = "";
		}
	}

	public Image getCover() {
		return cover;
	}

	public void setCover(Image cover) {
		this.cover = cover;
	}
}