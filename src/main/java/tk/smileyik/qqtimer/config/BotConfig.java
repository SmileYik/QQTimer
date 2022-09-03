package tk.smileyik.qqtimer.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.utils.BotConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author SmileYik
 * @Description TODO
 * @date 2022年09月02日 14:11
 */
public class BotConfig {
  /**
   * 時間表
   */
  public final static String SCHEDULE = "schedule";
  /**
   * 緩存圖片
   */
  public final static String PRODUCT_IMAGES = "product-images";

  private final Long qq;
  private final byte[] password;
  private final BotConfiguration.HeartbeatStrategy heartbeatStrategy;
  private final BotConfiguration.MiraiProtocol miraiProtocol;
  private Path workDirectory;

  private BotConfig(Long qq,
                    byte[] password,
                    BotConfiguration.HeartbeatStrategy heartbeatStrategy,
                    BotConfiguration.MiraiProtocol miraiProtocol,
                    Path workDirectory) {
    this.qq = qq;
    this.password = password;
    this.heartbeatStrategy = heartbeatStrategy;
    this.miraiProtocol = miraiProtocol;
    mkdirs(workDirectory.toFile());

    try {
      this.workDirectory = workDirectory.toRealPath();
      mkdirs(getPath(SCHEDULE).toFile());
      mkdirs(getPath(PRODUCT_IMAGES).toFile());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static BotConfig load(Reader reader) throws NoSuchAlgorithmException {
    JsonObject config = JsonParser.parseReader(reader).getAsJsonObject();

    JsonElement passwordElement = config.get("password");
    byte[] password;
    // md5 password
    if (passwordElement.isJsonArray()) {
      JsonArray array = (JsonArray) passwordElement;
      password = new byte[array.size()];
      for (int i = 0; i < password.length; ++i) {
        password[i] = array.get(i).getAsByte();
      }
    } else {
      String psw = passwordElement.getAsString();
      // md5 hex password
      if (psw.length() == 32 && psw.matches("[0-9abcdef]+")) {
        password = new byte[16];
        for (int i = 0; i < 16; ++i) {
          password[i] = (byte) Integer.parseInt(psw.substring(0, 2), 16);
          psw = psw.substring(2);
        }
      } else {
        // raw password
        MessageDigest messageDigest = MessageDigest.getInstance("md5");
        password = messageDigest.digest(psw.getBytes(StandardCharsets.UTF_8));
      }
    }

    return new BotConfig(
        config.get("qq").getAsLong(),
        password,
        BotConfiguration.HeartbeatStrategy.valueOf(config.get("heartbeat").getAsString()),
        BotConfiguration.MiraiProtocol.valueOf(config.get("protocol").getAsString()),
        Paths.get(config.get("workDirectory").getAsString()).normalize()
    );
  }

  private void mkdirs(File path) {
    if (!path.exists() && !path.mkdirs()) {
      throw new RuntimeException("Can not create directory: " + path);
    }
  }

  public Path getWorkDirectory() {
    return workDirectory;
  }

  /**
   * Create a bot from config.
   *
   * @return a new bot
   */
  public Bot newBot() {
    BotConfiguration botConfiguration = new BotConfiguration();
    botConfiguration.setProtocol(miraiProtocol);
    botConfiguration.setHeartbeatStrategy(heartbeatStrategy);
    botConfiguration.setWorkingDir(workDirectory.toFile());
    botConfiguration.fileBasedDeviceInfo(workDirectory + "/device.json");
    return BotFactory.INSTANCE.newBot(qq, password, botConfiguration);
  }

  public Path getPath(String sub) {
    return Paths.get(workDirectory.toString(), sub);
  }
}
