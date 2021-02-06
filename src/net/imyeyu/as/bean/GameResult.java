package net.imyeyu.as.bean;

/**
 * 游戏结果结算
 * 
 * @author 夜雨
 * @createdAt 2021-02-02 20:35:07
 *
 */
public class GameResult {

	private int scPerfect, ctsMax, scBase, scCTS, scTotal;
	private String level;
	
	
	public int getScPerfect() {
		return scPerfect;
	}

	public void setScPerfect(int scPerfect) {
		this.scPerfect = scPerfect;
	}

	public int getCtsMax() {
		return ctsMax;
	}

	public void setCtsMax(int ctsMax) {
		this.ctsMax = ctsMax;
	}

	public int getScBase() {
		return scBase;
	}

	public void setScBase(double scBase) {
		this.scBase = (int) scBase;
	}

	public int getScCTS() {
		return scCTS;
	}

	public void setScCTS(double scCTS) {
		this.scCTS = (int) scCTS;
	}

	public int getScTotal() {
		return scTotal;
	}

	public void setScTotal(double scTotal) {
		this.scTotal = (int) scTotal;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(double total, double scTotal) {
		this.level = scToLevel(total, scTotal);
	}
	
	/**
	 * 分数评估
	 * 
	 * @param total   理论完美总分
	 * @param scTotal 总分
	 * @return
	 */
	private String scToLevel(double total, double scTotal) {
		final double percent = scTotal / total;
		if (.95 < percent) return "SSS";
		if (.9  < percent) return "SS";
		if (.85 < percent) return "S";
		if (.8  < percent) return "A";
		if (.7  < percent) return "B";
		if (.6  < percent) return "C";
		return "D";
	}
}