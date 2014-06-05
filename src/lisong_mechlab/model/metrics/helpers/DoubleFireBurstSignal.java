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
package lisong_mechlab.model.metrics.helpers;

import lisong_mechlab.model.Efficiencies;
import lisong_mechlab.model.item.BallisticWeapon;

/**
 * This class calculates the burst damage to a time for a weapon that is capable of double fire, such as the Ultra AC/5.
 * 
 * @author Emily Björk
 */
public class DoubleFireBurstSignal implements IntegratedSignal{

   private final BallisticWeapon weapon;
   private final Efficiencies    efficiencies;
   private final double          range;

   /**
    * @param aWeapon
    *           The weapon to generate the signal for.
    * @param aEfficiencies
    *           The {@link Efficiencies} of the pilot.
    * @param aRange
    */
   public DoubleFireBurstSignal(BallisticWeapon aWeapon, Efficiencies aEfficiencies, double aRange){
      if( !aWeapon.canDoubleFire() )
         throw new IllegalArgumentException("DoubleFireBurstSignal is only usable with weapons that can actually double fire!");
      weapon = aWeapon;
      efficiencies = aEfficiencies;
      range = aRange;
   }

   @Override
   public double integrateFromZeroTo(double aTime){
      return probableDamage(aTime) * weapon.getDamagePerShot() * weapon.getRangeEffectivity(range);
   }

   private double probableDamage(double aTime){
      if(aTime < 0)
         return 0;
      final double p_jam = weapon.getJamProbability();
      final double cd = weapon.getRawSecondsPerShot(efficiencies);
      final double jamtime = weapon.getJamTime();
      return p_jam * (1+probableDamage(aTime - jamtime - cd)) + (1 - p_jam) * (2+probableDamage(aTime - cd));
   }
}