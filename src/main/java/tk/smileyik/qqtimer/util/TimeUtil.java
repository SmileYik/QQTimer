package tk.smileyik.qqtimer.util;

import java.util.Calendar;

/**
 * @author SmileYik
 * @Description TODO
 * @date 2022年09月02日 16:29
 */
public class TimeUtil {
  public static long getNextDay() {
    return getNextDay(System.currentTimeMillis());
  }

  public static long getNextDay(long now) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(now);
    calendar.roll(Calendar.DATE, true);
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 2);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);
    return calendar.getTimeInMillis();
  }
}
