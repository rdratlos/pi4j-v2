package com.pi4j.extension.addonboard;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  AddOnBoard.java
 *
 * This file is part of the Pi4J project. More information about
 * this project can be found here:  https://pi4j.com/
 * **********************************************************************
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

import com.pi4j.context.Context;
import com.pi4j.exception.InitializeException;
import com.pi4j.exception.ShutdownException;
import com.pi4j.extension.Plugin;
import com.pi4j.extension.PluginService;
import java.util.ServiceLoader;

/**
 * <p>AddOnBoard class.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public interface AddOnBoard extends Plugin {

    /** Constant <code>NAME="Add-on Board"</code> */
    public static String NAME = "Add-on Board";
    /** Constant <code>ID="addonboard"</code> */
    public static String ID = "addonboard";

    // Platform name and unique ID
    /** Constant <code>PLATFORM_NAME="NAME +  Platform"</code> */
    public static String PLATFORM_NAME = NAME + " Platform";
    /** Constant <code>PLATFORM_ID="ID + -platform"</code> */
    public static String PLATFORM_ID = ID + "-platform";
    /** Constant <code>PLATFORM_DESCRIPTION="Pi4J platform used for mock testing."</code> */
    public static String PLATFORM_DESCRIPTION = "Pi4J add-on board platform to manage Raspberry Pi expansion boards";


    static Iterable<AddOnBoard> getAddOnBoards() {
        return ServiceLoader.load(AddOnBoard.class);
    }

   /** {@inheritDoc} */
    @Override
    public void initialize(PluginService service) throws InitializeException;

    /** {@inheritDoc} */
    @Override
    public void shutdown(Context context) throws ShutdownException;
}
