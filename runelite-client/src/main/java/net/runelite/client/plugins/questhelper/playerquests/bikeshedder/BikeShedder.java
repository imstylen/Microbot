/*
 * Copyright (c) 2024, pajlada <https://github.com/pajlada>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.questhelper.playerquests.bikeshedder;

import com.google.common.collect.ImmutableMap;
import net.runelite.client.plugins.questhelper.collections.ItemCollections;
import net.runelite.client.plugins.questhelper.panel.PanelDetails;
import net.runelite.client.plugins.questhelper.questhelpers.BasicQuestHelper;
import net.runelite.client.plugins.questhelper.requirements.item.ItemRequirement;
import net.runelite.client.plugins.questhelper.requirements.player.SpellbookRequirement;
import net.runelite.client.plugins.questhelper.requirements.util.Spellbook;
import net.runelite.client.plugins.questhelper.requirements.zone.Zone;
import net.runelite.client.plugins.questhelper.requirements.zone.ZoneRequirement;
import net.runelite.client.plugins.questhelper.steps.ConditionalStep;
import net.runelite.client.plugins.questhelper.steps.DetailedQuestStep;
import net.runelite.client.plugins.questhelper.steps.NpcStep;
import net.runelite.client.plugins.questhelper.steps.ObjectStep;
import net.runelite.client.plugins.questhelper.steps.QuestStep;
import net.runelite.client.plugins.questhelper.steps.widget.NormalSpells;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.NullObjectID;
import net.runelite.api.coords.WorldPoint;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BikeShedder extends BasicQuestHelper
{
	private DetailedQuestStep moveToLumbridge;
	private DetailedQuestStep confuseHans;
	private DetailedQuestStep equipLightbearer;

	private ItemRequirement anyLog;
	private ObjectStep useLogOnBush;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		var lumbridge = new Zone(new WorldPoint(3217, 3210, 0), new WorldPoint(3226, 3228, 0));
		var outsideLumbridge = new ZoneRequirement(false, lumbridge);

		var steps = new ConditionalStep(this, confuseHans);
		steps.addStep(outsideLumbridge, moveToLumbridge);
		steps.addStep(new ZoneRequirement(new WorldPoint(3222, 3218, 0)), equipLightbearer);
		steps.addStep(new ZoneRequirement(new WorldPoint(3223, 3218, 0)), useLogOnBush);
		return new ImmutableMap.Builder<Integer, QuestStep>()
			.put(-1, steps)
			.build();
	}

	@Override
	protected void setupRequirements()
	{
		moveToLumbridge = new DetailedQuestStep(this, new WorldPoint(3221, 3218, 0), "Move to outside Lumbridge Castle");

		var normalSpellbook = new SpellbookRequirement(Spellbook.NORMAL);

		confuseHans = new NpcStep(this, NpcID.HANS, new WorldPoint(3221, 3218, 0), "Cast Confuse on Hans", normalSpellbook);
		confuseHans.addSpellHighlight(NormalSpells.CONFUSE);

		var lightbearer = new ItemRequirement("Lightbearer", ItemID.LIGHTBEARER).highlighted();
		equipLightbearer = new DetailedQuestStep(this, "Equip a Lightbearer", lightbearer.equipped());

		anyLog = new ItemRequirement("Any log", ItemCollections.LOGS_FOR_FIRE).highlighted();
		useLogOnBush = new ObjectStep(this, NullObjectID.NULL_10778, new WorldPoint(3223, 3217, 0), "Use log on bush", anyLog);
		useLogOnBush.addIcon(ItemID.LOGS);
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		var panels = new ArrayList<PanelDetails>();

		panels.add(new PanelDetails("Move to Lumbridge", List.of(moveToLumbridge)));
		panels.add(new PanelDetails("Normal Spellbook", List.of(confuseHans)));
		panels.add(new PanelDetails("Equip Lightbearer", List.of(equipLightbearer)));
		panels.add(new PanelDetails("Use log on mysterious bush", List.of(useLogOnBush), List.of(anyLog)));

		return panels;
	}
}
