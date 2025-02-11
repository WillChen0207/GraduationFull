package com.twilight.twilight.Model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SimilarityResultDTO {
    private Long userId1; //目标用户<id>
    private Long userId2; //相似用户<id>
    private Double compositeSimilarity; //综合相似度

    public SimilarityResultDTO(Long userId1, Long userId2, Double compositeSimilarity) {
        this.userId1 = userId1;
        this.userId2 = userId2;
        this.compositeSimilarity = compositeSimilarity;
    }

    public SimilarityResultDTO() {
    }
}

