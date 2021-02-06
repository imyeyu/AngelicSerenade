package net.imyeyu.as.component;

import javafx.beans.property.SimpleListProperty;
import javafx.collections.ListChangeListener;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.Border;
import javafx.scene.paint.Paint;
import net.imyeyu.px.PixelFX;
import net.imyeyu.px.extend.BgFill;
import net.imyeyu.px.extend.BorderX;

/**
 * 编辑器曲谱轨道组件
 * 
 * @author 夜雨
 * @createdAt 2021-01-26 16:47:56
 *
 */
public class EditorTrack extends AnchorPane {

	private static final Border BORDER = new BorderX(PixelFX.LIGHT_GRAY).bottom().build();

	private Background bg;

	public EditorTrack(Paint color) {
		bg = new BgFill(color).build();
		
		setBorder(BORDER);
		setPrefHeight(40);
	}
	
	/**
	 * 绑定轨道数据
	 * 
	 * @param ticks 数据列
	 */
	public void bindTicks(SimpleListProperty<EditorTick> ticks) {
		// 数据绑定 UI
		ticks.addListener((ListChangeListener<EditorTick>) c -> {
			while (c.next()) {
				if (c.wasAdded()) {
					// 添加
					for (EditorTick tick : c.getAddedSubList()) {
						tick.setBackground(bg);
						getChildren().add(tick);
					}
					continue;
				}
				if (c.wasRemoved()) {
					// 移除
					for (EditorTick tick : c.getRemoved()) {
						getChildren().remove(tick);
					}
				}
			}
		});
	}
}