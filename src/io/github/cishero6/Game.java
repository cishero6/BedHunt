package io.github.cishero6;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class Game implements CommandExecutor {
	int[] countdownBefore = { 10 };
	int[] countdownToHide = { 35 };
	boolean[] START = { true };
	boolean[] canBeCancelled = { true };
	Plugin m_plugin;

	Game(Plugin plugin) {
		m_plugin = plugin;
	}
	
	void setUpGame(Player player) {
		player.getWorld().setPVP(false);
		player.getWorld().setGameRule(GameRule.KEEP_INVENTORY, true);
		player.getWorld().setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
		countdownToHide[0] = 35;
		countdownBefore[0] = 10;
		canBeCancelled[0] = true;
		START[0] = true;
		io.github.cishero6.Main.curPlayers = 0;
		io.github.cishero6.Main.gameCreated = true;
		io.github.cishero6.Main.gameStarted = false;
		io.github.cishero6.Main.center = player.getLocation();
		io.github.cishero6.BedHuntEvents.beds.clear();
		io.github.cishero6.BedHuntEvents.blocks.clear();
	}
	
	void deleteGame(Player player) {
		player.getWorld().setGameRule(GameRule.KEEP_INVENTORY, true);
		player.getWorld().setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, false);
		player.getWorld().setPVP(true);
		io.github.cishero6.Main.gameCreated = false;
		io.github.cishero6.Main.gameStarted = false;
		for (int i = 0; i < io.github.cishero6.Main.curPlayers; i++) {
			io.github.cishero6.Main.players[i].sendMessage(ChatColor.YELLOW + "The BedHunt game was rejected!");
			io.github.cishero6.Main.players[i].getInventory().clear();
			io.github.cishero6.Main.players[i].setScoreboard(null);
			io.github.cishero6.Main.players[i] = null;
		}
		io.github.cishero6.Main.curPlayers = 0;
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 4; j++) {
				io.github.cishero6.Main.Teams[i][j] = null;
			}
		}
		for (Location loc : io.github.cishero6.BedHuntEvents.blocks) {
			player.getWorld().getBlockAt(loc).setType(Material.AIR);
		}
		for (Location loc : io.github.cishero6.BedHuntEvents.beds) {
			player.getWorld().getBlockAt(loc).setType(Material.AIR);
		}
		for (Entity ent: player.getWorld().getEntities()) {
			if (ent instanceof Item) ent.remove();
		}
	}

	void putPlayerInGame(Player player) {
		player.getInventory().clear();
		ItemStack teamselector = new ItemStack(Material.ENCHANTING_TABLE);
		ItemStack armor = new ItemStack(Material.IRON_LEGGINGS);
		ItemMeta meta2 = armor.getItemMeta();
		meta2.setUnbreakable(true);
		armor.setItemMeta(meta2);
		player.getInventory().setLeggings(armor);
		armor = new ItemStack(Material.IRON_BOOTS);
		meta2 = armor.getItemMeta();
		meta2.setUnbreakable(true);
		armor.setItemMeta(meta2);
		player.getInventory().setBoots(armor);
		ItemMeta meta = teamselector.getItemMeta();
		meta.setDisplayName(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Select Team");
		List<String> lore = new ArrayList<String>();
		lore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "Click to select team!");
		meta.setLore(lore);
		teamselector.setItemMeta(meta);
		player.getInventory().addItem(teamselector);

		io.github.cishero6.Main.players[io.github.cishero6.Main.curPlayers] = player;
		io.github.cishero6.Main.curPlayers++;
		player.sendMessage(ChatColor.GREEN + "You joined the game!");
	}
	
	void giveGameplayInventory(Player player) {
		ItemStack item = new ItemStack(Material.IRON_SWORD);
		ItemMeta meta = item.getItemMeta();
		meta.setUnbreakable(true);
		item.setItemMeta(meta);
		player.getInventory().addItem(item);
		item.setType(Material.BOW);
		meta = item.getItemMeta();
		meta.addEnchant(Enchantment.ARROW_INFINITE, 1, false);
		meta.setUnbreakable(true);
		item.setItemMeta(meta);
		player.getInventory().addItem(item);
		item.setType(Material.IRON_AXE);
		meta = item.getItemMeta();
		meta.setUnbreakable(true);
		meta.removeEnchant(Enchantment.ARROW_INFINITE);
		item.setItemMeta(meta);
		player.getInventory().addItem(item);
		meta.setUnbreakable(false);
		item.setItemMeta(meta);
		item.setType(Material.ENDER_PEARL);
		item.setAmount(16);
		player.getInventory().addItem(item);
		item.setType(Material.GOLDEN_APPLE);
		item.setAmount(5);
		player.getInventory().addItem(item);
		item.setType(Material.COOKED_BEEF);
		item.setAmount(32);
		player.getInventory().addItem(item);
		item.setType(Material.ARROW);
		item.setAmount(1);
		player.getInventory().addItem(item);
		item.setType(Material.WATER_BUCKET);
		item.setAmount(1);
		player.getInventory().addItem(item);
		switch (io.github.cishero6.Main.getTeamID(player)) {
		case 0:
			item.setType(Material.RED_WOOL);
			item.setAmount(64);
			player.getInventory().addItem(item);
			if (player == io.github.cishero6.Main.Teams[0][0]) {
				item.setType(Material.RED_BED);
				item.setAmount(1);
				player.getInventory().addItem(item);
			}
			break;
		case 1:
			item.setType(Material.BLUE_WOOL);
			item.setAmount(64);
			player.getInventory().addItem(item);
			if (player == io.github.cishero6.Main.Teams[1][0]) {
				item.setType(Material.BLUE_BED);
				item.setAmount(1);
				player.getInventory().addItem(item);
			}
			break;
		case 2:
			item.setType(Material.GREEN_WOOL);
			item.setAmount(64);
			player.getInventory().addItem(item);
			if (player == io.github.cishero6.Main.Teams[2][0]) {
				item.setType(Material.GREEN_BED);
				item.setAmount(1);
				player.getInventory().addItem(item);
			}
			break;
		case 3:
			item.setType(Material.YELLOW_WOOL);
			item.setAmount(64);
			player.getInventory().addItem(item);
			if (player == io.github.cishero6.Main.Teams[3][0]) {
				item.setType(Material.YELLOW_BED);
				item.setAmount(1);
				player.getInventory().addItem(item);
			}
			break;
		case 4:
			item.setType(Material.MAGENTA_WOOL);
			item.setAmount(64);
			player.getInventory().addItem(item);
			if (player == io.github.cishero6.Main.Teams[4][0]) {
				item.setType(Material.MAGENTA_BED);
				item.setAmount(1);
				player.getInventory().addItem(item);
			}
			break;
		case 5:
			item.setType(Material.BLACK_WOOL);
			item.setAmount(64);
			player.getInventory().addItem(item);
			if (player == io.github.cishero6.Main.Teams[5][0]) {
				item.setType(Material.BLACK_BED);
				item.setAmount(1);
				player.getInventory().addItem(item);
			}
			break;
		case 6:
			item.setType(Material.WHITE_WOOL);
			item.setAmount(64);
			player.getInventory().addItem(item);
			if (player == io.github.cishero6.Main.Teams[6][0]) {
				item.setType(Material.WHITE_BED);
				item.setAmount(1);
				player.getInventory().addItem(item);
			}
			break;
		case 7:
			item.setType(Material.CYAN_WOOL);
			item.setAmount(64);
			player.getInventory().addItem(item);
			if (player == io.github.cishero6.Main.Teams[7][0]) {
				item.setType(Material.CYAN_BED);
				item.setAmount(1);
				player.getInventory().addItem(item);
			}
			break;
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player player = (Player) sender;
		if (label.equalsIgnoreCase("set")) {
			if (!player.hasPermission("bedHunt.gameManager")) {
				player.sendMessage(ChatColor.RED + "You don't have permission to this command!");
				return true;
			}
			if (io.github.cishero6.Main.gameCreated) {
				player.sendMessage(ChatColor.RED + "Game is already created!");
				return true;
			}
			if (args.length != 2) {
				player.sendMessage(ChatColor.RED + "Wrong usage! /set <players/teams> <integer-value> ");
				return true;
			}

			if ((!args[0].equalsIgnoreCase("players")) && (!args[0].equalsIgnoreCase("teams"))) {
				player.sendMessage(ChatColor.RED + "Wrong usage! /set <players/teams> <integer-value> ");
				return true;
			}
			try {	
				if ((args[0].equalsIgnoreCase("players"))
						&& ((Integer.parseInt(args[1]) > 4) || (Integer.parseInt(args[1]) <= 0))) {
					player.sendMessage(
							ChatColor.RED + "Maximum amount of players is 4! And of course it should be positive lol");
					return true;
				}
				if ((args[0].equalsIgnoreCase("teams"))
						&& ((Integer.parseInt(args[1]) > 8) || (Integer.parseInt(args[1]) <= 1))) {
					player.sendMessage(
							ChatColor.RED + "Maximum amount of teams is 8! And of course it should be positive lol");
					return true;
				}
				if (args[0].equalsIgnoreCase("players")) {
					io.github.cishero6.Main.maxPlayers = Integer.parseInt(args[1]);
					player.sendMessage(ChatColor.GREEN + "Done!");
				}
				if (args[0].equalsIgnoreCase("teams")) {
					io.github.cishero6.Main.maxTeams = Integer.parseInt(args[1]);
					player.sendMessage(ChatColor.GREEN + "Done!");
				}
				return true;
			} catch (Exception e) {
				player.sendMessage(ChatColor.RED + "Wrong usage! /set <players/teams> <integer-value> ");
				return true;
			}
		}
		if (label.equalsIgnoreCase("game")) {
			if (args.length != 1) {
				player.sendMessage(ChatColor.RED
						+ "Wrong Usage! Usage: /game <create/join/clear/start/leave/info/center/joinAll>");
				return true;
			}
			switch (args[0]) {
			case "create":
				if (!player.hasPermission("bedHunt.gameManager")) {
					player.sendMessage(ChatColor.RED + "You don't have permission to this command!");
					return true;
				}
				if (io.github.cishero6.Main.gameCreated) {
					player.sendMessage(ChatColor.RED + "Game is alerady created!");
					return true;
				}
				setUpGame(player);
				player.sendMessage(ChatColor.GREEN + "Game created!");
				return true;
			case "join":
				if (!player.hasPermission("bedHunt.gameManager")) {
					player.sendMessage(ChatColor.RED + "You don't have permission to this command!");
					return true;
				}
				if (io.github.cishero6.Main.gameStarted) {
					player.sendMessage(ChatColor.RED + "Game has already started!");
					return true;
				}
				if (!io.github.cishero6.Main.gameCreated) {
					player.sendMessage(ChatColor.RED + "Game is not created!");
					return true;
				}
				if (io.github.cishero6.Main.curPlayers >= io.github.cishero6.Main.maxTeams
						* io.github.cishero6.Main.maxPlayers) {
					player.sendMessage(ChatColor.RED + "The game is full!");
					return true;
				}
				if (io.github.cishero6.Main.isPlaying(player)) {
					player.sendMessage(ChatColor.RED + "You are already in the game!");
					return true;
				}
				putPlayerInGame(player);
				return true;
			case "joinAll":
				if (!player.hasPermission("bedHunt.gameManager")) {
					player.sendMessage(ChatColor.RED + "You don't have permission to this command!");
					return true;
				}
				if (!io.github.cishero6.Main.gameCreated) {
					player.sendMessage(ChatColor.RED + "Game is not created!");
					return true;
				}
				for (Player p: player.getWorld().getPlayers()) {
					putPlayerInGame(p);
				}
				return true;
			case "clear":
				if (!player.hasPermission("bedHunt.gameManager")) {
					player.sendMessage(ChatColor.RED + "You don't have permission to this command!");
					return true;
				}
				if (!canBeCancelled[0]) {
					player.sendMessage(ChatColor.RED + "You can't clear game now!");
					return true;
				}
				if (!io.github.cishero6.Main.gameCreated) {
					player.sendMessage(ChatColor.RED + "Game is not created!");
					return true;
				}
				deleteGame(player);
				return true;
			case "info":
				player.sendMessage("Max players = " + io.github.cishero6.Main.maxPlayers);
				player.sendMessage("Max teams = " + io.github.cishero6.Main.maxTeams);
				player.sendMessage("Players in game = " + io.github.cishero6.Main.curPlayers);
				return true;
			case "leave":
				if (!io.github.cishero6.Main.gameCreated) {
					player.sendMessage(ChatColor.RED + "Game is not created!");
					return true;
				}
				if (!io.github.cishero6.Main.isPlaying(player)) {
					player.sendMessage(ChatColor.RED + "You are not in the game!");
					return true;
				}
				if (io.github.cishero6.Main.curPlayers == 1) {
					deleteGame(player);
					return true;
				}
				player.setScoreboard(null);
				for (int i = 0; i < 8; i++) {
					for (int j = 0; j < 4; j++) {
						if (player == io.github.cishero6.Main.Teams[i][j]) {
							for (int p = j; p < io.github.cishero6.Main.maxPlayers - 1; p++) {
								io.github.cishero6.Main.Teams[i][p] = io.github.cishero6.Main.Teams[i][p + 1];
							}
							io.github.cishero6.Main.Teams[i][io.github.cishero6.Main.maxPlayers - 1] = null;
						}
					}
				}
				for (int i = 0; i < io.github.cishero6.Main.curPlayers; i++) {
					if (player == io.github.cishero6.Main.players[i]) {
						io.github.cishero6.Main.curPlayers--;
						for (int j = i; j < io.github.cishero6.Main.curPlayers - 1; j++) {
							io.github.cishero6.Main.players[j] = io.github.cishero6.Main.players[j + 1];
						}
						io.github.cishero6.Main.players[io.github.cishero6.Main.maxPlayers - 1] = null;
						player.sendMessage(ChatColor.YELLOW + "You've left the game!");
						player.getInventory().clear();
						return true;
					}
				}
				return true;
			case "start":
				if (!player.hasPermission("bedHunt.gameManager")) {
					player.sendMessage(ChatColor.RED + "You don't have permission to this command!");
					return true;
				}
				if (!io.github.cishero6.Main.gameCreated) {
					player.sendMessage(ChatColor.RED + "Game is not created!");
					return true;
				}
				if (!io.github.cishero6.Main.isPlaying(player)) {
					player.sendMessage(ChatColor.RED + "You are not in the game!");
					return true;
				}
				for (int i = 0; i < io.github.cishero6.Main.curPlayers; i++) {
					if (io.github.cishero6.Main.getTeamID(io.github.cishero6.Main.players[i]) == -1) {
						io.github.cishero6.Main.playerJoinRandomTeam(io.github.cishero6.Main.players[i]);
					}
					io.github.cishero6.Main.players[i].getInventory().remove(Material.ENCHANTING_TABLE);
					io.github.cishero6.Main.gameStarted = true;
					player.getWorld().setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
					for (int j = 0; j < io.github.cishero6.Main.maxTeams; j++) {
						io.github.cishero6.Main.bedExists[j] = false;
					}
					giveGameplayInventory(io.github.cishero6.Main.players[i]);
				}

				canBeCancelled[0] = false;
				new BukkitRunnable() {
					ItemStack[][] armorHold = new ItemStack[io.github.cishero6.Main.curPlayers][4];
					ItemStack[] nullarmor = { null, null, null, null };

					@Override
					public void run() {
						if (countdownBefore[0] > 1) {
							io.github.cishero6.Main.beforeGameCountdown = true;
							countdownBefore[0]--;
							for (int i = 0; i < io.github.cishero6.Main.curPlayers; i++) {
								io.github.cishero6.Main.players[i].sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD
										+ "BedHunt Game starts in " + countdownBefore[0] + " seconds!");
								io.github.cishero6.Main.players[i].playSound(
										io.github.cishero6.Main.players[i].getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL,
										10, 1);
							}
							return;
						}
						if (START[0]) {
							io.github.cishero6.Main.beforeGameCountdown = false;
							START[0] = false;
							for (int i = 0; i < io.github.cishero6.Main.curPlayers; i++) {
								io.github.cishero6.Main.players[i].sendTitle(
										ChatColor.GOLD + "" + ChatColor.BOLD + "Hiding time!",
										ChatColor.GOLD + "" + ChatColor.BOLD + "You've got " + ChatColor.RED
												+ countdownToHide[0] + ChatColor.GOLD + " seconds to hide your bed!",
										10, 80, 20);
								io.github.cishero6.Main.players[i].addPotionEffect(
										new PotionEffect(PotionEffectType.INVISIBILITY, countdownToHide[0] * 20, 0));
								io.github.cishero6.Main.players[i].addPotionEffect(
										new PotionEffect(PotionEffectType.SPEED, countdownToHide[0] * 20, 1));

								armorHold[i] = io.github.cishero6.Main.players[i].getInventory().getArmorContents();
								io.github.cishero6.Main.players[i].getInventory().setArmorContents(nullarmor);
							}
							return;
						}
						if (countdownToHide[0] > 0) {
							countdownToHide[0]--;
							if (countdownToHide[0] < 11) {
								for (int i = 0; i < io.github.cishero6.Main.curPlayers; i++) {
									io.github.cishero6.Main.players[i].sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD
											+ "Fight starts in " + countdownToHide[0] + " seconds!");
									io.github.cishero6.Main.players[i].playSound(
											io.github.cishero6.Main.players[i].getLocation(),
											Sound.BLOCK_NOTE_BLOCK_BELL, 10, 1);
								}
							}
							return;
						}
						if (io.github.cishero6.Main.players[0] == null)
							this.cancel();
						io.github.cishero6.Main.players[0].getWorld().setPVP(true);
						for (int i = 0; i < io.github.cishero6.Main.curPlayers; i++) {
							io.github.cishero6.Main.players[i].getInventory().setArmorContents(armorHold[i]);
						}
						io.github.cishero6.BedHuntEvents.updateScoreboard();
						canBeCancelled[0] = true;
						this.cancel();
					}

				}.runTaskTimer(m_plugin, 0, 20);
				for (int i = 0; i < io.github.cishero6.Main.curPlayers; i++) {
					io.github.cishero6.Main.players[i].teleport(io.github.cishero6.Main.center);
				}
				io.github.cishero6.BedHuntEvents.updateScoreboard();
				return true;
			case "center":
				if (!player.hasPermission("bedHunt.gameManager")) {
					player.sendMessage(ChatColor.RED + "You don't have permission to this command!");
					return true;
				}
				if (!io.github.cishero6.Main.isPlaying(player)) {
					player.sendMessage(ChatColor.RED + "You have to be in the game!");
					return true;
				}
					io.github.cishero6.Main.center = player.getLocation();
					player.sendMessage(ChatColor.GREEN + "Center set!");
					return true;

			default:
				player.sendMessage(ChatColor.RED
						+ "Wrong Usage! Usage: /game <create/join/clear/start/leave/info/center/joinAll>");
				return true;
			}
		}
		return false;
	}
}
