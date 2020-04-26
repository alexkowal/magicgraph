package starter;

import domain.Edge;
import domain.Graph;
import domain.ResearchResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import utils.Graph6Reader;
import utils.GraphUtils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;

@Data
@Slf4j
@AllArgsConstructor
public class Task implements Runnable {
    private String graph6String;
    private JavaMailSender javaMailSender;

    @Override
    public void run() {
        GraphUtils graphUtils = new GraphUtils();
        Graph graph1 = null;
        try {
            graph1 = Graph6Reader.readGraph6Format(graph6String);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ResearchResult researchResult = null;
        try {
            researchResult = graphUtils.checkMagicPermutationExist(graph1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (researchResult.getResult()) {
            Collections.sort(researchResult.getEdgeList(), Comparator.comparing(Edge::getV1));
            log.info("Permutation for Graph found: " + graph6String + " " + researchResult.getEdgeList());
            sendEmail("Graph found:", "Permutation for Graph found: " + graph6String + " " + researchResult.getEdgeList());
//            try (BufferedWriter bw = new BufferedWriter(new FileWriter("/Users/aleksandr/magicgraph/src/main/java/files/" + graph6String + ".txt"))) {
//                bw.write("Permutation for Graph found: " + graph6String + " " + researchResult.getEdgeList());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        } else {
            log.info("Not found: " + graph6String);
        }
    }

    private void sendEmail(String subject, String message) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo("alexkowalkowale@gmail.com");
        msg.setSubject(subject);
        msg.setText(message);
        javaMailSender.send(msg);
    }
}