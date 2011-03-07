package csstricks;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class DownloadUbuntu {

	public static void main(String[] args) throws IOException {

		Download download = new Download();

		File file = new File("/home/feng/Downloads/ubuntu-11-04-alpha3.iso");

		URL url = new URL(
				"http://cdimage.ubuntu.com/releases/natty/alpha-3/natty-desktop-amd64.iso");

		download.download(url, file);

	}

}
