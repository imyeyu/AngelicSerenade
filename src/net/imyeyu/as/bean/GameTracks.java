package net.imyeyu.as.bean;

import java.util.ArrayList;
import java.util.List;

import net.imyeyu.as.component.GameTick;

/**
 * 轨道数据对象（游戏中）
 * 
 * @author 夜雨
 * @createdAt 2021-01-26 17:12:25
 *
 */
public class GameTracks {
	
	private List<GameTick> a, s, d, j, k, l;

	public List<GameTick> getA() {
		return a;
	}

	public void setA(List<GameTick> a) {
		this.a = a;
	}

	public List<GameTick> getS() {
		return s;
	}

	public void setS(List<GameTick> s) {
		this.s = s;
	}

	public List<GameTick> getD() {
		return d;
	}

	public void setD(List<GameTick> d) {
		this.d = d;
	}

	public List<GameTick> getJ() {
		return j;
	}

	public void setJ(List<GameTick> j) {
		this.j = j;
	}

	public List<GameTick> getK() {
		return k;
	}

	public void setK(List<GameTick> k) {
		this.k = k;
	}

	public List<GameTick> getL() {
		return l;
	}

	public void setL(List<GameTick> l) {
		this.l = l;
	}
	
	public List<List<GameTick>> getTracks() {
		List<List<GameTick>> r = new ArrayList<>();
		r.add(a);
		r.add(s);
		r.add(d);
		r.add(j);
		r.add(k);
		r.add(l);
		return r;
	}
}