package ie.dd.test;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ie.dd.MainApp;
import ie.dd.model.Server;

public class SQLRunnerTest {

	private static MainApp mainApp;
	Server server;

	@Before
	public void setUp() throws Exception {
		mainApp = new MainApp();
		mainApp.setDb();
		server = new Server("1", "MyServerName");
	}


	@Test
	public void testListServers() {
		
		List<Server> servers = mainApp.listServers();
		assertTrue(servers.contains(server));
	}

}
