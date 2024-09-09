package me.cyphers.fruitservers.tokenperms.Command.Factory;


import me.cyphers.fruitservers.tokenperms.Plugin;
import org.bukkit.command.CommandSender;

public abstract class TargettedCommandExecutor extends CommandExecutor {
    public TargettedCommandExecutor(Plugin plugin) {
        super(plugin);
    }

    public abstract void executeSelfTarget(CommandSender paramCommandSender, String[] paramArrayOfString);

    public abstract void executeOtherTarget(CommandSender paramCommandSender, String[] paramArrayOfString);

    public void execute(CommandSender sender, String[] args) {
        if (sender.hasPermission(getOtherTargetPermission())) {
            executeOtherTarget(sender, args);
        } else {
            executeSelfTarget(sender, args);
        }
    }

    public abstract String getOtherTargetPermission();

    public abstract String getSelfTargetPermission();

    public String getPermission() {
        return getSelfTargetPermission();
    }

    public boolean hasPermission(CommandSender sender) {
        return (sender.hasPermission(getSelfTargetPermission()) || sender.hasPermission(getOtherTargetPermission()));
    }
}
