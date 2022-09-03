package tk.smileyik.qqtimer;

import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import tk.smileyik.qqtimer.config.BotConfig;
import tk.smileyik.qqtimer.config.CreatorScheduleConfig;
import tk.smileyik.qqtimer.entity.Task;
import tk.smileyik.qqtimer.util.ImageUtil;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author SmileYik
 * @Description TODO
 * @date 2022年09月02日 16:26
 */
public class ScheduleTimer implements Runnable {
  private static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();
  private static ScheduleTimer scheduleTimer;
  private static ScheduledFuture<?> scheduledFuture;

  private final BotConfig botConfig;
  private final Bot bot;
  private final Map<Long, CreatorScheduleConfig> creatorScheduleConfigMap;
  private final PriorityQueue<Task> priorityQueue;

  private ScheduleTimer(Bot bot, BotConfig botConfig) {
    this.bot = bot;
    this.botConfig = botConfig;
    creatorScheduleConfigMap = new HashMap<>();
    priorityQueue = new PriorityQueue<>(Task::compareTo);
    File[] files = botConfig.getPath(BotConfig.SCHEDULE).toFile().listFiles();
    assert files != null;
    for (File file : files) {
      if (file.isFile()) {
        CreatorScheduleConfig creatorScheduleConfig = new CreatorScheduleConfig(file);
        creatorScheduleConfigMap.put(
            Long.parseLong(file.getName().split("\\.")[0]),
            creatorScheduleConfig
        );
        try {
          priorityQueue.addAll(creatorScheduleConfig.getTodayTasks());
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }


  public static void start(Bot bot, BotConfig botConfig) {
    if (scheduledFuture != null) {
      scheduledFuture.cancel(true);
    }
    scheduleTimer = new ScheduleTimer(bot, botConfig);
    scheduledFuture = SCHEDULED_EXECUTOR_SERVICE.scheduleWithFixedDelay(
        scheduleTimer,
        1L, 1L, TimeUnit.MINUTES
    );
  }

  public static void addTask(Task task) {
    if (!scheduleTimer.creatorScheduleConfigMap.containsKey(task.getCreatorQq())) {
      scheduleTimer.creatorScheduleConfigMap.put(task.getCreatorQq(), new CreatorScheduleConfig(new File(
          scheduleTimer.botConfig.getPath(BotConfig.SCHEDULE).toFile(), task.getCreatorQq() + ".json"
      )));
    }
    try {
      scheduleTimer.creatorScheduleConfigMap.get(task.getCreatorQq()).addTask(task);
    } catch (IOException e) {
      e.printStackTrace();
    }
    scheduleTimer.refreshTodayTask();
  }

  public static void addTasks(List<Task> tasks) {
    if (tasks.size() == 0) {
      return;
    }
    Task task = tasks.get(0);
    if (!scheduleTimer.creatorScheduleConfigMap.containsKey(task.getCreatorQq())) {
      scheduleTimer.creatorScheduleConfigMap.put(task.getCreatorQq(), new CreatorScheduleConfig(new File(
          scheduleTimer.botConfig.getPath(BotConfig.SCHEDULE).toFile(), task.getCreatorQq() + ".json"
      )));
    }
    try {
      scheduleTimer.creatorScheduleConfigMap.get(task.getCreatorQq()).addTasks(tasks);
    } catch (IOException e) {
      e.printStackTrace();
    }
    scheduleTimer.refreshTodayTask();
  }


  @Override
  public void run() {
    long timestamp = System.currentTimeMillis();
    while (!priorityQueue.isEmpty() && priorityQueue.peek().getTimestamp() < timestamp) {
      doTask(priorityQueue.poll());
    }
    if (isNextDay(timestamp)) {
      refreshTodayTask();
    }
  }

  /**
   * 执行任务.
   * @param task
   */
  private void doTask(Task task) {
    try {
      File file = ImageUtil.productImage(botConfig, task.getMsg());
      bot.getFriend(task.getTargetQq()).sendMessage(
          new MessageChainBuilder()
              .append(Contact.uploadImage(bot.getAsFriend(), file))
              .asMessageChain()
      );
      creatorScheduleConfigMap.get(task.getCreatorQq()).removeTaskByTimestamp(task.getTimestamp());
    } catch (IOException e) {
      throw new RuntimeException(e);
    } catch (FontFormatException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * 是否已经过了一天
   * @param timestamp
   * @return
   */
  private boolean isNextDay(long timestamp) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(timestamp);
    int today = calendar.get(Calendar.DAY_OF_YEAR);
    calendar.add(Calendar.MINUTE, -1);
    int yesterday = calendar.get(Calendar.DAY_OF_YEAR);
    return today != yesterday;
  }

  /**
   * 更新今天的任务表.
   */
  private void refreshTodayTask() {
    List<Task> tasks = new ArrayList<>();
    creatorScheduleConfigMap.values().forEach(it -> {
      try {
        tasks.addAll(it.getTodayTasks());
      } catch (IOException e) {
        e.printStackTrace();
      }
    });
    priorityQueue.clear();
    priorityQueue.addAll(tasks);
  }
}
