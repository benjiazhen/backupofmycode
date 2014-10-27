package com.bing.face_re;

public class FacePojo {

	private int id;
	private int idtwo;
	private String name;
	private String path;

	public FacePojo() {
	}

	public FacePojo(int id, String name, String path, int idtwo) {
		super();
		this.id = id;
		this.idtwo = idtwo;
		this.name = name;
		this.path = path;
		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	public int getIdtwo() {
		return idtwo;
	}

	public void setIdtwo(int idtwo) {
		this.idtwo = idtwo;
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	};

}
