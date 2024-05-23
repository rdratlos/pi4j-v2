package com.pi4j.plugin.linuxfs.provider.gpio.digital;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: PLUGIN   :: LinuxFS I/O Providers
 * FILENAME      :  LinuxFsDigitalOutputProviderImpl.java
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

import com.pi4j.io.gpio.digital.DigitalOutputConfig;
import com.pi4j.io.gpio.digital.DigitalOutputProviderBase;
import com.pi4j.plugin.linuxfs.provider.gpio.LinuxGpio;
import com.pi4j.provider.exception.ProviderException;
import java.io.IOException;

/**
 * <p>LinuxFsDigitalOutputProviderImpl class.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public class LinuxFsDigitalOutputProviderImpl extends DigitalOutputProviderBase implements LinuxFsDigitalOutputProvider {

    /**
     * <p>Constructor for LinuxFsDigitalOutputProviderImpl.</p>
     */
    public LinuxFsDigitalOutputProviderImpl(){
        this.id = ID;
        this.name = NAME;
    }

    /** {@inheritDoc} */
    @Override
    public LinuxFsDigitalOutput create(DigitalOutputConfig config) {

        LinuxGpio gpio = new LinuxGpio(config.address());

        // Remove hanging GPIO pin in sysfs, e. g. after program crash
        try {
            if (gpio.isExported()) {
                gpio.unexport();
            }
        } catch (java.io.IOException e) {
            throw new ProviderException(e);
        }

        try {
            gpio.export();
        } catch (java.io.IOException e) {
            throw new ProviderException(e);
        }
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new ProviderException(e);
        }
        try {
            if (!gpio.isExported()) {
                throw new IOException(String.format("Failed to create sysfs gpio%d device", config.address()));
            }
        } catch (java.io.IOException e) {
            throw new ProviderException(e);
        }

        try {
            gpio.direction(LinuxGpio.Direction.OUT);
        } catch (java.io.IOException e) {
            throw new ProviderException(e);
        }

        return new LinuxFsDigitalOutput(gpio, this, config);
    }
}
