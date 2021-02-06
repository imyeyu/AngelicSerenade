package net.imyeyu.as.component;

import static net.imyeyu.engine.Framework.engine;

import javafx.geometry.Pos;
import net.imyeyu.engine.bean.Size;
import net.imyeyu.px.bean.TipsLevel;

/**
 * 提示会话
 * 
 * @author 夜雨
 * @createdAt 2021-01-23 10:26:58
 *
 */
public class DialogTips extends DialogText {
	
	private static DialogTips dialog;
	
	private ASButton ok;

	public void onLaunch() {
		super.onLaunch();
		
		ok = new ASButton("好", Pos.CENTER);
		ok.setOnAction(event -> engine.dialogClose());
		setButtons(ok);
	}
	
	/**
	 * 尺寸
	 * 
	 * @param size
	 * @return
	 */
	public DialogTips size(Size size) {
		setSize(size);
		return this;
	}
	/**
	 * 等级
	 * 
	 * @param level
	 * @return
	 */
	public DialogTips level(TipsLevel level) {
		setLevel(level);
		return this;
	}
	/**
	 * 标题（如果要设置等级，优先调用 level()，否则会覆盖）
	 * 
	 * @param title
	 * @return
	 */
	public DialogTips title(String title) {
		setTitle(title);
		return this;
	}
	
	/**
	 * 关闭事件
	 * 
	 * @param event
	 */
	public DialogTips onClose(OnClose event) {
		super.onClose = event;
		return this;
	}
	
	/**
	 * 成功
	 * 
	 * @param msg
	 */
	public void success(String msg) {
		setLevel(TipsLevel.SUCCESS);
		this.msg(msg);
	}
	
	/**
	 * 警告
	 * 
	 * @param msg 内容
	 */
	public void warning(String msg) {
		setLevel(TipsLevel.WARNING);
		this.msg(msg);
	}
	
	/**
	 * 错误
	 * 
	 * @param msg 内容
	 */
	public void error(String msg) {
		setLevel(TipsLevel.ERROR);
		this.msg(msg);
	}
	
	/**
	 * 显示消息
	 * 
	 * @param msg 内容
	 */
	public void msg(String msg) {
		super.setMsg(msg);
		engine.dialogShow("tips");
	}
	
	/**
	 * 会话对象
	 * 
	 * @return
	 */
	public static DialogTips instance() {
		if (dialog == null) {
			 dialog = (DialogTips) engine.getDialog("tips");
		}
		dialog.size(Size.S);
		dialog.level(TipsLevel.TIPS);
		return dialog;
	}
}
