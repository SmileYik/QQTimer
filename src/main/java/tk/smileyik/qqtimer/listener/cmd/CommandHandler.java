package tk.smileyik.qqtimer.listener.cmd;

public interface CommandHandler<T> {
  void handle(T it);

  String startCommand();
}
