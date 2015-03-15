/**
 * espeon
 * 
 * With espeon you can generate templates based on mysql database structures.
 * It uses FreeMarker as template engine so you can use it to generate i.e.
 * source-code
 * 
 * Copyright (c) 2010 Christoph Kappestein <k42b3.x@gmail.com>
 * 
 * This file is part of espeon. espeon is free software: you can 
 * redistribute it and/or modify it under the terms of the GNU 
 * General Public License as published by the Free Software Foundation, 
 * either version 3 of the License, or at any later version.
 * 
 * espeon is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with espeon. If not, see <http://www.gnu.org/licenses/>.
 */

package com.k42b3.espeon;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;

/**
 * Espeon
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       https://github.com/k42b3/espeon
 * @version    $Revision: 193 $
 */
public class Espeon
{
	public static String version = "0.0.4 beta";
	public static String configFile = "espeon.conf.xml";

	public static String templatePath = "templates";
	public static String outputPath = "output";
	
	private String host = "127.0.0.1";
	private String db = null;
	private String user = null;
	private String pw = null;

	private boolean mysqlConfig = false;

	private Connection con;
	private Configuration cfg;

	private Logger logger;

	public Espeon() throws Exception
	{
		// logger
		logger = Logger.getLogger("com.k42b3.espeon");
		logger.setLevel(Level.INFO);

		// load config
		loadConfig();

		// set template config
		File templatePath = new File(Espeon.templatePath);
		
		if(templatePath.isDirectory())
		{
			this.cfg = new Configuration();

			this.cfg.setDirectoryForTemplateLoading(new File(Espeon.templatePath));

			this.cfg.setObjectWrapper(new DefaultObjectWrapper());
		}
		else
		{
			throw new Exception("You have to create the template directory: '" + templatePath.getAbsolutePath());
		}

		// check outputdir
		File output = new File(Espeon.outputPath);
		
		if(!output.isDirectory())
		{
			throw new Exception("You have to create the output directory: " + output.getAbsolutePath());
		}
	}

	public boolean hasMysqlConfig()
	{
		return mysqlConfig;
	}

	public String getHost()
	{
		return host;
	}
	
	public String getDb()
	{
		return db;
	}
	
	public String getUser()
	{
		return user;
	}
	
	public String getPw()
	{
		return pw;
	}

	public void loadConfig() throws Exception
	{
		File config = new File(Espeon.configFile);

		if(config.isFile())
		{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(config);

			Element rootElement = (Element) doc.getDocumentElement();

			rootElement.normalize();

			// template dir
			Element templateDir = (Element) rootElement.getElementsByTagName("templateDir").item(0);

			if(templateDir != null && templateDir.getTextContent() != null && !templateDir.getTextContent().isEmpty())
			{
				Espeon.templatePath = templateDir.getTextContent();
			}

			// output dir
			Element outputDir = (Element) rootElement.getElementsByTagName("outputDir").item(0);

			if(outputDir != null && outputDir.getTextContent() != null && !outputDir.getTextContent().isEmpty())
			{
				Espeon.outputPath = outputDir.getTextContent();
			}

			// mysql
			Element mysql = (Element) rootElement.getElementsByTagName("mysql").item(0);

			if(mysql != null)
			{
				this.mysqlConfig = true;

				if(mysql.hasAttribute("host"))
				{
					this.host = mysql.getAttribute("host");
				}
				
				if(mysql.hasAttribute("db"))
				{
					this.db = mysql.getAttribute("db");
				}
				
				if(mysql.hasAttribute("user"))
				{
					this.user = mysql.getAttribute("user");
				}
				
				if(mysql.hasAttribute("pw"))
				{
					this.pw = mysql.getAttribute("pw");
				}
			}
		}
		else
		{
			logger.info("Found no config " + Espeon.configFile);
		}
	}

	public void connect(String host, String db, String user, String pw) throws Exception
	{
		if(host == null)
		{
			if(this.host != null)
			{
				host = this.host;
			}
			else
			{
				throw new Exception("No host specified");
			}
		}
		
		if(db == null)
		{
			if(this.db != null)
			{
				db = this.db;
			}
			else
			{
				throw new Exception("No database specified");
			}
		}

		if(user == null)
		{
			if(this.user != null)
			{
				user = this.user;
			}
			else
			{
				throw new Exception("No user specified");
			}
		}
		
		if(pw == null)
		{
			if(this.pw != null)
			{
				pw = this.pw;
			}
			else
			{
				throw new Exception("No pw specified");
			}
		}

		logger.info("Connect to " + host + "/" + db + "?user=" + user);

		con = DriverManager.getConnection("jdbc:mysql://" + host + "/" + db + "?user=" + user + "&amp;password=" + pw);
	}

	public void generate(ArrayList<String> templates, HashMap<String, HashMap<String, Object>> tables)
	{
		logger.info("Generate " + templates.size() + " template/s for " + tables.size() + " table/s");

		Iterator<Entry<String, HashMap<String, Object>>> it = tables.entrySet().iterator();

		while(it.hasNext())
		{
			Entry<String, HashMap<String, Object>> entry = it.next();

			for(int i = 0; i < templates.size(); i++)
			{
				try
				{
					Template temp = cfg.getTemplate(templates.get(i));

					Writer out = new FileWriter(Espeon.outputPath + "/" + entry.getKey() + "-" + templates.get(i));

					temp.process(entry.getValue(), out);

					out.flush();
				}
				catch(Exception e)
				{
					Espeon.handleException(e);
				}
			}
		}
	}

	public Connection getConnection()
	{
		return con;
	}

	public Configuration getConfiguration()
	{
		return cfg;
	}

	public HashMap<String, Object> getParams(String table) throws Exception
	{
		HashMap<String, Object> params = new HashMap<String, Object>();
		Object[][] rows = this.getTableStructure(table);

		Object firstColumn = "";
		Object lastColumn = "";
		Object primaryKey = "";
		ArrayList<Object> unqiueKey = new ArrayList<Object>();
		ArrayList<Object> fields = new ArrayList<Object>();
		ArrayList<HashMap<String, String>> columns = new ArrayList<HashMap<String, String>>();

		for(int i = 0; i < rows.length; i++)
		{
			String rField   = rows[i][0] != null ? rows[i][0].toString() : "";
			String rType    = rows[i][1] != null ? rows[i][1].toString() : "";
			String rNull    = rows[i][2] != null ? rows[i][2].toString() : "";
			String rKey     = rows[i][3] != null ? rows[i][3].toString() : "";
			String rDefault = rows[i][4] != null ? rows[i][4].toString() : "";
			String rExtra   = rows[i][5] != null ? rows[i][5].toString() : "";

			lastColumn = rField;

			if(i == 0)
			{
				firstColumn = rField;
			}

			if(rKey.equals("PRI"))
			{
				primaryKey = rField;
			}

			if(rKey.equals("UNI"))
			{
				unqiueKey.add(rField);
			}


			fields.add(rField);


			String rLength = "";
			int pos = rType.indexOf('(');

			if(pos != -1)
			{
				String rawLength = rType.substring(pos + 1);
				rLength = rawLength.substring(0, rawLength.length() - 1);
				rType = rType.substring(0, pos);
			}

			HashMap<String, String> c = new HashMap<String, String>();

			c.put("field", rField);
			c.put("type", rType);
			c.put("length", rLength);
			c.put("null", rNull);
			c.put("key", rKey);
			c.put("default", rDefault);
			c.put("extra", rExtra);

			columns.add(c);
		}

		int pos = table.lastIndexOf('_');
		String name;
		String namespace;

		if(pos != -1)
		{
			name = Espeon.convertTableToClass(table.substring(pos + 1));
			namespace = Espeon.convertTableToClass(table.substring(0, pos));
		}
		else
		{
			name = Espeon.convertTableToClass(table);
			namespace = "";
		}

		params.put("table", table);
		params.put("name", name);
		params.put("namespace", namespace);
		params.put("firstColumn", firstColumn);
		params.put("lastColumn", lastColumn);
		params.put("primaryKey", primaryKey);
		params.put("unqiueKey", unqiueKey);
		params.put("fields", fields);
		params.put("columns", columns);

		return params;
	}

	public Object[][] getTableStructure(String table) throws Exception
	{
		PreparedStatement ps = con.prepareStatement("DESCRIBE " + table);

		ps.execute();

		ResultSet result = ps.getResultSet();

		result.last();

		Object[][] rows = new Object[result.getRow()][6];

		result.beforeFirst();

		while(result.next())
		{
			int row = result.getRow() - 1;

			rows[row][0] = result.getString("Field");
			rows[row][1] = result.getString("Type");
			rows[row][2] = result.getString("Null");
			rows[row][3] = result.getString("Key");
			rows[row][4] = result.getString("Default");
			rows[row][5] = result.getString("Extra");
		}

		logger.info("Describe table " + table + " found " + rows.length + " fields");

		return rows;
	}

	public List<String> getTables() throws Exception
	{
		List<String> tables = new ArrayList<String>();
		PreparedStatement ps = con.prepareStatement("SHOW TABLES");

		ps.execute();

		ResultSet result = ps.getResultSet();

		while(result.next())
		{
			tables.add(result.getString(1));
		}

		logger.info("Found " + tables.size() + " tables");

		return tables;
	}

	public void runGui() throws Exception
	{
		com.k42b3.espeon.gui.Main panel = new com.k42b3.espeon.gui.Main(this);

		registerViewCallbacks(panel);

		panel.run();
	}

	public void runCmd(String[] args)
	{
		com.k42b3.espeon.cmd.Main panel = new com.k42b3.espeon.cmd.Main(this, args);

		registerViewCallbacks(panel);

		panel.run();
	}

	private void registerViewCallbacks(View view)
	{
		view.setConnectCallback(new ConnectCallback() {

			public void onConnect(String host, String db, String user, String pw) throws Exception
			{
				connect(host, db, user, pw);
			}

		});

		view.setGenerateCallback(new GenerateCallback() {

			public void onGenerate(ArrayList<String> templates, HashMap<String, HashMap<String, Object>> tables)
			{
				generate(templates, tables);
			}

		});
	}

	public static String getAbout()
	{
		StringBuilder out = new StringBuilder();

		out.append("Version: espeon " + version + "\n");
		out.append("Author: Christoph \"k42b3\" Kappestein" + "\n");
		out.append("Website: http://code.google.com/p/delta-quadrant" + "\n");
		out.append("License: GPLv3 <http://www.gnu.org/licenses/gpl-3.0.html>" + "\n");
		out.append("\n");
		out.append("With espeon you can generate sourcecode from database structures. It was" + "\n");
		out.append("mainly developed to generate PHP classes for the PSX framework (phpsx.org)" + "\n");
		out.append("but because it uses a template engine (FreeMarker) you can use it for any" + "\n");
		out.append("purpose you like." + "\n");

		return out.toString();
	}

	public static String convertTableToClass(String table)
	{
		String[] parts = table.split("_");
		String className = "";
		int length = parts.length;

		for(int i = 0; i < parts.length; i++)
		{
			className+= Character.toUpperCase(parts[i].charAt(0)) + parts[i].substring(1);

			if(i < length - 1)
			{
				className+= "_";
			}
		}

		return className;
	}

	public static String convertTableToPath(String table)
	{
		return Espeon.convertTableToClass(table).replace('_', '/');
	}

	public static void handleException(Exception e)
	{
		e.printStackTrace();
		Logger.getLogger("com.k42b3.espeon").warning(e.getMessage());
	}
}
