package io.github.cishero6;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;


public class BedHuntEvents implements Listener {
	Plugin m_plugin;

	BedHuntEvents(Plugin plugin) {
		m_plugin = plugin;
	}

	static List<Location> blocks = new ArrayList<Location>();
	static List<Location> beds = new ArrayList<Location>();
	static Material[] BedsEnum = { Material.RED_BED, Material.BLUE_BED, Material.GREEN_BED, Material.YELLOW_BED,
			Material.MAGENTA_BED, Material.BLACK_BED, Material.WHITE_BED, Material.CYAN_BED };
	static Material[] WoolsEnum = { Material.RED_WOOL, Material.BLUE_WOOL, Material.GREEN_WOOL, Material.YELLOW_WOOL,
			Material.MAGENTA_WOOL, Material.BLACK_WOOL, Material.WHITE_WOOL, Material.CYAN_WOOL };
	static String[] TeamIDs = new String[] { "Red", "Blue", "Green", "Yellow", "Magenta", "Black", "White", "Cyan" };

	void manageBreakBed(int teamId, BlockBreakEvent e) {
		io.github.cishero6.Main.bedExists[teamId] = false;
		e.setDropItems(false);
		io.github.cishero6.BedHuntEvents.updateScoreboard();
		for (int j = 0; j < io.github.cishero6.Main.maxPlayers; j++) {
			if (io.github.cishero6.Main.Teams[teamId][j] != null) {
				io.github.cishero6.Main.Teams[teamId][j].sendMessage();
				io.github.cishero6.Main.Teams[teamId][j].sendTitle(
						ChatColor.RED + "" + ChatColor.BOLD + "Your bed has been destroyed!",
						ChatColor.RED + "" + ChatColor.ITALIC + "You Will no longer respawn!", 10, 60, 10);
				io.github.cishero6.Main.Teams[teamId][j].playSound(
						io.github.cishero6.Main.Teams[teamId][j].getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 10, 1);
			}
		}
		beds.remove(e.getBlock().getLocation());
		for (Player p : io.github.cishero6.Main.players) {
			p.playSound(p.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 10, 1);
			p.sendMessage(ChatColor.GOLD + TeamIDs[teamId] + " bed has been destroyed!");
		}
		return;
	}

	@EventHandler()
	public void onQuit(PlayerQuitEvent event) {
		if (io.github.cishero6.Main.isPlaying(event.getPlayer())) {
			return;
		}
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 4; j++) {
				if (event.getPlayer() == io.github.cishero6.Main.Teams[i][j]) {
					for (int p = j; p < io.github.cishero6.Main.maxPlayers - 1; p++) {
						io.github.cishero6.Main.Teams[i][p] = io.github.cishero6.Main.Teams[i][p + 1];
					}
					io.github.cishero6.Main.Teams[i][io.github.cishero6.Main.maxPlayers - 1] = null;
				}
			}
		}
		for (int i = 0; i < io.github.cishero6.Main.curPlayers; i++) {
			if (event.getPlayer() == io.github.cishero6.Main.players[i]) {
				io.github.cishero6.Main.curPlayers--;
				for (int j = i; j < io.github.cishero6.Main.curPlayers - 1; j++) {
					io.github.cishero6.Main.players[j] = io.github.cishero6.Main.players[j + 1];
				}
				io.github.cishero6.Main.players[io.github.cishero6.Main.maxPlayers - 1] = null;
			}
		}
	}

	@EventHandler()
	public void onFall(EntityDamageEvent event) {
		if (!event.getEntity().getWorld().getPVP()) {
			if (event.getCause() == DamageCause.FALL) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler()
	public void onBreak(BlockBreakEvent event) {
		if (!io.github.cishero6.Main.gameCreated) {
			return;
		}
		if (!io.github.cishero6.Main.isPlaying(event.getPlayer())) {
			return;
		}
		if (io.github.cishero6.Main.beforeGameCountdown) {
			event.setCancelled(true);
			return;
		}
		if (!event.getPlayer().getWorld().getPVP()) {
			if (!blocks.contains(event.getBlock().getLocation())) {
				event.setCancelled(true);
				return;
			}
			blocks.remove(event.getBlock().getLocation());
			return;
		}
		for (int i = 0; i < 8; i++) {
			if (event.getBlock().getType() == BedsEnum[i]) {
				if (io.github.cishero6.Main.getTeamID(event.getPlayer()) == i) {
					event.getPlayer().sendMessage(ChatColor.RED + "You can't break your bed!");
					event.setCancelled(true);
					return;
				}
				manageBreakBed(i, event);
			}
		}
		if (!blocks.contains(event.getBlock().getLocation())) {
			event.setCancelled(true);
			return;
		}
		blocks.remove(event.getBlock().getLocation());
	}

	@EventHandler()
	public void onBlockPlace(BlockPlaceEvent event) {
		if (!io.github.cishero6.Main.isPlaying(event.getPlayer()))
			return;
		if (io.github.cishero6.Main.beforeGameCountdown) {
			event.setCancelled(true);
			return;
		}
		if (event.getBlock().getType() == Material.ENCHANTING_TABLE) {
			event.setCancelled(true);
			return;
		}
		if (io.github.cishero6.Main.gameStarted) {
			if ((event.getBlock().getType() == Material.RED_BED) || (event.getBlock().getType() == Material.BLUE_BED)
					|| (event.getBlock().getType() == Material.GREEN_BED)
					|| (event.getBlock().getType() == Material.YELLOW_BED)
					|| (event.getBlock().getType() == Material.BLACK_BED)
					|| (event.getBlock().getType() == Material.MAGENTA_BED)
					|| (event.getBlock().getType() == Material.WHITE_BED)
					|| (event.getBlock().getType() == Material.CYAN_BED)) {

				for (int j = 0; j < io.github.cishero6.Main.maxPlayers; j++) {
					if (io.github.cishero6.Main.Teams[io.github.cishero6.Main
							.getTeamID(event.getPlayer())][j] != null) {
						io.github.cishero6.Main.Teams[io.github.cishero6.Main.getTeamID(event.getPlayer())][j]
								.setBedSpawnLocation(event.getBlock().getLocation().add(0, 0, 0), false);
					}
				}
				if (event.getPlayer().getBedSpawnLocation() == null) {
					event.getPlayer().sendMessage(ChatColor.RED + "Invalid place for a bed!");
					event.setCancelled(true);
					return;
				}
				io.github.cishero6.Main.bedExists[io.github.cishero6.Main.getTeamID(event.getPlayer())] = true;
				updateScoreboard();
				beds.add(event.getBlock().getLocation());
				for (int j = 0; j < io.github.cishero6.Main.maxPlayers; j++) {
					if (io.github.cishero6.Main.Teams[io.github.cishero6.Main
							.getTeamID(event.getPlayer())][j] != null) {
						io.github.cishero6.Main.Teams[io.github.cishero6.Main.getTeamID(event.getPlayer())][j]
								.sendTitle(ChatColor.GOLD + "Your bed is placed!",
										ChatColor.GOLD + "" + ChatColor.BOLD + "Protect it!", 20, 100, 20);
					}
				}
				return;
			}
			if ((event.getBlock().getType() != Material.BLACK_WOOL)
					&& (event.getBlock().getType() != Material.BLUE_WOOL)
					&& (event.getBlock().getType() != Material.GREEN_WOOL)
					&& (event.getBlock().getType() != Material.RED_WOOL)
					&& (event.getBlock().getType() != Material.YELLOW_WOOL)
					&& (event.getBlock().getType() != Material.WHITE_WOOL)
					&& (event.getBlock().getType() != Material.CYAN_WOOL)
					&& (event.getBlock().getType() != Material.MAGENTA_WOOL)) {
				event.setCancelled(true);
				return;
			}
			if ((event.getPlayer().getBedSpawnLocation() == null)
					&& io.github.cishero6.Main.bedExists[io.github.cishero6.Main.getTeamID(event.getPlayer())]) {
				event.getPlayer().sendMessage(ChatColor.RED + "This will corrupt your bed!");
				event.setCancelled(true);
				return;
			}
			for (int i = 0; i < io.github.cishero6.Main.maxTeams; i++) {
				if (io.github.cishero6.Main.Teams[i][0] != null) {
					if ((io.github.cishero6.Main.Teams[i][0].getBedSpawnLocation() == null)
							&& io.github.cishero6.Main.bedExists[i]) {
						event.getPlayer().sendMessage(ChatColor.RED + "This will corrupt " + TeamIDs[i] + " Team bed!");
						event.setCancelled(true);
						return;
					}
				}
			}
			blocks.add(event.getBlock().getLocation());
			return;
		}
	}

	@EventHandler()
	public void OnRightClick(PlayerInteractEvent event) {
		if ((io.github.cishero6.Main.gameCreated)) {
			if ((event.getPlayer().getInventory().getItemInMainHand().getType() == Material.ENCHANTING_TABLE)
					&& io.github.cishero6.Main.isPlaying(event.getPlayer())) {
				io.github.cishero6.Main.createTeamSelectorInv();
				event.getPlayer().openInventory(io.github.cishero6.Main.chooseTeamInventory);
			}
		}
	}

	@EventHandler()
	public void OnDrop(PlayerDropItemEvent event) {
		Player player = (Player) event.getPlayer();
		if (io.github.cishero6.Main.isPlaying(player)) {
			event.setCancelled(true);
		}
	}

	@EventHandler()
	public void OnChoose(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		if (!io.github.cishero6.Main.isPlaying(player)) {
			return;
		}
		if (event.getCurrentItem() == null) {
			return;
		}
		if (event.getCurrentItem().getItemMeta().hasLore() && !io.github.cishero6.Main.gameStarted) {
			if (event.getCurrentItem().getType() == Material.BARRIER) {
				player.closeInventory();
				return;
			}
			for (int i = 0; i < 8; i++) {
				if (event.getCurrentItem().getType() == WoolsEnum[i]) {
					io.github.cishero6.Main.playerRemoveTeam(player);
					io.github.cishero6.Main.playerJoinTeam(player, i);
					player.closeInventory();
					return;
				}
			}
			if (event.getCurrentItem().getType() == Material.REDSTONE_BLOCK) {
				io.github.cishero6.Main.playerRemoveTeam(player);
				io.github.cishero6.Main.playerJoinRandomTeam(player);
				player.closeInventory();
				return;
			}
		}
//might need else return
		if ((event.getCurrentItem().getType() == Material.LEATHER_CHESTPLATE)
				|| (event.getCurrentItem().getType() == Material.LEATHER_HELMET)
				|| (event.getCurrentItem().getType() == Material.IRON_BOOTS)
				|| (event.getCurrentItem().getType() == Material.IRON_LEGGINGS)) {
			event.setCancelled(true);
			return;
		}
	}

	public int checkWin() {
		int ID = -2;
		int count = 0;
		for (int i = 0; i < io.github.cishero6.Main.maxTeams; i++) {
			for (int j = 0; j < io.github.cishero6.Main.maxPlayers; j++) {
				if (io.github.cishero6.Main.Teams[i][j] != null) {
					count++;
					ID = i;
					break;
				}
			}
		}
		if (count > 1)
			return -1;
		else
			return ID;
	}

	@EventHandler()
	public void onMonsterSpawn(EntitySpawnEvent event) {
		if (io.github.cishero6.Main.gameCreated) {
			if (event.getEntity() instanceof Animals || event.getEntity() instanceof Monster) event.setCancelled(true);
		}
	}
	
	@EventHandler()
	public void onDeath(PlayerDeathEvent event) {
		if ((io.github.cishero6.Main.gameStarted) && (io.github.cishero6.Main.gameCreated)) {
			if (!io.github.cishero6.Main.bedExists[io.github.cishero6.Main.getTeamID(event.getEntity())]
					|| (event.getEntity().getBedSpawnLocation() == null)) {
				io.github.cishero6.Main.playerRemoveTeam(event.getEntity());
				io.github.cishero6.BedHuntEvents.updateScoreboard();
				event.getEntity().setGameMode(GameMode.SPECTATOR);
				event.getEntity().sendTitle(ChatColor.RED + "Game over!", ChatColor.GOLD + "Tou can't respawn anymore!",
						0, 100, 0);
				if (checkWin() >= 0) {
					// event.getEntity().getWorld().getBlockAt(io.github.cishero6.Main.Teams[checkWin()][0].getBedLocation()).setType(Material.AIR);
					io.github.cishero6.Main.Teams[checkWin()][0].getWorld().setPVP(false);
					for (int i = 0; i < io.github.cishero6.Main.curPlayers; i++) {
						io.github.cishero6.Main.players[i].teleport(io.github.cishero6.Main.center);
						io.github.cishero6.Main.players[i].setGameMode(GameMode.SURVIVAL);
						io.github.cishero6.Main.players[i].sendTitle(
								ChatColor.BOLD + "" + ChatColor.GOLD + "Game Ended!",
								ChatColor.GOLD + "" + TeamIDs[checkWin()] + " Team Won!!", 1, 100, 1);
					}
					io.github.cishero6.Main.players[0].getWorld().setPVP(true);
					for (int i = 0; i < io.github.cishero6.Main.curPlayers; i++) {
						io.github.cishero6.Main.players[i]
								.sendMessage(ChatColor.YELLOW + "The BedHunt game was rejected!");
						io.github.cishero6.Main.players[i].getInventory().clear();
						io.github.cishero6.Main.players[i] = null;
					}
					for (int i = 0; i < 8; i++) {
						for (int j = 0; j < 4; j++) {
							io.github.cishero6.Main.Teams[i][j] = null;
						}
					}
					io.github.cishero6.Main.curPlayers = 0;
					io.github.cishero6.Main.gameCreated = false;
					for (Location loc : blocks) {
						event.getEntity().getWorld().getBlockAt(loc).setType(Material.AIR);
					}
					for (Location loc : beds) {
						event.getEntity().getWorld().getBlockAt(loc).setType(Material.AIR);
					}
					return;
				}
			} else {
				event.getEntity().setGameMode(GameMode.SPECTATOR);
				new BukkitRunnable() {
					int timer = 5;

					@Override
					public void run() {
						timer--;
						if (timer <= 0) {
							event.getEntity().setGameMode(GameMode.SURVIVAL);
							if (event.getEntity().getBedSpawnLocation() != null)
								event.getEntity().teleport(event.getEntity().getBedSpawnLocation());
							this.cancel();
						}
						if (timer != 0)
							event.getEntity().sendTitle(ChatColor.RED + "You Died!",
									ChatColor.GOLD + "Respawning in " + timer + " seconds!", 0, 20, 0);

					}
				}.runTaskTimer(m_plugin, 0, 20);
			}

		}
	}

	public static void createBoard(Player player) {
		ScoreboardManager manager = Bukkit.getScoreboardManager();
		Scoreboard board = manager.getNewScoreboard();
		Objective obj = board.registerNewObjective("BedHuntScoreboard", "dummy",
				ChatColor.GOLD + "" + ChatColor.BOLD + "BedHunt Stats");
		obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		Score score = obj.getScore(ChatColor.BLUE + "-=-=-=-=-=-=-=-=-=-=-");
		score.setScore(8);
		String teamSymbol = "";
		for (int i = 0; i < 8; i++) {
			if (io.github.cishero6.Main.Teams[i][0] != null)
				teamSymbol = ChatColor.YELLOW + "" + io.github.cishero6.Main.getPlayersInTeam(i);
			if (io.github.cishero6.Main.bedExists[i])
				teamSymbol = ChatColor.GREEN + "Bed";
			if (io.github.cishero6.Main.Teams[i][0] == null)
				teamSymbol = ChatColor.RED + "0";
			score = obj.getScore("" + TeamIDs[i] + " - " + teamSymbol);
			score.setScore(7 - i);
		}
		player.setScoreboard(board);
		return;
	}

	public static void updateScoreboard() {
		for (int i = 0; i < io.github.cishero6.Main.curPlayers; i++) {
			createBoard(io.github.cishero6.Main.players[i]);
		}
	}
}
