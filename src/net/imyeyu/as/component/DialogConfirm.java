package net.imyeyu.as.component;

import static net.imyeyu.engine.Framework.engine;

import javafx.geometry.Pos;
import net.imyeyu.engine.bean.Size;
import net.imyeyu.px.bean.TipsLevel;

/**
 * 确认会话
 * 
 * @author 夜雨
 * @createdAt 2021-01-23 17:25:31
 *
 */
public class DialogConfirm extends DialogText {
	
	private static DialogConfirm dialog;
	
	private boolean canCancel = true;
	private ASButton yes, no, cancel;
	
	OnYes onYes;
	OnNo onNo;
	OnCancel onCancel;
	
	public void onLaunch() {
		super.onLaunch();
		
		yes = new ASButton("是", Pos.CENTER);
		no = new ASButton("否", Pos.CENTER);
		cancel = new ASButton("取消", Pos.CENTER);
		
		yes.setOnAction(event -> {
			engine.dialogClose();
			if (onYes != null) {
				onYes.handle();
			}
		});
		no.setOnAction(event -> {
			engine.dialogClose();
			if (onNo != null) {
				onNo.handle();
			}
		});
		cancel.setOnAction(event -> {
			engine.dialogClose();
			if (onCancel != null) {
				onCancel.handle();
			}
		});
		
		setButtons(yes, no, cancel);
	}
	
	/**
	 * 不允许取消
	 * 
	 * @return
	 */
	public DialogConfirm notCancel() {
		canCancel = false;
		return this;
	}
	
	/**
	 * 尺寸
	 * 
	 * @param size
	 * @return
	 */
	public DialogConfirm size(Size size) {
		setSize(size);
		return this;
	}
	
	/**
	 * 等级
	 * 
	 * @param level
	 * @return
	 */
	public DialogConfirm level(TipsLevel level) {
		setLevel(level);
		return this;
	}
	
	/**
	 * 标题（如果要设置等级，优先调用 level()，否则会覆盖）
	 * 
	 * @param title
	 * @return
	 */
	public DialogConfirm title(String title) {
		setTitle(title);
		return this;
	}
	
	/**
	 * 确定事件
	 * 
	 * @param onYes
	 * @return
	 */
	public DialogConfirm onYes(OnYes onYes) {
		this.onYes = onYes;
		return this;
	}
	
	/**
	 * 否定事件
	 * 
	 * @param onNo
	 * @return
	 */
	public DialogConfirm onNo(OnNo onNo) {
		this.onNo = onNo;
		return this;
	}
	
	/**
	 * 取消事件
	 * 
	 * @param onCancel
	 * @return
	 */
	public DialogConfirm onCancel(OnCancel onCancel) {
		this.onCancel = onCancel;
		return this;
	}
	
	/**
	 * 关闭事件
	 * 
	 * @param event
	 */
	public DialogConfirm onClose(OnClose event) {
		super.onClose = event;
		return this;
	}
	
	/**
	 * 显示消息
	 * 
	 * @param msg 内容
	 */
	public void msg(String msg) {
		super.setMsg(msg);
		if (canCancel) {
			setButtons(yes, no, cancel);
		} else {
			setButtons(yes, no);
		}
		engine.dialogShow("confirm");
	}
	
	/**
	 * 会话对象
	 * 
	 * @return
	 */
	public static DialogConfirm instance() {
		if (dialog == null) {
			 dialog = (DialogConfirm) engine.getDialog("confirm");
		}
		dialog.onYes(null);
		dialog.onNo(null);
		dialog.onCancel(null);
		dialog.onClose(null);
		
		dialog.size(Size.S);
		dialog.level(TipsLevel.TIPS);
		return dialog;
	}
	
	public static interface OnYes {
		void handle();
	}
	
	public static interface OnNo {
		void handle();
	}
	
	public static interface OnCancel {
		void handle();
	}
}