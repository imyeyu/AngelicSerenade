package net.imyeyu.as.ctrl;

import static net.imyeyu.engine.Framework.*;

import net.imyeyu.as.view.ViewStartup;
import net.imyeyu.px.service.RunLater;

/**
 * 启动页面控制层
 * 
 * @author 夜雨
 * @createdAt 2021-01-20 21:43:38
 *
 */
public class Startup extends ViewStartup {

	protected void onShow() {
		RunLater.time(4000).event(() -> engine.gotoView("menu"));
	}
}