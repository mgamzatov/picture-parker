package ru.bugdealers.pictureparker.utilits;

import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

/**
 * Created by Rashid.Iaraliev on 20.10.17.
 */
@Component
public class ScriptRunner {
    public void runProcess() throws Exception {
        Process p = Runtime.getRuntime().exec("1.sh");

        if (!p.waitFor(5, TimeUnit.SECONDS)) {
            p.destroy();
        }

        BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

        // read the output from the command
        System.out.println("Here is the standard output of the command:\n");
        StringBuilder result = new StringBuilder();
        String s;
        while ((s = stdInput.readLine()) != null) {
            result.append(s);
        }
    }
}
