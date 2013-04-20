package com.app.chinastores;

public class Store {
	private char type;
	private String address;
	private double lat;
	private double lon;
	private double val;
	private double nval;
	private String foto;
	private String info;
	private String comments;
	private boolean confirmed;
	private final static String icon= "@drawable/icon";
	
	public Store(char type, String address, double val, String foto, String info, String comments){
	setType(type);
	setAddress(address);
	setVal(val);
	setNumval(0);
	setFoto(foto);
	setInfo(info);
	setComments(comments);
	}
	
	public Store(char type, String address, double val, String info){
		setType(type);
		setAddress(address);
		setVal(val);
		setNumval(0);
		setFoto(icon);
		setInfo(info);
		setComments("");
	}

	public boolean isConfirmed() {
		return confirmed;
	}

	public void setConfirmed(boolean confirmed) {
		this.confirmed = confirmed;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}
	public void addComent(String comment){
		this.comments += comment +"\n";
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getFoto() {
		return foto;
	}

	public void setFoto(String foto) {
		this.foto = foto;
	}

	public double getNumval() {
		return nval;
	}

	public void setNumval(double nval) {
		this.nval = nval;
	}
	
	public void valorar(int valoracion){
		double total = nval*val;
		total += valoracion;
		nval++;
		val=total/nval;		
	}

	public double getVal() {
		return val;
	}

	public void setVal(double val) {
		this.val = val;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public char getType() {
		return type;
	}

	public void setType(char type) {
		this.type = type;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
}
