package vttp2022.workshop39marvel.controllerTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import vttp2022.workshop39marvel.model.Comment;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class MarvelControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testGetCharacters() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/characters?searchTerm=spiderman&limit=10&offset=0", String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void testPostCommentToMongo() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Comment comment = new Comment();
        comment.setComment("test comment");

        HttpEntity<Comment> request = new HttpEntity<>(comment, headers);

        ResponseEntity<String> response = restTemplate.postForEntity("/api/character/123/comment", request, String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void testGetComments() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/character/comments?id=123&limit=10", String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void testGetCommentById() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/character/comment?commentId=123", String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void testUpdateCommentToMongo() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Comment comment = new Comment();
        comment.setComment("updated comment");

        HttpEntity<Comment> request = new HttpEntity<>(comment, headers);

        ResponseEntity<String> response = restTemplate.exchange("/api/character/editcomment/123", HttpMethod.PUT, request, String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testDeleteCommentById() {
        ResponseEntity<String> response = restTemplate.exchange("/api/character/comments?id=123", HttpMethod.DELETE, null, String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

}