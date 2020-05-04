package domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Graph {
    List<Vertex> vertices;
    List<Edge> edges;
    Integer magicNumber = 0;
    String name;

    @Override
    public String toString() {
        return "Graph{" +
                "edges=" + edges + "}";
    }
}
