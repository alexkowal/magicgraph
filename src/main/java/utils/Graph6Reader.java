package utils;

import com.google.common.collect.Lists;
import domain.Edge;
import domain.Graph;
import domain.Vertex;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class Graph6Reader {
    private static final Integer STANDART_CHARACTER_REDUCER = 63;

    public static Graph readGraph6Format(String graphInGraph6Format) throws IOException {
        Integer numberOfVertices = getNumberOfVerices(graphInGraph6Format);
        List<Integer> chars = Lists.newArrayList();
        for (int i = 1; i < graphInGraph6Format.length(); i++) {
            chars.add(Integer.valueOf(graphInGraph6Format.charAt(i)) - 63);
        }
        int[][] matrix = new int[numberOfVertices][numberOfVertices];
        String matrixInRow = getMatrixInRowFormat(chars);

        List<Vertex> vertices = Lists.newArrayList();
        for (int i = 0; i < numberOfVertices; i++) {
            vertices.add(new Vertex(i, Lists.newArrayList()));
        }
        List<Edge> edges = Lists.newArrayList();
        int pos = 0;
        for (int i = 0; i < numberOfVertices; i++) {
            for (int j = 0; j < i; j++) {
                if (matrixInRow.charAt(pos) == '1') {
                    edges.add(new Edge(i, j, edges.size() + 1));
                }
                matrix[i][j] = matrixInRow.charAt(pos) == '1' ? 1 : 0;
                pos++;
            }
        }

        return buildGraphFromVerticesAndEdges(vertices, edges);
    }

    private static StringBuilder ToBin(int a) {
        StringBuilder res = new StringBuilder();
        while (a != 0) {
            res.append(a % 2);
            a /= 2;
        }
        while (res.length() < 6)
            res.append(0);
        return res.reverse();

    }

    private static Integer getNumberOfVerices(String graphInGraph6Format) {
        return graphInGraph6Format.charAt(0) - STANDART_CHARACTER_REDUCER;
    }

    private static String getMatrixInRowFormat(List<Integer> chars) {
        StringBuilder matrixInRow = new StringBuilder("");
        for (Integer aChar : chars) {
            int a = aChar;
            matrixInRow = matrixInRow.append(ToBin(a));
        }
        return String.valueOf(matrixInRow);
    }

    private static Graph buildGraphFromVerticesAndEdges(List<Vertex> vertices, List<Edge> edges) {
        Graph graph = new Graph(vertices, edges);
        for (Edge e : edges) {
            vertices.stream()
                    .filter(vertex -> vertex.getNum().equals(e.getV1()) || vertex.getNum().equals(e.getV2()))
                    .collect(Collectors.toList())
                    .forEach(vertex -> vertex.getEdges().add(e));
        }
        return graph;
    }

}
