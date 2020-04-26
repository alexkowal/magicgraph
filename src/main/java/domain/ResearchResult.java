package domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ResearchResult {
    Boolean result = false;
    List<Edge> edgeList;
}
