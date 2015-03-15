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

package com.k42b3.espeon.model;

import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import com.k42b3.espeon.Espeon;

/**
 * SqlTable
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       https://github.com/k42b3/espeon
 * @version    $Revision: 190 $
 */
public class SqlTable implements TableModel
{
	private ArrayList<TableModelListener> listener;
	
	private String[] columns = {"Active", "Field", "Type", "Null", "Key", "Default", "Extra"};
	private Object[][] rows;
	private Espeon inst;

	public SqlTable(Espeon inst)
	{
		this.rows = new Object[0][0];
		this.inst = inst;
		
		this.listener = new ArrayList<TableModelListener>();
	}

	public void loadTable(String table)
	{
		try
		{
			// get table structure
			Object[][] params = inst.getTableStructure(table);
			this.rows = new Object[params.length][this.columns.length];

			for(int i = 0; i < params.length; i++)
			{
				this.rows[i][0] = Boolean.TRUE;
				this.rows[i][1] = params[i][0];
				this.rows[i][2] = params[i][1];
				this.rows[i][3] = params[i][2];
				this.rows[i][4] = params[i][3];
				this.rows[i][5] = params[i][4];
				this.rows[i][6] = params[i][5];
			}

			// update table
			for(int i = 0; i < this.listener.size(); i++)
			{
				TableModelEvent e = new TableModelEvent(this, TableModelEvent.HEADER_ROW);
				
				this.listener.get(i).tableChanged(e);
			}
		}
		catch(SQLException e)
		{
			JOptionPane.showMessageDialog(null, e.getMessage(), "SQL Exception", JOptionPane.WARNING_MESSAGE);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public void addTableModelListener(TableModelListener l) 
	{
		if(!this.listener.contains(l))
		{
			this.listener.add(l);
		}
	}

	public Class<?> getColumnClass(int columnIndex) 
	{
		if(columnIndex == 0)
		{
			return Boolean.class;
		}
		else
		{
			return String.class;
		}
	}

	public int getColumnCount() 
	{
		return this.columns.length;
	}

	public String getColumnName(int columnIndex) 
	{
		if(columnIndex >= 0 && columnIndex < this.columns.length)
		{
			return this.columns[columnIndex];
		}
		else
		{
			return null;
		}
	}

	public int getRowCount() 
	{
		return this.rows.length;
	}

	public Object getValueAt(int rowIndex, int columnIndex) 
	{
		if((rowIndex >= 0 && rowIndex < this.rows.length) && (columnIndex >= 0 && columnIndex < this.columns.length))
		{
			return this.rows[rowIndex][columnIndex];
		}
		else
		{
			return null;
		}
	}

	public boolean isCellEditable(int rowIndex, int columnIndex)
	{
		return columnIndex == 0 ? true : false;
	}

	public void removeTableModelListener(TableModelListener l) 
	{
		this.listener.remove(l);
	}

	public void setValueAt(Object aValue, int rowIndex, int columnIndex) 
	{
		if((rowIndex >= 0 && rowIndex < this.rows.length) && (columnIndex >= 0 && columnIndex < this.columns.length))
		{
			this.rows[rowIndex][columnIndex] = aValue;
		}
	}
}
