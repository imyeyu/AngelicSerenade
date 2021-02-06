package net.imyeyu.as.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.LogManager;

import org.jaudiotagger.audio.wav.WavFileReader;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

import com.google.gson.Gson;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.image.Image;
import net.imyeyu.as.bean.ASFile;
import net.imyeyu.as.bean.Music;
import net.imyeyu.itools.IOUtils;

/**
 * AS 歌曲及轨道加载，包含所有信息（异步）
 * 
 * @author 夜雨
 * @createdAt 2021-01-05 16:52:57
 *
 */
public class MusicReader extends Service<List<ASFile>> {
	
	public MusicReader() {
		LogManager.getLogManager().reset();
	}
	
	protected Task<List<ASFile>> createTask() {
		return new Task<List<ASFile>>() {
			protected List<ASFile> call() throws Exception {
				List<ASFile> list = new ArrayList<>();
				File[] asFolder = new File("as").listFiles();
				File wavFile;
				Gson gson = new Gson();
				ASFile as;
				String name;
				
				// AS 文件
				List<File> asFiles = new ArrayList<>();
				for (int i = 0; i < asFolder.length; i++) {
					if (asFolder[i].isFile() && asFolder[i].getName().endsWith("as")) {
						asFiles.add(asFolder[i]);
					}
				}
				// 缓存
				Map<String, Music> cache = new HashMap<>();
				// 遍历 AS 文件
				File asFile;
				for (int i = 0; i < asFiles.size(); i++) {
					asFile = asFiles.get(i);
					name = asFile.getName();
					updateMessage(name);
					try {
						// ASFile
						as = gson.fromJson(IOUtils.fileToString(asFile, "UTF-8"), ASFile.class);
						if (!cache.containsKey(as.getWav())) {
							wavFile = new File("as" + File.separator + "wav" + File.separator + as.getWav());
							if (wavFile.exists()) {
								cache.put(as.getWav(), getMetaData(wavFile));
							} else {
								throw new FileNotFoundException("找不到 AS 曲谱映射的 WAV 音频");
							}
						}
						as.setMusic(cache.get(as.getWav()));
						
						updateProgress(i + 1, asFiles.size());
						Thread.sleep(200);
						list.add(as);
					} catch (Exception e) {
						throw e;
					}
				}
				cache = null;
				if (list.isEmpty()) {
					return null;
				} else {
					list.sort(Comparator.comparing(ASFile::getName));
					return list;
				}
			}

			/**
			 * 获取音频元素据
			 * 
			 * @param file 文件
			 * @return
			 * @throws Exception
			 */
			private Music getMetaData(File file) throws Exception {
				Tag tag = new WavFileReader().read(file).getTag();
				
				Music music = new Music();
				music.setFileName(file.getName());
				music.setFilePath(file.getAbsolutePath());
				
				music.setTitle(tag.getFirst(FieldKey.TITLE));
				music.setArtist(tag.getFirst(FieldKey.ARTIST));
				music.setAlbum(tag.getFirst(FieldKey.ALBUM));
				music.setYear(tag.getFirst(FieldKey.YEAR));
				if (tag.getFirstArtwork() != null) {
					music.setCover(new Image(new ByteArrayInputStream(tag.getFirstArtwork().getBinaryData())));
				}
				
				return music;
			}
		};
	}
}