package domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Edge {
    Integer v1;
    Integer v2;
    Integer weight;

    @Override
    public String toString() {
        return "Edge:" + v1 + " -> "+ v2 + " : " + weight + "\n";
    }
}
