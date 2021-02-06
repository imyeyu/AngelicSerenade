package net.imyeyu.as;

import static net.imyeyu.engine.Framework.engine;

import javafx.scene.image.Image;
import net.imyeyu.as.bean.BGM;
import net.imyeyu.as.component.DialogConfirm;
import net.imyeyu.as.component.DialogLoading;
import net.imyeyu.as.component.DialogTips;
import net.imyeyu.as.component.GameFinishDialog;
import net.imyeyu.as.ctrl.About;
import net.imyeyu.as.ctrl.Editor;
import net.imyeyu.as.ctrl.EngineLogo;
import net.imyeyu.as.ctrl.Menu;
import net.imyeyu.as.ctrl.Option;
import net.imyeyu.as.ctrl.PlaySelector;
import net.imyeyu.as.ctrl.PlayStage;
import net.imyeyu.as.ctrl.Startup;
import net.imyeyu.as.service.BGMService;
import net.imyeyu.as.service.SoundService;
import net.imyeyu.engine.bean.EngineConfig;
import net.imyeyu.engine.core.GameFX;
import net.imyeyu.itools.config.Config;
import net.imyeyu.itools.config.Configer;
import net.imyeyu.px.PixelFX;

/**
 * 天使恋曲
 * 
 * @author 夜雨
 * @createdAt 2021-01-17 08:43:40
 *
 */
public class AngelicSerenade extends GameFX {
	
	// 静态资源
	public static final String RES_PATH = "/net/imyeyu/as/res/";
	// 配置
	private static Config config;

	public static void main(String[] args) {
		try {
			// 禁止 DPI 缩放
			System.setProperty("glass.win.minHiDPI", "1");
			
			// 配置文件
			Configer configer = new Configer(RES_PATH.substring(1) + "AngelicSerenade.ini");
			try {
				config = configer.get();
			} catch (Exception e) {
				config = configer.reset();
				config.put("cache-isResetConfig", true);
			}
			
			// 启动
			GameFX.launch(args);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 引擎初始化
	 * 
	 */
	public void onInit() {
		// 配置
		EngineConfig config = new EngineConfig();
		config.setSize(1286, 929);
		config.setIcon(RES_PATH + "icon.png");
		config.setDebug(AngelicSerenade.config.is("debug"));
		config.setCanResize(false);
		config.setTitle("天使恋曲 - 夜雨");
		config.setFPS(60);
		config.setCursor(new Image(RES_PATH + "cursor.png"));
		config.setCSS(PixelFX.CSS, RES_PATH + "style.css");
		engine.setConfig(config);
		// 页面
		engine.regView("engine", new EngineLogo());
		engine.regView("startup", new Startup());
		engine.regView("menu", new Menu());
		engine.regView("playSelector", new PlaySelector());
		engine.regView("playStage", new PlayStage());
		engine.regView("editor", new Editor());
		engine.regView("option", new Option());
		engine.regView("about", new About());
		// 会话
		engine.regDialog("tips", new DialogTips());
		engine.regDialog("confirm", new DialogConfirm());
		engine.regDialog("gameFinish", new GameFinishDialog());
		engine.regDialog("loading", new DialogLoading());
	}

	/**
	 * 引擎启动
	 * 
	 */
	public void onLaunch() {
		BGMService.instance();
		BGMService.instance().setBGM(BGM.MENU);
		SoundService.init();
		
		engine.gotoView("engine");
	}

	/**
	 * 关闭
	 * 
	 */
	public boolean onShutdown() {
		new Configer("AngelicSerenade").set(config);
		return super.onShutdown();
	}
	
	public static Config getConfig() {
		return config;
	}
}