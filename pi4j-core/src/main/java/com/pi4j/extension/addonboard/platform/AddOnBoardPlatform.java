package com.pi4j.extension.addonboard.platform;

/*-
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: LIBRARY  :: Java Library (CORE)
 * FILENAME      :  AddOnBoardPlatform.java
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
import com.pi4j.extension.addonboard.AddOnBoard;
import com.pi4j.platform.Platform;
import com.pi4j.platform.PlatformBase;
import java.util.HashSet;
import java.util.Set;
import com.pi4j.provider.Provider;
import com.pi4j.provider.exception.ProviderNotFoundException;

/**
 * <p>AddOnBoardPlatform class.</p>
 *
 * @author Robert Savage (<a href="http://www.savagehomeautomation.com">http://www.savagehomeautomation.com</a>)
 * @version $Id: $Id
 */
public class AddOnBoardPlatform extends PlatformBase<AddOnBoardPlatform> implements Platform {

    protected Set<String> supportedProviderIds = new HashSet<>();

    /**
     * <p>Constructor for MockPlatform.</p>
     */
    public AddOnBoardPlatform(){
        super(AddOnBoard.PLATFORM_ID,
              AddOnBoard.PLATFORM_NAME,
              AddOnBoard.PLATFORM_DESCRIPTION);
    }

    /**
     * <p>Constructor for AddOnBoardPlatform.</p>
     * @param id Platform ID
     * @param name Platform name
     * @param description Platform description
     */
    public AddOnBoardPlatform(String id, String name, String description){
        super(id,
              name,
              description);
    }

    /** {@inheritDoc} */
    @Override
    public int priority() {
        return 4;
    }

    /** {@inheritDoc} */
    @Override
    public boolean enabled(Context context) {
        // the Mock Platform is always available when detected
        // there are no logic checked required to determine when
        // and if the mock platforms should be enabled
        return true;
    }

    /** {@inheritDoc} */
    @Override
    protected String[] getProviders() {
        return new String[] {};
    }

    /**
     * <p>provider.</p>
     * 
     * Bugfix:<br>
     * This method fixes a bug in Pi4J v2 class {@link com.pi4j.platform.Platform#provider(java.lang.String) Platform},
     * which requires a provider class to be known to Pi4J core library. This bug breaks the extensibility
     * of Pi4J to other platforms.
     *
     * @param providerId a {@link java.lang.String} object.
     * @param <T> a T object.
     * @return a T object.
     * @throws com.pi4j.provider.exception.ProviderNotFoundException if any.
     */
/*
    @Override
    public <T extends Provider> T provider(String providerId) throws ProviderNotFoundException {
        for(Provider provider : providers().values()) {
            if (provider.getId().equals(providerId)) {
                return (T) provider;
            }
        }
        // unable to resolve provider by 'id
        throw new ProviderNotFoundException(providerId);
    }
*/
}
