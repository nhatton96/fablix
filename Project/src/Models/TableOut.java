package Models;

import java.util.ArrayList;
import java.util.List;

public class TableOut {
	String name;
	List<String> att = new ArrayList<String>();
	List<String> atttype = new ArrayList<String>();
	
	public void setName(String g) {
		this.name = g;
	}
	public void addAtt(String g) {
		if(!att.contains(g))
			att.add(g);
	}
	
	public void addType(String s) {
		if(!atttype.contains(s))
			atttype.add(s);
	}
}