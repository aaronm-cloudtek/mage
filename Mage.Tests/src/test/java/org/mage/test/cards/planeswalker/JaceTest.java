/*
 *  Copyright 2010 BetaSteward_at_googlemail.com. All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without modification, are
 *  permitted provided that the following conditions are met:
 *
 *     1. Redistributions of source code must retain the above copyright notice, this list of
 *        conditions and the following disclaimer.
 *
 *     2. Redistributions in binary form must reproduce the above copyright notice, this list
 *        of conditions and the following disclaimer in the documentation and/or other materials
 *        provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY BetaSteward_at_googlemail.com ``AS IS'' AND ANY EXPRESS OR IMPLIED
 *  WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 *  FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL BetaSteward_at_googlemail.com OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 *  ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *  The views and conclusions contained in the software and documentation are those of the
 *  authors and should not be interpreted as representing official policies, either expressed
 *  or implied, of BetaSteward_at_googlemail.com.
 */
package org.mage.test.cards.planeswalker;

import mage.constants.PhaseStep;
import mage.constants.Zone;
import mage.counters.CounterType;
import org.junit.Test;
import org.mage.test.serverside.base.CardTestPlayerBase;

/**
 *
 * @author LevelX2
 */
public class JaceTest extends CardTestPlayerBase {

    @Test
    public void TelepathUnboundSecondAbility() {
        // +1: Up to one target creature gets -2/-0 until your next turn.
        // -3: You may cast target instant or sorcery card from your graveyard this turn. If that card would be put into your graveyard this turn, exile it instead.
        // -9: You get an emblem with "Whenever you cast a spell, target opponent puts the top five cards of his or her library into his or her graveyard".
        addCard(Zone.BATTLEFIELD, playerA, "Jace, Telepath Unbound"); // starts with 7 Loyality counters

        // As an additional cost to cast Magmatic Insight, discard a land card.
        // Draw two cards.
        addCard(Zone.GRAVEYARD, playerA, "Magmatic Insight");// {R}
        addCard(Zone.HAND, playerA, "Plains");

        addCard(Zone.BATTLEFIELD, playerA, "Mountain", 1);

        activateAbility(1, PhaseStep.PRECOMBAT_MAIN, playerA, "-3: You may cast target instant", "Magmatic Insight");

        castSpell(1, PhaseStep.PRECOMBAT_MAIN, playerA, "Magmatic Insight");

        setStopAt(1, PhaseStep.BEGIN_COMBAT);
        execute();

        assertPermanentCount(playerA, "Jace, Telepath Unbound", 1);
        assertCounterCount("Jace, Telepath Unbound", CounterType.LOYALTY, 2);  // 5 - 3 = 2

        assertExileCount("Magmatic Insight", 1);

        assertHandCount(playerA, 2);

    }

    /**
     * I know it's been a bit a rules question recently but I believe flip
     * planeswalkers shouldn't be exiled by Containment priest when flipping as
     * happens when using xmage (at least with Jace).
     */
    @Test
    public void testContainmentPriestWithFlipPlaneswalker() {
        addCard(Zone.BATTLEFIELD, playerA, "Island", 4);
        addCard(Zone.GRAVEYARD, playerA, "Mountain", 4);

        // {T}: Draw a card, then discard a card. If there are five or more cards in your graveyard,
        // exile Jace, Vryn's Prodigy, then return him to the battefield transformed under his owner's control.
        addCard(Zone.BATTLEFIELD, playerA, "Jace, Vryn's Prodigy", 1); // {2}{R} - 3/2
        addCard(Zone.HAND, playerA, "Pillarfield Ox", 1);

        // Flash
        // If a nontoken creature would enter the battlefield and it wasn't cast, exile it instead.
        addCard(Zone.BATTLEFIELD, playerB, "Containment Priest", 1); // {2}{U}{U}

        activateAbility(3, PhaseStep.PRECOMBAT_MAIN, playerA, "{T}: Draw a card");
        setChoice(playerA, "Pillarfield Ox");

        setStopAt(3, PhaseStep.BEGIN_COMBAT);
        execute();

        assertGraveyardCount(playerA, "Pillarfield Ox", 1);
        assertExileCount("Jace, Vryn's Prodigy", 0);
        assertPermanentCount(playerA, "Jace, Telepath Unbound", 1);

    }

    /**
     * I know it's been a bit a rules question recently but I believe flip
     * planeswalkers shouldn't be exiled by Containment priest when flipping as
     * happens when using xmage (at least with Jace).
     */
    @Test
    public void testJaceUnravelerOfSecretsEmblem() {
        // +1: Scry 1, then draw a card.
        // -2: Return target creature to its owner's hand.
        // -8: You get an emblem with "Whenever an opponent casts his or her first spell each turn, counter that spell."
        addCard(Zone.BATTLEFIELD, playerA, "Jace, Unraveler of Secrets", 1); // starts with 5 Loyality counters
        addCounters(1, PhaseStep.UPKEEP, playerA, "Jace, Unraveler of Secrets", CounterType.LOYALTY, 5);

        addCard(Zone.BATTLEFIELD, playerB, "Plains", 2);
        addCard(Zone.HAND, playerB, "Perimeter Captain", 2);

        activateAbility(1, PhaseStep.PRECOMBAT_MAIN, playerA, "-8: You get an emblem");

        castSpell(2, PhaseStep.PRECOMBAT_MAIN, playerB, "Perimeter Captain");
        castSpell(2, PhaseStep.PRECOMBAT_MAIN, playerB, "Perimeter Captain");

        setStopAt(2, PhaseStep.END_TURN);
        execute();

        assertEmblemCount(playerA, 1);

        assertPermanentCount(playerB, "Perimeter Captain", 1);
        assertGraveyardCount(playerB, "Perimeter Captain", 1);

    }

}
