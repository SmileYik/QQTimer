package tk.smileyik.qqtimer.listener.cmd;

import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.message.data.MessageChain;
import tk.smileyik.qqtimer.ScheduleTimer;
import tk.smileyik.qqtimer.entity.Task;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * @author SmileYik
 * @Description TODO
 * @date 2022年09月02日 17:03
 */
public class AddClassTask implements CommandHandler<FriendMessageEvent> {
  /*
  添加课程
  当前周次
  课程起始周
  课程结束周
  周次：   [单周|双周|全周] = [1|2|0]
  星期几：[1 - 7]
  几点： 24小时制
  给谁
  $$
  消息
   */


  @Override
  public void handle(FriendMessageEvent it) {
    MessageChain message = it.getMessage();
    String rawMessage = message.contentToString();
    String[] split = rawMessage.split("\\$\\$");
    String[] command = split[0].split("\n");

    Calendar calendar = Calendar.getInstance();
    long now = System.currentTimeMillis();
    calendar.setTimeInMillis(now);
    int nowWeek = Integer.parseInt(command[1]);
    int startWeek = Integer.parseInt(command[2]);
    int endWeek = Integer.parseInt(command[3]);
    int weekType = Integer.parseInt(command[4]);
    int week = Integer.parseInt(command[5]);
    int hour = Integer.parseInt(command[6].split(":")[0]);
    int min = Integer.parseInt(command[6].split(":")[1]);
    long target = Long.parseLong(command[7]);
    calendar.set(Calendar.DAY_OF_WEEK, (week + 1) % 7);
    calendar.set(Calendar.HOUR_OF_DAY, hour);
    calendar.set(Calendar.MINUTE, min);
    calendar.set(Calendar.SECOND, 0);
    List<Task> tasks = new ArrayList<>();
    for (int i = nowWeek; i <= endWeek; ++i) {
      if (calendar.getTimeInMillis() > now) {
        boolean flag = false;
        if (weekType == 0) {
          flag = true;
        } else if (weekType == 1 && (i & 0x1) == 1) {
          flag = true;
        } else if ((i & 0x1) == 0) {
          flag = true;
        }

        if (flag) {
          Task task = new Task();
          task.setTimestamp(calendar.getTimeInMillis());
          task.setMsg(split[1]);
          task.setCreatorQq(it.getSender().getId());
          task.setTargetQq(target);
          task.setTaskId((int) (Math.random() * 100000) + "");
          tasks.add(task);
        }
      }
      calendar.roll(Calendar.WEEK_OF_YEAR, true);
    }
    ScheduleTimer.addTasks(tasks);
    it.getSender().sendMessage("添加課程成功！");
  }

  @Override
  public String startCommand() {
    return "添加课程";
  }
}
