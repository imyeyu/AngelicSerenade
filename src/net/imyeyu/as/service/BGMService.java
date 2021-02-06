package net.imyeyu.as.service;

import java.net.URISyntaxException;

import javafx.scene.media.MediaPlayer.Status;
import net.imyeyu.as.bean.BGM;
import net.imyeyu.engine.media.BGMServer;
import net.imyeyu.itools.service.NumberService;
import net.imyeyu.itools.service.ServiceEvent;

/**
 * 背景音乐服务
 * 
 * @author 夜雨
 * @createdAt 2021-01-24 19:22:53
 *
 */
public class BGMService extends BGMServer<BGM> {
	
	private static BGMService service;
	
	/**
	 * 修改音频对象
	 * 
	 * @param bgm
	 */
	public void setBGM(BGM bgm) {
		set(bgm);
	}

	/**
	 * 播放
	 * 
	 */
	public void play() {
		if (getPlayer() != null) {
			if (getPlayer().getStatus() != Status.PLAYING) {
				getPlayer().setVolume(0);
				getPlayer().play();
				NumberService service = new NumberService(0, 12, 1, 180);
				service.valueProperty().addListener((obs, o, n) -> {
					double v = getPlayer().getVolume() * 20 + 1;
					getPlayer().setVolume(v < 12 ? v / 20d : .6);
				});
				service.start();
			}
		}
	}
	
	/**
	 * 暂停
	 * 
	 */
	public void pause() {
		if (getPlayer() != null) {
			getPlayer().setVolume(.6);
			NumberService service = new NumberService(0, 12, 1, 180);
			service.valueProperty().addListener((obs, o, n) -> {
				double v = getPlayer().getVolume() * 20 - 1;
				getPlayer().setVolume(1 < v ? v / 20d : 0);
			});
			service.messageProperty().addListener((obs, o, event) -> {
				if (ServiceEvent.FINISH.equals(event)) {
					getPlayer().pause();
				}
			});
			service.start();
		}
	}
	
	/**
	 * 服务对象
	 * 
	 * @return
	 */
	public static BGMService instance() {
		if (service == null) {
			try {
				service = new BGMService();
				service.getList().putAll(BGM.getList());
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}
		return service;
	}
}