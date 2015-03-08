package v3lop5.mojangapi;

import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import v3lop5.mojangapi.api.MojangAPI;
import v3lop5.mojangapi.commands.CMD_MojangAPI;
import v3lop5.mojangapi.commands.Tab_MojangAPI;

public class Main extends JavaPlugin {

	private MessageListener ml;
	private MojangAPI api;
	private ConsoleCommandSender cs;

	@Override
	public void onEnable() {
		super.onEnable();
		this.cs = getServer().getConsoleSender();
		cs.sendMessage("§9==================================");
		saveDefaultConfig();
		this.ml = new MessageListener();
		cs.sendMessage("§fConfig §7und §fNachrichten §a§lgeladen§7!");
		this.api = new MojangAPI(this);
		cs.sendMessage(ml.getPrefix() + "§a§lgeladen§7!");
		CMD_MojangAPI cmdapi = new CMD_MojangAPI(getMojangAPI());
		Tab_MojangAPI tabapi = new Tab_MojangAPI();
		getCommand("mojangapi").setExecutor(cmdapi);
		getCommand("api").setExecutor(cmdapi);
		getCommand("mojangapi").setTabCompleter(tabapi);
		getCommand("api").setTabCompleter(tabapi);
		cs.sendMessage("§fCommands §a§lregistriert§7!");
		cs.sendMessage("§9==================================");

	}

	@Override
	public void onDisable() {
		super.onDisable();
		cs.sendMessage("§9==================================");
		cs.sendMessage(ml.getPrefix() + "wurde §cdisabled§7!");

		cs.sendMessage("§9==================================");

	}

	public MessageListener getMessageListener() {
		return this.ml;
	}

	public MojangAPI getMojangAPI() {
		return this.api;
	}
}
