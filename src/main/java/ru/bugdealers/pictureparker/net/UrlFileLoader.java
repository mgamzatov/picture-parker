package ru.bugdealers.pictureparker.net;

import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

@Component
public class UrlFileLoader {
    public void downloadFileFromUrl(String urlStr, String file) throws IOException {
        URL url = new URL(urlStr);
        try(ReadableByteChannel rbc = Channels.newChannel(url.openStream());
            FileOutputStream fos = new FileOutputStream(file)) {

            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        }
    }
}
