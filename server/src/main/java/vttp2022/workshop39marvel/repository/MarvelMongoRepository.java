package vttp2022.workshop39marvel.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import vttp2022.workshop39marvel.model.InsertedComment;

@Repository
public class MarvelMongoRepository {
    
    @Autowired
    private MongoTemplate mongoTemplate;

    public InsertedComment insertComment(InsertedComment insertedComment) {
        return mongoTemplate.insert(insertedComment, "comments");
    }

    public List<InsertedComment> getCommentsById(String id, int limit) {
        Query query = new Query(Criteria.where("characterId").is(id)).limit(limit);
        List<InsertedComment> insertedComments = mongoTemplate.find(query, InsertedComment.class, "comments");
        return insertedComments;
    }

    public List<InsertedComment> getCommentById(String commentId) {
        Query query = new Query(Criteria.where("commentId").is(commentId));
        List<InsertedComment> insertedComments = mongoTemplate.find(query, InsertedComment.class, "comments");
        return insertedComments;
    }

    public void updateCommentToMongo(String commentId, String updatedComment) {
        Query query = new Query(Criteria.where("commentId").is(commentId));

        Update update = new Update();
        update.set("comment", updatedComment);

        mongoTemplate.updateFirst(query, update, "comments");

    }

    public void deleteCommentById(String id) {
        Query query = new Query(Criteria.where("commentId").is(id));
        mongoTemplate.remove(query, "comments");
    }

}
