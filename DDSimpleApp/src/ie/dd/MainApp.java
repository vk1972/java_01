package ie.dd;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.xml.stream.XMLStreamException;

import ie.dd.model.Server;
import ie.dd.sql.DbConnection;
import ie.dd.sql.SQLRunner;
import ie.dd.xml.XMLConnection;
import ie.dd.xml.XMLRunner;

public class MainApp {
	
	private static Logger logger = Logger.getLogger(MainApp.class.getName());
	
	private static String APP_NAME = "DDSimpleApp";

	private static MainApp mainApp;
	private static Properties props;
	private static DbConnection dbConnection;
	private static SQLRunner sqlRunner;
	private static XMLConnection xmlConnection;
	private static XMLRunner xmlRunner;
	private static String decodedPath;
	static {
			decodedPath = getDecodedPath() ;
	}

	public static void main(String[] args)
			throws ClassNotFoundException, SQLException, IOException, XMLStreamException {

		boolean running = true;
		showHelp();
		mainApp = getMainApp();

		setLoggingToCwd();

		Scanner in = new Scanner(System.in);
		String option = null;
		while (running) {
			if (in.hasNextLine()) {
				option = in.nextLine();

				if ("help".equals(option)) {
					showHelp();
				} else if ("quit".equals(option)) {
					running = false;
				} else if ("countServers".equals(option)) {

					// TODO implement...
				} else if ("addServer".equalsIgnoreCase(option)) {
					mainApp.setXML();
					mainApp.setDb();
					mainApp.addServers();
				} else if ("listServers".equalsIgnoreCase(option)) {
					mainApp.setDb();
					mainApp.listServers();
				} else if ("deleteServer".equalsIgnoreCase(option)) {
					// TODO implement...
				} else if ("editServer".equalsIgnoreCase(option)) {
					// TODO implement...
				} else {
					if (!"\\n".equals(option) && !"".equals(option)) {
						System.out.println("Unknown option!");
						System.out.println("Enter option:");
					}
				}
			}
		}
		in.close();
	}

	private static MainApp getMainApp() {
		if (mainApp == null)
			mainApp = new MainApp();
		return mainApp;
	}
	
	private static String getDecodedPath() {
		String decodedPath = null;
		try {
		    String path = MainApp.class.getProtectionDomain().getCodeSource().getLocation().getPath();
			decodedPath = URLDecoder.decode(path, "UTF-8");
			decodedPath = decodedPath.substring(0, decodedPath.indexOf("/", decodedPath.lastIndexOf(APP_NAME)));
			logger.log(Level.INFO, "main.getDecodedPath", decodedPath);
		} catch (UnsupportedEncodingException e) {
			logger.log(Level.SEVERE, "main.getDecodedPath", e);
			System.out.println(e.getMessage());
		}
		//System.out.println("cwd: " + decodedPath);
		return decodedPath;
	}

	public void setDb() throws IOException {

		if (dbConnection == null) {
			loadProperties();

			String url = props.getProperty("url");
			String userName = props.getProperty("userName");
			String password = props.getProperty("password");
			String schema = props.getProperty("schema");

			dbConnection = new DbConnection(url);
			dbConnection.setConnectionUser(userName, password);
			dbConnection.setConnectionSchema(schema);
		}
	}

	private void setXML() throws IOException {

		if (xmlConnection == null) {
			loadProperties();
			String xmlFile = props.getProperty("xmlFile");
			xmlConnection = new XMLConnection(xmlFile);
		}
	}

	private static void loadProperties() throws IOException {
		if (props == null) {
			props = new Properties();
			System.out.println(decodedPath + "/services.properties");
			FileInputStream in = new FileInputStream(decodedPath + "/services.properties");
			props.load(in);
			in.close();
		}
	}

	private static void setLoggingToCwd() {
		final LogManager logManager = LogManager.getLogManager();
		try (final InputStream is = new FileInputStream(decodedPath + "/logging.properties");) {
			logManager.readConfiguration(is);
			new File(decodedPath + "/log").mkdir();
			FileHandler fileHandler = new FileHandler(decodedPath + "/log/log.txt");
			Logger logger = Logger.getLogger("ie.dd");
			logger.addHandler(fileHandler);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "main.setLoggingToCwd", e);
			System.out.println(e.getMessage());
		}
	}

	private static SQLRunner getSQLRunner() {
		if (sqlRunner == null)
			sqlRunner = new SQLRunner();
		return sqlRunner;
	}

	private static XMLRunner getXMLRunner() {
		if (xmlRunner == null)
			xmlRunner = new XMLRunner();
		return xmlRunner;
	}

	public List<Server> listServers() {
		SQLRunner sqlRunner = getSQLRunner();
		List<Server> servers = sqlRunner.listServers(dbConnection);
		if (sqlRunner.isError()) {
			System.out.println("There was an error while accessing database.\nPlease check log file!");
		} else {
			if (servers.size() > 0) {
				for (Server s : servers) {
					System.out.println(s.getId() + "\t" + s.getName());
				}
			} else {
				System.out.println("There is no servers persisted to database!");
			}
		}
		return servers;
	}

	private void addServers() throws FileNotFoundException, XMLStreamException {
		XMLRunner xmlRunner = getXMLRunner();
		SQLRunner sqlRunner = getSQLRunner();
		List<Server> servers = xmlRunner.listServers(xmlConnection);
		if (xmlRunner.isError()) {
			System.out.println("There was an error while accessing xml files.\nPlease check log file!");
		} else {
			if (servers.size() > 0) {
				for (Server s : servers) {
					sqlRunner.insertServer(s, dbConnection);
					if (sqlRunner.isError()) {
						System.out.println("There was an error while accessing database.\nPlease check log file!");
					}
				}
			} else {
				System.out.println("There is no servers defined in xml!");
			}
		}
	}
	
	
	private static void showHelp() {
		System.out.println("");
		System.out.println("help to display this message");
		System.out.println("addServer to display the current number of servers present");
		System.out.println("countServers to display the current number of servers present");
		System.out.println("listServers to display details of all servers servers");
		System.out.println("deleteServer to delete a server (takes one more arg - the id of the server to delete)");
		System.out.println(
				"editServer to change the name of a server identified by id (takes 2 additional args - the id and the new name)");
		System.out.println("quit to quit application");
		System.out.println("");
	}
}
