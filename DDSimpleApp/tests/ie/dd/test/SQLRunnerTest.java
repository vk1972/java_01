package ie.dd.test;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ie.dd.MainApp;
import ie.dd.model.Server;

public class SQLRunnerTest {

	private static MainApp mainApp;
	Server server1, server2;
	String serversNewName = "SRV_NEW_NAME";

	@Before
	public void setUp() throws Exception {
		mainApp = new MainApp();
		mainApp.setDb();
		mainApp.setXML();
		server1 = new Server("1", "MyServerName");
		server2 = new Server("2", "MyAnotherServerName");
	}


	@Test
	public void deleteAllServers() {
        MainApp.getSQLRunner().deleteAllServers(MainApp.getDbConnection());
		int servers = mainApp.countServers();
		assertTrue(servers == 0);
	}	
		
	@Test
	public void testAddServer() {
		mainApp.addServers();
		int count = mainApp.countServers();
		assertTrue(count == 2);
	}
	
	@Test
	public void testListServers() {
		List<Server> servers = mainApp.listServers();
		assertTrue(servers.size() == 2 && servers.contains(server1) && servers.contains(server2));
	}
	
	@Test
	public void testDeleteServer() {
		String[] id = {server1.getId()};
		mainApp.deleteServer(id);
		List<Server> servers = mainApp.listServers();
		assertFalse(servers.size() == 1 && servers.contains(server1.getId()) && servers.contains(server2));
	}
	
	@Test
	public void testEditServer() {
		String[] s = {server2.getId(), serversNewName};
		mainApp.editServer(s);
		List<Server> servers = mainApp.listServers();
		int index = servers.indexOf(server2);
		Server s2 = servers.get(index);
		assertTrue(serversNewName.equals(s2.getName()));
	}	

}
