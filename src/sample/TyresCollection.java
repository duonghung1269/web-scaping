package sample;

import java.util.ArrayList;
import java.util.List;

public class TyresCollection {
	
	private int id;
	private String name;
	private String branch;
	private String pageUrl;
	private String imageUrl;
	private List<Tyres> tyresList = new ArrayList<>();
	
	public TyresCollection() {
		// TODO Auto-generated constructor stub
	}
	
	public TyresCollection(String name, String pageUrl, String imageUrl) {
		super();
		this.name = name;
		this.pageUrl = pageUrl;
		this.imageUrl = imageUrl;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public List<Tyres> getTyresList() {
		return tyresList;
	}
	public void setTyresList(List<Tyres> tyresList) {
		this.tyresList = tyresList;
	}

	public String getPageUrl() {
		return pageUrl;
	}

	public void setPageUrl(String pageUrl) {
		this.pageUrl = pageUrl;
	}
	
	public void addTyres(Tyres tyres) {
		tyresList.add(tyres);
	}

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
}
