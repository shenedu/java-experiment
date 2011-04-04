package image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.Test;

import com.thebuzzmedia.imgscalr.Scalr;

public class ImageResize {

    @Test
    public void testScalrResize() throws IOException{
        BufferedImage img = ImageIO.read(new File("/home/feng/Downloads/a.jpg"));
        BufferedImage resizedImg = Scalr.resize(img, 100);
        ImageIO.write(resizedImg, "png", new File("/tmp/a.png"));
    }
}
