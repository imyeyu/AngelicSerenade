package net.imyeyu.as.bean;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import net.imyeyu.as.component.EditorTick;

/**
 * 轨道数据对象（编辑器）
 * <br>6 轨道，数据为轨道节点，同时也是毫秒储存对象
 * 
 * @author 夜雨
 * @createdAt 2021-01-23 19:52:01
 *
 */
public class EditorTracks {

	private SimpleListProperty<EditorTick> a, s, d, j, k, l;
	private List<SimpleListProperty<EditorTick>> tracks;

	public EditorTracks() {
		a = new SimpleListProperty<>();
		s = new SimpleListProperty<>();
		d = new SimpleListProperty<>();
		j = new SimpleListProperty<>();
		k = new SimpleListProperty<>();
		l = new SimpleListProperty<>();
		
		a.set(FXCollections.observableArrayList());
		s.set(FXCollections.observableArrayList());
		d.set(FXCollections.observableArrayList());
		j.set(FXCollections.observableArrayList());
		k.set(FXCollections.observableArrayList());
		l.set(FXCollections.observableArrayList());

		tracks = new ArrayList<>();
		tracks.add(a);
		tracks.add(s);
		tracks.add(d);
		tracks.add(j);
		tracks.add(k);
		tracks.add(l);
	}
	
	/**
	 * 清除所有轨道点
	 * 
	 */
	public void clear() {
		for (int i = 0; i < tracks.size(); i++) {
			tracks.get(i).clear();
		}
	}
	
	public List<Integer> getDataA() {
		return toList(a);
	}
	
	public List<Integer> getDataS() {
		return toList(s);
	}
	
	public List<Integer> getDataD() {
		return toList(d);
	}
	
	public List<Integer> getDataJ() {
		return toList(j);
	}
	
	public List<Integer> getDataK() {
		return toList(k);
	}
	
	public List<Integer> getDataL() {
		return toList(l);
	}
	
	public List<Integer> toList(SimpleListProperty<EditorTick> ets) {
		List<Integer> r = new ArrayList<>();
		for (int i = 0; i < ets.size(); i++) {
			r.add(ets.get(i).getMS());
		}
		return r;
	}
	
	public SimpleListProperty<EditorTick> getA() {
		return a;
	}

	public void setA(SimpleListProperty<EditorTick> a) {
		this.a = a;
	}

	public SimpleListProperty<EditorTick> getS() {
		return s;
	}

	public void setS(SimpleListProperty<EditorTick> s) {
		this.s = s;
	}

	public SimpleListProperty<EditorTick> getD() {
		return d;
	}

	public void setD(SimpleListProperty<EditorTick> d) {
		this.d = d;
	}

	public SimpleListProperty<EditorTick> getJ() {
		return j;
	}

	public void setJ(SimpleListProperty<EditorTick> j) {
		this.j = j;
	}

	public SimpleListProperty<EditorTick> getK() {
		return k;
	}

	public void setK(SimpleListProperty<EditorTick> k) {
		this.k = k;
	}

	public SimpleListProperty<EditorTick> getL() {
		return l;
	}

	public void setL(SimpleListProperty<EditorTick> l) {
		this.l = l;
	}

	public List<SimpleListProperty<EditorTick>> getTracks() {
		return tracks;
	}

	public void setTracks(List<SimpleListProperty<EditorTick>> tracks) {
		this.tracks = tracks;
	}
}