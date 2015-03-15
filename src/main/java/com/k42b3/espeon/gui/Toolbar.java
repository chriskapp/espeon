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

import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * Toolbar
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       https://github.com/k42b3/espeon
 * @version    $Revision: 190 $
 */
public class Toolbar extends JPanel
{
	private JButton btnConnect;
	private JButton btnGenerate;
	private JButton btnAbout;
	private JButton btnExit;
	
	public Toolbar()
	{
		super();
		
		this.btnConnect  = new JButton("Connect");
		this.btnGenerate = new JButton("Generate");
		this.btnAbout    = new JButton("About");
		this.btnExit     = new JButton("Exit");
		
		this.btnConnect.setMnemonic(java.awt.event.KeyEvent.VK_C);
		this.btnGenerate.setMnemonic(java.awt.event.KeyEvent.VK_G);
		this.btnAbout.setMnemonic(java.awt.event.KeyEvent.VK_A);
		this.btnExit.setMnemonic(java.awt.event.KeyEvent.VK_Q);
		
		this.setLayout(new FlowLayout(FlowLayout.LEADING));
		
		this.add(this.btnConnect);
		this.add(this.btnGenerate);
		this.add(this.btnAbout);
		this.add(this.btnExit);
	}
	
	public JButton getConnect()
	{
		return this.btnConnect;
	}
	
	public JButton getGenerate()
	{
		return this.btnGenerate;
	}
	
	public JButton getAbout()
	{
		return this.btnAbout;
	}
	
	public JButton getExit()
	{
		return this.btnExit;
	}
}
