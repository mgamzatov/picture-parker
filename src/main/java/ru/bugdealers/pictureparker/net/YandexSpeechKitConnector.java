package ru.bugdealers.pictureparker.net;

import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.UUID;

@Component
@PropertySource("classpath:privacy.properties")
public class YandexSpeechKitConnector {

    @Value("${yandexSpeechKitKey}")
    private String yandexSpeechKitKey;

    private OkHttpClient okHttpClient;
    private String uuid;

    @Autowired
    public YandexSpeechKitConnector(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
        this.uuid = UUID.randomUUID().toString().replaceAll("-", "");
    }

    public String recognizeTextFromAudio(File audio) {
        String text = "";
        try {
            String xml = getXmlResponse(audio);
            text = getTextFromXml(xml);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return text;
    }

    private String getXmlResponse(File audio) throws IOException {
        RequestBody body = RequestBody.create(MediaType.parse("audio/ogg;codecs=opus"), audio);
        Request request = new Request.Builder()
                .url("http://asr.yandex.net/asr_xml" +
                        "?uuid=" + uuid +
                        "&key=" + yandexSpeechKitKey +
                        "&lang=ru-RU" +
                        "&topic=queries")
                .addHeader("Content-Type", "audio/ogg;codecs=opus")
                .addHeader("Transfer-Encoding", "chunked")
                .post(body)
                .build();
        Response response = okHttpClient.newCall(request).execute();
        return response.body().string();
    }

    private String getTextFromXml(String xml) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource is = new InputSource();
        try (StringReader stringReader = new StringReader(xml)) {
            is.setCharacterStream(stringReader);
            Document document = builder.parse(is);
            Element root = document.getDocumentElement();
            Element message = (Element) root.getElementsByTagName("variant").item(0);
            return message.getTextContent();
        }
    }
}
