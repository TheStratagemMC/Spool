package code.akselm.spool;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.CommandSenderCreateNPCEvent;
import net.citizensnpcs.api.event.PlayerCreateNPCEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.nms.v1_10_R1.entity.EntityHumanNPC;
import net.citizensnpcs.nms.v1_10_R1.network.EmptyChannel;
import net.citizensnpcs.nms.v1_10_R1.network.EmptyNetworkManager;
import net.minecraft.server.v1_10_R1.EntityPlayer;
import net.minecraft.server.v1_10_R1.EnumProtocolDirection;
import net.minecraft.server.v1_10_R1.NetworkManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_10_R1.CraftServer;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcmonkey.sentinel.SentinelTarget;
import org.mcmonkey.sentinel.SentinelTrait;

import java.util.HashSet;
import java.util.concurrent.ThreadFactory;

/**
 * Created by axel on 8/2/2016.
 */
public class Spool extends JavaPlugin {
    HashSet<Integer> npcCache = new HashSet<>();
    FakeGroup group = new FakeGroup(100, new ThreadFactory(){

        @Override
        public Thread newThread(Runnable r) {
            return new FakeThread();
        }

    });

    public class FakeThread extends Thread{
        public void run(){
            //
        }
    }

    public class FakeGroup extends ThreadPerChannelEventLoopGroup{
        public FakeGroup(int maxThreads, ThreadFactory fac){
            super(maxThreads, fac);
        }

        public ThreadPerChannelEventLoop newChild(){
            try {
                return super.newChild(new Object[]{});
            }catch(Exception e){
                e.printStackTrace();
                return new ThreadPerChannelEventLoop(this);
            }
        }
        public ChannelFuture register(Channel var1, ChannelPromise var2) {
            if(var1 == null) {
                Bukkit.broadcastMessage("Balk1");
                throw new NullPointerException("channel");
            } else if(var2 == null) {
                Bukkit.broadcastMessage("Balk2");
                throw new NullPointerException("promise");
            } else {
                var1.unsafe().register(newChild(), var2);
                return var2;
            }
        }
    }
    public void onEnable(){
        getCommand("spoolspawn").setExecutor(new CommandExecutor() {
            @Override
            public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
                if (!sender.hasPermission("spool.admin")) return false;
                if (!(sender instanceof Player)) return false;

                Player p = (Player)sender;
                if (args.length < 1){
                    p.sendMessage("Please specify a name.");
                    return true;
                }
                String name = args[0];
                NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, name);

                Cancellable event = new PlayerCreateNPCEvent((Player)sender, npc);
                Bukkit.getPluginManager().callEvent((Event)event);
                if (!event.isCancelled()){
                    npc.spawn(p.getLocation());
                    SentinelTrait trait = new SentinelTrait();
                    trait.chaseRange = 850;
                    trait.attackRate = 5;
                    trait.targets.add(SentinelTarget.PLAYERS);
                    trait.targets.add(SentinelTarget.MONSTERS);
                    trait.targets.add(SentinelTarget.NPCS);

                    //trait.linkToNPC(npc);
                    npc.addTrait(trait);

                    ((HumanEntity)npc.getEntity()).getInventory().setItemInMainHand(new ItemStack(Material.DIAMOND_SWORD));
                    p.sendMessage("Spawned Sentinel NPC");

                    Entity e = npc.getEntity();
                    EntityPlayer ep = ((EntityPlayer)((CraftEntity)e).getHandle());

                    try{
                        NetworkManager m = new FakeNetworkManager(EnumProtocolDirection.CLIENTBOUND);
                        //Bukkit.broadcastMessage((m.channel == null) + "");

                        Channel c = new FakeChannel(m.channel, group.newChild());
                        ChannelPromise promise = new DefaultChannelPromise(c);
                       // Bukkit.broadcastMessage((c==null) +","+ (promise==null));
                        group.register(c, promise);
                        m.channel = c;

                        ((CraftServer)Bukkit.getServer()).getHandle().a(m, ep);
                    }catch(Exception ex){
                        ex.printStackTrace();
                        p.sendMessage("Failed join");
                    }
                    npcCache.add(npc.getId());
                    //Bukkit.broadcastMessage(ChatColor.YELLOW + npc.getName() + " has joined the game");
                    //PlayerJoinEvent eve = new PlayerJoinEvent(ep.getBukkitEntity(), ChatColor.YELLOW + npc.getName() + " has joined the game");
                    //Bukkit.getPluginManager().callEvent(eve);
                }
                else {
                    p.sendMessage("Failed");
                    return true;
                }
                return true;
            }
        });
    }

    public void onDisable(){
        for (int i : npcCache){
            CitizensAPI.getNPCRegistry().getById(i).destroy();
        }
    }
}
