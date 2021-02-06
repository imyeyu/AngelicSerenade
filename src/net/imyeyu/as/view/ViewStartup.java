package net.imyeyu.as.view;

import javafx.scene.image.ImageView;
import net.imyeyu.as.AngelicSerenade;
import net.imyeyu.engine.bean.Transition;
import net.imyeyu.engine.core.View;
import net.imyeyu.px.PixelFX;
import net.imyeyu.px.extend.BgFill;

/**
 * 启动页面视图层
 * 
 * @author 夜雨
 * @createdAt 2021-01-20 21:21:31
 *
 */
public class ViewStartup extends View {

	public void onLaunch() {
		setHideKeyFrames(Transition.toKeyFrames(this, Transition.OPACITY_TO_0, 500));
		
		setBackground(new BgFill(PixelFX.BLACK).build());
		getChildren().add(new ImageView(AngelicSerenade.RES_PATH + "startup.png"));
	}
}