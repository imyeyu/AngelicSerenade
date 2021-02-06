package net.imyeyu.as.service;

import java.io.File;
import java.nio.file.Files;

import com.google.gson.Gson;

import net.imyeyu.as.bean.ASFile;
import net.imyeyu.as.bean.EditorTracks;
import net.imyeyu.as.bean.Tracks;
import net.imyeyu.itools.IOUtils;

/**
 * AS 文件管理器，AS 文件结构为 JSON，数据对象为 {@link net.imyeyu.as.bean.ASFile}
 * 
 * @author 夜雨
 * @createdAt 2021-01-09 17:12:52
 *
 */
public class ASManager {
	
	private static final File AS = new File("as");
	private static final File WAV = new File("as/wav");
	private static final String SEP = File.separator;

	public ASManager() {
		AS.mkdirs();
		WAV.mkdirs();
	}
	
	/**
	 * 保存轨道
	 * 
	 * @param wavSource 源音频
	 * @param as        目标保存文件
	 * @param tracks    轨道数据
	 * @return 成功操作返回 true
	 */
	public boolean save(File wavSource, File as, EditorTracks ets) throws Exception {
		String name = as.getName();
		int dot = 0;
		if ((dot = name.lastIndexOf(".")) != -1) {
			name = name.substring(0, dot);
		}
		File wavTarget = new File("as" + SEP + "wav" + SEP + name + ".wav");
		// 转 AS 文件
		Tracks tracks = new Tracks();
		tracks.setA(ets.getDataA());
		tracks.setS(ets.getDataS());
		tracks.setD(ets.getDataD());
		tracks.setJ(ets.getDataJ());
		tracks.setK(ets.getDataK());
		tracks.setL(ets.getDataL());
		
		ASFile asFile = new ASFile();
		asFile.setName(name);
		asFile.setWav(wavTarget.getName());
		asFile.setTracks(tracks);
		// 生成文件
		IOUtils.stringToFile(as, new Gson().toJson(asFile));
		// 复制 WAV
		if (wavSource.length() != wavTarget.length()) {
			if (wavTarget.exists()) {
				wavTarget.delete();
			}
			Files.copy(wavSource.toPath(), wavTarget.toPath());
		}
		return true;
	}
	
	/**
	 * 获取 AS 文件数据
	 * 
	 * @param as AS 文件对象
	 * @return ASFile 对象
	 */
	public ASFile getDataByASFile(File as) {
		return new Gson().fromJson(IOUtils.fileToString(as, "UTF-8"), ASFile.class);
	}
}