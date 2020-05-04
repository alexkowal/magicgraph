package starter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import utils.GraphTask;

import java.io.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;

import static java.util.Objects.nonNull;

@Service
public class MagicPermutationFinder {
    private JavaMailSender javaMailSender;

    @Autowired
    public MagicPermutationFinder(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void readGraphsFromFileAndCountMagicGraphs() throws Exception {
        ThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(7);
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader("/Users/aleksandr/magicgraph/src/main/java/files/graph6.txt"))) {
            String graph6String;
            while (nonNull(graph6String = bufferedReader.readLine())) {
                Task task = new Task(graph6String, javaMailSender);
                executor.submit(task);
            }
        }
        executor.shutdown();
    }

    public void readGraphsFromFile(InputStream is) throws Exception {
        ThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(7);
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is))) {
            String graph6String;
            while (nonNull(graph6String = bufferedReader.readLine())) {
                Task task = new Task(graph6String, javaMailSender);
                executor.submit(task);
            }
        }
        executor.shutdown();
    }

    public void readGraphsFromFileV2(InputStream is) throws Exception {
        ThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(7);
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is))) {
            String graph6String;
            while (nonNull(graph6String = bufferedReader.readLine())) {
                GraphTask task = new GraphTask(graph6String, javaMailSender);
                executor.submit(task);
            }
        }
        executor.shutdown();
    }
}
