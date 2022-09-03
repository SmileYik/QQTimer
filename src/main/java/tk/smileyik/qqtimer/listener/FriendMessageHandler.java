package tk.smileyik.qqtimer.listener;

import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.message.data.MessageChain;
import tk.smileyik.qqtimer.listener.cmd.CommandHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * @author SmileYik
 * @Description TODO
 * @date 2022年09月02日 16:55
 */
public class FriendMessageHandler {

  private final List<CommandHandler<FriendMessageEvent>> handlers;

  public FriendMessageHandler() {
    handlers = new ArrayList<>();
  }

  public void addHandler(CommandHandler<FriendMessageEvent> handler) {
    handlers.add(handler);
  }

  public void handle(FriendMessageEvent event) {
    MessageChain message = event.getMessage();
    String content = message.contentToString();
    for (CommandHandler<FriendMessageEvent> handler : handlers) {
      if (content.startsWith(handler.startCommand())) {
        handler.handle(event);
        break;
      }
    }
  }
}
