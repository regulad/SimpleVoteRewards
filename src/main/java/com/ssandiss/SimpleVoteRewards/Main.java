package com.ssandiss.SimpleVoteRewards;

import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;
import java.util.logging.Logger;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {
   private static final Logger log = Logger.getLogger("Minecraft");
   public static Economy econ = null;
   public static Permission perms = null;
   public static Chat chat = null;

   @EventHandler
   public void onPlayerVote(VotifierEvent event) {
      Vote v = event.getVote();
      String player = v.getUsername();
      Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&a" + player + "&2 just voted on &6" + v.getServiceName()));
      Player p = (Player)Bukkit.getOfflinePlayer(player);

      for(int i = 0; i < 36; ++i) {
         if (this.getConfig().getItemStack("Item " + i) != null) {
            ItemStack loading = this.getConfig().getItemStack("Item " + i);
            p.getInventory().addItem(new ItemStack[]{loading});
         }
      }

      econ.bankDeposit(player, (double)this.getConfig().getInt("Money amount "));
   }

   public void onEnable() {
      if (!this.setupEconomy()) {
         log.severe(String.format("[%s] - no Vault found!", this.getDescription().getName()));
      } else {
         this.getServer().getPluginManager().registerEvents(this, this);
         this.setupPermissions();
         this.setupChat();
      }
   }

   private boolean setupEconomy() {
      if (this.getServer().getPluginManager().getPlugin("Vault") == null) {
         return false;
      } else {
         RegisteredServiceProvider rsp = this.getServer().getServicesManager().getRegistration(Economy.class);
         if (rsp == null) {
            return false;
         } else {
            econ = (Economy)rsp.getProvider();
            return econ != null;
         }
      }
   }

   private boolean setupChat() {
      RegisteredServiceProvider rsp = this.getServer().getServicesManager().getRegistration(Chat.class);
      chat = (Chat)rsp.getProvider();
      return chat != null;
   }

   private boolean setupPermissions() {
      RegisteredServiceProvider rsp = this.getServer().getServicesManager().getRegistration(Permission.class);
      perms = (Permission)rsp.getProvider();
      return perms != null;
   }

   public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
      Player p = (Player)sender;
      if (label.equalsIgnoreCase("SetVoteReward") || label.equalsIgnoreCase("svr") || label.equalsIgnoreCase("SetVoteRewards")) {
         if (p.hasPermission("SVR.setreward")) {
            if (args.length == 0) {
               for(int i = 0; i < p.getInventory().getSize(); ++i) {
                  ItemStack item = p.getInventory().getItem(i);
                  this.getConfig().set("Item " + i, item);
                  this.saveConfig();
               }

               this.getConfig().set("Money amount: ", econ.getBalance(p));
               this.saveConfig();
               p.getInventory().clear();
               p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aYou successfully saved your inventory as a vote reward"));
               p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aAll of those items will be given to the player when they vote."));
               p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aThe amount of money you had on the current time will also be given."));
            } else {
               p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cWrong syntax&7."));
               p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cCorrect usage: /SetVoteReward or /SVR "));
            }
         } else {
            p.sendMessage(ChatColor.RED + "You don't have permissions.");
         }
      }

      return false;
   }

   @EventHandler
   public void onPlayerJoin(PlayerJoinEvent event) {
      event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&6SimpleVoteRewards &amade by &bhttp://Www.Plugins4You.com"));
   }
}
