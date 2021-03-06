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
package lisong_mechlab.view.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import lisong_mechlab.model.loadout.LoadoutBase;
import lisong_mechlab.model.loadout.LoadoutStandard;
import lisong_mechlab.model.loadout.OpStripArmor;
import lisong_mechlab.util.message.MessageXBar;
import lisong_mechlab.view.mechlab.LoadoutFrame;

/**
 * This action sets the armor to max on the given {@link LoadoutStandard}.
 * 
 * @author Emily Björk
 */
public class StripArmorAction extends AbstractAction {
    private static final long    serialVersionUID = -5939335331941199195L;
    private final LoadoutBase<?> loadout;
    private final LoadoutFrame   loadoutFrame;
    private final MessageXBar    xBar;

    /**
     * Creates a new {@link StripArmorAction}.
     * 
     * @param aLoadout
     *            The {@link LoadoutStandard} to set armor for.
     * @param anXBar
     *            The {@link MessageXBar} to signal armor changes on.
     */
    public StripArmorAction(LoadoutFrame aLoadout, MessageXBar anXBar) {
        super("Strip Armor");
        loadoutFrame = aLoadout;
        loadout = aLoadout.getLoadout();
        xBar = anXBar;
    }

    @Override
    public void actionPerformed(ActionEvent aArg0) {
        loadoutFrame.getOpStack().pushAndApply(new OpStripArmor(loadout, xBar));
    }
}
