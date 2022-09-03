package tk.smileyik.qqtimer.config;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import tk.smileyik.qqtimer.entity.Task;
import tk.smileyik.qqtimer.util.TimeUtil;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.*;

/**
 * @author SmileYik
 * @Description TODO
 * @date 2022年09月02日 15:20
 */
public class CreatorScheduleConfig {
  private final File file;

  public CreatorScheduleConfig(File file) {
    this.file = file;
    if (!file.exists()) {
      try {
        writeJsonToFile("[]");
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

  }

  public List<Task> getTodayTasks() throws IOException {
    long timestamp = TimeUtil.getNextDay();
    List<Task> tasks = new ArrayList<>();
    synchronized (file) {
      Gson gson = new Gson();
      Task[] allTasks = gson.fromJson(Files.newBufferedReader(file.toPath()), Task[].class);
      for (Task task : allTasks) {
        if (task.getTimestamp() <= timestamp) {
          tasks.add(task);
        } else {
          break;
        }
      }
    }
    return tasks;
  }

  public void addTask(Task task) throws IOException {
    synchronized (file) {
      Gson gson = new Gson();
      List<Task> tasks = new ArrayList<>(Arrays.asList(gson.fromJson(Files.newBufferedReader(file.toPath()), Task[].class)));
      int i = Collections.binarySearch(tasks, task);
      if (i < 0) {
        i = -(i + 1);
        tasks.add(i, task);
      }
      writeJsonToFile(gson.toJson(tasks));
    }
  }

  public void addTasks(List<Task> tasks) throws IOException {
    synchronized (file) {
      Gson gson = new Gson();
      List<Task> allTask = new ArrayList<>(Arrays.asList(gson.fromJson(Files.newBufferedReader(file.toPath()), Task[].class)));
      allTask.addAll(tasks);
      allTask.sort(Comparator.naturalOrder());
      writeJsonToFile(gson.toJson(allTask));
    }
  }

  public void removeTaskById(String taskId) throws IOException {
    synchronized (file) {
      Gson gson = new Gson();
      JsonArray array = (JsonArray) JsonParser.parseReader(Files.newBufferedReader(file.toPath()));
      for (int i = 0; i < array.size(); ++i) {
        array.get(i);
        Task task = gson.fromJson(array.get(i).toString(), Task.class);
        if (task.getTaskId().equals(taskId)) {
          array.remove(i);
          --i;
        }
      }
      writeJsonToFile(array.toString());
    }
  }

  public void removeTaskByTimestamp(long timestamp) throws IOException {
    synchronized (file) {
      Gson gson = new Gson();
      List<Task> tasks = new ArrayList<>(Arrays.asList(gson.fromJson(Files.newBufferedReader(file.toPath()), Task[].class)));
      Task task = new Task();
      task.setTimestamp(timestamp);
      int i;
      while ((i = Collections.binarySearch(tasks, task)) >= 0) {
        tasks.remove(i);
      }
      writeJsonToFile(gson.toJson(tasks));
    }
  }

  private void writeJsonToFile(String json) throws IOException {
    Files.write(
        file.toPath(),
        json.getBytes(StandardCharsets.UTF_8),
        StandardOpenOption.CREATE,
        StandardOpenOption.WRITE,
        StandardOpenOption.TRUNCATE_EXISTING
    );
  }
}
