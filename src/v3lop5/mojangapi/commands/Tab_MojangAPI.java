package v3lop5.mojangapi.commands;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class Tab_MojangAPI implements TabCompleter {

	List<String> args1;

	public Tab_MojangAPI() {
		this.args1 = new LinkedList<>();
		args1.add("help");
		args1.add("info");
		args1.add("names");
		args1.add("name");
		args1.add("time");
		args1.add("spawn");
	}

	@Override
	public List<String> onTabComplete(CommandSender cs, Command arg1,
			String arg2, String[] args) {

		if (args.length == 1) {
			LinkedList<String> temp = new LinkedList<>();

			for (String s : args1) {
				if (s.startsWith(args[0]))
					temp.add(s);
			}

			return temp;
		}
		return null;
	}
}
