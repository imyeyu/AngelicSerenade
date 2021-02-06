package net.imyeyu.as.ctrl;

import static net.imyeyu.engine.Framework.engine;
import static net.imyeyu.engine.Framework.timer;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import net.imyeyu.as.bean.ASFile;
import net.imyeyu.as.bean.GameResult;
import net.imyeyu.as.bean.GameTracks;
import net.imyeyu.as.bean.Track;
import net.imyeyu.as.component.GameFinishDialog;
import net.imyeyu.as.component.GameTick;
import net.imyeyu.as.service.BGMService;
import net.imyeyu.as.view.ViewAbout;
import net.imyeyu.as.view.ViewEditor;
import net.imyeyu.as.view.ViewOption;
import net.imyeyu.as.view.ViewPlaySelector;
import net.imyeyu.as.view.ViewPlayStage;
import net.imyeyu.px.extend.BgImage;
import net.imyeyu.px.service.RunLater;

/**
 * 游戏舞台后台
 * 
 * @author 夜雨
 * @createdAt 2021-01-14 00:11:30
 *
 */
public class PlayStage extends ViewPlayStage {
	
	// 随机背景
	private static final Image[] bgs = {ViewAbout.BG, ViewEditor.BG, ViewPlaySelector.BG, ViewOption.BG, BG};

	// 轨道数据
	private GameTracks tracks = new GameTracks();
	
	private double start, ms = 0;  // 起始时间，当前播放时间
	private ObservableList<GameTick> ticks = FXCollections.observableArrayList(); // 并轨列表

	private MediaPlayer player; // 播放器
	private GameFinishDialog finishDialog;
	
	public void onLaunch() {
		super.onLaunch();
		
		finishDialog = (GameFinishDialog) engine.getDialog("gameFinish");
		
		// 绑定计分
		score.textProperty().bind(scShow.asString());
		// 连击加分
		super.scBoost.textProperty().bind(Bindings.concat("+", ctsSC.asString()));
		super.scBoost.visibleProperty().bind(ctsSC.lessThan(1).not());
	}
	
	/**
	 * 初始化播放器
	 * 
	 * @param asFile       AS 文件
	 * @param isUseCoverBg 是否使用歌曲封面作为背景
	 */
	public void initPlayer(ASFile asFile, boolean isUseCoverBg) {
		// 背景
		if (isUseCoverBg && asFile.getMusic().getCover() != null) {
			setBackground(new BgImage(asFile.getMusic().getCover()).coverCenter().build());
		} else {
			final Random r = new Random();
			setBackground(new BgImage(bgs[r.nextInt(4)]).cover().build());
		}
		// 初始化播放器
		player = new MediaPlayer(new Media(new File(asFile.getMusic().getFilePath()).toURI().toString()));
		// 播放完成
		player.setOnEndOfMedia(() -> {
			RunLater.time(1000).event(() -> {
				double cts = 0;
				for (int i = 4; i < ticks.size(); i++) {
					cts += CTS_UNIT * i - 3;
				}
				// 理论满分
				final double total = ticks.size() * SC_PERFECT + cts;
				GameResult result = new GameResult();
				result.setScPerfect(scPerfect);
				result.setCtsMax(ctsMax);
				result.setScBase(scBase);
				result.setScCTS(scCTS);
				result.setScTotal(scTotal);
				result.setLevel(total, scTotal);
				
				finishDialog.setData(result);
				engine.dialogShow("gameFinish");
			});
		});
		// 设置轨道数据
		tracks.setA(dataToTrack(asFile.getTracks().getA(), Track.A));
		tracks.setS(dataToTrack(asFile.getTracks().getS(), Track.S));
		tracks.setD(dataToTrack(asFile.getTracks().getD(), Track.D));
		tracks.setJ(dataToTrack(asFile.getTracks().getJ(), Track.J));
		tracks.setK(dataToTrack(asFile.getTracks().getK(), Track.K));
		tracks.setL(dataToTrack(asFile.getTracks().getL(), Track.L));
		// 监听键盘
		engine.getScene().setOnKeyPressed(event -> {
			if (isASDJKL(event.getCode())) {
				nearTicks.clear();
				GameTick t = null, n = null;
				
				for (int i = forStart; i < forEnd + 1; i++) {
					t = ticks.get(i);
					// 附近轨道点（击中容错之内）
					if (t.getMS() < ms + OFFSET_GREAT && ms - OFFSET_GREAT < t.getMS() && !t.wasActived()) {
						// 绝对偏差
						t.setActiveABSms(Math.abs(ms - t.getMS()));
						nearTicks.add(t);
					}
				}
				if (!nearTicks.isEmpty()) {
					for (int i = 0; i < nearTicks.size(); i++) {
						n = nearTicks.get(i);
						if (n.getTrack().toString().equals(event.getCode().toString())) {
							// 击中
							n.active();
							calScore(n.getActiveABSms());
							return;
						}
					}
				}
				// 击错
				ctsCal = 0;
				ctsSC.set(0);
			}
		});
	}
	private List<GameTick> nearTicks = new ArrayList<>(); // 附近轨道点
	
	// ------ 计分 ------
	
	/* 统计 */
	private int    scPerfect = 0; // 完美次数
	private int    ctsMax    = 0; // 最多连击
	private double scBase    = 0; // 基础分
	private double scCTS     = 0; // 连击加成分
	private double scTotal   = 0; // 总分
	
	/** 显示的总分（实际总分 scTotal 由 calScore() 计算，显示分数 scShow 由帧渲染逐渐追加至 scTotal）*/
	private SimpleIntegerProperty scShow  = new SimpleIntegerProperty(0);
	
	/**
	 * 连击机制有待改进，现在是指数式加分，一旦中断几乎拿不到 C 以上的评分
	 */
	// cts = continuities
	private int ctsCal                   = 0; // 连击次数
	private static final double CTS_UNIT = 1; // 连击加成基础倍率
	/** 连击分数 */
	private SimpleIntegerProperty ctsSC = new SimpleIntegerProperty(0);
	/** 击中精准容错（毫秒） */
	private static final double OFFSET_PERFECT = 100, OFFSET_GREAT = 300;
	/** 击中精准分数 */
	private static final double SC_PERFECT = 10, SC_GREAT = 5;
	/** 结果（完美，很好，错过） */
	private boolean isPerfect = false, isGreat = false, isMiss = false;
	/**
	 * <br>计算分数
	 * <br>总分 += 击中精准分 + 连击基础倍率 * 完美连击次数
	 * 
	 * @param activeOffset 击中绝对偏差值
	 */
	private void calScore(double activeOffset) {
		isMiss    = OFFSET_GREAT < activeOffset;
		isPerfect = !isMiss && activeOffset < OFFSET_PERFECT;
		isGreat   = !isMiss && !isPerfect;

		if (isPerfect) {
			scPerfect++;
			
			scBase  += SC_PERFECT;
			scTotal += SC_PERFECT;
			
			// 完美连击
			if (3 < ctsCal) {
				ctsSC.set((int) (CTS_UNIT * ctsCal - 3));
				
				scCTS   += ctsSC.get();
				scTotal += ctsSC.get();
			}
			ctsCal++;
			if (ctsMax < ctsCal) {
				ctsMax = ctsCal;
			}
		}
		if (isGreat) {
			scBase  += SC_GREAT;
			scTotal += SC_GREAT;
		}
		if (!isMiss) {
			if (60 < scTotal - scShow.get()) {
				scShow.set((int) (scTotal - 60));
			}
		}
		if (isMiss) {
			ctsSC.set(0);
		}
	}
	
	/**
	 * 键盘事件过滤
	 * 
	 * @param code
	 * @return
	 */
	private boolean isASDJKL(KeyCode code) {
		switch (code) {
			case A:
			case S:
			case D:
			case J:
			case K:
			case L:
				return true;
			default:
				return false;
		}
	}

	/**
	 * 数据轨道转轨道节点组件
	 * 
	 * @param l    数据
	 * @param name 所属轨道
	 * @return
	 */
	private List<GameTick> dataToTrack(List<Integer> l, Track name) {
		List<GameTick> r = new ArrayList<>();
		for (int i = 0; i < l.size(); i++) {
			r.add(new GameTick(name, l.get(i) + 4000));
		}
		return r;
	}
	
	protected void onShow() {
		// 轨道点列表
		for (int i = 0; i < tracks.getTracks().size(); i++) {
			List<GameTick> ts = tracks.getTracks().get(i);
			for (int j = 0; j < ts.size(); j++) {
				ticks.add(ts.get(j));
			}
		}
		// 排序，方便遍历
		Collections.sort(ticks);
		// 上轨
		for (int i = 0; i < ticks.size(); i++) {
			super.tracks.getChildren().addAll(ticks.get(i).getNodes());
		}
		
		BGMService.instance().pause();
		start = timer.getNowMillis();
		RunLater.time(4000).event(() -> {
			player.play();
		});
	}

	/** 每毫秒偏移像素（距离打击点） */
	private double pVms = 970 / 4E3;
	/** 遍历偏移 */
	private int forStart = 0, forEnd = 0;
	// 栈缓存
	private GameTick dt;
	protected void onUpdate(double time) {
		if (start != 0) {
			ms = timer.getNowMillis() - start;
			for (int i = forStart; i < ticks.size(); i++) {
				dt = ticks.get(i);
				if (dt.getMS() - 4E3 < ms) {
					if (-90 < dt.getX()) {
						dt.setX(dt.getX() - timer.getDeltaMillis() * pVms);
						if (dt.getX() < 260 && !dt.wasActived() && !dt.isMiss()) {
							// 错过
							dt.miss();
							ctsCal = 0;
							ctsSC.set(0);
						}
					} else {
						forStart = i;
					}
				} else {
					forEnd = i;
					break;
				}
			}
			if (scShow.get() < scTotal) {
				scShow.set(scShow.add(1).get());
			}
		}
	}
	
	protected void onHide() {
		engine.getScene().setOnKeyPressed(null);
		
		player.dispose();
		player = null;
		
		start = scBase = scCTS = scTotal = scPerfect = ctsCal = ctsMax = forStart = forEnd = 0;
		
		ctsSC.set(0);
		scShow.set(0);
		
		ticks.clear();
		super.tracks.getChildren().clear();
		setOpacity(1);
	}
}