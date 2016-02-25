package dragonball.model.battle;

import java.util.ArrayList;
import java.util.Random;

import dragonball.exceptions.InvalidAttackException;
import dragonball.exceptions.NotEnoughCollectiblesException;
import dragonball.model.attack.Attack;
import dragonball.model.attack.PhysicalAttack;
import dragonball.model.cell.Collectible;
import dragonball.model.character.fighter.Fighter;
import dragonball.model.character.fighter.PlayableFighter;
import dragonball.model.character.fighter.Saiyan;
import dragonball.model.player.Player;

public class Battle {
	private BattleOpponent me;
	private BattleOpponent foe;
	private BattleOpponent currentOpponent;
	private BattleListener listener;
	private boolean meBlocking;
	private boolean foeBlocking;

	public Battle(BattleOpponent me, BattleOpponent foe) {
		this.me = me;
		this.foe = foe;
		this.currentOpponent = me;

		// set current values appropriately
		Fighter meFighter = (Fighter) me;
		meFighter.setHealthPoints(meFighter.getMaxHealthPoints());
		meFighter.setKi(0);
		meFighter.setStamina(meFighter.getMaxStamina());
		// reset a saiyan's transformation state in case it was transformed in a previous battle
		if (me instanceof Saiyan) {
			Saiyan meSaiyan = (Saiyan) me;
			meSaiyan.setTransformed(false);
		}

		Fighter foeFighter = (Fighter) foe;
		foeFighter.setHealthPoints(foeFighter.getMaxHealthPoints());
		foeFighter.setKi(0);
		foeFighter.setStamina(foeFighter.getMaxStamina());
	}

	public BattleOpponent getMe() {
		return me;
	}

	public BattleOpponent getFoe() {
		return foe;
	}

	public BattleOpponent getCurrentOpponent() {
		return currentOpponent;
	}

	public BattleOpponent getOtherOpponent() {
		return currentOpponent == me ? foe : me;
	}

	public ArrayList<Attack> getCurrentOpponentAttacks() {
		Fighter currentFighter = (Fighter) currentOpponent;

		ArrayList<Attack> attacks = new ArrayList<>();
		// make sure to include the physical attack as well
		attacks.add(new PhysicalAttack());
		attacks.addAll(currentFighter.getSuperAttacks());
		attacks.addAll(currentFighter.getUltimateAttacks());
		return attacks;
	}

	private void switchTurn() {
		currentOpponent = getOtherOpponent();
	}

	private void endTurn() {
		// reset block mode
		if (currentOpponent == me && foeBlocking) {
			foeBlocking = false;
		} else if (currentOpponent == foe && meBlocking) {
			meBlocking = false;
		}

		// if i'm dead
		if (((Fighter) me).getHealthPoints() == 0) {
			// tell everyone my opponent won
			notifyListeners(new BattleEvent(this, BattleEventType.ENDED, foe));
			// if my opponent is dead
		} else if (((Fighter) foe).getHealthPoints() == 0) {
			// tell everyone i won
			notifyListeners(new BattleEvent(this, BattleEventType.ENDED, me));
		} else {
			switchTurn();

			getCurrentOpponent().onFoeTurn();
			getOtherOpponent().onMyTurn();

			notifyListeners(new BattleEvent(this, BattleEventType.NEW_TURN));
		}
	}

	public void start() {
		notifyListeners(new BattleEvent(this, BattleEventType.STARTED));
		notifyListeners(new BattleEvent(this, BattleEventType.NEW_TURN));
	}

	// used to automate turn for opponent a.k.a. ai
	public void play() {
		if (new Random().nextInt(100) > 15) {
			ArrayList<Attack> attacks = getCurrentOpponentAttacks();
			do {
				try {
					Attack randomAttack = attacks.get(new Random().nextInt(attacks.size()));
					attack(randomAttack);
					break;
				} catch (InvalidAttackException e) {

				}
			} while (true);
		} else {
			block();
		}
	}

	// perform an attack and end turn
	public void attack(Attack attack) throws InvalidAttackException {
		attack.onUse(currentOpponent, getOtherOpponent(),
				(currentOpponent == me && foeBlocking) || (currentOpponent == foe && meBlocking));

		notifyListeners(new BattleEvent(this, BattleEventType.ATTACK, attack));

		endTurn();
	}

	// perform a block and end turn
	public void block() {
		if (currentOpponent == me) {
			meBlocking = true;
		} else if (currentOpponent == foe) {
			foeBlocking = true;
		}

		notifyListeners(new BattleEvent(this, BattleEventType.BLOCK));

		endTurn();
	}

	// use a collectible and end turn
	public void use(Player player, Collectible collectible) throws NotEnoughCollectiblesException {
		switch (collectible) {
		case SENZU_BEAN:
			if (player.getSenzuBeans() > 0) {
				PlayableFighter activeFighter = player.getActiveFighter();
				activeFighter.setHealthPoints(activeFighter.getMaxHealthPoints());
				activeFighter.setStamina(activeFighter.getMaxStamina());

				player.setSenzuBeans(player.getSenzuBeans() - 1);

				notifyListeners(new BattleEvent(this, BattleEventType.USE, collectible));
			} else {
				throw new NotEnoughCollectiblesException(Collectible.SENZU_BEAN);
			}
			break;
		default:
			break;
		}

		endTurn();
	}

	public void setListener(BattleListener listener) {
		this.listener = listener;
	}

	public void notifyListeners(BattleEvent e) {
		if (listener != null) {
			listener.onBattleEvent(e);
		}
	}
}
