/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2024 Evan Debenham
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.items.rings;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Shopkeeper;
import com.shatteredpixel.shatteredpixeldungeon.items.EquipableItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.Pickaxe;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class RingOfEnergy extends Ring {

	{
		icon = ItemSpriteSheet.Icons.RING_ENERGY;
	}

	public String statsInfo() {
		if (isIdentified()){
			String info = Messages.get(this, "stats",
					Messages.decimalFormat("#.##", 100f * (Math.pow(1.15f, soloBuffedBonus()) - 1f)));
			if (isEquipped(Dungeon.hero) && soloBuffedBonus() != combinedBuffedBonus(Dungeon.hero)){
				info += "\n\n" + Messages.get(this, "combined_stats",
						Messages.decimalFormat("#.##", 100f * (Math.pow(1.15f, combinedBuffedBonus(Dungeon.hero)) - 1f)));
			}
			return info;
		} else {
			return Messages.get(this, "typical_stats",
					Messages.decimalFormat("#.##", 15f));
		}
	}
	
	@Override
	protected RingBuff buff( ) {
		return new Energy();
	}
	
	public static float wandChargeMultiplier( Char target ){
		return (float)Math.pow(1.15, getBuffedBonus(target, Energy.class));
	}

	public static float artifactChargeMultiplier( Char target ){
		float bonus = (float)Math.pow(1.15, getBuffedBonus(target, Energy.class));

		if (target instanceof Hero && ((Hero) target).heroClass != HeroClass.ROGUE && ((Hero) target).hasTalent(Talent.LIGHT_CLOAK)){
			bonus *= 1f + (0.2f * ((Hero) target).pointsInTalent(Talent.LIGHT_CLOAK)/3f);
		}

		if (target instanceof Hero && ((Hero) target).subClass == HeroSubClass.GRAVEROBBER ){
			bonus *= artifactChargeBonus(target);
		}

		return bonus;
	}

	public static float armorChargeMultiplier( Char target ){
		return (float)Math.pow(1.15, getBuffedBonus(target, Energy.class));
	}

	public static float artifactChargeBonus( Char target ){
		int value = 0;

		for (Item i : ((Hero) target).belongings.getAllItems(Item.class)){
			if (i.value() > 0) value += i.value();

			if (i.unique && !i.stackable) value += 100 * (i.level() + 1);
		}

		//classArmor value is 0! so..
		ClassArmor classArmor = ((Hero) target).belongings.getItem(ClassArmor.class);
		if (((Hero) target).belongings.contains(classArmor)) {
			int armorValue = (20 * classArmor.tier);

			if (classArmor.hasGoodGlyph()) {
				armorValue *= 1.5;
			}
			if (classArmor.cursedKnown && (classArmor.cursed || classArmor.hasCurseGlyph())) {
				armorValue /= 2;
			}
			if (classArmor.levelKnown && classArmor.level() > 0) {
				armorValue *= (classArmor.level() + 1);
			}

			value += armorValue;
		}

		value = Math.round( value / 100f );

		return 1f + (0.01f * value);
	}
	
	public class Energy extends RingBuff {
	}
}
