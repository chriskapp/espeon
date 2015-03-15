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
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import com.k42b3.espeon.GenerateCallback;
import com.k42b3.espeon.model.FileTemplate;

/**
 * GeneratePanel
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       https://github.com/k42b3/espeon
 * @version    $Revision: 193 $
 */
public class GeneratePanel extends JFrame
{
	public static boolean isActive = false;
	
	private HashMap<String, HashMap<String, Object>> tables;

	private JButton btnGenerate;
	private JButton btnCancel;
	
	private FileTemplate tm;

	private GenerateCallback callback;

	public GeneratePanel(HashMap<String, HashMap<String, Object>> tables)
	{
		this.tables = tables;


		this.setLocationRelativeTo(null);

		this.setSize(200, 200);

		this.setMinimumSize(this.getSize());

		this.setResizable(false);

		this.setTitle("Generate");

		this.setLayout(new BorderLayout());


		this.tm = new FileTemplate();

		JScrollPane scrTable = new JScrollPane(new JTable(this.tm));
		
		scrTable.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));		
		
		scrTable.setPreferredSize(new Dimension(180, 120));
		
		this.add(scrTable, BorderLayout.CENTER);

		
		JPanel panelButtons = new JPanel();
		
		panelButtons.setLayout(new FlowLayout());
		
		this.btnGenerate = new JButton("Generate");
		this.btnGenerate.setPreferredSize(new Dimension(100, 24));
		this.btnGenerate.addActionListener(new generateHandler());
		
		panelButtons.add(this.btnGenerate);
		
		this.btnCancel = new JButton("Cancel");
		this.btnCancel.setPreferredSize(new Dimension(100, 24));
		this.btnCancel.addActionListener(new cancelHandler());
		
		panelButtons.add(this.btnCancel);
		
		this.add(panelButtons, BorderLayout.SOUTH);
	}
	
	public void setCallback(GenerateCallback callback)
	{
		this.callback = callback;
	}

	public void close()
	{
		setVisible(false);
		
		GeneratePanel.isActive = false;
	}

	public class generateHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent e) 
		{
			ArrayList<String> templates = new ArrayList<String>();

			for(int i = 0; i < tm.getRowCount(); i++)
			{
				if((Boolean) tm.getValueAt(i, 0))
				{
					templates.add((String) tm.getValueAt(i, 1));
				}
			}

			if(templates.size() > 0)
			{
				try
				{
					callback.onGenerate(templates, tables);

					JOptionPane.showMessageDialog(null, "You have successful generated the code", "Informations", JOptionPane.INFORMATION_MESSAGE);
				}
				catch(Exception ex)
				{
					JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}

				close();
			}
			else
			{
				JOptionPane.showMessageDialog(null, "You must select min one template", "Information", JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}

	public class cancelHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent e) 
		{
			close();
		}
	}
}
