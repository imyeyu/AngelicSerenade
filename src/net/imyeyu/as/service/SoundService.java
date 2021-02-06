package net.imyeyu.as.service;

import net.imyeyu.as.bean.Sound;
import net.imyeyu.engine.media.SoundServer;

/**
 * 音效服务
 * 
 * @author 夜雨
 * @createdAt 2021-01-20 22:05:40
 *
 */
public class SoundService extends SoundServer<Sound> {
	
	private static SoundService service;
	
	public static void init() {
		if (service == null) {
			service = new SoundService();
			service.start();
		}
		service.getList().putAll(Sound.getList());
	}

	/**
	 * 播放音效
	 * 
	 * @param sound
	 */
	public static void play(Sound sound) {
		service.set(sound);
	}
}