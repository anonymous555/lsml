/*
 * @formatter:off
 * Li Song Mechlab - A 'mech building tool for PGI's MechWarrior: Online.
 * Copyright (C) 2013  Emily Björk
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
//@formatter:on
package lisong_mechlab.view.preferences;

import lisong_mechlab.util.message.MessageXBar;

/**
 * This class is a container class for all the individual preferences classes.
 * 
 * @author Emily Björk
 */
public class Preferences {
    public final FontPreferences   fontPreferences   = new FontPreferences();
    public final SmurfyPreferences smurfyPreferences = new SmurfyPreferences();
    public final UiPreferences     uiPreferences;

    /**
     * Creates a new preferences object.
     * 
     * @param aXBar
     *            The {@link MessageXBar} to send notifications of preference changes on.
     */
    public Preferences(MessageXBar aXBar) {
        uiPreferences = new UiPreferences(aXBar);
    }
}
