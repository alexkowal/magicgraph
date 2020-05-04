package domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Vertex {
    Integer num;
    List<Edge> edges;
    List<List<Integer>> permutations;

    public Vertex(Integer num) {
        this.num = num;
    }

    @Override
    public String toString(){
        return (num+" , " + edges );

    }}
