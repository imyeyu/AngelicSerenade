package net.imyeyu.as.component;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import net.imyeyu.engine.bean.Size;
import net.imyeyu.engine.core.Dialog;
import net.imyeyu.px.PixelFX;

/**
 * 加载中会话框
 * 
 * @author 夜雨
 * @createdAt 2021-02-04 21:25:12
 *
 */
public class DialogLoading extends Dialog<BorderPane> {
	
	private Label title, content;
	private ProgressBar pb;
	private SimpleDoubleProperty pbValue = new SimpleDoubleProperty(-1);

	public void onLaunch() {
		setSize(Size.S);
		
		ImageView icon = new ImageView(WARNING);
		
		title = new Label("正在加载..");
		title.setTextFill(PixelFX.GRAY);
		
		content = new Label();
		
		VBox contentBox = new VBox(title, content);
		contentBox.setAlignment(Pos.TOP_CENTER);
		contentBox.setSpacing(6);
		
		pb = new ProgressBar();
		pb.setPrefWidth(dialog.getMaxWidth() - dialog.getPadding().getLeft() - dialog.getPadding().getRight());
		
		BorderPane.setMargin(icon, new Insets(16, 0, 12, 0));
		BorderPane.setAlignment(icon, Pos.CENTER);
		dialog.setTop(icon);
		dialog.setCenter(contentBox);
		dialog.setBottom(pb);
		
		pbValue.addListener((obs, o, pbv) -> {
			if (pbv.intValue() != -1) {
				if (o.intValue() == -1) {
					pb.setProgress(0);
				}
				isAdding = pb.getProgress() < pbv.doubleValue();
				isReducing = !isAdding;
			} else {
				pb.setProgress(-1);
			}
		});
	}
	
	/**
	 * 设置标题
	 * 
	 * @param title
	 */
	public void setTitle(String title) {
		this.title.setText(title);;
	}
	
	/**
	 * 显示文本
	 * 
	 * @param text
	 */
	public void setText(String text) {
		this.content.setText(text);
	}
	
	public DoubleProperty progressProperty() {
		return pbValue;
	}

	private double pbNow = 0;
	private boolean isAdding = false, isReducing = false;
	protected void onUpdate(double time) {
		super.onUpdate(time);
		
		pbNow = pb.getProgress();
		if (isAdding) {
			pb.setProgress(pbNow + .01);
			if (pbValue.get() < pbNow) {
				isAdding = false;
			}
		}
		if (isReducing) {
			pb.setProgress(pbNow - .01);
			if (pbNow < pbValue.get()) {
				isReducing = false;
			}
		}
	}
}