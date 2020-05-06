package utils;

import com.google.common.collect.Lists;
import domain.Graph;
import domain.ResearchResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.io.IOException;
import java.util.List;

@Data
@AllArgsConstructor
public class GraphTask implements Runnable {
    private String graph6String;
    private JavaMailSender javaMailSender;

    @Override
    public void run() {
        Graph graph1 = null;
        try {
            graph1 = Graph6Reader.readGraph6Format(graph6String);
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<Integer> possibleValues = Lists.newArrayList();
        for (int i = 0; i < graph1.getEdges().size(); i++) {
            possibleValues.add(i + 1);
        }
        System.out.println(graph6String);
        try {
            Long k = 0l;
            if (checkEdgesCount(graph1)) {
//                    graph1.setMagicNumber(i);
                boolean found = GraphUtilsV3.generateAndCheck(graph1, 0, possibleValues,
                        new ResearchResult(false, Lists.newArrayList()), k);
            if(found)
                sendEmail("Graph found - " + graph6String.charAt(0), "Permutation for Graph found: " + graph6String + " " + graph1.getEdges());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendEmail(String subject, String message) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo("alexkowalkowale@gmail.com");
        msg.setSubject(subject);
        msg.setText(message);
        javaMailSender.send(msg);
    }

    private boolean checkEdgesCount(Graph graph) {
        int mod = graph.getVertices().size() % 3;
        int mN = 0;
        if (mod == 0) {
            mN = 4 * graph.getVertices().size() / 3;
        } else if (mod == 1) {
            mN = (4 * graph.getVertices().size() + 5) / 3;
        } else if (mod == 2) {
            mN = (4 * graph.getVertices().size() + 1) / 3;
        }
        return graph.getEdges().size() >= mN;
    }
}
