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

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.k42b3.espeon.ConnectCallback;
import com.k42b3.espeon.Espeon;

/**
 * ConnectPanel
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       https://github.com/k42b3/espeon
 * @version    $Revision: 193 $
 */
public class ConnectPanel extends JFrame
{
	public static boolean isActive = false;
	
	private Espeon inst;

	private JTextField txtHost;
	private JTextField txtDb;
	private JTextField txtUser;
	private JTextField txtPw;

	private JButton btnConnect;
	private JButton btnCancel;
	
	private ConnectCallback callback;
	
	public ConnectPanel(Espeon inst)
	{
		ConnectPanel.isActive = true;

		this.setLocationRelativeTo(null);
		
		this.setSize(200, 180);

		this.setMinimumSize(this.getSize());

		this.setResizable(false);

		this.setTitle("Connect");

		this.setLayout(new GridLayout(0, 1));


		JPanel panelHost = new JPanel();

		panelHost.setLayout(new FlowLayout());

		JLabel lblHost = new JLabel("Host:");
		lblHost.setPreferredSize(new Dimension(80, 20));
		
		this.txtHost = new JTextField();
		this.txtHost.setText(inst.getHost());
		this.txtHost.setPreferredSize(new Dimension(120, 24));
		
		panelHost.add(lblHost);
		panelHost.add(this.txtHost);
		
		this.add(panelHost);
		
		
		JPanel panelDb = new JPanel();
		
		panelDb.setLayout(new FlowLayout());

		JLabel lblDb = new JLabel("Database:");
		lblDb.setPreferredSize(new Dimension(80, 20));
		
		this.txtDb = new JTextField();
		this.txtDb.setText(inst.getDb());
		this.txtDb.setPreferredSize(new Dimension(120, 24));
		
		panelDb.add(lblDb);
		panelDb.add(this.txtDb);
		
		this.add(panelDb);
		
		
		JPanel panelUser = new JPanel();
		
		panelUser.setLayout(new FlowLayout());

		JLabel lblUser = new JLabel("User:");
		lblUser.setPreferredSize(new Dimension(80, 20));
		
		this.txtUser = new JTextField();
		this.txtUser.setText(inst.getUser());
		this.txtUser.setPreferredSize(new Dimension(120, 24));
		
		panelUser.add(lblUser);
		panelUser.add(this.txtUser);
		
		this.add(panelUser);
		

		JPanel panelPw = new JPanel();
		
		panelPw.setLayout(new FlowLayout());

		JLabel lblPw = new JLabel("Password:");
		lblPw.setPreferredSize(new Dimension(80, 20));
		
		this.txtPw = new JTextField();
		this.txtPw.setText(inst.getPw());
		this.txtPw.setPreferredSize(new Dimension(120, 24));

		panelPw.add(lblPw);
		panelPw.add(this.txtPw);
		
		this.add(panelPw);
		
		
		JPanel panelButtons = new JPanel();
		
		panelButtons.setLayout(new FlowLayout());
		
		this.btnConnect = new JButton("Connect");
		this.btnConnect.setPreferredSize(new Dimension(100, 24));
		this.btnConnect.addActionListener(new connectHandler());
		
		panelButtons.add(this.btnConnect);
		
		this.btnCancel = new JButton("Cancel");
		this.btnCancel.setPreferredSize(new Dimension(100, 24));
		this.btnCancel.addActionListener(new cancelHandler());
		
		panelButtons.add(this.btnCancel);
		
		this.add(panelButtons);
	}
	
	public void setCallback(ConnectCallback callback)
	{
		this.callback = callback;
	}

	public void close()
	{
		setVisible(false);
		
		ConnectPanel.isActive = false;
	}
	
	public class connectHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent e) 
		{
			try
			{
				callback.onConnect(txtHost.getText(), txtDb.getText(), txtUser.getText(), txtPw.getText());

				close();
			}
			catch(Exception ex)
			{
				JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
