package v3lop5.mojangapi.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.server.v1_7_R4.ChatSerializer;
import net.minecraft.server.v1_7_R4.PacketPlayOutChat;

import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class JsonBuilder {
	 
    //JsonBuilder by FisheyLP
 
    public enum ClickAction {
        RUN_COMMAND, SUGGEST_COMMAND, OPEN_URL
    }
    public enum HoverAction {
        SHOW_TEXT
    }
 
    private List<String> extras = new ArrayList<String>();
    private final static List<ChatColor> other = Arrays.asList(ChatColor.ITALIC, ChatColor.MAGIC,
            ChatColor.BOLD, ChatColor.UNDERLINE, ChatColor.STRIKETHROUGH);
 
    public JsonBuilder parse(String text) {
        String regex = "[&§]{1}([a-fA-Fl-oL-O0-9]){1}";
        text = text.replaceAll(regex, "§$1");
        String[] words = text.split(regex);
 
        int index = words[0].length();
        for(String word : words) {
            try {
                if(index != words[0].length())
            withText(word).withColor("§"+text.charAt(index - 1));
            } catch(Exception e){}
            index += word.length() + 2;
        }
        return this;
    }
    public JsonBuilder withText(String text) {
        extras.add("{text:\"" + text + "\"}");
        return this;
    }
 
    public JsonBuilder withColor(ChatColor color) {
        addSegment(other.contains(color) ? color.name().toLowerCase()+":true" : "color:"+color.name().toLowerCase());
        return this;
    }
 
    public JsonBuilder withColor(String color) {
        while(color.length() != 1) color = color.substring(1);
        withColor(ChatColor.getByChar(color));
        return this;
    }
 
    public JsonBuilder withClickEvent(ClickAction action, String value) {
        addSegment("clickEvent:{action:" + action.toString().toLowerCase()
                + ",value:\"" + value + "\"}");
        return this;
    }
 
    public JsonBuilder withHoverEvent(HoverAction action, String value) {
        addSegment("hoverEvent:{action:" + action.toString().toLowerCase()
                + ",value:\"" + value + "\"}");
        return this;
    }
 
    private void addSegment(String segment) {
        String lastText = extras.get(extras.size() - 1);
        lastText = lastText.substring(0, lastText.length() - 1)
                + ","+segment+"}";
        extras.remove(extras.size() - 1);
        extras.add(lastText);
    }
 
    public String toString() {
        String json = "{text:\"\",extra:[";
        for (String extra : extras)
            json = json + extra + ",";
        json = json.substring(0, json.length() - 1) + "]}";
        return extras.size() == 0 ? "" : json;
    }
 
    public void sendJson(Player p) {
    ((CraftPlayer) p).getHandle().playerConnection.sendPacket(
    new PacketPlayOutChat(ChatSerializer.a(toString()), true));
    }
}
