package net.imyeyu.as.view;

import static net.imyeyu.engine.Framework.engine;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.TextAlignment;
import net.imyeyu.as.AngelicSerenade;
import net.imyeyu.as.bean.EditorTracks;
import net.imyeyu.as.component.ASButton;
import net.imyeyu.as.component.EditorTrack;
import net.imyeyu.engine.bean.Transition;
import net.imyeyu.engine.core.View;
import net.imyeyu.px.PixelFX;
import net.imyeyu.px.extend.AnchorPaneX;
import net.imyeyu.px.extend.BgFill;
import net.imyeyu.px.extend.BgImage;
import net.imyeyu.px.extend.BorderX;

/**
 * 曲谱编辑器视图层
 * 
 * @author 夜雨
 * @createdAt 2021-01-21 16:41:30
 *
 */
public class ViewEditor extends View {

	public static final Image BG = new Image(AngelicSerenade.RES_PATH + "bg_editor.jpg");

	// 轨道容器宽度
	protected double tracksWidth = engine.getConfig().getWidth() - 46;

	// 轨道数据
	protected EditorTracks tracks;
	// 轨道
	protected EditorTrack trackA, trackS, trackD, trackJ, trackK, trackL;
	
	protected Label title, time;
	protected Canvas axis, trackAxis;
	protected Slider pb, trackPB;
	protected ASButton select, explain, toggle, save, exit;
	
	protected Region selectorRegion; // 轨道点选择器
	protected ScrollPane tracksSP;   // 轨道滚动控制器
	protected AnchorPane selector;   // 轨道和轨道点选择器容器
	
	protected StackPane header;
	protected BorderPane bottom;

	public void onLaunch() {
		setShowKeyFrames(Transition.toKeyFrames(this, Transition.OPACITY_TO_1, 1000));
		setHideKeyFrames(Transition.toKeyFrames(this, Transition.OPACITY_TO_0, 500));
		
		title = new Label("请导入歌曲");
		explain = new ASButton("说明", Pos.CENTER);
		
		explain.setAlignment(Pos.CENTER_RIGHT);
		
		header = new StackPane(title, explain);
		header.setOpacity(0);
		header.setPadding(new Insets(6));
		header.setBackground(new BgFill("#FFFC").build());
		
		// 播放控制
		time = new Label("00:00.0");
		time.setTextAlignment(TextAlignment.JUSTIFY);

		select = new ASButton("导入", Pos.CENTER);
		toggle = new ASButton("播放", Pos.CENTER);
		save = new ASButton("保存", Pos.CENTER);
		exit = new ASButton("退出", Pos.CENTER);
		
		BorderPane topCtrl = new BorderPane();
		topCtrl.setPadding(new Insets(0, 32, 0, 20));
		topCtrl.setLeft(new HBox(select, toggle));
		topCtrl.setRight(new HBox(save, exit));
		
		StackPane topCtrlSP = new StackPane(time, topCtrl);
		topCtrlSP.setPadding(new Insets(6, 8, 6, 8));
		// 总进度时轴
		axis = new Canvas();
		axis.setWidth(tracksWidth + 40);
		axis.setHeight(39);
		// 总进度控制
		pb = new Slider();
		pb.getStyleClass().add("pb");
		pb.setPickOnBounds(false);
		// 总进度容器
		StackPane pbAxis = new StackPane(axis, pb);
		StackPane.setMargin(pb, new Insets(0, -6, 0, -6));
		pbAxis.setBorder(new BorderX(PixelFX.GRAY).width(1, 0, 1, 0).build());
		pbAxis.setPrefHeight(39);
		// 总控和总进度
		VBox topCtrlsAndPB = new VBox(topCtrlSP, pbAxis);
		
		// 轨道进度控制
		trackPB = new Slider();
		trackPB.getStyleClass().add("pb-track");
		trackPB.setPadding(new Insets(0, 12, 0, 12));
		trackPB.setOnMouseClicked(event -> event.consume());
		trackPB.setPickOnBounds(false);
		// 轨道时轴
		trackAxis = new Canvas();
		trackAxis.setWidth(tracksWidth);
		trackAxis.setHeight(32);

		// 轨道面板
		VBox tracksLabel = new VBox();
		tracksLabel.setPadding(new Insets(32, 0, 0, 0));
		tracksLabel.setBorder(new BorderX(PixelFX.GRAY).right().build());
		Paint[] colors = {
			Paint.valueOf("#C96"),
			Paint.valueOf("#666"),
			Paint.valueOf("#C99"),
			Paint.valueOf("#FF7A9B"),
			Paint.valueOf("#09C"),
			Paint.valueOf("#9C6")
		};
		char[] keys = "ASDJKL".toCharArray();
		final Border bottomBorder = new BorderX(PixelFX.GRAY).bottom().build();
		for (int i = 0; i < keys.length; i++) {
			Label key = new Label(String.valueOf(keys[i]));
			key.setBackground(new BgFill(colors[i]).build());
			key.setAlignment(Pos.CENTER);
			key.setPrefSize(40, 40);
			key.setBorder(bottomBorder);
			tracksLabel.getChildren().add(key);
		}
		// 轨道
		trackA = new EditorTrack(colors[0]);
		trackS = new EditorTrack(colors[1]);
		trackD = new EditorTrack(colors[2]);
		trackJ = new EditorTrack(colors[3]);
		trackK = new EditorTrack(colors[4]);
		trackL = new EditorTrack(colors[5]);
		
		VBox track = new VBox(trackA, trackS, trackD, trackJ, trackK, trackL);
		track.setPrefWidth(tracksWidth);
		// 嵌套 AP，让轨道可选
		selectorRegion = new Region();
		selectorRegion.setBorder(new BorderX("#BB6469").width(2).dashed().build());
		// 轨道和进度条
		selector = new AnchorPane(track, trackPB);
		AnchorPaneX.def(trackPB, 0, -19, 0, -19); // 溢出 Slider
		AnchorPaneX.def(track, 32, 0, 0, 0);
		
		// 轨道和进度（可滚动）
		tracksSP = new ScrollPane(selector);
		tracksSP.setFocusTraversable(false);
		
		// 轨道进度和时轴
		AnchorPane axisAndTracks = new AnchorPane(tracksSP, trackAxis);
		AnchorPaneX.def(trackAxis, 0, 0, null, 0);
		AnchorPaneX.def(tracksSP);
		
		// 轨道标签和轨道
		BorderPane music = new BorderPane();
		music.setLeft(tracksLabel);
		music.setCenter(axisAndTracks);
		
		bottom = new BorderPane();
		bottom.setOpacity(0);
		bottom.setBackground(new BgFill("#FFFB").build());
		bottom.setPrefHeight(368);
		bottom.setTop(topCtrlsAndPB);
		bottom.setCenter(music);
		
		BorderPane root = new BorderPane();
		root.setTop(header);
		root.setBottom(bottom);

		setBackground(new BgImage(BG).build());
		getChildren().add(root);
	}
}