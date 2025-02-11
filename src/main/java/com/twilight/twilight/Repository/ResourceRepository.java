package com.twilight.twilight.Repository;

import com.twilight.twilight.Model.InteractionDTO;
import com.twilight.twilight.Model.RecommendationDTO;
import com.twilight.twilight.Model.Resource;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ResourceRepository extends Neo4jRepository<Resource, UUID> {

    @Query("MATCH (r) WHERE id(r) = $id RETURN r")
    Optional<Resource> findById(Long id);

    @Query("MATCH (r) WHERE id(r) = $id DELETE r")
    void deleteById(Long id);

    @Query("MATCH (r) WHERE r.id = $uuid DELETE r")
    void deleteByUUID(UUID uuid);

    @Query("""
            MATCH (u:User), (r:Resource)
            WHERE id(u) = $userId AND id(r) = $resourceId
            MERGE (u)-[ir:interact]->(r)
            ON CREATE SET ir.provide = true
            ON MATCH SET ir.provide = true
            """)
    void provide(Long userId, Long resourceId);

    @Query("""
            MATCH (resource:Resource)
            WHERE resource.id = $uuid
            RETURN id(resource) AS resourceId
            """)
    Long getId(UUID uuid);

    @Query("match (:User)-[i:interact]->(r:Resource) where id(r)=$resourceId\n" +
            "delete i")
    void clear(Long resourceId);

    @Query("""
            MATCH (u:User), (r:Resource)
            WHERE id(u) = $userId AND id(r) = $resourceId
            MERGE (u)-[ir:interact]->(r)
            ON CREATE SET ir.download = 1
            ON MATCH SET ir.download = COALESCE(ir.download, 0) + 1
            RETURN r.content
            """)
    String download(Long userId, Long resourceId);

    @Query("""
            MATCH (u:User), (r:Resource)
            WHERE id(u) = $userId AND id(r) = $resourceId
            MERGE (u)-[ir:interact]->(r)
            ON CREATE SET ir.collect = $flag
            ON MATCH SET ir.collect = $flag
            """)
    void collect(Long userId, Long resourceId, Integer flag);

    @Query("""
            MATCH (u:User), (r:Resource)
            WHERE id(u) = $userId AND id(r) = $resourceId
            MERGE (u)-[ir:interact]->(r)
            ON CREATE SET ir.view = 1
            ON MATCH SET ir.view = COALESCE(ir.view, 0) + 1
            """)
    void view(Long userId, Long resourceId);

    @Query("""
            MATCH (u:User), (r:Resource)
            WHERE id(u) = $userId AND id(r) = $resourceId
            MERGE (u)-[ir:interact]->(r)
            ON CREATE SET ir.like = $flag
            ON MATCH SET ir.like = $flag
            """)
    void like(Long userId, Long resourceId, Integer flag);

    @Query("""
        MATCH (u1:User)
        WHERE id(u1) = $userId
        MATCH (u2:User)
        WHERE id(u2) IN $similarUserIds
        MATCH (u2)-[i:interact]->(r:Resource)
        WHERE NOT (u1)-[:interact]->(r)
        AND ($keyword IS NULL OR toLower(r.resourceName) CONTAINS toLower($keyword) OR toLower(r.content) CONTAINS toLower($keyword))
        WITH r,
             SUM(CASE WHEN i.like = 1 THEN 0.2 ELSE 0 END) +
             SUM(i.download * 0.4) +
             SUM(CASE WHEN i.collect = 1 THEN 0.3 ELSE 0 END) +
             SUM(i.view * 0.1) AS score
        RETURN id(r) AS resourceId, r.resourceName AS resourceName, r.resourceType AS resourceType, score
        ORDER BY score DESC
        LIMIT $recNum
        """)
    List<RecommendationDTO> getRecommend(List<Long> similarUserIds, Long userId, Integer recNum, String keyword);


    @Query("""
            MATCH (u:User)-[ir:interact]->(r:Resource)
            WHERE id(u)=$userId AND id(r)=$resourceId
            RETURN COALESCE(ir.view, 0) AS view, COALESCE(ir.like, 0) AS like, COALESCE(ir.collect, 0) AS collect, COALESCE(ir.download, 0) AS download""")
    InteractionDTO getInteractionStates(Long userId, Long resourceId);
}
