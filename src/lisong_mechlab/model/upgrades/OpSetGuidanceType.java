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
package lisong_mechlab.model.upgrades;

import lisong_mechlab.model.item.Ammunition;
import lisong_mechlab.model.item.Item;
import lisong_mechlab.model.item.MissileWeapon;
import lisong_mechlab.model.loadout.LoadoutBase;
import lisong_mechlab.model.loadout.LoadoutStandard;
import lisong_mechlab.model.loadout.component.ConfiguredComponentBase;
import lisong_mechlab.model.loadout.component.OpAddItem;
import lisong_mechlab.model.loadout.component.OpRemoveItem;
import lisong_mechlab.model.upgrades.Upgrades.UpgradesMessage;
import lisong_mechlab.model.upgrades.Upgrades.UpgradesMessage.ChangeMsg;
import lisong_mechlab.util.OperationStack;
import lisong_mechlab.util.OperationStack.CompositeOperation;
import lisong_mechlab.util.OperationStack.Operation;
import lisong_mechlab.util.message.MessageDelivery;

/**
 * This {@link Operation} changes the guidance status of a {@link LoadoutStandard}.
 * 
 * @author Emily Björk
 */
public class OpSetGuidanceType extends CompositeOperation {
    private final GuidanceUpgrade oldValue;
    private final GuidanceUpgrade newValue;
    private final Upgrades        upgrades;
    private final LoadoutBase<?>  loadout;

    /**
     * Creates a {@link OpSetGuidanceType} that only affects a stand-alone {@link UpgradesMutable} object This is useful
     * only for altering {@link UpgradesMutable} objects which are not attached to a {@link LoadoutBase} in any way.
     * 
     * @param aUpgrades
     *            The {@link UpgradesMutable} object to alter with this {@link Operation}.
     * @param aGuidanceUpgrade
     *            The new upgrade to use.
     */
    public OpSetGuidanceType(Upgrades aUpgrades, GuidanceUpgrade aGuidanceUpgrade) {
        super(aGuidanceUpgrade.getName(), null);
        upgrades = aUpgrades;
        loadout = null;
        oldValue = upgrades.getGuidance();
        newValue = aGuidanceUpgrade;
    }

    /**
     * Creates a new {@link OpSetGuidanceType} that will change the guidance upgrade of a {@link LoadoutStandard}.
     * 
     * @param aMessageDelivery
     *            A {@link MessageDelivery} to signal changes in guidance status on.
     * @param aLoadout
     *            The {@link LoadoutBase} to alter.
     * @param aGuidanceUpgrade
     *            The new upgrade to use.
     */
    public OpSetGuidanceType(MessageDelivery aMessageDelivery, LoadoutBase<?> aLoadout, GuidanceUpgrade aGuidanceUpgrade) {
        super(aGuidanceUpgrade.getName(), aMessageDelivery);
        upgrades = aLoadout.getUpgrades();
        loadout = aLoadout;
        oldValue = upgrades.getGuidance();
        newValue = aGuidanceUpgrade;
    }

    @Override
    public void buildOperation() {
        if (loadout != null) {
            if (newValue.getExtraSlots(loadout) > loadout.getNumCriticalSlotsFree())
                throw new IllegalArgumentException("Too few critical slots available in loadout!");

            for (ConfiguredComponentBase part : loadout.getComponents()) {
                if (newValue.getExtraSlots(part) > part.getSlotsFree())
                    throw new IllegalArgumentException("Too few critical slots available in "
                            + part.getInternalComponent().getLocation() + "!");
            }

            if (newValue.getExtraTons(loadout) > loadout.getFreeMass()) {
                throw new IllegalArgumentException("Too heavy to add artmemis!");
            }

            addOp(new OperationStack.Operation() {
                private void set(GuidanceUpgrade aValue) {
                    if (aValue != upgrades.getGuidance()) {
                        upgrades.setGuidance(aValue);
                        messageBuffer.post(new UpgradesMessage(ChangeMsg.GUIDANCE, upgrades));
                    }
                }

                @Override
                protected void undo() {
                    set(oldValue);
                }

                @Override
                public String describe() {
                    return "Set guidance (internal)";
                }

                @Override
                protected void apply() {
                    set(newValue);
                }
            });

            for (ConfiguredComponentBase component : loadout.getComponents()) {
                for (Item item : component.getItemsEquipped()) {
                    // FIXME: What about fixed missile launchers?
                    if (item instanceof MissileWeapon) {
                        MissileWeapon oldWeapon = (MissileWeapon) item;
                        MissileWeapon newWeapon = newValue.upgrade(oldWeapon);
                        if (oldWeapon != newWeapon) {
                            addOp(new OpRemoveItem(messageBuffer, loadout, component, oldWeapon));
                            addOp(new OpAddItem(messageBuffer, loadout, component, newWeapon));
                        }
                    }
                    else if (item instanceof Ammunition) {
                        Ammunition oldAmmo = (Ammunition) item;
                        Ammunition newAmmo = newValue.upgrade(oldAmmo);
                        if (oldAmmo != newAmmo) {
                            addOp(new OpRemoveItem(messageBuffer, loadout, component, oldAmmo));
                            addOp(new OpAddItem(messageBuffer, loadout, component, newAmmo));
                        }
                    }
                }
            }
        }
    }
}
