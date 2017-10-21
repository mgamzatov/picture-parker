package ru.bugdealers.pictureparker.utilits;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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
    private static final String DESCRIPTION_SCRIPT = "test_description.py";
    private static final String QUESTION_SCRIPT = "test_question.py";
    private static final String IMAGE_SCRIPT = "test_image.py";
    private static final String PYTHON_COMMAND = "python";
    private static final String END_OF_INPUT = "#END";
    private Logger logger = LoggerFactory.getLogger(ScriptRunner.class);

    @Value("${script.folder}")
    private String scriptFolder;

    public long getPictureIdByImage(String pathToFile) {
        String command = PYTHON_COMMAND + " " + scriptFolder + IMAGE_SCRIPT + " " + pathToFile;
        // Если не смогли определить, то возвращаем стандартный ответ с идентификатором -1
        long result = -1;
        try {
            String[] lines = runProcess(command).split("\n");
            String id = lines[lines.length-1];
            result = Long.parseLong(id);
        } catch (Exception e) {
            logger.error(e.toString());
        }
        return result;
    }

    public long getPictureIdByDescription(String description) {
        String command = PYTHON_COMMAND + " " + scriptFolder + DESCRIPTION_SCRIPT + " " + description;
        // Если не смогли определить, то возвращаем стандартный ответ с идентификатором -1
        long result = -1;
        try {
            result = Long.parseLong(runProcess(command));
        } catch (Exception e) {
            logger.error(e.toString());
        }
        return result;
    }

    public long getStandardQuestionIdByQuestion(String question) {
        String command = PYTHON_COMMAND + " " + scriptFolder + QUESTION_SCRIPT + " " + question;
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

        logger.info(command);
        if (!p.waitFor(120, TimeUnit.SECONDS)) {
            p.destroy();
        }

        BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
        BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

        StringBuilder result = new StringBuilder();
        String s;
        while ((s = stdInput.readLine()) != null) {
            result.append(s);
        }
        while ((s = stdError.readLine()) != null) {
            logger.error(s);
        }
        logger.info(result.toString());
        return result.toString();
    }
}
