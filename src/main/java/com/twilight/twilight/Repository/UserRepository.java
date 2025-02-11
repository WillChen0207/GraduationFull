package com.twilight.twilight.Repository;

import com.twilight.twilight.Model.InfoDTO;
import com.twilight.twilight.Model.Interaction;
import com.twilight.twilight.Model.SimilarityResultDTO;
import com.twilight.twilight.Model.User;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends Neo4jRepository<User, UUID> {

    @Query("MATCH (u:User) WHERE u.userName = $content OR u.email = $content RETURN u")
    User findByContent(@Param("content") String content);

    @Query("MATCH (u:User) WHERE (u.userName = $content OR u.email = $content) AND u.password = $password RETURN id(u)")
    Optional<Long> loginCheck(@Param("content") String content, @Param("password") String password);


    @Query("MATCH (u) WHERE id(u) = $userId RETURN u")
    Optional<User> findById(Long userId);

    @Query("MATCH (u) WHERE id(u) = $id RETURN id(u) AS userId, u.userName AS userName, u.email AS email, u.userType AS userType")
    InfoDTO getInfoById(Long id);

    @Query("MATCH (u) WHERE id(u) = $id DELETE u")
    void deleteById(Long id);

    @Query("MATCH (u) WHERE u.id = $uuid DELETE u")
    void deleteByUUID(UUID uuid);

    @Query("""
            MATCH (u1:User)-[i1:interact]->(r:Resource)<-[i2:interact]-(u2:User)
            WHERE id(u1) = $userId
            WITH u1, u2,
                     (COALESCE(i1.download, 0) * 0.4 + COALESCE(i1.collect, 0) * 0.3 + COALESCE(i1.like, 0) * 0.2 + COALESCE(i1.view, 0) * 0.1) AS prefs1,
                 (COALESCE(i2.download, 0) * 0.4 + COALESCE(i2.collect, 0) * 0.3 + COALESCE(i2.like, 0) * 0.2 + COALESCE(i2.view, 0) * 0.1) AS prefs2
            WITH u1, u2, prefs1, prefs2,
                   CASE WHEN size([prefs1]) = 0 OR size([prefs2]) = 0 OR all(x IN [prefs1] WHERE x = 0) OR all(x IN [prefs2] WHERE x = 0)
                        THEN 0
                        ELSE gds.similarity.cosine([prefs1], [prefs2]) END AS compositeSimilarity
            RETURN id(u1) AS userId1,
                   id(u2) AS userId2,
                   compositeSimilarity
            ORDER BY compositeSimilarity DESC
            LIMIT $simNum""")
    List<SimilarityResultDTO> calcSim(Long userId, Integer simNum);

    @Query("""
            MATCH (u:User)-[ir:interact]->(r:Resource)
            WHERE id(u) = $userId AND (ir.collect = 1 OR ir.download > 0)
            RETURN id(u) AS userId, id(r) AS resourceId, r.resourceName AS resourceName, r.resourceType AS resourceType, COALESCE(ir.view,0) AS view, COALESCE(ir.like,0) AS like, COALESCE(ir.collect,0) AS collect, COALESCE(ir.download,0) AS download
            """)
    List<Interaction> getInteractedRes(Long userId);
}
