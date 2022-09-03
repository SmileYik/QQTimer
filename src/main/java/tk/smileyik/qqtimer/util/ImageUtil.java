package tk.smileyik.qqtimer.util;

import tk.smileyik.qqtimer.config.BotConfig;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * @author SmileYik
 * @Description TODO
 * @date 2022年09月03日 12:42
 */
public class ImageUtil {
  public static File productImage(BotConfig botConfig, String str) throws IOException, FontFormatException {
    BufferedImage read = new BufferedImage(370, 480, 1);
    Graphics2D graphics = read.createGraphics();
    graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    graphics.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_DEFAULT);
    Font font = Font.createFont(
        0, Class.class.getResourceAsStream("/WenQuanWeiMiHei-1.ttf")
    ).deriveFont(26.0f);
    int r = (int) (Math.random() * 225);
    int g = (int) (Math.random() * 225);
    int b = (int) (Math.random() * 225);
    Color background = new Color(r, g, b);
    Color fontColor = new Color(255 - r, 255 - g, 255 - b);
    graphics.setColor(background);
    graphics.fillRect(0, 0, read.getWidth(), read.getHeight());
    graphics.setFont(font);
    graphics.setColor(fontColor);
    graphics.drawString(str, 50, 50);
    read.flush();
    File outFile = new File(botConfig.getPath(BotConfig.PRODUCT_IMAGES).toFile(), UUID.randomUUID() + ".png");
    ImageIO.write(read, "png", outFile);
    return outFile;
  }
}
