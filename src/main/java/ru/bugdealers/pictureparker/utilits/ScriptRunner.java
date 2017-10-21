package ru.bugdealers.pictureparker.utilits;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.bugdealers.pictureparker.bot.VkBotRunner;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

/**
 * Created by Rashid.Iaraliev on 20.10.17.
 */
@Component
public class ScriptRunner {
    private static final String DESCRIPTION_SCRIPT = "/home/r/hackathon.sh";
    private static final String QUESTION_SCRIPT = "2.sh";
    private Logger logger = LoggerFactory.getLogger(VkBotRunner.class);


    public long getPictureIdByDescription(String description) {
        String command = DESCRIPTION_SCRIPT + " -" +description;
        // Если не смогли определить, то возвращаем стандартный ответ с идентификатором -1
        long result = -1;
        try {
            result = Long.parseLong(runProcess(command));
        } catch (Exception e) {
            logger.error(e.toString());
        }
        return result;
    }


    private String runProcess(String command) throws Exception {
        Process p = Runtime.getRuntime().exec(command);

        if (!p.waitFor(5, TimeUnit.SECONDS)) {
            p.destroy();
        }

        BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

        StringBuilder result = new StringBuilder();
        String s;
        while ((s = stdInput.readLine()) != null) {
            result.append(s);
        }
        return result.toString();
    }
}
