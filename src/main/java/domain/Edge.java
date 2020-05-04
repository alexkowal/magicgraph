package domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Edge {
    Integer v1;
    Integer v2;
    Integer weight;
    boolean marked = false;

    @Override
    public String toString() {
        return "Edge:" + v1 + " -> " + v2 + " : " + weight + "\n";
    }
}
