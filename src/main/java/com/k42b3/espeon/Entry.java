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

import javax.swing.UIManager;

/**
 * Entry
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       https://github.com/k42b3/espeon
 * @version    $Revision: 193 $
 */
public class Entry 
{
	public static void main(String[] args)
	{
		try
		{
			Espeon inst = new Espeon();

			if(args.length == 0)
			{
				// set look and feel
				String lookAndFeel = UIManager.getSystemLookAndFeelClassName();

				UIManager.setLookAndFeel(lookAndFeel);


				inst.runGui();
			}
			else
			{
				inst.runCmd(args);
			}
		}
		catch(Exception e)
		{
			Espeon.handleException(e);
		}
	}
}
