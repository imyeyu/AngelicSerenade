package net.imyeyu.as.component;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.layout.Border;
import javafx.scene.layout.Region;
import net.imyeyu.px.PixelFX;
import net.imyeyu.px.extend.BorderX;

/**
 * 曲谱编辑器轨道点
 * 
 * @author 夜雨
 * @createdAt 2021-01-26 16:48:09
 *
 */
public class EditorTick extends Region {
	
	private static final Border SELECTED_BORDER = new BorderX(PixelFX.RED).width(2).build();

	private int ms;
	private SimpleBooleanProperty selectedProperty = new SimpleBooleanProperty(false);

	public EditorTick(int ms, double x) {
		this.ms = ms;
		selectedProperty.set(false);

		setPrefSize(8, 39);
		setLayoutX(x);
		selectedProperty.addListener((obs, o, isSelected) -> {
			setBorder(isSelected ? SELECTED_BORDER : null);
		});
	}

	/**
	 * 获取位置（毫秒）
	 * 
	 * @return
	 */
	public int getMS() {
		return ms;
	}
	
	/**
	 * 卧轨位置
	 * 
	 * @param ms
	 */
	public void setMs(Integer ms) {
		this.ms = ms;
	}

	/**
	 * 选择状态
	 * 
	 * @return
	 */
	public boolean isSelected() {
		return selectedProperty.get();
	}

	/**
	 * 选择
	 * 
	 */
	public void select() {
		selectedProperty.set(true);
	}

	/**
	 * 取消选择
	 * 
	 */
	public void clearSelect() {
		selectedProperty.set(false);
	}
}