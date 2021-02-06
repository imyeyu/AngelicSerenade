package net.imyeyu.as.view;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.transform.Rotate;
import javafx.util.Callback;
import net.imyeyu.as.AngelicSerenade;
import net.imyeyu.as.bean.ASFile;
import net.imyeyu.as.component.ASButton;
import net.imyeyu.engine.bean.Transition;
import net.imyeyu.engine.core.View;
import net.imyeyu.itools.EncodeUtils;
import net.imyeyu.px.PixelFX;
import net.imyeyu.px.Zpix;
import net.imyeyu.px.component.SVGIcon;
import net.imyeyu.px.component.Switch;
import net.imyeyu.px.extend.BgFill;
import net.imyeyu.px.extend.BgImage;
import net.imyeyu.px.extend.BorderX;

/**
 * 开始游戏曲目选择视图层
 * 
 * @author 夜雨
 * @createdAt 2021-01-23 20:39:51
 *
 */
public class ViewPlaySelector extends View {

	public static final Image BG = new Image(AngelicSerenade.RES_PATH + "bg_play_selector.jpg");

	protected VBox root;
	protected Label title, artist, album, year;
	protected Switch useCoverBg;
	protected SVGPath prev, next;
	protected ASButton play, back;
	protected StackPane cover;
	protected Background coverBg;
	protected ListView<ASFile> list;

	private static final Rotate ROTATE = new Rotate();
	private static final Insets PADDING_LISTCELL = new Insets(4, 14, 4, 14);
	private static final DropShadow COVER_SHADOW = new DropShadow();

	{
		ROTATE.setPivotX(0);
		ROTATE.setPivotY(28);
		ROTATE.setAngle(90);
		
		COVER_SHADOW.setRadius(0);
		COVER_SHADOW.setOffsetX(3);
		COVER_SHADOW.setOffsetY(3);
		COVER_SHADOW.setSpread(3);
		COVER_SHADOW.setColor(Color.valueOf("#0008"));
		
		coverBg = new BgFill("#FFFA").build();
	}

	public void onLaunch() {
		setShowKeyFrames(Transition.toKeyFrames(this, Transition.OPACITY_TO_1, 300));
		setHideKeyFrames(Transition.toKeyFrames(this, Transition.OPACITY_TO_0, 800));
		
		// 封面
		cover = new StackPane();
		cover.setBackground(coverBg);
		cover.setBorder(new BorderX("#999").width(2).build());
		cover.setEffect(COVER_SHADOW);
		// 选曲操作
		prev = new SVGIcon("M0,16h2v-2h2v-2h2v-2h2V8h2V6h2V4h2V2h2V0h2v34h-2v-2h-2v-2h-2v-2h-2v-2H8v-2H6v-2H4v-2H2v-2H0V16z");
		next = new SVGIcon("M18,18h-2v2h-2v2h-2v2h-2v2H8v2H6v2H4v2H2v2H0V0h2v2h2v2h2v2h2v2h2v2h2v2h2v2h2v2h2V18z");
		prev.setEffect(ASButton.SHADOW_LEFT);
		next.setEffect(ASButton.SHADOW_LEFT);

		BorderPane coverBox = new BorderPane();
		BorderPane.setAlignment(prev, Pos.CENTER);
		BorderPane.setAlignment(next, Pos.CENTER);
		BorderPane.setMargin(cover, new Insets(4, 16, 4, 16));
		coverBox.setLeft(prev);
		coverBox.setCenter(cover);
		coverBox.setRight(next);
		coverBox.setPrefHeight(260);
		// 歌曲列表
		list = new ListView<>();
		list.getStyleClass().add("play-list");
		list.setOrientation(Orientation.HORIZONTAL);
		list.setCellFactory(new Callback<ListView<ASFile>, ListCell<ASFile>>() {
			public ListCell<ASFile> call(ListView<ASFile> param) {
				return new ListCell<ASFile>() {
					protected void updateItem(ASFile item, boolean empty) {
						super.updateItem(item, empty);
						if (empty || item == null) {
							setGraphic(null);
						} else {
							Label label = new Label(item.getName());
							label.setAlignment(Pos.CENTER);
							if (!EncodeUtils.hasChinese(label.getText())) {
								label.setLayoutY(-24);
								label.getTransforms().add(ROTATE);
								label.setPrefSize(240, 24);
							} else {
								label.setWrapText(true);
								label.setPrefSize(28, 240);
							}
							label.opacityProperty().bind(item.opacityProperty());
							Pane pane = new Pane(label);
							pane.setPrefWidth(28);
							setPadding(PADDING_LISTCELL);
							setGraphic(pane);
						}
					}
				};
			}
		});
		list.setMaxHeight(265);
		list.setPlaceholder(new Label("没有可用歌曲"));
		
		// 歌曲信息
		Label labelTitle = new Label("歌曲名: ");
		Label labelArtist = new Label("演唱: ");
		Label labelAlbum = new Label("专辑: ");
		Label labelYear = new Label("出版年份: ");

		title = new Label();
		artist = new Label();
		album = new Label();
		year = new Label();
		
		Tooltip tipsTitle = new Tooltip();
		Tooltip tipsArtist = new Tooltip();
		Tooltip tipsAlbum = new Tooltip();
		Tooltip tipsYear = new Tooltip();

		final Insets infoPadding = new Insets(8, 0, 8, 0);
		GridPane info = new GridPane();
		Label[] labels = {labelTitle, labelArtist, labelAlbum, labelYear};
		Label[] values = {title, artist, album, year};
		Tooltip[] valuesTips = {tipsTitle, tipsArtist, tipsAlbum, tipsYear};
		for (int i = 0; i < labels.length; i++) {
			final int j = i;
			labels[i].setTextFill(PixelFX.GRAY);
			// 超长提示
			values[i].textProperty().addListener((obs, o, text) -> {
				String[] s = text.split("");
				int c = 0;
				for (int k = 0; k < s.length; k++) {
					c += s[k].getBytes().length > 1 ? 2 : 1;
				}
				if (23 < c) {
					values[j].setTooltip(valuesTips[j]);
				} else {
					values[j].setTooltip(null);
				}
			});
			valuesTips[i].textProperty().bind(values[i].textProperty());
			
			GridPane.setHalignment(labels[i], HPos.RIGHT);
			info.add(labels[i], 0, i + 1);
			info.add(values[i], 1, i + 1);
		}
		ColumnConstraints col = new ColumnConstraints();
		col.setMinWidth(130);
		info.getColumnConstraints().add(col);
		info.setPadding(infoPadding);
		info.setBorder(new BorderX(PixelFX.GRAY).width(1, 0, 1, 0).build());
		info.setVgap(6);
		
		useCoverBg = new Switch(" 使用封面作为游戏背景");
		useCoverBg.setSelected(true);
		HBox useCoverBgBox = new HBox(useCoverBg);
		useCoverBgBox.setAlignment(Pos.CENTER);
		
		play = new ASButton("开始", Pos.CENTER_LEFT);
		Zpix.css(play.getButton(), Zpix.L);
		back = new ASButton("返回", Pos.CENTER_RIGHT);
		Zpix.css(back.getButton(), Zpix.L);
		
		HBox action = new HBox(play, back);
		action.setAlignment(Pos.CENTER);
		action.setSpacing(64);

		root = new VBox(coverBox, list, info, useCoverBgBox, action);
		VBox.setMargin(info, infoPadding);
		VBox.setMargin(list, new Insets(0, 26, 0, 26));
		root.setMaxWidth(520);
		root.setSpacing(16);
		root.setPadding(new Insets(24, 32, 0, 32));
		root.setBackground(new BgFill("#FFF9").build());
		root.setTranslateX(-420);
		root.setOpacity(0);

		setBackground(new BgImage(BG).build());
		getChildren().add(root);
	}
}