package application;


public class News {
	private Integer id;
	private String urlText;
	private String trustworthiness;
	private String prediction_percentage;
	
	public News() {
		
	}
	
	public News(Integer id, String urlText, String trustworthiness, String prediction_percentage) {
		this.id = id;
		this.urlText = urlText;
		this.trustworthiness = trustworthiness;
		this.prediction_percentage = prediction_percentage;
	}
	
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getTextArticle() {
		return urlText;
	}
	public void setTextArticle(String urlText) {
		this.urlText = urlText;
	}
	
	public String getTrustworthiness() {
		return trustworthiness;
	}
	
	public void setTrustworthiness(String trustworthiness) {
		 this.trustworthiness= trustworthiness;
	}
	
	public String getPredictionPercentage() {
		return prediction_percentage;
	}
	
	public void setPredictionPercentage(String prediction_percentage) {
		 this.prediction_percentage= prediction_percentage;
	}
	
	
}
