package net.imyeyu.as.view;

import static net.imyeyu.engine.Framework.RES_PATH;

import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import net.imyeyu.engine.bean.Transition;
import net.imyeyu.engine.core.View;
import net.imyeyu.px.PixelFX;
import net.imyeyu.px.Zpix;
import net.imyeyu.px.extend.BgFill;

/**
 * 引擎启动页面视图层
 * 
 * @author 夜雨
 * @createdAt 2021-01-20 21:21:42
 *
 */
public class ViewEngineLogo extends View {
	
	protected Scale scale = new Scale(.4, .4);
	protected Rotate rotate = new Rotate();
	protected Translate translate = new Translate();
	
	protected Label title;
	protected ImageView logo;

	public void onLaunch() {
		setShowKeyFrames(Transition.toKeyFrames(this, Transition.OPACITY_TO_1, 800));
		setHideKeyFrames(Transition.toKeyFrames(this, Transition.OPACITY_TO_0, 1000));
		// 变换
		translate.setY(-80);
		
		rotate = new Rotate();
		rotate.setAxis(Rotate.Y_AXIS);
		rotate.setPivotX(80);
		rotate.setPivotY(80);
		
		scale.setPivotX(80);
		scale.setPivotY(80);
		
		logo = new ImageView(RES_PATH + "logo.png");
		logo.setOpacity(0);
		logo.getTransforms().addAll(rotate, translate, scale);

		title = new Label("Yeyu Game Engine");
		title.setTextFill(PixelFX.WHITE);
		title.setTranslateY(52);
		title.setOpacity(0);
		Zpix.css(title, Zpix.L);

		setOpacity(0);
		setBackground(new BgFill(PixelFX.BLACK).build());
		getChildren().addAll(logo, title);
	}
}