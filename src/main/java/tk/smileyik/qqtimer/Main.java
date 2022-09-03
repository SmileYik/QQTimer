package tk.smileyik.qqtimer;

import net.mamoe.mirai.Bot;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import tk.smileyik.qqtimer.config.BotConfig;
import tk.smileyik.qqtimer.listener.FriendMessageHandler;
import tk.smileyik.qqtimer.listener.cmd.AddClassTask;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * @author SmileYik
 * @Description TODO
 * @date 2022年09月02日 11:03
 */
public class Main {

  public static void main(String[] args) throws NoSuchAlgorithmException, IOException {
    String config = "config.json";
    if (args.length == 1) {
      config = args[0];
    }

    BotConfig botConfig = BotConfig.load(Files.newBufferedReader(Paths.get(config)));
    Bot bot = botConfig.newBot();
    bot.login();
    AddClassTask addClassTask = new AddClassTask();
    FriendMessageHandler handler = new FriendMessageHandler();
    handler.addHandler(addClassTask);
    GlobalEventChannel.INSTANCE.parentScope(bot).subscribeAlways(FriendMessageEvent.class, handler::handle);
    ScheduleTimer.start(bot, botConfig);
  }
}
