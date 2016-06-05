package ie.dd.xml;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import ie.dd.model.Server;
import ie.dd.sql.SQLRunner;

public class XMLRunner {

	private boolean error;
	private static Logger logger = Logger.getLogger(SQLRunner.class.getName());

	public List<Server> listServers(XMLConnection xmlConnection) {
		error = false;
		File file = xmlConnection.getConnection();
		List<Server> servers = new ArrayList<Server>();
		try {
			if (file.isDirectory()) {
				File[] files = file.listFiles();
				for (File f : files) {
					JAXBContext jaxbContext = JAXBContext.newInstance(Server.class);
					Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
					Server server = (Server) jaxbUnmarshaller.unmarshal(f);
					servers.add(server);
				}
			} else {
				Exception e = new Exception("Folder for xml definitions does not exists!");
				logger.log(Level.SEVERE, "xml.listServers", e);
				System.out.println(e.getMessage());
				error = true;
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, "xml.listServers", e);
			System.out.println(e.getMessage());
			error = true;
		}

		return servers;
	}

	public boolean isError() {
		return error;
	}
}