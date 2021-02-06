package net.imyeyu.as.view;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import net.imyeyu.as.AngelicSerenade;
import net.imyeyu.engine.bean.Transition;
import net.imyeyu.engine.core.View;
import net.imyeyu.px.PixelFX;
import net.imyeyu.px.Zpix;
import net.imyeyu.px.extend.AnchorPaneX;
import net.imyeyu.px.extend.BgImage;

/**
 * 游戏舞台前台
 * 
 * @author 夜雨
 * @createdAt 2021-01-13 23:58:11
 *
 */
public class ViewPlayStage extends View {
	
	public static final Image BG = new Image(AngelicSerenade.RES_PATH + "bg_play_stage.jpg");
	private static final Image STAFF = new Image(AngelicSerenade.RES_PATH + "staff.png");
	private static final Image SCORE = new Image(AngelicSerenade.RES_PATH + "score.png");
	
	protected Pane tracks;
	protected Label scBoost, score;

	public void onLaunch() {
		setHideKeyFrames(Transition.toKeyFrames(this, Transition.OPACITY_TO_0, 500));
		
		// 轨道
		tracks = new Pane();
		tracks.setBackground(new BgImage(STAFF).build());
		// 分数
		ImageView scIcon = new ImageView(SCORE);
		scIcon.setLayoutX(20);
		scIcon.setLayoutY(194);
		
		scBoost = new Label("scb");
		scBoost.setAlignment(Pos.BASELINE_RIGHT);
		scBoost.setTextFill(PixelFX.GREEN);
		scBoost.setPrefWidth(260);
		scBoost.setLayoutY(200);
		
		score = new Label("0");
		score.setAlignment(Pos.BASELINE_RIGHT);
		score.setPrefWidth(260);
		score.setLayoutY(230);
		Zpix.css(score, Zpix.L);
		
		AnchorPane main = new AnchorPane(tracks, scIcon, scBoost, score);
		main.setPrefHeight(310);
		AnchorPaneX.def(tracks);
		
		BorderPane root = new BorderPane();
		root.setBottom(main);
		getChildren().add(root);
	}
}