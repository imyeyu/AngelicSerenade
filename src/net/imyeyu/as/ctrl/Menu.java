package net.imyeyu.as.ctrl;

import static net.imyeyu.engine.Framework.engine;
import static net.imyeyu.engine.Framework.timer;

import java.util.List;

import net.imyeyu.as.AngelicSerenade;
import net.imyeyu.as.component.DialogTips;
import net.imyeyu.as.service.BGMService;
import net.imyeyu.as.view.ViewMenu;
import net.imyeyu.engine.bean.BezierPoint;
import net.imyeyu.engine.utils.CubicBezier;
import net.imyeyu.itools.config.Config;

/**
 * 菜单控制层
 * 
 * @author 夜雨
 * @createdAt 2021-01-20 21:56:44
 *
 */
public class Menu extends ViewMenu {
	
	private Config config = AngelicSerenade.getConfig();

	private int frame = 0;
	private double frameLength = timer.getFPS() * .8;
	private boolean canRun = false;
	private List<BezierPoint> bps;

	public void onLaunch() {
		super.onLaunch();
		
		bps = new CubicBezier(.08, .82, .17, 1).precision(frameLength).build();
		
		play.setOnAction(event -> engine.gotoView("playSelector"));
		editor.setOnAction(event -> engine.gotoView("editor"));
		moreAS.setOnAction(event -> DialogTips.instance().warning("未开发功能"));
		
		option.setOnAction(event -> engine.gotoView("option"));
		about.setOnAction(event -> engine.gotoView("about"));
		exit.setOnAction(event -> engine.shutdown());
	}
	
	protected void onShow() {
		BGMService.instance().play();
		frame = 0;
		canRun = true;
	}
	
	protected void onUpdate(double time) {
		if (canRun) {
			if (frame < bps.size()) {
				double y = bps.get(frame).y;
				as.setTranslateY(230 - 60 * y);
				as.setOpacity(y);
			}
			if (frameLength * .3 < frame) {
				if (root.getOpacity() < 1) {
					root.setOpacity((frame - frameLength * .3) / frameLength);
				} else {
					canRun = false;
					if (config.is("firstStartup")) {
						DialogTips.instance().warning("允许 javaw.exe 使用高性能的图形渲染显卡可获得更好的流畅度");
						config.put("firstStartup", false);
					}
				}
			}
			if (canRun) {
				frame++;
			}
		}
	}
	
	protected void onHide() {
		as.setOpacity(0);
		root.setOpacity(0);
	}
}