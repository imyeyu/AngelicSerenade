package net.imyeyu.as.ctrl;

import static net.imyeyu.engine.Framework.engine;
import static net.imyeyu.engine.Framework.timer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.SimpleListProperty;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Duration;
import net.imyeyu.as.AngelicSerenade;
import net.imyeyu.as.bean.ASFile;
import net.imyeyu.as.bean.EditorTracks;
import net.imyeyu.as.component.DialogConfirm;
import net.imyeyu.as.component.DialogTips;
import net.imyeyu.as.component.EditorTick;
import net.imyeyu.as.service.ASManager;
import net.imyeyu.as.service.BGMService;
import net.imyeyu.as.view.ViewEditor;
import net.imyeyu.engine.bean.BezierPoint;
import net.imyeyu.engine.bean.Size;
import net.imyeyu.engine.utils.CubicBezier;
import net.imyeyu.itools.IOUtils;
import net.imyeyu.itools.config.Config;
import net.imyeyu.px.Zpix;
import net.imyeyu.px.bean.TipsLevel;

/**
 * 曲谱编辑器控制层
 * 
 * @author 夜雨
 * @createdAt 2021-01-21 16:41:14
 *
 */
public class Editor extends ViewEditor {

	private Config config = AngelicSerenade.getConfig();
	
	private double s = 0;            // 总秒数
	private double ms = 0;           // 总毫秒
	private double msi = 0;          // 当前毫秒
	private double pVms = 0;         // 每毫秒像素
	private double msVview = 0;      // 可视区毫秒量
	private double spacing = 0;      // 轨道时轴间距
	private double centerMS = 0;     // 进度达到轨道容器中心的毫秒量
	private double tracksScale = 40; // 轨道缩放倍率，越大轨道越长 [20, 60]
	private double offsetScroll = 0; // 每 100 毫秒滚动偏移量（偏移中心）
	
	private double selectorX = 0, selectorY = 0; // 轨道选择起点
	private boolean isAddedSelector = false;
	
	private File wav;
	private ASFile asFile;
	private boolean isSeeking = false, isChangedTick = false;
	private ASManager asManager = new ASManager();
	private MediaPlayer player;
	private GraphicsContext g, gt;

	// 说明
	private static String explainData = IOUtils.jarFileToString(AngelicSerenade.RES_PATH.substring(1) + "EditorExplain.txt");

	private FileChooser saveFileChooser;
	
	{
		saveFileChooser = new FileChooser();
		saveFileChooser.setTitle("保存 AS 曲谱");
		saveFileChooser.getExtensionFilters().setAll(new ExtensionFilter("天使恋曲曲谱", "*.as"));
		saveFileChooser.setInitialDirectory(new File("as"));
	}

	public void onLaunch() {
		super.onLaunch();
		
		// 轨道数据
		tracks = new EditorTracks();
		// 绑定轨道
		trackA.bindTicks(tracks.getA());
		trackS.bindTicks(tracks.getS());
		trackD.bindTicks(tracks.getD());
		trackJ.bindTicks(tracks.getJ());
		trackK.bindTicks(tracks.getK());
		trackL.bindTicks(tracks.getL());
		
		// 总时轴绘制对象
		g = axis.getGraphicsContext2D();
		// 轨道时轴绘制对象
		gt = trackAxis.getGraphicsContext2D();
		
		// 说明
		explain.setOnAction(event -> {
			DialogTips.instance().size(Size.L).title("说明").msg(explainData.trim());
		});
		// 导入文件
		select.setOnAction(event -> {
			ExtensionFilter support = new ExtensionFilter("支持的文件", "*.wav", "*.as");
			ExtensionFilter formatAS = new ExtensionFilter("天使恋曲曲谱", "*.as");
			ExtensionFilter formatWAV = new ExtensionFilter("无损波形音频", "*.wav");
			ExtensionFilter all = new ExtensionFilter("所有文件", "*.*");

			File directory = new File(config.getString("filePath"));
			directory = directory.exists() ? directory : new File("/");
			FileChooser fc = new FileChooser();
			fc.setTitle("选择文件");
			fc.setInitialDirectory(directory);
			fc.getExtensionFilters().addAll(support, formatWAV, formatAS, all);
			File file = fc.showOpenDialog(null);
			if (file != null) {
				// 导入歌曲
				if (file.getName().endsWith(".wav")) {
					wav = file;
					asFile = null;
					isChangedTick = false;
					
					initPlayer();
					config.put("filePath", file.getParentFile().getAbsolutePath());
					toggle.requestFocus();
					return;
				}
				// 导入 AS 轨道数据
				if (file.getName().endsWith(".as")) {
					asFile = asManager.getDataByASFile(file);
					wav = new File("as" + File.separator + "wav" + File.separator + asFile.getWav());
					isChangedTick = false;
					
					initPlayer();
					config.put("filePath", file.getParentFile().getAbsolutePath());
					toggle.requestFocus();
				}
			}
		});
		// 播放状态切换
		toggle.setOnAction(event -> togglePlay());
		// 保存
		save.setOnAction(event -> {
			try {
				if (asFile != null) {
					asManager.save(wav, new File("as" + File.separator + asFile.getName() + ".as"), tracks);
					isChangedTick = false;
					config.put("cache-reload-list", true);
					DialogTips.instance().success("保存成功");
				} else {
					if (wav == null) {
						DialogTips.instance().error("未导入音频或曲谱数据");
						return;
					}
					saveFileChooser.setInitialFileName(wav.getName().substring(0, wav.getName().lastIndexOf(".")) + ".as");
					File file = saveFileChooser.showSaveDialog(null);
					if (file != null) {
						asManager.save(wav, file, tracks);
						isChangedTick = false;
						config.put("cache-reload-list", true);
						DialogTips.instance().success("保存成功");
					}
				}
			} catch (Exception e) {
				DialogTips.instance().level(TipsLevel.ERROR).title("保存失败").msg(e.getMessage());
			}
		});
		// 退出
		exit.setOnAction(event -> {
			if (isChangedTick) {
				DialogConfirm.instance().level(TipsLevel.WARNING).size(Size.M).title("警告").onYes(() -> {
					try {
						boolean isSaved = false;
						if (asFile != null) {
							isSaved = asManager.save(wav, new File("as" + File.separator + asFile.getName() + ".as"), tracks);
						} else {
							saveFileChooser.setInitialFileName(wav.getName().substring(0, wav.getName().lastIndexOf(".")) + ".as");
							File file = saveFileChooser.showSaveDialog(null);
							if (file != null) {
								isSaved = asManager.save(wav, file, tracks);
							}
						}
						if (isSaved) {
							isChangedTick = false;
							config.put("cache-reload-list", true);
							DialogTips.instance().level(TipsLevel.SUCCESS).onClose(() -> {
								engine.gotoView("menu");
							}).msg("保存成功");
						}
					} catch (Exception e) {
						DialogTips.instance().error(e.getMessage());
					}
				}).onNo(() -> {
					engine.gotoView("menu");
				}).msg("已修改轨道数据，是否保存");
			} else {
				engine.gotoView("menu");
			}
		});
		
		// 轨道点选择 - 按下
		selector.setOnMousePressed(event -> {
			if (32 < event.getY() && event.getY() < selector.getHeight() - 4) {
				isAddedSelector = true;
				
				selectorRegion.setPrefSize(0, 0);
				selector.getChildren().add(selectorRegion);
				
				selectorX = event.getX();
				selectorY = event.getY();
				selectorRegion.setLayoutX(selectorX);
				selectorRegion.setLayoutY(selectorY);
			}
		});
		// 轨道点选择 - 拖拽
		selector.setOnMouseDragged(event -> {
			if (isAddedSelector) {
				// 往右下
				if (selectorX < event.getX() && selectorY < event.getY()) {
					if (event.getX() < selector.getWidth()) {
						selectorRegion.setPrefWidth(event.getX() - selectorX);
					}
					selectorRegion.setPrefHeight((event.getY() < selector.getHeight() ? event.getY() : selector.getHeight()) - selectorY);
					return;
				}
				// 往左下
				if (event.getX() < selectorX && selectorY < event.getY()) {
					if (0 < event.getX()) {
						selectorRegion.setLayoutX(event.getX());
						selectorRegion.setPrefWidth(selectorX - event.getX());
					}
					selectorRegion.setPrefHeight((event.getY() < selector.getHeight() ? event.getY() : selector.getHeight()) - selectorY);
					return;
				}
				// 往右上
				if (selectorX < event.getX() && event.getY() < selectorY) {
					if (event.getX() < selector.getWidth()) {
						selectorRegion.setPrefWidth(event.getX() - selectorX);
					}
					selectorRegion.setLayoutY(32 < event.getY() ? event.getY() : 32);
					selectorRegion.setPrefHeight(selectorY - (32 < event.getY() ? event.getY() : 32));
					return;
				}
				// 往左上
				if (event.getX() < selectorX && event.getY() < selectorY) {
					if (0 < event.getX()) {
						selectorRegion.setLayoutX(event.getX());
						selectorRegion.setPrefWidth(selectorX - event.getX());
					}
					selectorRegion.setLayoutY(32 < event.getY() ? event.getY() : 32);
					selectorRegion.setPrefHeight(selectorY - (32 < event.getY() ? event.getY() : 32));
				}
			}
		});
		// 轨道点选择 - 释放
		selector.setOnMouseReleased(event -> {
			if (isAddedSelector) {
				selectTracks(
					selectorRegion.getLayoutX(),
					selectorRegion.getLayoutX() + selectorRegion.getWidth(),
					selectorRegion.getLayoutY(),
					selectorRegion.getLayoutY() + selectorRegion.getHeight()
				);
				selector.getChildren().remove(selectorRegion);
				isAddedSelector = false;
			}
		});
		
		// 快捷键
		setOnKeyReleased(event -> {
			if (player != null) {
				if (event.isShiftDown()) {
					switch (event.getCode()) {
						case SPACE:
							togglePlay();
							break;
						case LEFT:
							player.seek(Duration.millis(trackPB.getValue() - 1e4));
							break;
						case RIGHT:
							player.seek(Duration.millis(trackPB.getValue() + 1e4));
							break;
						default:
							break;
					}
					return;
				}
				switch (event.getCode()) {
					// 打点轨道
					case A: a((int) player.getCurrentTime().toMillis()); break;
					case S: s((int) player.getCurrentTime().toMillis()); break;
					case D: d((int) player.getCurrentTime().toMillis()); break;
					case J: j((int) player.getCurrentTime().toMillis()); break;
					case K: k((int) player.getCurrentTime().toMillis()); break;
					case L: l((int) player.getCurrentTime().toMillis()); break;
					// 删除选中轨道点
					case DELETE:
						delete();
					default:
						break;
				}
				// 修改标记
				if (!isChangedTick) {
					switch (event.getCode()) {
						case A: case S: case D: case J: case K: case L: case DELETE:
							isChangedTick = true;
							break;
						default: break;
					}
				}
			}
		});

		drawAxis();
		drawTrackAxis(0);
	}

	/**
	 * 加载轨道点
	 * 
	 */
	private void loadTracks() {
		tracks.getA().setAll(dataToTick(asFile.getTracks().getA(), pVms));
		tracks.getS().setAll(dataToTick(asFile.getTracks().getS(), pVms));
		tracks.getD().setAll(dataToTick(asFile.getTracks().getD(), pVms));
		tracks.getJ().setAll(dataToTick(asFile.getTracks().getJ(), pVms));
		tracks.getK().setAll(dataToTick(asFile.getTracks().getK(), pVms));
		tracks.getL().setAll(dataToTick(asFile.getTracks().getL(), pVms));
	}
	
	/**
	 * 轨道数据转 Tick 对象（转为 UI 组件），需在加载歌曲后使用（需要 pVms）
	 * 
	 * @param axis 轨道数据
	 * @param pVms 每毫秒像素
	 * @return
	 */
	private List<EditorTick> dataToTick(List<Integer> axis, double pVms) {
		List<EditorTick> r = new ArrayList<>();
		int j;
		for (int i = 0; i < axis.size(); i++) {
			j = axis.get(i);
			r.add(new EditorTick(j, j * pVms));
		}
		return r;
	}
	
	/**
	 * 打点 A 轨道
	 * 
	 * @param nowMS 当前时刻
	 */
	private void a(int nowMS) {
		tracks.getA().add(new EditorTick(nowMS, nowMS * pVms));
	}

	
	/**
	 * 打点 S 轨道
	 * 
	 * @param nowMS 当前时刻
	 */
	private void s(int nowMS) {
		tracks.getS().add(new EditorTick(nowMS, nowMS * pVms));
	}

	
	/**
	 * 打点 D 轨道
	 * 
	 * @param nowMS 当前时刻
	 */
	private void d(int nowMS) {
		tracks.getD().add(new EditorTick(nowMS, nowMS * pVms));
	}

	
	/**
	 * 打点 J 轨道
	 * 
	 * @param nowMS 当前时刻
	 */
	private void j(int nowMS) {
		tracks.getJ().add(new EditorTick(nowMS, nowMS * pVms));
	}

	
	/**
	 * 打点 K 轨道
	 * 
	 * @param nowMS 当前时刻
	 */
	private void k(int nowMS) {
		tracks.getK().add(new EditorTick(nowMS, nowMS * pVms));
	}

	
	/**
	 * 打点 L 轨道
	 * 
	 * @param nowMS 当前时刻
	 */
	private void l(int nowMS) {
		tracks.getL().add(new EditorTick(nowMS, nowMS * pVms));
	}
	
	/**
	 * 轨道点选择
	 * 
	 * @param x1 横向范围起点
	 * @param x2 横向范围终点
	 * @param y1 纵向范围起点
	 * @param y2 纵向范围终点
	 */
	private void selectTracks(double x1, double x2, double y1, double y2) {
		if (player != null) {
			// 选中轨道范围
			final int t1 = (int) Math.floor((y1 - 32) / 40);
			final int t2 = (int) Math.floor((y2 - 32) / 40);
			// 选中时轴范围
			final double ms1 = 1 / pVms * x1;
			final double ms2 = 1 / pVms * x2;
			// 轨道
			SimpleListProperty<EditorTick> track;
			EditorTick tick;
			for (int i = 0; i < 6; i++) {
				if (t1 <= i && i <= t2) {
					// 选中轨道
					track = tracks.getTracks().get(i);
					// 轨道点
					for (int j = 0; j < track.size(); j++) {
						tick = track.get(j);
						if (ms1 < tick.getMS() && tick.getMS() < ms2) {
							tick.select();
						} else {
							tick.clearSelect();
						}
					}
				} else {
					// 未选中轨道
					track = tracks.getTracks().get(i);
					// 轨道点
					for (int j = 0; j < track.size(); j++) {
						track.get(j).clearSelect();
					}
				}
			}
		}
	}
	
	/**
	 * 删除选中轨道点
	 * 
	 */
	private void delete() {
		if (player != null) {
			// 轨道
			SimpleListProperty<EditorTick> track;
			EditorTick tick;
			List<EditorTick> selected = new ArrayList<>();
			for (int i = 0; i < 6; i++) {
				selected.clear();
				// 轨道
				track = tracks.getTracks().get(i);
				for (int j = 0; j < track.size(); j++) {
					tick = track.get(j);
					if (tick.isSelected()) {
						// 选中轨道点
						selected.add(tick);
					}
				}
				// 移除
				track.removeAll(selected);
			}
		}
	}
	
	/**
	 * 播放状态切换
	 * 
	 */
	private void togglePlay() {
		if (player != null) {
			Status status = player.getStatus();
			if (status == Status.PAUSED || status == Status.READY || status == Status.STOPPED) {
				player.play();
			} else {
				player.pause();
			}
		} else {
			DialogTips.instance().msg("请导入歌曲");
		}
	}

	/**
	 * 准备播放器
	 * 
	 * @param wav 音乐文件
	 */
	private void initPlayer() {
		// 初始化
		title.setText(wav.getName());

		if (player != null) player.dispose();
		player = new MediaPlayer(new Media(wav.toURI().toString()));

		// 播放器 - 准备就绪
		player.setOnReady(() -> {
			s = player.getTotalDuration().toSeconds();
			ms = player.getTotalDuration().toMillis();
			
			// 进度长度
			pb.setMax(ms);
			trackPB.setMax(ms);
			pb.setValue(0);
			// 轨道长度
			selector.setPrefWidth(s * tracksScale);
			// 轨道时轴间距
			spacing = selector.getPrefWidth() / s;
			// 每毫秒像素
			pVms = selector.getPrefWidth() / ms;
			// 可视区毫秒量
			msVview = tracksWidth / pVms;
			// 进度到达轨道可视区中心的毫秒量
			centerMS = msVview / 2;
			// 每 100 毫秒滚动偏移量
			offsetScroll = centerMS / ((ms - centerMS * 4) / 100);
			// 绘制轨道区时轴
			drawTrackAxis(0);
			// 绘制总时轴
			drawAxis();
			
			if (asFile != null) {
				// 加载轨道数据
				loadTracks();
			} else {
				// 清除轨道
				tracks.getA().clear();
				tracks.getS().clear();
				tracks.getD().clear();
				tracks.getJ().clear();
				tracks.getK().clear();
				tracks.getL().clear();
			}
		});
		// 播放器 - 绑定播放进度条
		player.currentTimeProperty().addListener((tmp, o, n) -> {
			if (!isSeeking) {
				msi = n.toMillis();
				// 进度
				pb.setValue(msi);
				trackPB.setValue(msi);
				// 滚动偏移
				if (centerMS < msi) {
					centerMS -= offsetScroll;
					tracksSP.setHvalue(msi / ms - centerMS / ms);
				} else {
					tracksSP.setHvalue(0);
				}
			}
		});
		// 播放器 - 播放完成
		player.setOnEndOfMedia(() -> {
			player.seek(Duration.ZERO);
			player.stop();
			// 重置动态参数
			centerMS = msVview / pVms;
			tracksSP.setHvalue(0);
		});
		// 播放器 - 播放状态切换
		player.statusProperty().addListener((tmp, o, status) -> {
			toggle.setText(status.equals(Status.PLAYING) ? "暂停" : "播放");
		});
		
		// 总进度 - 值监听
		pb.valueProperty().addListener((tmp, o, n) -> {
			time.setText(millis2time(n.intValue()));
			trackPB.setValue(n.doubleValue());
		});
		// 总进度 - 按下
		pb.setOnMousePressed(event -> isSeeking = true);
		// 总进度 - 拖拽
		pb.setOnMouseDragged(event -> reBuildCenterMS());
		// 总进度 - 释放
		pb.setOnMouseReleased(event -> {
			reBuildCenterMS();
			player.seek(Duration.millis(pb.getValue()));
			isSeeking = false;
		});

		// 时轴 - 按下
		axis.setOnMousePressed(event -> isSeeking = true);
		// 时轴 - 拖拽
		axis.setOnMouseDragged(event -> {
			pb.setValue(event.getX() / axis.getWidth() * ms);
			reBuildCenterMS();
		});
		// 时轴 - 释放
		axis.setOnMouseReleased(event -> {
			player.seek(Duration.millis(pb.getValue()));
			isSeeking = false;
		});

		// 轨道进度 - 值监听
		trackPB.valueProperty().addListener((tmp, o, n) -> {
			time.setText(millis2time(n.intValue()));
			pb.setValue(n.doubleValue());
		});
		// 轨道进度 - 按下
		trackPB.setOnMousePressed(event -> isSeeking = true);
		// 轨道进度 - 释放
		trackPB.setOnMouseReleased(event -> {
			reBuildCenterMS();
			player.seek(Duration.millis(pb.getValue()));
			isSeeking = false;
		});

		// 轨道时轴 - 按下
		trackAxis.setOnMousePressed(event -> isSeeking = true);
		// 轨道时轴 - 拖拽
		trackAxis.setOnMouseDragged(event -> {
			trackPB.setValue(tracksSP.getHvalue() * (ms - msVview) + event.getX() / pVms);
		});
		// 轨道时轴 - 释放
		trackAxis.setOnMouseReleased(event -> {
			reBuildCenterMS();
			player.seek(Duration.millis(pb.getValue()));
			isSeeking = false;
		});
		
		// 滚动面板
		tracksSP.hvalueProperty().addListener((obs, o, hValue) -> {
			// 时轴偏移
			drawTrackAxis((selector.getWidth() - tracksWidth) * -hValue.doubleValue());
		});
	}
	
	/**
	 * 重新计算居中点偏移量
	 * 
	 */
	private void reBuildCenterMS() {
		centerMS = msVview / 2;
		if (centerMS < trackPB.getValue()) {
			centerMS = centerMS - (trackPB.getValue() - centerMS) / 100 * offsetScroll;
		}
		tracksSP.setHvalue(pb.getValue() / ms - centerMS / ms);
	}

	// 时轴颜色
	private static final Color AXIS_COLOR = Color.valueOf("#A43C4B");
	
	// 栈缓存
	private double gPos = 0;
	/**
	 * 绘制总时轴
	 * 
	 * @param offset 偏移量
	 */
	private void drawAxis() {
		double spacing = axis.getWidth() / s;
		g.clearRect(0, 0, axis.getWidth(), axis.getHeight());

		g.setStroke(AXIS_COLOR);
		g.setLineWidth(1);
		g.setFont(Zpix.getS());
		g.setFill(AXIS_COLOR);
		g.setTextAlign(TextAlignment.CENTER);
		
		for (int i = 0; i < s; i++) {
			gPos = spacing * i;
			if (i % 30 == 0) {
				g.setLineWidth(2);
				g.strokeLine(gPos, 18, gPos, 30);
				g.setLineWidth(1);
				if (0 < i) {
					g.fillText(second2time(i), gPos, 14);
				}
				continue;
			}
			if (i % 15 == 0) {
				g.strokeLine(gPos, 18, gPos, 30);
				continue;
			}
			g.strokeLine(gPos, 21, gPos, 27);
		}
		g.strokeLine(0, 24, axis.getWidth(), 24);
	}
	
	// 栈缓存
	private double gtPos = 0; // 绘制位置
	/**
	 * 绘制轨道时轴
	 * 
	 * @param offset 偏移量
	 */
	private void drawTrackAxis(double offset) {
		gt.clearRect(0, 0, trackAxis.getWidth(), trackAxis.getHeight());

		gt.setStroke(AXIS_COLOR);
		gt.setLineWidth(1);
		gt.setFont(Zpix.getS());
		gt.setFill(AXIS_COLOR);
		gt.setTextAlign(TextAlignment.CENTER);
		
		for (int i = 0; i < s; i++) {
			gtPos = offset + spacing * i;
			if (gtPos < 0 || trackAxis.getWidth() < gtPos) {
				continue;
			}
			if (i % 20 == 0) {
				gt.setLineWidth(2);
				gt.strokeLine(gtPos, 32 - 13, gtPos, 32);
				gt.setLineWidth(1);
				if (0 < i) {
					gt.fillText(second2time(i), gtPos, 15);
				}
			} else {
				gt.strokeLine(gtPos, 32 - 10, gtPos, 32);
			}
		}
		gt.strokeLine(0, 32, trackAxis.getWidth(), 32);
	}

	// 格式化时间 00:00.0
	private String millis2time(int millis) {
		return second2time(millis / 1000) + "." + millis / 100 % 10;
	}

	// 格式化时间 00:00
	private String second2time(int second) {
		return String.format("%02d", Math.abs(second / 60)) + ":" + String.format("%02d", second % 60);
	}
	
	protected void onShow() {
		BGMService.instance().pause();
		
		frame = 0;
		canRun = true;
	}
	
	private int frame = 0;
	private boolean canRun = false;
	private List<BezierPoint> bps = new CubicBezier(.08, .82, .17, 1).precision(timer.getFPS() * .8).build();
	
	protected void onUpdate(double time) {
		if (canRun) {
			if (frame < bps.size()) {
				double y = bps.get(frame).y;
				header.setOpacity(y);
				bottom.setOpacity(y);
				header.setTranslateY(-60 + 60 * y);
				bottom.setTranslateY(368 - (368 * y));
				frame++;
			} else {
				canRun = false;
			}
		}
	}

	protected void onHide() {
		if (player != null) {
			player.dispose();
			player = null;
		}
		// 动态参数
		s = ms = msi = pVms = msVview = centerMS = offsetScroll = 0;
		trackPB.setValue(0);
		tracksSP.setHvalue(0);
		drawTrackAxis(0);
		drawAxis();
		// 事件
		wav = null;
		asFile = null;
		pb.setOnMouseReleased(null);
		axis.setOnMouseDragged(null);
		trackPB.setOnMouseReleased(null);
		trackAxis.setOnMouseDragged(null);
		// 轨道
		tracks.clear();
		title.setText("请导入歌曲");
		
		header.setOpacity(0);
		bottom.setOpacity(0);
		header.setTranslateY(-60);
		bottom.setTranslateY(368);
	}
}