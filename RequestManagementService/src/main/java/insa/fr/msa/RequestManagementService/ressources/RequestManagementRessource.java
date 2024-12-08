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

	
	public Connection Connect() throws SQLException {
        // Class.forName("com.mysql.cj.jdbc.Driver");
        Connection conn = DriverManager.getConnection("jdbc:mysql://srv-bdens.insa-toulouse.fr:3306/projet_gei_060", "projet_gei_060", "Yoor8Xei");
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
			requestList.add(new Request(rs.getInt(1),rs.getInt(2),rs.getInt(3),rs.getString(4),rs.getString(5),rs.getString(6)));
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
			Request requete = new Request(rs.getInt(1),rs.getInt(2),rs.getInt(3),rs.getString(4),rs.getString(5),rs.getString(6));
			return requete;
		}
		return null;
	}
	
	@PutMapping("/requests/{id}/{status}")
	public String changeRequestStatus(@PathVariable("id") int id , @PathVariable("status") String status) throws SQLException {
	
		String query = "UPDATE requete SET status = " + status + " WHERE id = " + id;
		Connection db = Connect();
		Statement stm = db.createStatement();
		stm.executeUpdate(query);
		
		return "Status changed";
	}
	
	
	@PutMapping("/requests/acceptRequest/{idHelper}/{rqId}")
	public String  acceptRequest(@PathVariable("idHelper") int idHelper , @PathVariable("rqId") int rqId) throws SQLException {
		Connection db = Connect();
		String query = "UPDATE requete SET volontaire = " + idHelper  + " WHERE id = " + rqId;
		Statement stm = db.createStatement();
		stm.executeUpdate(query);
		
		return "Request Accepted";
	}
	
	
	@PutMapping("/requests/admin/validate/{idrq}")
	public String ValidateRequest(@PathVariable("idrq") int idrq) throws SQLException {
		Connection db = Connect();
		String query = "UPDATE requete SET AdminResponse = 'ACCEPTED' WHERE id = " + idrq;
		Statement stm = db.createStatement();
		stm.executeUpdate(query);
		
		return "Requete Valider";
	}
	
	@PostMapping("/requests/addRequest")
	public String addRequest(@RequestBody Request newRequest) throws SQLException {
		String query ="INSERT INTO requete(patient,status,description,AdminResponse) VALUES (?,?,?,?)";
		Connection db = Connect();
		PreparedStatement pstm = db.prepareStatement(query);
		pstm.setInt(1, newRequest.getIdNeedy());
		pstm.setString(2, "WAITING FOR HELPER");
		pstm.setString(3, newRequest.getDescription());
		pstm.setString(4, "WAITING FOR APPROVAL");
		pstm.executeUpdate();
		
		return "Added" + newRequest.ToString();
	}

	@PostMapping("/requests/addMission")
	public String addMission(@RequestBody Request newRequest) throws SQLException {
		String query ="INSERT INTO requete(volontaire,status,description,AdminResponse) VALUES (?,?,?,?)";
		Connection db = Connect();
		PreparedStatement pstm = db.prepareStatement(query);
		pstm.setInt(1, newRequest.getIdHelper());
		pstm.setString(2, "LOOKING FOR NEEDY");
		pstm.setString(3, newRequest.getDescription());
		pstm.setString(4, "WAITING FOR APPROVAL");
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
			listRequest.add(new Request(rs.getInt(1),rs.getInt(2),rs.getInt(3),rs.getString(4),rs.getString(5),rs.getString(6)));
		}
		return listRequest;
	}
	
	@PutMapping("/refuseRequest/{idRq}")
	public String refuseRequest(@PathVariable("idRq") int idRq, @RequestParam("commentaire") String commentaire) throws SQLException {
	    System.out.println(commentaire);
	    String query = "UPDATE requete SET AdminResponse = ? WHERE id = ?";
	    
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

	@GetMapping("/requests/filter")
	public List<Request> filterRequestByStatus(@RequestParam("filters") String filters) throws SQLException{
		List<Request> filtered = new ArrayList<Request>();
		System.out.println(filters);
		String query = "SELECT * FROM requete WHERE status = " + "'" + filters + "'";
		Connection db = Connect();
		Statement stm = db.createStatement();
		ResultSet rs = stm.executeQuery(query);
		while(rs.next()) {
			filtered.add(new Request(rs.getInt(1),rs.getInt(2),rs.getInt(3),rs.getString(4),rs.getString(5),rs.getString(6)));
		}
		return filtered;
	}

	@GetMapping("/testazure")
	public String testAzure(){
		String ascii = """
			⠀⠀⠀⠀⠀⠀⠀⠀⢀⢔⠾⢋⠷⢃⠠⠒⠈⠀⢀⣀⢂⢠⣲⢦⡪⠝⠀⢠⠎⠀⠀⠀⠀⠀⣠⣴⢪⡃⠜⠋⠉⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠄⡀⠖⠁⠀⢀⠀⠀⡀⠀⠀⠀⠀⢀⣤⠦⡻⠂⠀⠀⠀⢼⡆⠀⠀⠁⡔⡀⡸⠀⢠⠃⠀⢸⣐⣷⣏⠉⠁⠉⢻⡄⠀⠀⡱⠑⢆⣨⠟⠊⠉⠀⠀⠈⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
⠀⠀⠀⠀⠀⠀⢀⢔⠕⢁⠔⠁⠐⣁⣤⠴⠚⠉⢀⣠⠖⡫⠃⠁⠀⠀⣰⠃⠀⠀⡠⠀⢠⠞⡵⠃⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣀⠀⢀⠔⠈⠀⠀⠀⠔⡰⢃⠔⠁⠀⠀⠀⠼⡫⣠⠎⠀⠀⢀⠤⠀⠀⠀⠀⠀⠀⠰⢁⠃⢀⠇⠀⠀⣼⠋⣟⡆⠇⢀⠀⠸⢎⢵⠀⠱⠱⡈⢧⠄⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
⠀⠀⠀⠀⠀⣠⡡⠁⢠⣁⣀⡴⡚⠅⠐⠈⢀⡴⠋⠐⠁⢀⡠⡤⠄⣰⠃⠀⠀⣰⠁⠀⣠⠊⠀⠀⠀⠀⢀⣀⣠⣤⠤⠶⠚⠋⠉⢀⡠⠀⠀⠀⠀⢠⣮⠞⣐⠅⠀⠀⠀⠀⢀⡴⠋⠁⣀⣠⠔⠁⠀⠀⢠⠃⠀⠀⠀⢦⠂⢠⠊⠀⠀⠚⠙⢰⢸⣷⢰⠈⢆⠀⠙⣮⢣⠀⠐⡔⡒⣧⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
⠀⠀⠀⠀⢠⠟⣠⠖⠋⢁⠚⠃⣀⠔⠚⡷⠉⣉⡤⡲⢭⠞⢉⠃⣰⠏⠀⠀⡴⣉⡀⡚⠁⠑⠒⠀⡛⠛⢉⢁⠄⠀⣠⠗⠀⢀⡴⠋⠀⢀⠤⢠⢾⠋⢡⠞⠁⠀⡠⠒⠀⢀⣎⣠⢞⢵⠟⠁⠀⢀⠔⠠⠃⠀⡔⡐⠀⡇⠀⠁⠀⠀⠀⠀⡃⠈⠀⣿⠈⡀⡇⠣⡀⠈⢧⠡⡀⠈⢊⢜⣧⡀⠀⠀⠀⠀⠀⠀⠀⣀⡠⢤⣤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
⠀⠀⠀⠀⢸⢶⠃⠀⢀⠃⢠⡞⠁⢀⡼⡷⢋⣥⣮⠴⠁⡠⣵⢻⡟⠀⢀⡼⢋⢊⠌⠀⡠⠊⢀⠊⢀⣀⣆⠃⠀⣰⠃⢀⡴⠋⠀⢀⠔⠕⡡⠞⣠⠝⠁⠀⣠⠊⢀⣤⠖⣡⠞⣕⡡⠁⠀⣠⡞⡡⣶⡵⠀⣸⢣⠇⢸⡟⠀⠀⠀⡀⠀⢠⠁⡆⠀⡷⠀⡇⣏⡄⢻⠄⠀⠱⡷⣄⠀⠡⡹⡇⠀⢀⡀⠄⠒⠈⠁⠀⠀⠀⠀⠉⠓⢤⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
⠀⠀⠀⠀⠈⠙⠀⠀⡎⢠⠋⠀⣠⡮⠔⠈⣩⠞⠁⢀⢊⡾⢡⡄⠁⢠⡾⠡⠡⢂⠠⠊⢀⠔⣀⡴⢋⡏⠎⠀⣸⠃⣰⠟⠁⠀⡐⠁⡡⡊⠔⠈⡁⢐⣔⡟⢡⠞⡑⣡⠎⣡⠞⠝⠀⢀⣮⢟⠊⡸⠹⠁⢰⠃⣼⠀⣿⠂⢰⠀⢰⠃⠀⡌⢰⡗⢰⡧⠀⢫⡷⠇⠀⣎⢆⠀⡇⠏⢳⡤⠜⠓⠈⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠈⠓⢄⡀⠀⠀⠀⠀⠀⠀⠀⠀
⠀⠀⠀⠀⠀⠀⠀⠀⠑⣿⣖⡾⠋⠀⡠⠊⠁⢠⣖⣵⡭⡂⠁⠘⠄⢳⠁⡶⠓⣡⣰⣖⡥⠞⠁⣀⢼⢱⠀⣰⢃⡼⠃⠀⢠⡪⣪⠞⠋⡀⢔⣠⠦⠛⠉⣠⡳⢊⡴⢣⠞⢁⠊⠀⣠⡿⠛⢁⠎⢠⡳⢡⠏⢸⠟⢠⢟⠀⢸⠀⢸⠁⠀⢁⡿⠁⢠⠇⠀⠘⡇⠘⠀⡆⣾⠠⠓⠉⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠙⠦⡀⠀⠀⠀⠀⠀⠀
⠀⠀⠀⠀⠀⠀⠀⠀⡀⠝⠛⠀⡠⢊⣠⠾⠗⡾⠁⢳⠀⣽⡄⠀⢘⠾⣊⠴⢋⡵⢫⣷⣃⢀⠔⠁⣿⠆⠀⠀⡞⢁⠀⠴⠛⠘⣀⣔⡬⢖⠋⠁⢀⠔⣶⡟⠡⢈⡕⠛⠠⠂⠀⣰⠋⠀⡰⠁⢀⣧⢡⡎⢀⠟⠀⣸⢹⠀⢸⠀⡸⠀⠀⣾⠁⠀⡌⠀⣀⡸⠙⣄⠶⠋⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠉⠦⡀⠀⠀⠀⠀
⠀⠀⠀⠀⠀⠀⠀⠉⠀⠀⣰⣪⠖⠋⠀⢀⠜⢀⢌⠜⡆⣿⠼⣺⢗⣟⣡⡎⢡⠃⣤⠹⠘⠢⡤⢄⣛⡐⠠⠼⠍⠐⠀⣀⡤⡞⠉⠁⣤⠋⢀⠔⠁⣼⠏⠐⢠⠎⠐⠰⠃⠀⡼⠁⠀⡜⠀⡰⣻⢃⡞⠀⣸⠃⢀⡟⢰⠀⢸⠀⣷⠀⣸⠁⠀⡘⠀⣼⡿⠁⢠⢏⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠙⢦⠀⠀⠀
⠀⠀⠀⠀⠀⠀⠀⠀⠀⢎⣸⠃⠀⠀⡠⢃⣴⠟⣡⣴⣿⣷⠛⠛⡈⡇⢠⠗⢸⣾⠸⡇⠀⠀⠈⠑⡾⣫⢒⣴⣶⢟⠵⢡⠌⠀⠀⠔⠃⡠⠁⠀⣾⢋⠌⡰⠁⠀⠠⢁⡄⠐⠀⠀⢞⡒⡰⢠⡏⡾⢠⢧⡏⠀⢺⠯⢥⠀⢸⠀⡽⢠⠃⠀⡰⠇⣸⠗⠃⢠⢳⡆⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠄⠀⠅⠀⠀⠀⠀⠈⠷⡄⠀
⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣨⣢⢔⣡⠖⢫⠽⠑⡟⠉⢸⢯⢏⠉⣵⡇⣸⠀⢘⢨⠃⡁⠓⠒⢢⢞⢜⣥⣫⣿⡧⠃⠀⡌⠀⠀⠈⠀⠊⠀⠀⣼⠃⠊⡐⠀⠀⢠⠣⡞⢠⡆⠀⡎⠀⠀⠁⣼⡝⢠⠟⡸⠀⠀⢸⢐⣸⠂⢸⠀⡇⢂⠄⡠⣧⣷⠏⠀⢠⡇⡈⡇⠀⠀⠀⠀⠀⠀⠀⠀⠀⣴⡆⠀⢰⡆⠀⢠⣾⣷⡀⢀⣾⠀⠀⣾⠆⠀⠀⠀⠀⠀⠀⠀⠀⠀⢹⡆
⠀⠀⠀⠀⠀⠀⠀⠀⠀⠚⠿⠚⠋⠁⠌⠁⡠⠊⡐⣡⢟⡌⡈⢒⡏⢰⢸⢰⢸⢺⡄⠀⢀⡴⣷⢿⠏⢈⣿⠟⠷⢆⡤⢀⣀⠀⠀⠀⠀⠀⢀⣏⠌⡔⠀⠀⠀⣆⠾⢁⡞⡇⡜⠀⠀⠀⣀⣯⡴⣥⢷⠓⠒⠋⠉⠡⢸⠀⢸⠀⠇⠎⣠⠱⢸⠇⠀⢠⠃⢠⢁⡇⠀⠀⠀⠀⠀⠀⠀⠀⢰⣿⣷⢀⡿⢁⣴⠟⣹⣿⠀⣾⣷⣶⣾⡟⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠸⡇
⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⠔⠋⣀⣪⠞⡑⢁⢧⠙⠀⢇⢸⣿⢺⡞⡚⡯⢴⡙⠉⢀⡐⠂⡘⡞⠀⠅⠠⠉⠁⠚⠣⠝⣔⠶⣀⠀⠁⡰⠀⠀⠀⠀⡏⣄⡜⠀⡷⢓⣢⠿⠍⠛⠋⠠⡡⠂⠀⠀⠀⢀⠀⢀⡇⢨⠀⣶⡜⢸⠀⡟⠀⠀⢆⣠⠃⡈⡇⠀⠀⠀⠀⠀⠀⠀⠀⣿⠉⣿⣿⢃⣾⠿⠟⢻⣟⣸⡟⠁⢠⡿⠁⢀⣴⠆⠀⠀⠀⠀⠀⠀⠀⢠⡇
⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⠴⠞⠧⠜⠋⢁⠂⠌⡰⢃⣾⣦⠀⢸⡀⣿⣄⢁⠇⠀⠀⢯⣝⣖⣿⣯⣿⣒⡭⠥⣐⡒⠤⢀⠀⠀⠈⠉⡛⢆⢠⠃⠀⠀⠀⠀⠑⠋⢀⣀⠻⠉⠁⠀⠀⢀⡠⠔⣒⣀⣭⣝⣛⣫⣿⣿⣧⠘⠀⣳⠀⣼⢠⠃⠀⠀⠸⣹⢀⠃⡇⠀⠀⠀⠄⠀⠀⠀⠀⠀⠀⠘⠃⠘⠃⠀⠀⠙⠋⠛⠀⠀⠙⠃⢠⠞⠁⠀⠀⠀⠀⠀⠀⠀⠀⢸⠃
⠀⠀⠀⠀⠀⠀⠀⠀⠀⠈⠁⠀⠀⠀⠀⢀⠂⣼⡞⠁⣾⡡⠘⡆⢸⡇⢸⡘⡈⢶⠀⠀⠀⢼⣣⠀⠀⢸⡄⠈⢽⡇⠛⠷⣦⣽⠀⠀⠀⠘⡟⡇⠀⠀⠀⠀⠀⠀⢠⣾⠆⠀⠀⠀⠀⢀⣯⡴⣾⠛⠩⠧⠄⣸⠃⠀⠀⣼⠆⢰⣇⡼⢰⣿⠄⠀⠀⠠⢿⠸⢰⡇⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣀⣀⣤⡀⣤⡄⢀⣠⣬⣥⣤⣀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢸⠀
⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣨⡴⠋⠀⢰⣿⠁⢀⣿⣸⠁⢶⠧⠀⠘⢇⠀⠀⠘⢞⡄⠀⠀⠙⠧⣀⡂⠤⠂⠈⢿⣇⠀⠀⠀⠼⢿⠀⠀⠀⠀⠀⠀⢠⠘⠆⠀⠀⠀⠀⣼⡏⠀⠈⠢⢄⡤⠴⠫⠀⠀⡰⠸⡄⢳⣷⢇⡂⣯⠀⠀⢠⠃⠎⢀⠟⡇⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠙⢻⡟⠉⣿⠋⠀⣿⠟⠉⠉⣽⡟⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢸⠀
⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠉⠀⠀⠀⣿⡏⠀⣼⢣⣿⡄⢸⡇⠀⠀⠈⠂⠀⠀⠀⠜⢄⡀⠀⠀⠀⠀⠀⠀⢀⡟⠯⡶⡶⢀⡶⠋⠀⠀⠀⠀⠀⠀⠀⠁⠀⢀⣄⡀⢰⡟⠳⡀⠀⠀⠀⠀⠄⠁⢀⠗⠣⠀⡌⡟⡆⢸⠇⠈⠁⡤⢸⡀⢠⠎⠀⡇⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⡿⠁⠀⠀⠀⢰⡟⣀⣤⡾⠋⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢸⠀
⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⡟⢣⠜⠁⡾⠼⡆⠀⣷⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠉⠉⠉⠁⠀⠚⠋⠀⠌⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠓⣞⣤⡀⠁⠉⠉⠉⠉⠀⠀⡀⠀⠀⠀⠠⣷⢸⡁⠈⠀⠀⢀⡇⠀⢡⢿⠁⠀⡇⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣶⡾⠿⠟⠂⠀⠀⠿⠿⠛⠉⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢸⠀
⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠄⠊⠁⠀⡸⡷⢣⠺⣆⢸⡇⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠂⠠⡰⠇⠘⡄⠀⠀⠀⢸⠀⢠⢿⢹⠀⠀⡇⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠈⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢸⠀
⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⡵⠓⠁⠀⠈⠛⣷⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠁⠈⠀⠀⠀⠀⠀⠐⠁⠀⠀⠁⠀⠀⢀⣇⣠⣟⡟⡜⡀⠀⡇⠀⠀⠀⠀⠀⠀⠀⠀⠀⣾⡆⢠⣷⡆⠀⣼⠆⣰⡆⢰⣷⠀⠀⣾⠃⠀⠀⠀⠀⠀⠀⠀⠀⠀⢸⠀
⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠁⠀⠀⠀⠀⠀⠀⢹⡆⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠂⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣼⠋⣿⢰⠇⢸⣱⡀⢤⠀⠀⠀⠀⠀⠀⠀⠀⢸⣿⢠⡾⣿⣇⣼⠏⢠⡿⢀⣿⢿⣧⣼⠇⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢸⠂
⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⣧⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠠⠀⠀⠀⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣼⠇⠀⣿⣿⠀⠀⡏⠁⢸⠀⠀⠀⠀⠀⠀⠀⠀⢸⣿⡟⠁⢿⣿⠋⢀⣾⠁⣾⠃⠈⣿⡟⠀⣤⡦⠀⠀⠀⠀⠀⠀⠀⠀⢸⠀
⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢹⡄⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢰⡟⠀⢰⣟⠇⠀⠈⠀⠀⡼⠀⠀⠀⠀⠀⠀⠀⠀⠈⠉⠀⠀⠈⠁⠀⠈⠃⠀⠉⠀⠀⠈⠁⠀⠉⠀⠀⠀⠀⠀⠀⠀⠀⢀⠞⠀
⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠈⣇⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣿⠁⠀⡾⠊⠀⠀⠀⠀⠀⠹⣄⠀⠀⠀⠀⠀⠀⠐⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠎⠀⠀
⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢻⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠀⠀⠀⠀⠀⢐⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣼⠇⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠈⠓⢄⠀⠀⠀⠀⢀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⠋⠀⠀⠀
⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠈⣷⡄⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠋⢦⡀⠀⢀⠄⠀⠀⠀⠀⢔⠀⠀⠀⠃⠀⠀⠀⠈⠁⠀⠀⠀⠀⠀⠀⠀⣴⡟⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠈⠂⢄⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣰⠃⠀⠀⠀⠀
⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢻⡽⢆⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠈⠁⠀⠈⠀⠀⠀⠀⠀⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⠞⣿⠇⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠈⠐⠢⢄⣀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⢀⣠⣴⡾⠁⠀⠀⠀⠀⠀
⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠈⣩⡎⢳⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠐⠀⠀⠀⠀⠀⠀⠀⠀⠠⠀⠀⡄⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⠞⠁⣼⣿⠀⠀⠀⠀⠀⠀⠀⠀⠀⡠⠀⠒⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠈⠉⠓⢾⠗⣀⠤⠤⠤⠄⠀⠀⠀⠀⠈⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢯⣳⠀⠹⣆⠀⠀⠀⠀⠀⠰⠦⠄⣀⣀⡀⠀⠀⠀⠀⠀⢀⡀⠀⠀⠀⠀⠂⠄⠀⠀⠀⣀⡀⠠⠴⠛⠁⠀⠀⠀⢀⡜⠁⠀⡰⣿⡇⠀⡀⠀⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⠀⠀⠀⠀⠀⠀⠀⠀⢠⢋⠖⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣿⢌⡆⠀⠈⢷⡀⠀⠀⠀⠀⠀⠀⠀⠀⠨⡙⢿⣿⣛⠛⠛⠛⠛⠛⠛⢻⣛⣩⢿⡋⠉⠀⠀⠀⠀⠀⠀⠀⢀⡴⠋⠀⢀⣴⠿⣿⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢠⠗⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⡎⠳⣵⠀⠀⠀⠹⣦⠀⠈⠀⠀⠀⠀⠀⠀⠀⠑⠦⣤⣤⣭⣭⣭⣭⣭⢩⡽⠞⠃⠀⠀⠀⠀⠠⠀⠀⢀⡴⠋⠀⠁⠀⣲⠯⢸⡗⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠠⠋⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⡇⠀⠈⠀⠀⠂⠀⠈⢳⡄⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠃⠀⡁⠀⠃⠀⡤⠀⠀⠀⠀⠀⠀⠀⡴⠋⠀⠀⠀⠀⢰⡿⢌⣿⡅⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⡇⠀⠀⠀⠀⠁⠀⠄⠩⡿⣦⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠒⠀⠀⠀⠀⠀⠀⠀⠈⠀⠀⠀⣠⠮⠀⠀⠀⠀⠀⠀⢐⡺⢁⣓⠀⠀⠀⠀⠀⠀⠀⠀⠈⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣼⣧⠀⢈⠠⡀⠀⠀⠀⠀⢘⣝⢿⣆⠀⠀⠈⠀⠀⠤⠀⠀⠀⠀⠐⠀⠀⠀⠀⠀⠀⠀⠁⣠⣾⠏⠀⠀⠀⠀⠀⠀⠒⠁⠀⢸⢸⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠂⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢼⣿⣿⠀⠀⠑⢌⢰⠀⠀⠀⠀⠳⣅⡫⠳⣤⡀⡀⡀⠀⠀⠀⠐⠀⠀⠀⠀⠀⠀⠀⠀⣠⣾⡽⠏⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣿⡇⠀⠀⠀⠀⠀⠀⠀⠀⢀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⣠⣴⣿⣿⣿⣿⣷⠀⠀⠀⢳⣍⠢⠀⠀⠀⠀⢻⡟⠆⠈⠹⠶⣤⣀⣀⣏⣈⡀⣀⢀⣀⣀⣠⣴⠿⣛⢝⠃⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣿⣿⣶⣤⡀⠀⠀⠀⠀⠀⠊⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⣴⣾⣿⣿⣿⣿⣿⣿⣿⣿⣆⠀⠀⠈⢯⡫⡠⠀⠀⠀⠀⠻⣯⡀⠀⠀⠀⠀⠈⠉⠉⠙⠙⠋⠛⠙⢫⣍⡳⠎⠀⠀⠀⠀⠀⠐⠀⠀⠀⠀⠀⠀⢀⣿⣿⣿⣿⣿⣧⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⣤⣼⣿⣿⣿⣿⣾⣿⣿⣿⣿⣿⣿⣿⣿⣿⣷⣄⠀⠈⡟⣬⡐⠀⠀⠀⠀⠱⡷⣂⠥⠀⡄⠀⠀⠀⠀⠀⠀⠀⠀⣜⣪⠍⠀⠀⠀⠀⡠⠎⠁⣀⠀⠀⠀⠀⠀⣸⣿⣿⣿⣿⣿⣿⣿⣷⣤⣀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣠⣴⣾⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣶⣄⠸⡦⡋⡄⠀⠀⠀⠀⠜⢎⠙⠕⣂⠀⠤⢀⠀⠀⠀⠀⠜⠳⠋⠀⠀⠀⢀⡸⢋⡥⠴⣇⠀⠀⠀⠀⠀⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣷⣾⣦⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣴⣾⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣻⣿⣿⣿⣿⣿⣿⣷⣌⡢⡹⡄⠀⠀⠀⠀⠘⡖⠔⣒⡠⠄⡭⠃⠀⠀⠐⡺⠁⠀⠀⢀⡴⢿⡙⠃⣐⠒⠉⠁⢀⣠⣴⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣷⣦⣀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
⠀⠀⠀⠀⠀⢀⣴⣾⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣶⣧⣀⡀⠀⠀⠀⠻⠅⡒⠄⢹⡁⠀⠀⠀⢻⠀⠀⠀⢠⣾⣿⡋⢠⠑⢀⣃⣤⣶⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣾⣤⣀⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀"""
			return ascii;
	}
}
