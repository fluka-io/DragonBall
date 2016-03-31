package dragonball.view.world;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.HashSet;
import java.util.Set;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JPanel;

import dragonball.custom.Tuple;
import dragonball.view.cell.CellView;
import dragonball.view.custom.HudView;
import dragonball.view.custom.MenuView;
import dragonball.view.custom.MenuView.Menu;
import dragonball.view.custom.MenuView.Menu.Item;
import dragonball.view.game.GameView;

@SuppressWarnings("serial")
public class WorldView extends JPanel {
	private static final int CELL_SIZE = 48;
	private CellView[][] map;
	private Clip clip;
	private Set<Listener> listeners = new HashSet<>();
	private ArrayList<Tuple<String, Item.Listener>> createFighterSubItems;
	private ArrayList<Tuple<String, Item.Listener>> activeFighterSubItems;
	private ArrayList<Tuple<String, Item.Listener>> upgradeFighterSubItems;
	private ArrayList<Tuple<String, Item.Listener>> assignFighterAttackSubItems;

	public WorldView(int size) {
		map = new CellView[size][size];
		setLayout(new GridLayout(size, size, 0, 0));
		setPreferredSize(new Dimension(size * CELL_SIZE, size * CELL_SIZE));

		createFighterSubItems = new ArrayList<>();
		activeFighterSubItems = new ArrayList<>();
		upgradeFighterSubItems = new ArrayList<>();
		assignFighterAttackSubItems = new ArrayList<>();

		enterWorld();
	}

	public CellView[][] getMap() {
		return map;
	}

	public void renderMap() {
		removeAll();

		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[i].length; j++) {
				add(map[i][j]);
			}
		}

		repaint();
		validate();
	}

	public void enterWorld() {
		try {
			clip = AudioSystem.getClip();
			AudioInputStream inputStream = AudioSystem.getAudioInputStream(
					new BufferedInputStream(new FileInputStream("sfx" + File.separator + "world.wav")));
			clip.open(inputStream);

			if (GameView.PLAY_SOUND) {
				clip.loop(Clip.LOOP_CONTINUOUSLY);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void leaveWorld() {
		if (clip != null) {
			clip.stop();
		}
	}

	public void addCreateFighterSubItem(String name, Item.Listener listener) {
		createFighterSubItems.add(new Tuple<>(name, listener));
	}

	public void addActiveFighterSubItem(String name, Item.Listener listener) {
		activeFighterSubItems.add(new Tuple<>(name, listener));
	}

	public void addUpgradeFighterSubItem(String name, Item.Listener listener) {
		upgradeFighterSubItems.add(new Tuple<>(name, listener));
	}

	public void addAssignFighterAttackSubItem(String name, Item.Listener listener) {
		assignFighterAttackSubItems.add(new Tuple<>(name, listener));
	}

	public void clearAssignFighterAttackSubItems() {
		assignFighterAttackSubItems.clear();
	}

	public void showMenu() {
		final MenuView menuView = MenuView.getInstance();
		menuView.setOrigin(0, (int) (getPreferredSize().getHeight()));
		if (menuView.isShowing()) {
			return;
		}

		final Menu menu = menuView.addMenu();

		final Item createFighterItem = menu.addItem("Create Fighter");
		Dimension createFighterItemSize = createFighterItem.getPreferredSize();
		createFighterItem.setPreferredSize(new Dimension(250, (int) createFighterItemSize.getHeight()));
		menu.notifyListenersOnItemAdded(createFighterItem);
		createFighterItem.addListener(new Item.Listener() {
			@Override
			public void onAction(Item item) {
				final Menu createFighterMenu = menuView.addMenu();
				for (Tuple<String, Item.Listener> createFighterSubItemTuple : createFighterSubItems) {
					String itemName = createFighterSubItemTuple.getA();
					Item.Listener itemListener = createFighterSubItemTuple.getB();
					Item createFighterSubItem = createFighterMenu.addItem(itemName, itemListener);
					Dimension size = createFighterSubItem.getPreferredSize();
					createFighterSubItem.setPreferredSize(new Dimension(250, (int) size.getHeight()));
					createFighterMenu.notifyListenersOnItemAdded(createFighterSubItem);
				}
			}
		});

		if (!activeFighterSubItems.isEmpty()) {
			final Item activeFighterItem = menu.addItem("Active Fighter");
			menu.notifyListenersOnItemAdded(activeFighterItem);
			activeFighterItem.addListener(new Item.Listener() {
				@Override
				public void onAction(Item item) {
					final Menu activeFighterMenu = menuView.addMenu();
					for (Tuple<String, Item.Listener> activeFighterSubItemTuple : activeFighterSubItems) {
						String itemName = activeFighterSubItemTuple.getA();
						Item.Listener itemListener = activeFighterSubItemTuple.getB();
						Item activeFighterSubItem = activeFighterMenu.addItem(itemName, itemListener);
						Dimension size = activeFighterSubItem.getPreferredSize();
						activeFighterSubItem.setPreferredSize(new Dimension(250, (int) size.getHeight()));
						activeFighterMenu.notifyListenersOnItemAdded(activeFighterSubItem);
					}
				}
			});

			final Item upgradeFighterItem = menu.addItem("Upgrade Fighter");
			menu.notifyListenersOnItemAdded(upgradeFighterItem);
			upgradeFighterItem.addListener(new Item.Listener() {
				@Override
				public void onAction(Item item) {
					final Menu upgradeFighterMenu = menuView.addMenu();
					for (Tuple<String, Item.Listener> upgradeFighterSubItemTuple : upgradeFighterSubItems) {
						String itemName = upgradeFighterSubItemTuple.getA();
						Item.Listener itemListener = upgradeFighterSubItemTuple.getB();
						Item upgradeFighterSubItem = upgradeFighterMenu.addItem(itemName, itemListener);
						Dimension size = upgradeFighterSubItem.getPreferredSize();
						upgradeFighterSubItem.setPreferredSize(new Dimension(250, (int) size.getHeight()));
						upgradeFighterMenu.notifyListenersOnItemAdded(upgradeFighterSubItem);
					}
				}
			});

			if (!assignFighterAttackSubItems.isEmpty()) {
				final Item assignAttackItem = menu.addItem("Assign Attack");
				menu.notifyListenersOnItemAdded(assignAttackItem);
				assignAttackItem.addListener(new Item.Listener() {
					@Override
					public void onAction(Item item) {
						final Menu assignAttackMenu = menuView.addMenu();
						for (Tuple<String, Item.Listener> assignAttackSubItemTuple : assignFighterAttackSubItems) {
							String itemName = assignAttackSubItemTuple.getA();
							Item.Listener itemListener = assignAttackSubItemTuple.getB();
							Item assignAttackSubItem = assignAttackMenu.addItem(itemName, itemListener);
							Dimension size = assignAttackSubItem.getPreferredSize();
							assignAttackSubItem.setPreferredSize(new Dimension(300, (int) size.getHeight()));
							assignAttackMenu.notifyListenersOnItemAdded(assignAttackSubItem);
						}
					}
				});
			}
		}

		final Item loadSaveItem = menu.addItem("Load/Save");
		loadSaveItem.addListener(new Item.Listener() {
			@Override
			public void onAction(Item item) {
				// TODO: Implement Load/Save menu
				Menu saveLoadMenu = menuView.addMenu();
				saveLoadMenu.addItem("Load", new Item.Listener() {
					@Override
					public void onAction(Item item) {
						menuView.removeAllMenus();
						HudView.getInstance().setText("Game loaded!");
					}
				});
				saveLoadMenu.addItem("Save", new Item.Listener() {
					@Override
					public void onAction(Item item) {
						menuView.removeAllMenus();
						HudView.getInstance().setText("Game saved!");
					}
				});
			}
		});

		final Item exitItem = menu.addItem("Exit");
		exitItem.addListener(new Item.Listener() {
			@Override
			public void onAction(Item item) {
				final Menu exitMenu = menuView.addMenu();
				Item exitConfirmItem = exitMenu.addItem("Are you sure?");
				Dimension size = exitConfirmItem.getPreferredSize();
				exitConfirmItem.setPreferredSize(new Dimension(220, (int) size.getHeight()));
				exitMenu.notifyListenersOnItemAdded(exitConfirmItem);

				exitMenu.addItem("Yes", new Item.Listener() {
					@Override
					public void onAction(Item item) {
						menuView.removeAllMenus();
						notifyListenersOnMenuExit();
					}
				});

				exitMenu.addItem("No", new Item.Listener() {
					@Override
					public void onAction(Item item) {
						menuView.removeAllMenus();
					}
				});
			}
		});
	}

	public void addListener(Listener listener) {
		listeners.add(listener);
	}

	public void notifyListenersOnMenuExit() {
		for (Listener listener : listeners) {
			listener.onMenuExit();
		}
	}

	public interface Listener extends EventListener {
		void onMenuExit();
	}
}
