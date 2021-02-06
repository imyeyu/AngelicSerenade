package net.imyeyu.as.ctrl;

import static net.imyeyu.engine.Framework.engine;

import net.imyeyu.as.AngelicSerenade;
import net.imyeyu.as.view.ViewOption;
import net.imyeyu.itools.config.Config;

/**
 * 设置页面控制层
 * 
 * @author 夜雨
 * @createdAt 2021-02-03 11:41:16
 *
 */
public class Option extends ViewOption {
	
	private Config config = AngelicSerenade.getConfig();
	
	public void onLaunch() {
		super.onLaunch();
		// 显示调试信息
		debugInfo.selectedProperty().addListener((obs, o, isSelected) -> {
			engine.setDebug(isSelected);
		});
		// 保存
		save.setOnAction(event -> {
			config.bindUpdate();
			engine.gotoView("menu");
		});
		// 关闭
		close.setOnAction(event -> engine.gotoView("menu"));
	}
	
	protected void onHide() {
		setOpacity(1);
	}
}