package net.imyeyu.as.ctrl;

import static net.imyeyu.engine.Framework.engine;
import static net.imyeyu.engine.Framework.timer;

import java.util.List;

import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.MultipleSelectionModel;
import net.imyeyu.as.AngelicSerenade;
import net.imyeyu.as.bean.ASFile;
import net.imyeyu.as.bean.Music;
import net.imyeyu.as.bean.Sound;
import net.imyeyu.as.component.ASButton;
import net.imyeyu.as.component.DialogLoading;
import net.imyeyu.as.service.BGMService;
import net.imyeyu.as.service.MusicReader;
import net.imyeyu.as.service.SoundService;
import net.imyeyu.as.view.ViewPlaySelector;
import net.imyeyu.engine.bean.BezierPoint;
import net.imyeyu.engine.utils.CubicBezier;
import net.imyeyu.itools.config.Config;
import net.imyeyu.px.extend.BgImage;
import net.imyeyu.px.service.RunLater;

/**
 * 歌曲选择控制层
 * 
 * @author 夜雨
 * @createdAt 2021-01-05 15:21:44
 *
 */
public class PlaySelector extends ViewPlaySelector {
	
	private Config config = AngelicSerenade.getConfig();
	
	// 选择器
	private MultipleSelectionModel<ASFile> selector;
	
	public void onLaunch() {
		super.onLaunch();
		
		selector = list.getSelectionModel();
		// 加载列表
		MusicReader musicReader = new MusicReader();
		musicReader.valueProperty().addListener((obs, o, result) -> {
			if (result != null) {
				list.getItems().setAll(result);
				selector.select(0);
				refreshMusicInfo();
			}
		});
		musicReader.exceptionProperty().addListener((obs, o, e) -> e.printStackTrace());
		musicReader.start();
		
		// 列表操作按钮
		prev.hoverProperty().addListener((obs, o, isHover) -> {
			if (isHover) {
				prev.setEffect(ASButton.SHADOW_HOVER);
				SoundService.play(Sound.HOVER);
			} else {
				prev.setEffect(ASButton.SHADOW_LEFT);
			}
		});
		next.hoverProperty().addListener((obs, o, isHover) -> {
			if (isHover) {
				next.setEffect(ASButton.SHADOW_HOVER);
				SoundService.play(Sound.HOVER);
			} else {
				next.setEffect(ASButton.SHADOW_LEFT);
			}
		});
		prev.setOnMousePressed(event -> SoundService.play(Sound.CLICK));
		next.setOnMousePressed(event -> SoundService.play(Sound.CLICK));
		list.setOnMousePressed(event -> SoundService.play(Sound.CLICK));
		// 上一首
		prev.setOnMouseClicked(event -> {
			if (0 < selector.getSelectedIndex()) {
				selector.select(selector.getSelectedIndex() - 1);
				list.scrollTo(selector.getSelectedIndex() - 3);
				refreshMusicInfo();
			}
		});
		// 下一首
		next.setOnMouseClicked(event -> {
			selector.select(selector.getSelectedIndex() + 1);
			list.scrollTo(selector.getSelectedIndex() - 3);
			refreshMusicInfo();
		});
		// 列表渐变
		selector.selectedIndexProperty().addListener((obs, o, selectedIndex) -> {
			if (selectedIndex != null) {
				final int index = selectedIndex.intValue();
				if (index != -1) {
					ObservableList<ASFile> items = list.getItems();
					items.get(index).setOpacity(1);
					for (int i = 0; i < items.size(); i++) {
						if (i == index - 3 || i == index + 3) {
							items.get(i).setOpacity(.4);
							continue;
						}
						if (i == index - 2 || i == index + 2) {
							items.get(i).setOpacity(.6);
							continue;
						}
						if (i == index - 1 || i == index + 1) {
							items.get(i).setOpacity(.8);
							continue;
						}
						if (index != i) {
							items.get(i).setOpacity(.2);
						}
					}
				}
				refreshMusicInfo();
			}
		});
		// 使用封面作为游戏背景
		useCoverBg.setOnAction(event -> SoundService.play(Sound.CLICK));
		// 开始游戏
		play.setOnAction(event -> {
			((PlayStage) engine.getView("playStage")).initPlayer(selector.getSelectedItem(), useCoverBg.isSelected());
			engine.gotoView("playStage");
		});
		// 返回
		back.setOnAction(event -> engine.gotoView("menu"));
	}
	
	/**
	 * 刷新歌曲信息
	 * 
	 */
	private void refreshMusicInfo() {
		ASFile asFile = selector.getSelectedItem();
		if (asFile != null) {
			Music music = asFile.getMusic();
			if (music != null) {
				title.setText(music.getTitle());
				artist.setText(music.getArtist());
				album.setText(music.getAlbum());
				year.setText(String.valueOf(music.getYear()));
				if (music.getCover() != null) {
					cover.getChildren().clear();
					cover.setBackground(new BgImage(music.getCover()).coverCenter().build());
					useCoverBg.setSelected(true);
					useCoverBg.setDisable(false);
				} else {
					final Label notCover = new Label("没有可用封面");
					cover.setBackground(coverBg);
					cover.getChildren().add(notCover);
					useCoverBg.setSelected(false);
					useCoverBg.setDisable(true);
				}
			}
		}
	}
	
	protected void onShow() {
		BGMService.instance().play();
		frame = 0;
		RunLater.time(800).event(() -> {
			if (config.has("cache-reload-list") && config.is("cache-reload-list")) {
				list.getItems().clear();
				
				DialogLoading dialog = (DialogLoading) engine.getDialog("loading");
				dialog.progressProperty().unbind();
				engine.dialogShow("loading");
				
				// 重新加载列表
				MusicReader musicReader = new MusicReader();
				musicReader.valueProperty().addListener((obs, o, result) -> {
					if (result != null) {
						list.getItems().setAll(result);
						selector.select(0);
						refreshMusicInfo();
					}
				});
				dialog.progressProperty().bind(musicReader.progressProperty());
				musicReader.messageProperty().addListener((obs, o, msg) -> dialog.setText(msg));
				musicReader.setOnSucceeded(event -> {
					engine.dialogClose();
					config.put("cache-reload-list", false);
				});
				musicReader.exceptionProperty().addListener((obs, o, e) -> e.printStackTrace());
				musicReader.start();
			}
		});
	}

	private int frame = 0;
	private List<BezierPoint> bps = new CubicBezier(.08, .82, .17, 1).precision(timer.getFPS() * .8).build();
	
	protected void onUpdate(double time) {
		if (-1 < frame && frame < bps.size()) {
			double y = bps.get(frame).y;
			root.setOpacity(y);
			root.setTranslateX(-420 + 100 * y);
			frame++;
		} else {
			frame = -1;
		}
	}
	
	protected void onHide() {
		root.setOpacity(0);
		root.setTranslateX(-420);
	}
}