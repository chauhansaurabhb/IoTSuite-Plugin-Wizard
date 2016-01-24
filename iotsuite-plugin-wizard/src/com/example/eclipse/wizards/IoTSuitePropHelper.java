package com.example.eclipse.wizards;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

/**
 * Helper class to manage properties files This class is provided as a
 * singlenton instance
 * 
 * @version 1.0
 */
public class IoTSuitePropHelper {

	/**
	 * Singleton instance of the class
	 */
	private static IoTSuitePropHelper _instance = null;

	/**
	 * Properties to file
	 */
	private Properties properties = null;

	/**
	 * Path to properties file in the file system
	 */
	private String pathToFile = null;

	/**
	 * Private constructor to ensure singleton pattern
	 */
	private IoTSuitePropHelper() {
		properties = new Properties();
		pathToFile = new String();
	}

	/**
	 * Method to provide the instance for the PropertiesHelper Class. Singleton
	 * Patter only let one instance
	 * 
	 * @return the PropertiesHelper instance
	 */
	public static IoTSuitePropHelper getInstance() {
		if (_instance == null) {
			_instance = new IoTSuitePropHelper();
			return _instance;
		} else {
			return _instance;
		}
	}

	/**
	 * Load a properties file from file system
	 * 
	 * @param path
	 *            String type referring the path to the properties file
	 * @param propertiesFile
	 *            String type referring to the properties file itself
	 * @throws IOException
	 *             If properties file cannot be found
	 */
	public void loadFile(String path, String propertiesFile) throws IOException {

		pathToFile = path + File.separator + propertiesFile;
		//FileReader reader = new FileReader(pathToFile);
		//properties.load(reader);
	}

	// /**
	// * Load a properties file stored in the current directory
	// *
	// * @param propertiesFile
	// * String type referring to the properties file itself
	// * @throws IOException
	// * If properties file cannot be found
	// */
	// public void loadFile() throws IOException {
	//
	// IWorkspace workspace = ResourcesPlugin.getWorkspace();
	// File workspaceDirectory = workspace.getRoot().getLocation().toFile();
	// String sPath = workspaceDirectory.getAbsolutePath();
	//
	// pathToFile = sPath + File.separator + "IoTSuite.properties";
	//
	// FileReader reader = new FileReader(pathToFile);
	// properties.load(reader);
	// }

	/**
	 * Read a property from a properties file. The properties file must be
	 * loaded previously using the loadFile method
	 * 
	 * @param property
	 *            String type referring the properties to read
	 * @return String type with the value of the property
	 */
	public String readProperty(String property) {

		return properties.getProperty(property);
	}

	/**
	 * Write a property to the properties file. The properties file must be
	 * loaded previously using the loadFile method
	 * 
	 * @param key
	 *            String type for the property key
	 * @param value
	 *            String type for the property value
	 * @throws IOException
	 *             If properties file cannot be written
	 */
	public void writeProperty(String key, String value) throws IOException {
		OutputStream output = null;
		try {

			output = new FileOutputStream(pathToFile);

			// set the properties value
			properties.setProperty(key, value);

			// save properties to project root folder
			properties.store(output, null);

		} catch (IOException io) {
			io.printStackTrace();
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
	}

	/**
	 * Update a property to the properties file. The properties file must be
	 * loaded previously using the loadFile method
	 * 
	 * @param key
	 *            String type for the property key
	 * @param value
	 *            String type for the property value
	 * @throws IOException
	 *             If properties file cannot be written
	 */
	public void updateProperty(String key, String value) throws IOException {
		this.writeProperty(key, value);
	}
}