package insa.fr.msa.RequestManagementService.ressources;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import insa.fr.msa.RequestManagementService.model.Request;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@RestController
public class RequestManagementRessource {

	@Value("${db.connection}")
	private String dbConnection;
	
	@Value("${db.host}")
	private String dbHost;
	
	@Value("${db.port}")
	private String dbPort;
	
	@Value("${server.port}")
	private String ServerPort;
	
	@Value("${db.uri}")
	private String dbUri;
	
	@Value("${db.name}")
	private String dbName;
	
	@Value("${db.login}")
	private String dbLogin;
	
	@Value("${db.pwd}")
	private String dbPwd;
	
	public Connection Connect() throws SQLException {
        // Class.forName("com.mysql.cj.jdbc.Driver");
        Connection conn = DriverManager.getConnection(dbUri, dbLogin, dbPwd);
        return conn;   
    }
	
	@GetMapping("/requests")
	public List<Request> GetAllRequests() throws SQLException {
		List<Request> requestList = new ArrayList<Request>();
		String query = "SELECT * FROM requete";
		Connection db = Connect();
		Statement stm = db.createStatement();
		ResultSet rs = stm.executeQuery(query);
		while(rs.next()) {
			requestList.add(new Request(rs.getInt(1),rs.getInt(2),rs.getInt(3),rs.getString(4),rs.getString(5)));
		}
		return requestList;
	}
	
	@GetMapping("/requests/{id}")
	public Request GetRequestInformationById(@PathVariable("id") int id) throws SQLException {
		String query = "SELECT * FROM requete WHERE id = " + id;
		Connection db = Connect();
		Statement stm = db.createStatement();
		ResultSet rs = stm.executeQuery(query);
		if(rs.next()) {
			Request requete = new Request(rs.getInt(1),rs.getInt(2),rs.getInt(3),rs.getString(4),rs.getString(5));
			return requete;
		}
		return null;
	}
	
	@PutMapping("/request/{id}/{status}")
	public String changeRequestStatus(@PathVariable("id") int id , @PathVariable("status") String status) throws SQLException {
	
		String query = "UPDATE requete SET status = " + status + " WHERE id = " + id;
		Connection db = Connect();
		Statement stm = db.createStatement();
		stm.executeUpdate(query);
		
		return "Status changed";
	}
	
	
	@PutMapping("/acceptRequest/{idHelper}/{rqId}")
	public String  acceptRequest(@PathVariable("idHelper") int idHelper , @PathVariable("rqId") int rqId) throws SQLException {
		Connection db = Connect();
		String query = "UPDATE requete SET volontaire = " + idHelper  + " WHERE id = " + rqId;
		Statement stm = db.createStatement();
		stm.executeUpdate(query);
		
		return "Request Accepted";
	}
	
	@PostMapping("/addRequest")
	public String addRequest(@RequestBody Request newRequest) throws SQLException {
		String query ="INSERT INTO requete(patient,status,description) VALUES (?,?,?)";
		Connection db = Connect();
		PreparedStatement pstm = db.prepareStatement(query);
		pstm.setInt(1, newRequest.getIdNeedy());
		pstm.setString(2, "WAITING FOR HELPER");
		pstm.setString(3, newRequest.getDescription());
		
		pstm.executeUpdate();
		
		return "Added" + newRequest.ToString();
	}
	
	@DeleteMapping("/delRequest/{id}")
	public String delRequest(@PathVariable("id") int id) throws SQLException {
		String query = "DELETE FROM requete WHERE id = " + id;
		Connection db = Connect();
		Statement stm = db.createStatement();
		stm.executeUpdate(query);
		return "Request Deleted";
	}
	
	@GetMapping("/requests/user/{id}")
	public List<Request> getUserRequests(@PathVariable("id") int id) throws SQLException{
		List<Request> listRequest = new ArrayList<Request>();
		String query = "SELECT * FROM requete WHERE volontaire = " + id + " OR patient = " + id;
		Connection db = Connect();
		Statement stm = db.createStatement();
		ResultSet rs = stm.executeQuery(query);
		while(rs.next()) {
			listRequest.add(new Request(rs.getInt(1),rs.getInt(2),rs.getInt(3),rs.getString(4),rs.getString(5)));
		}
		return listRequest;
	}
	
	@PutMapping("/refuseRequest/{idRq}")
	public String refuseRequest(@PathVariable("idRq") int idRq, @RequestParam("commentaire") String commentaire) throws SQLException {
	    System.out.println(commentaire);
	    String query = "UPDATE requete SET description = ? WHERE id = ?";
	    
	    try (Connection db = Connect(); 
	         PreparedStatement stmt = db.prepareStatement(query)) {
	        
	        stmt.setString(1, "Refus : " + commentaire); 
	        stmt.setInt(2, idRq);
	        
	        int rowsUpdated = stmt.executeUpdate();
	        
	        if (rowsUpdated > 0) {
	            return "Request refused with success";
	        } else {
	            return "Request not found";
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return "Error processing the request: " + e.getMessage();
	    }
	}

}
