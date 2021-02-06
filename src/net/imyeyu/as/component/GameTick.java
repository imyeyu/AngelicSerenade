package net.imyeyu.as.component;

import static net.imyeyu.engine.Framework.*;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import net.imyeyu.as.bean.Track;
import net.imyeyu.px.PixelFX;

/**
 * 轨道点（游戏中）
 * 
 * @author 夜雨
 * @createdAt 2021-01-25 16:15:31
 *
 */
public class GameTick implements Comparable<GameTick> {
	
	private static final double DEFAULT_X = engine.getConfig().getWidth() + 50;
	
	private final Rectangle r = new Rectangle();
	private final Ellipse e = new Ellipse();
	private final Text t = new Text();
	
	private final int rxd = 17;  // d 矩形偏移
	private final int rxp = -22; // p 矩形偏移
	private final int tx  = -10; // 文本偏移
	
	private int ms;                      // 数据
	private Track track;                 // 所属轨道
	private Paint color;                 // 颜色
	private double activeABSms;          // 激活时的绝对偏差
	private double x = DEFAULT_X, y = 0; // 位置
	private double offset = 0;           // 纵轴偏移
	
	//           d 音符类型    正在激活状态        被激活过           已错过            
	private boolean isd = false, isActive = false, wasActived = false, isMiss = false;
	
	public GameTick(Track track, int ms) {
		this.ms = ms;
		this.track = track;
		this.isd = track == Track.A || track == Track.S || track == Track.D;
		this.color = Track.getColor(track);
		
		y = Track.getOffset(track);
		if (isd) {
			r.setX(x + rxd);
			r.setY(y - 38);
			t.setX(x + tx);
		} else {
			r.setX(x + rxp);
			r.setY(y + 63);
			t.setX(x + tx);
		}
		// 柄
		r.setFill(color);
		r.setWidth(6);
		r.setHeight(103);
		// 圆
		e.setFill(color);
		e.setRotate(-15);
		e.setRadiusX(24);
		e.setRadiusY(18);
		e.setCenterX(x);
		e.setCenterY(y + 64);
		// 字
		t.setY(y + 74);
		t.setText(track.toString());
		t.setFill(Color.WHITE);
		t.setStyle("-fx-font-family: Consolas; -fx-font-weight: bold; -fx-font-size: 36");
	}
	
	/**
	 * 偏移值
	 * 
	 * @param x
	 */
	public void setX(double x) {
		this.x = x;
		if (isd) {
			d(x);
		} else {
			p(x);
		}
		if (isActive) { // 激活态
			if (isd) {
				r.setY(y - 38 + offset);
			} else {
				r.setY(y + 60 + offset);
			}
			e.setCenterY(y + 64 + offset);
			t.setY(y + 74 + offset);
			offset--;
			if (offset == -1) {
				isActive = false;
			}
		}
	}
	
	/**
	 * d 音符
	 * 
	 * @param x
	 */
	private void d(double x) {
		r.setX(x + rxd);
		e.setCenterX(x);
		t.setX(x + tx);
	}
	
	/**
	 * p 音符
	 * 
	 * @param x
	 */
	private void p(double x) {
		r.setX(x + rxp);
		e.setCenterX(x);
		t.setX(x + tx);
	}
	
	/**
	 * 激活
	 * 
	 */
	public void active() {
		isActive = true;
		
		wasActived = isActive;
		isMiss = !isActive;
		
		offset = 20;
	}
	
	/**
	 * 被激活过
	 * 
	 * @return
	 */
	public boolean wasActived() {
		return wasActived;
	}
	
	/**
	 * 获取激活时的绝对偏差
	 * 
	 * @return
	 */
	public double getActiveABSms() {
		return activeABSms;
	}
	
	/**
	 * 设置激活时的绝对偏差
	 * 
	 * @param activeABSms
	 */
	public void setActiveABSms(double activeABSms) {
		this.activeABSms = activeABSms;
	}
	
	/**
	 * 错过
	 * 
	 */
	public void miss() {
		isMiss = true;
		r.setFill(PixelFX.GRAY);
		e.setFill(PixelFX.GRAY);
	}
	
	/**
	 * 已错过
	 * 
	 * @return
	 */
	public boolean isMiss() {
		return isMiss;
	}
	
	public double getX() {
		return x;
	}
	
	public int getMS() {
		return ms;
	}
	
	public Track getTrack() {
		return track;
	}
	
	public Node[] getNodes() {
		Node[] nodes = {r, e, t};
		return nodes;
	}
	
	/**
	 * 排序
	 * 
	 */
	public int compareTo(GameTick t) {
		return getMS() - t.getMS();
	}
}