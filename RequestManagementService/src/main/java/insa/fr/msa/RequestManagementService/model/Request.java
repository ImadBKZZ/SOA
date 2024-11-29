package insa.fr.msa.RequestManagementService.model;

public class Request {
	private int idRequest;
	private int idNeedy;
	private int idHelper;
	private String RqStatus;
	private String Description;
	
	public Request(int idRequest,int idNeedy,int idHelper,String RqStatus,String Description) {
		this.idRequest = idRequest;
		this.idNeedy = idNeedy;
		this.idHelper = idHelper;
		this.RqStatus = RqStatus;
		this.Description = Description;
	}

	public Request(int idNeedy,int idHelper,String RqStatus,String Description) {
		this.idNeedy = idNeedy;
		this.idHelper = idHelper;
		this.RqStatus = RqStatus;
		this.Description = Description;
	}
	
	public Request(int idNeedy,String RqStatus,String Description) {
		this.idNeedy = idNeedy;
		this.RqStatus = RqStatus;
		this.Description = Description;
	}

	public Request() {
		
	}
	public int getIdRequest() {
		return idRequest;
	}

	public void setIdRequest(int idRequest) {
		this.idRequest = idRequest;
	}

	public int getIdNeedy() {
		return idNeedy;
	}

	public void setIdNeedy(int idNeedy) {
		this.idNeedy = idNeedy;
	}

	public int getIdHelper() {
		return idHelper;
	}

	public void setIdHelper(int idHelper) {
		this.idHelper = idHelper;
	}

	public String getRqStatus() {
		return RqStatus;
	}

	public void setRqStatus(String rqStatus) {
		RqStatus = rqStatus;
	}

	public String getDescription() {
		return Description;
	}

	public void setDescription(String description) {
		Description = description;
	}
	
	public String ToString() {
		return "Request --> Request_id : " + this.idRequest + " RequestNeedyID : " + this.idNeedy + " RequestHelerId : " + this.idHelper + " RequestStatus : " + this.RqStatus + " RequestDescription : " + this.Description;
	}
}
