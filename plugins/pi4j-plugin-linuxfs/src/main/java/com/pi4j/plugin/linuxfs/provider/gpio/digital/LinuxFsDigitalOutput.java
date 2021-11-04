package com.pi4j.plugin.linuxfs.provider.gpio.digital;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: PLUGIN   :: LinuxFS I/O Providers
 * FILENAME      :  LinuxFsDigitalOutput.java
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
import com.pi4j.io.exception.IOException;
import com.pi4j.io.gpio.digital.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.pi4j.plugin.linuxfs.internal.LinuxGpio;



/**
 * <p>LinuxFsDigitalOutput class.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public class LinuxFsDigitalOutput extends DigitalOutputBase implements DigitalOutput {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    protected int address = -1;
    protected LinuxGpio gpio;

    /**
     * <p>Constructor for LinuxFsDigitalOutput.</p>
     *
     * @param provider a {@link com.pi4j.io.gpio.digital.DigitalOutputProvider} object.
     * @param config a {@link com.pi4j.io.gpio.digital.DigitalOutputConfig} object.
     */
    public LinuxFsDigitalOutput(DigitalOutputProvider provider, DigitalOutputConfig config){
        super(provider, config);
    }

    /** {@inheritDoc} */
    @Override
    public DigitalOutput initialize(Context context) throws InitializeException {
        super.initialize(context);
        try {
            // create new LINUX GPIO instance for this pin address
            gpio = new LinuxGpio(this.address);

            // export GPIO pin via Linux File System
            if(!gpio.isExported()) gpio.export();;

            // set GPIO pin to DIRECTION=OUT via Linux File System
            gpio.direction(LinuxGpio.Direction.OUT);

            // enable GPIO interrupt via Linux File System (if supported)
            if(gpio.isInterruptSupported()) gpio.interruptEdge(LinuxGpio.Edge.BOTH);

        } catch (java.io.IOException e) {
            logger.error(e.getMessage(), e);
            throw new InitializeException(e);
        }
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public DigitalOutput state(DigitalState state) throws IOException {
        try {
            // set GPIO state via Linux File System
            gpio.state(state);
        } catch (java.io.IOException e) {
            logger.error(e.getMessage(), e);
            throw new IOException(e.getMessage(), e);
        }
        return super.state(state);
    }
}
