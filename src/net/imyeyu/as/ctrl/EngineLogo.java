package net.imyeyu.as.ctrl;

import static net.imyeyu.engine.Framework.engine;
import static net.imyeyu.engine.Framework.timer;

import java.util.List;

import net.imyeyu.as.view.ViewEngineLogo;
import net.imyeyu.engine.bean.BezierPoint;
import net.imyeyu.engine.utils.CubicBezier;
import net.imyeyu.px.service.RunLater;

/**
 * 引擎启动页面控制层
 * 
 * @author 夜雨
 * @createdAt 2021-01-20 21:26:26
 *
 */
public class EngineLogo extends ViewEngineLogo {

	private int frame = 0;
	private boolean canRun = false;
	private List<BezierPoint> bps = new CubicBezier(.08, .82, .17, 1).precision(timer.getFPS() * 1.5).build();
	
	protected void onShow() {
		frame = 0;
		RunLater.time(3500).event(() -> {
			canRun = true;
			RunLater.time(5000).event(() -> {
				engine.gotoView("startup");
			});
		});
	}
	
	protected void onUpdate(double time) {
		if (canRun) {
			if (frame < bps.size()) {
				double y = bps.get(frame).y;
				
				translate.setX(160 - y * 160);
				rotate.setAngle(360 * y);
				scale.setX(.4 + .6 * y);
				scale.setY(.4 + .6 * y);
				logo.setOpacity(y);
				
				if (30 < frame) {
					title.setOpacity((frame - 30) / 30d);
				}
				
				frame++;
			} else {
				canRun = false;
			}
		}
		super.onUpdate(time);
	}
}