package sample;

public class Tyres {

	private String name;
	private String sku;
	private String branch;
	private String width;
	private String profile;
	private String size;
	private String loadIndex;
	private String si;
	private String price;
	private String url;

	public Tyres() {
		// TODO Auto-generated constructor stub
	}
	
	public Tyres(String name, String sku, String branch, String width,
			String profile, String size, String loadIndex, String si,
			String price) {
		super();
		this.name = name;
		this.sku = sku;
		this.branch = branch;
		this.width = width;
		this.profile = profile;
		this.size = size;
		this.loadIndex = loadIndex;
		this.si = si;
		this.price = price;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public String getProfile() {
		return profile;
	}

	public void setProfile(String profile) {
		this.profile = profile;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getLoadIndex() {
		return loadIndex;
	}

	public void setLoadIndex(String loadIndex) {
		this.loadIndex = loadIndex;
	}

	public String getSi() {
		return si;
	}

	public void setSi(String si) {
		this.si = si;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
