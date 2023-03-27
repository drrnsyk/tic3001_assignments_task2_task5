package vttp2022.workshop39marvel.controller;

import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import vttp2022.workshop39marvel.model.Comment;
import vttp2022.workshop39marvel.model.InsertedComment;
import vttp2022.workshop39marvel.service.MarvelService;

@Controller
@RequestMapping(path="/api")
public class MarvelController {

    @Autowired
    private MarvelService marvelSvc;

    @GetMapping(value="/characters", produces=MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<String> getCharacters(@RequestParam String searchTerm, @RequestParam String limit, @RequestParam String offset) {
        
        System.out.printf(">>> query param: searchTerm=%s\n", searchTerm);
        System.out.printf(">>> query param: limit=%s\n", limit);
        System.out.printf(">>> query param: offset=%s\n", offset);

        String jsonStringCharacters = marvelSvc.getCharactersBySearchTerm(searchTerm, limit, offset);

        return ResponseEntity.ok(jsonStringCharacters);

    }

    @GetMapping(value="/character/{characterId}", produces=MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<String> getCharacterById(@PathVariable String characterId) {
        
        System.out.printf(">>> query param: id=%s\n", characterId);

        String jsonStringCharacter = marvelSvc.getCharacterById(characterId);

        return ResponseEntity.ok(jsonStringCharacter);
    }

    @PostMapping(value="/character/{characterId}/comment", produces=MediaType.APPLICATION_JSON_VALUE, consumes=MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<String> postCommentToMongo(@PathVariable String characterId, @RequestBody Comment commentObj) {
        
        System.out.printf(">>> query param: characterId=%s\n", characterId);
        System.out.printf(">>> query param: comment=%s\n", commentObj.getComment());


        Comment comment = commentObj;
        InsertedComment insertedComment = new InsertedComment(characterId, comment.getComment());

        System.out.println(insertedComment.getCharacterId());
        System.out.println(insertedComment.getComment());
        System.out.println(insertedComment.getPostedDate());

        String jsonStringInsertedComment = marvelSvc.insertComment(insertedComment);

        return ResponseEntity.ok(jsonStringInsertedComment);
    }

    @GetMapping(value="/character/comments", produces=MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<String> getComments(@RequestParam String id, @RequestParam(defaultValue="20") int limit) {

        System.out.printf(">>> query param: id=%s\n", id);
        System.out.printf(">>> query param: limit=%d\n", limit);

        String jsonStringInsertedComments = marvelSvc.getCommentsById(id, limit);

        return ResponseEntity.ok(jsonStringInsertedComments);
    }

    @GetMapping(value="/character/comment", produces=MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<String> getCommentById(@RequestParam String commentId) {
        
        System.out.printf(">>> query param: id=%s\n", commentId);

        String jsonStringInsertedComments = marvelSvc.getCommentById(commentId);

        return ResponseEntity.ok(jsonStringInsertedComments);

    }

    @PutMapping(value="/character/editcomment/{commentId}", produces=MediaType.APPLICATION_JSON_VALUE, consumes=MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<String> updateCommentToMongo(@PathVariable String commentId, @RequestBody Comment commentObj) {
        
        System.out.printf(">>> query param1: commentId=%s\n", commentId);
        System.out.printf(">>> query param2: comment=%s\n", commentObj.getComment());

        String updatedComment = commentObj.getComment();
        marvelSvc.updateCommentToMongo(commentId, updatedComment);

        return ResponseEntity.ok(null);
    }

    @DeleteMapping(value="/character/comments")
    @ResponseBody
    public ResponseEntity<String> deleteCommentById(@RequestParam String id) {

        System.out.printf(">>> query param: id=%s\n", id);

        marvelSvc.deleteCommentById(id);

        return ResponseEntity.ok(null);
    }
 
}
