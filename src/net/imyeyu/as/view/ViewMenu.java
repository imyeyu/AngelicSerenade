package net.imyeyu.as.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import net.imyeyu.as.AngelicSerenade;
import net.imyeyu.as.component.ASButton;
import net.imyeyu.engine.bean.Transition;
import net.imyeyu.engine.core.View;
import net.imyeyu.px.extend.BgImage;

/**
 * 菜单视图层
 * 
 * @author 夜雨
 * @createdAt 2021-01-20 21:56:26
 *
 */
public class ViewMenu extends View {
	
	protected ASButton play, editor, moreAS, option, about, exit;
	
	protected ImageView as;
	protected BorderPane root;
	
	public void onLaunch() {
		setShowKeyFrames(Transition.toKeyFrames(this, Transition.OPACITY_TO_1, 500));
		setHideKeyFrames(Transition.toKeyFrames(this, Transition.OPACITY_TO_0, 500));
		
		// AS Logo
		as = new ImageView(AngelicSerenade.RES_PATH + "as.png");
		as.setOpacity(0);
		as.setTranslateX(28);

		final Insets menuPadding = new Insets(560, 61, 0, 61);
		// 左菜单
		play = new ASButton("开始游戏", Pos.CENTER_LEFT);
		editor = new ASButton("编辑曲谱", Pos.CENTER_LEFT);
		moreAS = new ASButton("获取曲谱", Pos.CENTER_LEFT);
		VBox left = new VBox(play, editor, moreAS);
		
		left.setSpacing(16);
		left.setPadding(menuPadding);
		// 右菜单
		option = new ASButton("设置", Pos.CENTER_RIGHT);
		about = new ASButton("关于", Pos.CENTER_RIGHT);
		exit = new ASButton("退出", Pos.CENTER_RIGHT);
		VBox right = new VBox(option, about, exit);
		right.setSpacing(16);
		right.setPadding(menuPadding);
		
		root = new BorderPane();
		root.setBackground(new BgImage(AngelicSerenade.RES_PATH + "bg_menu.png").build());
		root.setOpacity(0);
		root.setLeft(left);
		root.setRight(right);

		getChildren().addAll(root, as);
	}
}