package tk.smileyik.qqtimer.entity;

import org.jetbrains.annotations.NotNull;

/**
 * @author SmileYik
 * @Description TODO
 * @date 2022年09月02日 15:02
 */
public class Task implements Comparable<Task> {
  private long timestamp;
  private String taskId;
  private long creatorQq;
  private long targetQq;
  private String msg;

  public Task() {

  }

  public Task(long timestamp, String taskId, long creatorQq, long targetQq, String msg) {
    this.timestamp = timestamp;
    this.taskId = taskId;
    this.creatorQq = creatorQq;
    this.targetQq = targetQq;
    this.msg = msg;
  }

  public long getCreatorQq() {
    return creatorQq;
  }

  public void setCreatorQq(long creatorQq) {
    this.creatorQq = creatorQq;
  }

  public long getTargetQq() {
    return targetQq;
  }

  public void setTargetQq(long targetQq) {
    this.targetQq = targetQq;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }

  public String getMsg() {
    return msg;
  }

  public void setMsg(String msg) {
    this.msg = msg;
  }

  public String getTaskId() {
    return taskId;
  }

  public void setTaskId(String taskId) {
    this.taskId = taskId;
  }

  @Override
  public int compareTo(@NotNull Task o) {
    return Long.compare(timestamp, o.timestamp);
  }

  @Override
  public String toString() {
    return "Task{" +
        "timestamp=" + timestamp +
        ", taskId='" + taskId + '\'' +
        ", creatorQq=" + creatorQq +
        ", targetQq=" + targetQq +
        ", msg='" + msg + '\'' +
        '}';
  }
}
