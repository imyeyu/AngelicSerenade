package net.imyeyu.as.ctrl;

import static net.imyeyu.engine.Framework.*;

import java.net.URI;
import java.util.List;

import javafx.scene.control.Hyperlink;
import javafx.scene.input.ScrollEvent;
import net.imyeyu.as.component.DialogLoading;
import net.imyeyu.as.component.DialogTips;
import net.imyeyu.as.view.ViewAbout;
import net.imyeyu.engine.bean.BezierPoint;
import net.imyeyu.engine.utils.CubicBezier;
import net.imyeyu.itools.NetworkUtils;
import net.imyeyu.px.service.RunAsync;

/**
 * 关于页面控制层
 * 
 * @author 夜雨
 * @createdAt 2021-01-21 15:44:09
 *
 */
public class About extends ViewAbout {
	
	public void onLaunch() {
		super.onLaunch();

		officialLink.setOnAction(event -> openURIInBrowser(officialLink));
		kogadoLink.setOnAction(event -> openURIInBrowser(kogadoLink));
		zpixLink.setOnAction(event -> openURIInBrowser(zpixLink));
		blogLink.setOnAction(event -> openURIInBrowser(blogLink));
		// 滚动
		rootSP.addEventFilter(ScrollEvent.SCROLL, event -> {
			event.consume();
			if (bps.size() <= frame) {
				nowScrollY = rootSP.getVvalue();
				deltaY = event.getDeltaY();
				scrollDirection = deltaY < 0 ? 1 : -1;
				deltaY = Math.abs(140 / (root.getHeight() - rootSP.getHeight()));
				frame = 0;
			}
		});
		// 反馈
		keep.setOnAction(event -> feedback(1));
		giveUp.setOnAction(event -> feedback(0));
		// 开源
		linkEngine.setOnAction(event -> openURIInBrowser(linkEngine));
		linkAS.setOnAction(event -> openURIInBrowser(linkAS));
		
		back.setOnAction(event -> engine.gotoView("menu"));
	}
	
	/**
	 * 反馈
	 * 
	 * @param type
	 */
	private void feedback(int type) {
		DialogLoading loading = (DialogLoading) engine.getDialog("loading");
		loading.setText("请稍后..");
		engine.dialogShow("loading");
		new RunAsync<String>() {
			public String todo() throws Exception {
				Thread.sleep(500);
				return NetworkUtils.doGet("http://as.imyeyu.net/feedback?type=" + type);
			}
			public void onFinish(String t) {
				engine.dialogClose();
				DialogTips.instance().onClose(() -> {
					config.put("feedback", true);
					root.getChildren().remove(feedback);
				}).success("感谢反馈!");
			}
		}.start();
	}
	
	/**
	 * 浏览器打开
	 * 
	 * @param link
	 */
	private void openURIInBrowser(Hyperlink link) {
		NetworkUtils.openURIInBrowser(URI.create(link.getText().replaceAll("\r\n|[\r\n]", "")));
	}

	private int scrollDirection = 1; // 滚动方向，正为下
	private double deltaY = 0, nowScrollY = 0;
	private List<BezierPoint> bps = new CubicBezier(.08, .82, .17, 1).precision(timer.getFPS() * .4).build();
	
	private int frame = bps.size();
	protected void onUpdate(double time) {
		if (frame < bps.size()) {
			rootSP.setVvalue(nowScrollY + deltaY * bps.get(frame).y * scrollDirection);
			frame++;
		}
	}
	
	protected void onHide() {
		setOpacity(1);
		rootSP.setVvalue(0);
	}
}