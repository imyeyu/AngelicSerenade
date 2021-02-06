package net.imyeyu.as.view;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import net.imyeyu.as.AngelicSerenade;
import net.imyeyu.as.component.ASButton;
import net.imyeyu.engine.bean.Transition;
import net.imyeyu.engine.core.View;
import net.imyeyu.itools.IOUtils;
import net.imyeyu.itools.config.Config;
import net.imyeyu.px.Zpix;
import net.imyeyu.px.extend.BgImage;

/**
 * 关于页面视图层
 * 
 * @author 夜雨
 * @createdAt 2021-01-21 15:40:26
 *
 */
public class ViewAbout extends View {
	
	protected Config config = AngelicSerenade.getConfig();

	public static final Image BG = new Image(AngelicSerenade.RES_PATH + "bg_about.png");

	protected VBox feedback, root;
	protected ASButton keep, giveUp, back;
	protected Hyperlink officialLink, kogadoLink, zpixLink, linkEngine, linkAS, blogLink;
	protected ScrollPane rootSP;

	public void onLaunch() {
		setHideKeyFrames(Transition.toKeyFrames(this, Transition.OPACITY_TO_0, 500));
		
		// 标题
		Label title = new Label("- 关于 -");
		Zpix.css(title, Zpix.L);
		TextFlow titleFlow = new TextFlow(title);
		titleFlow.setTextAlignment(TextAlignment.CENTER);
		// 内容
		Label crExplain = new Label(IOUtils.jarFileToString(AngelicSerenade.RES_PATH.substring(1) + "CRExplain.txt"));
		crExplain.setWrapText(true);
		Label other = new Label("附: ");
		// 链接
		Label labelOfficial = new Label("《天使恋曲》官方网站\n（2004 互联网存档）: ");
		officialLink = new Hyperlink("https://web.archive.org/web/20041011000209/http://www.kogado.com/html/kuroneko/as/AngelicSerenade2.htm");
		officialLink.setWrapText(true);
		kogadoLink = new Hyperlink("https://www.kogado.com");
		Label labelZpix = new Label("最像素字体: ");
		zpixLink = new Hyperlink("https://github.com/SolidZORO/zpix-pixel-font");
		zpixLink.setWrapText(true);
		// 链接表格
		final Insets labelMargin = new Insets(6, 0, 0, 0);
		GridPane links = new GridPane();
		GridPane.setMargin(labelOfficial, labelMargin);
		GridPane.setMargin(labelZpix, labelMargin);
		GridPane.setValignment(labelOfficial, VPos.TOP);
		GridPane.setValignment(labelZpix, VPos.TOP);
		links.setVgap(4);
		ColumnConstraints colLabel = new ColumnConstraints();
		colLabel.setPrefWidth(340);
		colLabel.setHalignment(HPos.RIGHT);
		ColumnConstraints colValue = new ColumnConstraints();
		colValue.setPrefWidth(460);
		links.getColumnConstraints().addAll(colLabel, colValue);
		links.addColumn(0, labelOfficial, new Label("工畫堂工作室: "), labelZpix);
		links.addColumn(1, officialLink, kogadoLink, zpixLink);
		// 说明
		Label explain = new Label(IOUtils.jarFileToString(AngelicSerenade.RES_PATH.substring(1) + "AboutExplain.txt"));
		explain.setWrapText(true);
		Label ask = new Label("你喜欢上面的设计吗?");
		keep = new ASButton("希望继续开发下去", Pos.CENTER_LEFT);
		keep.setAlignment(Pos.CENTER_LEFT);
		giveUp = new ASButton("程序卡顿，体验不好，没有必要再开发", Pos.CENTER_LEFT);
		giveUp.setAlignment(Pos.CENTER_LEFT);
		feedback = new VBox(ask, keep, giveUp);
		feedback.setSpacing(8);
		
		Label idea = new Label("如果你有大胆的想法:");
		
		Label labelEngine = new Label("夜雨游戏引擎: ");
		linkEngine = new Hyperlink("https://github.com/imyeyu/YeyuEngine");
		linkEngine.setWrapText(true);
		TextFlow tfEngine = new TextFlow(labelEngine, linkEngine);
		
		Label labelAS = new Label("天使恋曲: ");
		linkAS = new Hyperlink("https://github.com/imyeyu/AngelicSerenade");
		linkAS.setWrapText(true);
		TextFlow tfAS = new TextFlow(labelAS, linkAS);
		
		VBox ideaBox = new VBox(idea, tfEngine, tfAS);
		ideaBox.setSpacing(8);
		
		// 博客
		TextFlow dever = new TextFlow(new Label("开发者: 夜雨"));
		dever.setTextAlignment(TextAlignment.CENTER);
		
		blogLink = new Hyperlink("https://www.imyeyu.net");
		TextFlow blog = new TextFlow(new Label("个人博客: "), blogLink);
		blog.setTextAlignment(TextAlignment.CENTER);
		
		back = new ASButton("返回", Pos.CENTER);
		// 主容器
		final Insets pPadding = new Insets(32, 0, 0, 0);
		root = new VBox(titleFlow, crExplain, other, links, explain);
		VBox.setMargin(crExplain, pPadding);
		VBox.setMargin(explain, pPadding);
		VBox.setMargin(ideaBox, pPadding);
		VBox.setMargin(dever, pPadding);
		VBox.setMargin(back, pPadding);
		root.setSpacing(10);
		root.setPadding(new Insets(48, 32, 48, 32));
		if (config.isNot("feedback")) {
			root.getChildren().add(feedback);
		}
		root.getChildren().addAll(ideaBox, dever, blog, back);
		
		rootSP = new ScrollPane(root);
		rootSP.setStyle("-fx-background-color: #FFF9");
		rootSP.setFitToWidth(true);

		setBackground(new BgImage(BG).build());
		setPadding(new Insets(0, 260, 0, 260));
		getChildren().add(rootSP);
	}
}