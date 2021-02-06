package net.imyeyu.as.component;

import static net.imyeyu.engine.Framework.engine;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import net.imyeyu.as.bean.GameResult;
import net.imyeyu.engine.bean.Size;
import net.imyeyu.engine.core.Dialog;
import net.imyeyu.px.PixelFX;
import net.imyeyu.px.Zpix;

/**
 * 游戏结束分数结算
 * 
 * @author 夜雨
 * @createdAt 2021-02-02 10:31:04
 *
 */
public class GameFinishDialog extends Dialog<BorderPane> {
	
	private Label level, scPerfect, ctsMax, scBase, scCTS, scTotal;

	public void onLaunch() {
		level = new Label();
		level.setPadding(new Insets(8, 0, 8, 0));
		Zpix.css(level, Zpix.L);

		Label labelScPerfect = new Label("完美: ");
		Label labelCtsMax    = new Label("最多连击: ");
		Label labelScBase    = new Label("基础分: ");
		Label labelScCTS     = new Label("连击加成: ");
		Label labelScTotal   = new Label("总分: ");
		
		labelScPerfect.setTextFill(PixelFX.GRAY);
		labelCtsMax.setTextFill(PixelFX.GRAY);
		labelScBase.setTextFill(PixelFX.GRAY);
		labelScCTS.setTextFill(PixelFX.GRAY);
		labelScTotal.setTextFill(PixelFX.GRAY);
		
		scPerfect = new Label();
		ctsMax    = new Label();
		scBase    = new Label();
		scCTS     = new Label();
		scTotal   = new Label();
		scTotal.setPadding(new Insets(8, 0, 8, 0));
		Zpix.css(scTotal, Zpix.L);
		
		ColumnConstraints colLabel = new ColumnConstraints(240);
		colLabel.setHalignment(HPos.RIGHT);
		
		GridPane gp = new GridPane();
		gp.setPadding(new Insets(24, 0, 24, 0));
		gp.getColumnConstraints().add(colLabel);
		gp.addColumn(0, labelScPerfect, labelCtsMax, labelScBase, labelScCTS, labelScTotal);
		gp.addColumn(1, scPerfect, ctsMax, scBase, scCTS, scTotal);
		
		ASButton back = new ASButton("返回列表", Pos.CENTER);
		
		BorderPane.setAlignment(level, Pos.CENTER);
		dialog.setTop(level);
		dialog.setCenter(gp);
		dialog.setBottom(back);
		
		setSize(Size.L);
		
		back.setOnAction(event -> {
			engine.dialogClose();
			engine.gotoView("playSelector");
		});
	}
	
	/**
	 * 设置结算数据
	 * 
	 * @param result
	 */
	public void setData(GameResult result) {
		level.setText("\u2605 " + result.getLevel() + " \u2605");
		scPerfect.setText(String.valueOf(result.getScPerfect()));
		ctsMax.setText(String.valueOf(result.getCtsMax()));
		scBase.setText(String.valueOf(result.getScBase()));
		scCTS.setText(String.valueOf(result.getScCTS()));
		scTotal.setText(String.valueOf(result.getScTotal()));
	}
}