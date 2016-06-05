package ie.dd.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnection {

	private String url;
	private String userName;
	private String password;
	private String schema;

	private DbConnection(){}
	
	public DbConnection(String url){
        this.url = url;
	}
	
	public Connection getConnection() throws SQLException {
		Connection c = DriverManager.getConnection(url, userName, password);
		c.setAutoCommit(false);
		return c;
	}
	
	public void setConnectionUser(String userName, String password) {
		this.userName = userName;
		this.password = password;
	}
	
	public void setConnectionSchema(String schema) {
		this.schema = schema;
		SQLRunner.addSchemaToSQL(schema);
	}
	
	public String getConnectionSchema() {
		return schema;
	}
}