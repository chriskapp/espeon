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

package com.k42b3.espeon.cmd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;

import com.k42b3.espeon.ConnectCallback;
import com.k42b3.espeon.Espeon;
import com.k42b3.espeon.GenerateCallback;
import com.k42b3.espeon.View;

/**
 * Main
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       https://github.com/k42b3/espeon
 * @version    $Revision: 193 $
 */
public class Main implements View
{
	private Espeon inst;
	private ConnectCallback connectCb;
	private GenerateCallback generateCb;

	private String host;
	private String db;
	private String user;
	private String pw;

	private ArrayList<String> templates;
	private HashMap<String, HashMap<String, Object>> tables;

	private String[] args;
	private Options options;
	
	public Main(Espeon inst, String[] args)
	{
		this.inst = inst;
		this.args = args;

		templates = new ArrayList<String>();
		tables = new HashMap<String, HashMap<String, Object>>();


		options = new Options();

		options.addOption("h", "help", false, "Shows this help.");
		options.addOption("H", "host", true, "Connect to host (default is localhost).");
		options.addOption("u", "user", true, "User for login.");
		options.addOption("p", "password", true, "Password to use when connecting to server.");
		options.addOption("d", "database", true, "Database to use.");
		options.addOption("P", "pattern", true, "Table pattern.");
		options.addOption("t", "template", true, "Templates to use.");
		options.addOption("l", "list", false, "Shows all available tables.");
	}

	public void run()
	{
		try
		{
			CommandLineParser parser = new PosixParser();
			CommandLine cmd = parser.parse(options, args);

			// help
			HelpFormatter formatter = new HelpFormatter();

			if(cmd.hasOption('h'))
			{
				formatter.printHelp("java -jar espeon.jar [options]", options);

				return;
			}

			// mysql connection
			host = cmd.hasOption('H') ? cmd.getOptionValue('H') : null;
			user = cmd.hasOption('u') ? cmd.getOptionValue('u') : null;
			pw   = cmd.hasOption('p') ? cmd.getOptionValue('p') : "";
			db   = cmd.hasOption('d') ? cmd.getOptionValue('d') : null;

			connectCb.onConnect(host, db, user, pw);

			// list
			if(cmd.hasOption('l'))
			{
				List<String> tables = inst.getTables();

				for(int i = 0; i < tables.size(); i++)
				{
					System.out.println(tables.get(i));
				}

				return;
			}

			// tables
			if(cmd.hasOption('P'))
			{
				String pattern = cmd.getOptionValue('P');
				List<String> tables = inst.getTables();

				if(pattern != null)
				{
					for(int i = 0; i < tables.size(); i++)
					{
						if(tables.get(i).matches(pattern))
						{
							this.tables.put(tables.get(i), inst.getParams(tables.get(i)));
						}
					}
				}
			}

			if(tables.size() == 0)
			{
				throw new Exception("No tables selected please specify a valid pattern");
			}

			// templates
			if(cmd.hasOption('t'))
			{
				String[] templates = cmd.getOptionValue('t').split(",");

				for(int i = 0; i < templates.length; i++)
				{
					this.templates.add(templates[i]);
				}
			}

			if(this.templates.size() == 0)
			{
				throw new Exception("No templates selected");
			}


			generateCb.onGenerate(templates, tables);
		}
		catch(Exception e)
		{
			Espeon.handleException(e);
		}
	}

	public void setConnectCallback(ConnectCallback connectCb) 
	{
		this.connectCb = connectCb;
	}

	public void setGenerateCallback(GenerateCallback generateCb) 
	{
		this.generateCb = generateCb;
	}
}
