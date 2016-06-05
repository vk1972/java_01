package ie.dd.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import ie.dd.model.*;

public class SQLRunner {

	private boolean error;
	private static Logger logger = Logger.getLogger(SQLRunner.class.getName());

	private static String SQL_SELECT_SERVERS = "select ID, NAME from SCHEMA.SERVER";
	private static String SQL_COUNT_SERVERS = "select count(*) from SCHEMA.SERVER";
	private static String SQL_INSERT_SERVERS = "insert into SCHEMA.SERVER(ID, NAME)values(?, ?)";
	private static String SQL_DELETE_SERVER = "delete from SCHEMA.SERVER where ID = ?";
	private static String SQL_UPDATE_SERVER = "update SCHEMA.SERVER set NAME = ? where ID = ?";
	private static String SQL_DELETE_ALL_SERVERS = "delete from SCHEMA.SERVER";
	
	
	public static void addSchemaToSQL(String schema){
		SQL_SELECT_SERVERS = SQL_SELECT_SERVERS.replaceAll("SCHEMA", schema);
		SQL_COUNT_SERVERS = SQL_COUNT_SERVERS.replaceAll("SCHEMA", schema);
		SQL_INSERT_SERVERS = SQL_INSERT_SERVERS.replaceAll("SCHEMA", schema);
		SQL_DELETE_SERVER = SQL_DELETE_SERVER.replaceAll("SCHEMA", schema);
		SQL_UPDATE_SERVER = SQL_UPDATE_SERVER.replaceAll("SCHEMA", schema);
		SQL_DELETE_ALL_SERVERS = SQL_DELETE_ALL_SERVERS.replaceAll("SCHEMA", schema);
	}
	
	
	public int countServers(DbConnection dbConn) {
		error = false;
        int servers = -1;
		try (Connection c = dbConn.getConnection();
				PreparedStatement ps = c.prepareStatement(SQL_COUNT_SERVERS);
				ResultSet rs = ps.executeQuery();) {

			if (rs.next()) {
				servers = rs.getInt(1);
			}
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "db.countServers", e);
			System.out.println(e.getMessage());
			error = true;
		}

		return servers;	
	}

	public List<Server> listServers(DbConnection dbConn) {

		List<Server> servers = new ArrayList<Server>();
		error = false;

		try (Connection c = dbConn.getConnection();
				PreparedStatement ps = c.prepareStatement(SQL_SELECT_SERVERS);
				ResultSet rs = ps.executeQuery();) {

			while (rs.next()) {
				String id = rs.getString(1).trim();
				String name = rs.getString(2);
				Server s = new Server(id, name);
				servers.add(s);
			}
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "db.listServers", e);
			System.out.println(e.getMessage());
			error = true;
		}

		return servers;
	}

	public boolean insertServer(Server server, DbConnection dbConn) {
		error = false;

		try (Connection c = dbConn.getConnection(); 
			 PreparedStatement ps = c.prepareStatement(SQL_INSERT_SERVERS);
		) {
			ps.setString(1, server.getId());
			ps.setString(2, server.getName());
			ps.executeUpdate();
			c.commit();
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "db.insertServer", e);
			System.out.println(e.getMessage());
			error = true;
		}

		return error;
	}
	
	
	public int deleteServer(String id, DbConnection dbConn){
		error = false;
        int noOfRowsAffected = -100;
		try (Connection c = dbConn.getConnection(); 
			 PreparedStatement ps = c.prepareStatement(SQL_DELETE_SERVER);
		) {
			ps.setString(1, id);
			noOfRowsAffected = ps.executeUpdate();
			c.commit();
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "db.deleteServer", e);
			System.out.println(e.getMessage());
			error = true;
		}		
		return noOfRowsAffected;
	}
	
	public int deleteAllServers(DbConnection dbConn){
		error = false;
        int noOfRowsAffected = -100;
		try (Connection c = dbConn.getConnection(); 
			 PreparedStatement ps = c.prepareStatement(SQL_DELETE_ALL_SERVERS);
		) {
			noOfRowsAffected = ps.executeUpdate();
			c.commit();
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "db.deleteAllServers", e);
			System.out.println(e.getMessage());
			error = true;
		}		
		return noOfRowsAffected;
	}	
	

	public int updateServer(String id, String name, DbConnection dbConn){
		error = false;
        int noOfRowsAffected = -100;
		try (Connection c = dbConn.getConnection(); 
			 PreparedStatement ps = c.prepareStatement(SQL_UPDATE_SERVER);
		) {
			ps.setString(1, name);
			ps.setString(2, id);
			noOfRowsAffected = ps.executeUpdate();
			c.commit();
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "db.updateServer", e);
			System.out.println(e.getMessage());
			error = true;
		}		
		return noOfRowsAffected;
	}


	public boolean isError() {
		return error;
	}
}