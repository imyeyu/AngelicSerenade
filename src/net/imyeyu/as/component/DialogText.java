package net.imyeyu.as.component;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import net.imyeyu.engine.core.Dialog;
import net.imyeyu.px.bean.TipsLevel;

/**
 * 文本类型会话
 * 
 * @author 夜雨
 * @createdAt 2021-01-22 17:06:39
 *
 */
public abstract class DialogText extends Dialog<BorderPane> {
	
	private VBox header;
	private HBox bottom;
	private Text msg, cursor;
	private Label title;
	private ImageView icon;
	private StringBuilder sb;
	
	OnClose onClose;

	public void onLaunch() {
		title = new Label();
		icon = new ImageView();
		
		header = new VBox(title);
		header.setSpacing(6);
		header.setAlignment(Pos.CENTER);

		// 文本
		msg = new Text();
		cursor = new Text();
		TextFlow content = new TextFlow();
		content.setTextAlignment(TextAlignment.JUSTIFY);
		content.setPadding(new Insets(12, 8, 12, 8));
		content.getChildren().addAll(msg, cursor);
		content.setLineSpacing(4);
		
		bottom = new HBox();
		bottom.setSpacing(6);
		bottom.setAlignment(Pos.CENTER);
		
		dialog.setTop(header);
		dialog.setCenter(content);
		dialog.setBottom(bottom);
		
		sb = new StringBuilder();
	}
	
	/**
	 * 标题
	 * 
	 * @param title
	 */
	public void setTitle(String title) {
		this.title.setText("- " + title + " -");
	}
	
	/**
	 * 等级
	 * 
	 * @param level
	 */
	public void setLevel(TipsLevel level) {
		this.title.setText("- " + TipsLevel.toString(level) + " -");
		
		header.getChildren().clear();
		if (level != TipsLevel.TIPS) {
			switch (level) {
				case SUCCESS: icon.setImage(SUCCESS); break;
				case WARNING: icon.setImage(WARNING); break;
				case ERROR:   icon.setImage(ERROR);   break;
				default:                              break;
			}
			header.getChildren().addAll(icon, title);
		} else {
			header.getChildren().add(title);
		}
	}
	
	/**
	 * 内容
	 * 
	 * @param msg
	 */
	public void setMsg(String msg) {
		sb.setLength(0);
		sb.append(msg);
	}
	
	/**
	 * 底部按钮
	 * 
	 * @param btns
	 */
	public void setButtons(ASButton... btns) {
		bottom.getChildren().setAll(btns);
	}
	
	protected void onShow() {
		super.onShow();
		frame = 0;
		canRun = true;
	}
	
	private int frame = 0;
	private boolean canRun = false;
	protected void onUpdate(double time) {
		super.onUpdate(time);
		if (canRun) {
			if (frame <= sb.length()) {
				msg.setText(sb.substring(0, frame));
			}
			frame++;
			cursor.setText(5 < frame % 10 ? "_" : "");
		}
	}
	
	protected void onHide() {
		super.onHide();
		canRun = false;
		frame = 0;
		if (onClose != null) {
			onClose.handle();
			onClose = null;
		}
	}
	
	/**
	 * 会话关闭事件
	 * 
	 * @author 夜雨
	 * @createdAt 2021-01-23 18:37:57
	 *
	 */
	public static interface OnClose {
		void handle();
	}
}