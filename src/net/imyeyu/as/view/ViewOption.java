package net.imyeyu.as.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import net.imyeyu.as.AngelicSerenade;
import net.imyeyu.as.component.ASButton;
import net.imyeyu.engine.bean.Transition;
import net.imyeyu.engine.core.View;
import net.imyeyu.itools.config.Config;
import net.imyeyu.px.Zpix;
import net.imyeyu.px.component.Switch;
import net.imyeyu.px.extend.BgFill;
import net.imyeyu.px.extend.BgImage;

/**
 * 设置页面视图层
 * 
 * @author 夜雨
 * @createdAt 2021-01-21 15:40:26
 *
 */
public class ViewOption extends View {
	
	private Config config = AngelicSerenade.getConfig();
	
	public static final Image BG = new Image(AngelicSerenade.RES_PATH + "bg_option.jpg");
	
	protected VBox feedback, main;
	protected Switch debugInfo;
	protected ASButton save, close;

	public void onLaunch() {
		setHideKeyFrames(Transition.toKeyFrames(this, Transition.OPACITY_TO_0, 500));
		
		// 标题
		Label title = new Label("- 设置 -");
		Zpix.css(title, Zpix.L);
		
		Label labelDebugInfo = new Label("显示调试信息: ");
		debugInfo = new Switch();
		config.bindSelectedProperty(debugInfo, "debug");
		
		HBox debugInfoBox = new HBox(labelDebugInfo, debugInfo);
		
		main = new VBox(debugInfoBox);
		main.setPadding(new Insets(32, 0, 16, 0));
		
		save = new ASButton("保存", Pos.CENTER);
		close = new ASButton("关闭", Pos.CENTER);
		HBox ctrls = new HBox(save, close);
		ctrls.setAlignment(Pos.CENTER_RIGHT);
		
		BorderPane root = new BorderPane();
		BorderPane.setAlignment(title, Pos.CENTER);
		root.setPadding(new Insets(48, 32, 16, 32));
		root.setBackground(new BgFill("#FFF9").build());
		root.setTop(title);
		root.setCenter(main);
		root.setBottom(ctrls);

		setBackground(new BgImage(BG).build());
		setPadding(new Insets(0, 260, 0, 260));
		getChildren().add(root);
	}
}