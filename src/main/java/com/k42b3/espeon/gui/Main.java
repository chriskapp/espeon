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

package com.k42b3.espeon.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.k42b3.espeon.ConnectCallback;
import com.k42b3.espeon.Espeon;
import com.k42b3.espeon.GenerateCallback;
import com.k42b3.espeon.View;
import com.k42b3.espeon.model.SqlTable;

/**
 * Main
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       https://github.com/k42b3/espeon
 * @version    $Revision: 194 $
 */
public class Main extends JFrame implements View
{
	private Espeon inst;
	private Main self;
	private ConnectCallback connectCb;
	private GenerateCallback generateCb;

	private JList<String> list;
	private DefaultListModel<String> lm;
	private JTable table;
	private SqlTable tm;
	private Toolbar toolbar;

	public Main(Espeon inst) throws Exception
	{
		this.inst = inst;
		this.self = this;


		this.setTitle("espeon (version: " + Espeon.version + ")");
		
		this.setLocation(100, 100);
		
		this.setSize(700, 500);
		
		this.setMinimumSize(this.getSize());

		this.setLayout(new BorderLayout());


		// columns
		this.table = new JTable();

		this.tm = new SqlTable(inst);

		this.table.setModel(this.tm);

		this.table.setEnabled(false);
		
		JScrollPane scrTable = new JScrollPane(this.table);
		
		scrTable.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 4));		
		
		this.add(scrTable, BorderLayout.CENTER);


		// toolbar
		this.toolbar = new Toolbar();
		
		this.toolbar.getConnect().addActionListener(new connectHandler());
		
		this.toolbar.getGenerate().addActionListener(new generateHandler());
		
		this.toolbar.getGenerate().setEnabled(false);
		
		this.toolbar.getAbout().addActionListener(new aboutHandler());
		
		this.toolbar.getExit().addActionListener(new exitHandler());

		this.add(this.toolbar, BorderLayout.SOUTH);


		// list
		this.lm = new DefaultListModel<String>(); 

		this.list = new JList<String>(this.lm);

		this.list.setEnabled(false);

		this.list.addListSelectionListener(new listHandler());

		JScrollPane scrList = new JScrollPane(this.list);
		
		scrList.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		
		scrList.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		scrList.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		scrList.setPreferredSize(new Dimension(180, 400));

		this.add(scrList, BorderLayout.WEST);


		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public void setConnectCallback(ConnectCallback connectCb)
	{
		this.connectCb = connectCb;
	}

	public void setGenerateCallback(GenerateCallback generateCb)
	{
		this.generateCb = generateCb;
	}
	
	public void run()
	{
		SwingUtilities.invokeLater(new Runnable() {

			public void run() 
			{
				try
				{
					// set visivble
					self.pack();

					self.setVisible(true);

					// auto connect if config is available
					if(inst.hasMysqlConfig())
					{
						self.doConnect(null, null, null, null);
					}
				}
				catch(Exception e)
				{
					Espeon.handleException(e);
				}
			}

		});
	}

	public void doConnect(String host, String db, String user, String pw)
	{
		try
		{
			// connect
			connectCb.onConnect(host, db, user, pw);

			// list tables
			List<String> tables = inst.getTables();

			for(int i = 0; i < tables.size(); i++)
			{
				lm.addElement(tables.get(i));
			}

			// enable/disable buttons
			toolbar.getGenerate().setEnabled(true);
			toolbar.getConnect().setEnabled(false);

			list.setEnabled(true);
			table.setEnabled(true);
		}
		catch(Exception e)
		{
			JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.WARNING_MESSAGE);
		}
	}

	public class connectHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent e) 
		{
			SwingUtilities.invokeLater(new Runnable(){

				public void run() 
				{
					if(!ConnectPanel.isActive)
					{
						ConnectPanel win = new ConnectPanel(inst);

						win.pack();

						win.setCallback(new ConnectCallback(){

							public void onConnect(String host, String db, String user, String pw) throws Exception
							{
								doConnect(host, db, user, pw);
							}

						});

						win.setVisible(true);
					}
				}

			});
		}
	}

	public class generateHandler implements ActionListener
	{
		private HashMap<String, HashMap<String, Object>> tables;

		public void actionPerformed(ActionEvent e)
		{
			if(list.getSelectedIndex() != -1)
			{
				tables = new HashMap<String, HashMap<String, Object>>();

				List<String> selectedTables = list.getSelectedValuesList();

				for(int i = 0; i < selectedTables.size(); i++)
				{
					try
					{
						String table = selectedTables.get(i);
						HashMap<String, Object> params = inst.getParams(table);

						tables.put(table, params);
					}
					catch(Exception ex)
					{
						Espeon.handleException(ex);
					}
				}

				SwingUtilities.invokeLater(new Runnable(){

					public void run() 
					{
						GeneratePanel win = new GeneratePanel(tables);

						win.pack();

						win.setCallback(generateCb);

						win.setVisible(true);
					}

				});
			}
			else
			{
				JOptionPane.showMessageDialog(null, "Please select a table", "Information", JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}

	public class aboutHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent e) 
		{
			JOptionPane.showMessageDialog(null, Espeon.getAbout(), "About", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	public class exitHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent e) 
		{
			System.exit(0);
		}
	}
	
	public class listHandler implements ListSelectionListener
	{
		public void valueChanged(ListSelectionEvent e) 
		{
			if(e.getValueIsAdjusting())
			{
				JList list = (JList) e.getSource();

				tm.loadTable(list.getSelectedValue().toString());
			}
		}
	}
}
