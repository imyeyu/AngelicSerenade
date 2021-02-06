package net.imyeyu.as.component;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import net.imyeyu.as.AngelicSerenade;
import net.imyeyu.as.bean.Sound;
import net.imyeyu.as.service.SoundService;

/**
 * 按钮
 * 
 * @author 夜雨
 * @createdAt 2021-01-23 19:53:42
 *
 */
public class ASButton extends HBox {
	
	public static final Image ICON_LEFT = new Image(AngelicSerenade.RES_PATH + "menu_hover_left.png");
	public static final Image ICON_RIGHT = new Image(AngelicSerenade.RES_PATH + "menu_hover_right.png");
	
	public static final DropShadow SHADOW_LEFT = new DropShadow();
	public static final DropShadow SHADOW_RIGHT = new DropShadow();
	public static final DropShadow SHADOW_HOVER = new DropShadow();
	
	private Button btn;
	
	{
		SHADOW_LEFT.setRadius(4);
		SHADOW_LEFT.setOffsetX(0);
		SHADOW_LEFT.setOffsetY(0);
		SHADOW_LEFT.setSpread(.2);
		SHADOW_LEFT.setColor(Color.valueOf("#FF3300CC"));
		
		SHADOW_RIGHT.setRadius(4);
		SHADOW_RIGHT.setOffsetX(0);
		SHADOW_RIGHT.setOffsetY(0);
		SHADOW_RIGHT.setSpread(.2);
		SHADOW_RIGHT.setColor(Color.valueOf("#008DCBCC"));
		
		SHADOW_HOVER.setRadius(4);
		SHADOW_HOVER.setOffsetX(0);
		SHADOW_HOVER.setOffsetY(0);
		SHADOW_HOVER.setSpread(.2);
		SHADOW_HOVER.setColor(Color.valueOf("#2BB53BCC"));
	}

	/**
	 * <br>按钮构造器
	 * <br>只允许 CENTER_LEFT, CENTER_RIGHT 或 CENTER（代表左右都有图标）
	 * <br>pos 其他参数都表示不加图标
	 * 
	 * @param text 按钮文本
	 * @param pos  图标位置
	 */
	public ASButton(String text, Pos pos) {
		btn = new Button(text);
		
		btn.getStyleClass().clear();
		setSpacing(8);
		setAlignment(Pos.CENTER);
		
		switch (pos) {
			case CENTER:       centerBtn(); break;
			case CENTER_LEFT:  leftBtn();   break;
			case CENTER_RIGHT: rightBtn();  break;
			default:           defBtn();    break;
		}
		btn.hoverProperty().addListener((obs, o, isHover) -> {
			if (isHover) {
				btn.setEffect(SHADOW_HOVER);
				SoundService.play(Sound.HOVER);
			} else {
				if (pos == Pos.CENTER_RIGHT) {
					btn.setEffect(SHADOW_RIGHT);
				} else {
					btn.setEffect(SHADOW_LEFT);
				}
			}
		});
		btn.setOnMousePressed(event -> SoundService.play(Sound.CLICK));
		btn.disabledProperty().addListener((obs, o, isDisabled) -> {
			if (isDisabled) {
				btn.setEffect(null);
				btn.setOpacity(.6);
			} else {
				if (pos == Pos.CENTER_RIGHT) {
					btn.setEffect(SHADOW_RIGHT);
				} else {
					btn.setEffect(SHADOW_LEFT);
				}
			}
		});
		btn.disableProperty().bind(disabledProperty());
	}
	public ASButton(String text) {
		this(text, Pos.BASELINE_CENTER);
	}
	
	private void defBtn() {
		btn.setEffect(SHADOW_LEFT);
		getChildren().addAll(btn);
	}
	
	private void leftBtn() {
		ImageView iconL = new ImageView();
		btn.setEffect(SHADOW_LEFT);
		iconL.setImage(ICON_LEFT);
		getChildren().addAll(iconL, btn);
		iconL.visibleProperty().bind(btn.hoverProperty());
	}
	
	private void centerBtn() {
		ImageView iconL = new ImageView();
		ImageView iconR = new ImageView();
		btn.setEffect(SHADOW_LEFT);
		iconL.setImage(ICON_LEFT);
		iconR.setImage(ICON_RIGHT);
		getChildren().addAll(iconL, btn, iconR);
		iconL.visibleProperty().bind(btn.hoverProperty());
		iconR.visibleProperty().bind(btn.hoverProperty());
	}
	
	private void rightBtn() {
		ImageView iconR = new ImageView();
		btn.setEffect(SHADOW_RIGHT);
		iconR.setImage(ICON_RIGHT);
		getChildren().addAll(btn, iconR);
		iconR.visibleProperty().bind(btn.hoverProperty());
	}
	
	// 按钮常用方法传递
	public void setText(String text) {
		btn.setText(text);
	}
	public void setOnAction(EventHandler<ActionEvent> event) {
		btn.setOnAction(event);
	}
	
	/**
	 * 获取按钮对象
	 * 
	 * @return
	 */
	public Button getButton() {
		return btn;
	}
}