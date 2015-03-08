package v3lop5.mojangapi;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

public class MessageListener {

	private File f;
	private YamlConfiguration cfg;
	private HashMap<String, String> messages;

	public MessageListener() {
		f = new File("plugins/MojangAPI", "messages.yml");
		if (!f.exists()) {
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		this.messages = new LinkedHashMap<>();
		this.cfg = YamlConfiguration.loadConfiguration(f);
		copyDefaults();
		loadMessages();

	}

	private void loadMessages() {
		String reset = ChatColor.translateAlternateColorCodes('&',
				cfg.getString("Allgemein.Color"));
		for (String s : cfg.getKeys(true)) {
			String temp = ChatColor.translateAlternateColorCodes('&',
					cfg.getString(s));
			try {
				temp = temp.replaceAll("&z", reset);
			} catch (Exception e) {

			}

			this.messages.put(s.toLowerCase(), temp);
		}
	}

	public void reload() {
		f = new File("plugins/MojangAPI", "messages.yml");
		if (!f.exists()) {
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		this.messages = new LinkedHashMap<>();
		this.cfg = YamlConfiguration.loadConfiguration(f);
		copyDefaults();
		loadMessages();
	}

	private void copyDefaults() {
		cfg.addDefault("Allgemein.Prefix", "&8[&6Mj&eAPI&8] &z");
		cfg.addDefault("Allgemein.Color", "&7");
		cfg.addDefault("api.info.forname", "Informationen für &a%NAME&z:");
		cfg.addDefault("api.info.foruuid", "Informationen für &c%UUID&z:");
		cfg.addDefault("api.uuid.text", "&cUUiD&z: &e%UUID");
		cfg.addDefault("api.uuid.hover", "&7Klicke für UUiD Informationen");
		cfg.addDefault("api.noMatching",
				"&cEs wurde keine Übereinstimmung für &4&l%WHAT &z&cgefunden!");
		cfg.addDefault("api.legacy.text", "&9Legacy&z: %STATUS");
		cfg.addDefault("api.legacy.true", "&a&lJa");
		cfg.addDefault("api.legacy.false", "&c&lNein");
		cfg.addDefault("api.demo.text", "&bDemo&z: %STATUS");
		cfg.addDefault("api.demo.true", "&a&lJa");
		cfg.addDefault("api.demo.false", "&c&lNein");
		cfg.addDefault("api.error",
				"&cEs ist ein &lFehler &z&caufgetreten!\n&4FEHLERCODE: &l%FOR");
		cfg.addDefault("api.names.withOutTime", "&6%COUNT &zName: &a%NAME");
		cfg.addDefault("api.names.withTime",
				"&6%COUNT &zName: &a%NAME &zGeändert: &e%TIME");
		cfg.addDefault("api.names.hover",
				"&zKlicke um die Zeit umgerechnet zu bekommen");
		cfg.addDefault("api.info.namesforUUid", "Informationen für &c%UUID");
		cfg.addDefault("time.noLong",
				"&cDies ist keine mögliche Eingabe: &4&l%CAUSE");
		cfg.addDefault("time.time", "Die angefragte Zeit: &d%TIME");
		cfg.addDefault(
				"command.noPermission",
				"&cDu hast dazu keine Berechtigung!\n&zDir fehlt folgende Permission: &4&l&PERM");
		cfg.addDefault("api.cracked",
				"Der Name &a%NAME &zist nicht registriert!");

		cfg.addDefault("api.name.text", "&aName&z: &e%NAME");
		cfg.addDefault("api.name.hover", "&7Klicke für alle Usernames");
		cfg.addDefault("api.time.text", "&7Time: &e%TIME");
		cfg.addDefault("api.time.hover",
				"&zKlicke um die Zeit umgerechet zu bekommen!");
		cfg.addDefault("api.ispublic.text", "&9Public: &a%STATUS");
		cfg.addDefault("api.skin.text", "&7SKIN&z: &e%URL");
		cfg.addDefault("api.skin.hover", "&7Spawne den Spieler");
		cfg.addDefault("api.cape.text", "&7CAPE&z: &e%URL");
		cfg.addDefault("api.cape.hover", "&7Spawne den Spieler");

		cfg.addDefault("Help.Header", "Mögliche Commands von &9&lMojangAPI&z:");
		cfg.addDefault("Help.Command", "&c%CMD &8- &z%DES");

		cfg.addDefault("npc.spawned",
				"Ein NPC vom Spieler &a&l%NAME &zwurde gespawnt!");
		cfg.addDefault("npc.despawned",
				"Der NPC vom Spieler &a&l%NAME &zwurde despawnt!");
		cfg.options().copyDefaults(true);
		save();
	}

	private void save() {
		try {
			cfg.save(f);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getMSG(String what) {
		if (!this.messages.containsKey(what.toLowerCase())) {
			return what;
		}
		return this.messages.get(what.toLowerCase());
	}

	public String getPrefix() {
		return this.messages.get("allgemein.prefix");
	}

	public int messageAmount() {
		return this.messages.size();
	}

	public String getMessage(String what) {
		return getPrefix() + getMSG(what);
	}

	public void sendAllMessages(CommandSender cs) {
		for (String s : this.messages.values()) {
			cs.sendMessage(s);
		}
	}
}
