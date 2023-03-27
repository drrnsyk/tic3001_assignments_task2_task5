package vttp2022.workshop39marvel.service;

import java.io.Reader;
import java.io.StringReader;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

import vttp2022.workshop39marvel.model.Character;
import vttp2022.workshop39marvel.model.InsertedComment;
import vttp2022.workshop39marvel.repository.MarvelMongoRepository;
import vttp2022.workshop39marvel.repository.MarvelRedisRepository;;

@Service
public class MarvelService {
    
    @Autowired
    private MarvelRedisRepository marvelRedisRepo;

    @Autowired
    private MarvelMongoRepository marvelMongoRepo;
    
    // api call from marvel
    public static final String URL_NAME = "https://gateway.marvel.com:443/v1/public/characters";

    // inject into envrioment variables
    @Value("${PUBLIC_KEY}") // this is also the api key
    private String publicKey;

    @Value("${PRIVATE_KEY}") // required for the hash generation
    private String privateKey;

    public String getCharactersBySearchTerm(String searchTerm, String limit, String offset) {
        
        String payload;
        System.out.println(">>> Getting characters from Marvel API by NAME");
        
        try {

            // construct the query string
            // marvel requires addtional param of ts (long string timestamp) and hash md5(ts+privateKey+publicKey)
            Long ts = System.currentTimeMillis();
            String signature = "%d%s%s".formatted(ts, privateKey, publicKey);
            String hash = "";
            // Message digest = md5, sha1, sha512
            // Get an instance of MD5
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            // Calculate our hash
            // Update our message digest
            md5.update(signature.getBytes());
            // Get the MD5 digest
            byte[] h = md5.digest();
            // Stringify the MD5 digest
            hash = HexFormat.of().formatHex(h);

            // construct the url
            String urlName = UriComponentsBuilder.fromUriString(URL_NAME)
                .queryParam("nameStartsWith", URLEncoder.encode(searchTerm, "UTF-8"))
                .queryParam("limit", limit)
                .queryParam("offset", offset)
                .queryParam("ts", ts)
                .queryParam("apikey", publicKey)
                .queryParam("hash", hash)
                .toUriString();

            // build the url
            RequestEntity<Void> req = RequestEntity.get(urlName).build();

            // decare template and response entity
            RestTemplate template = new RestTemplate();
            ResponseEntity<String> resp;

            // throws an exception if status code not in between 200 - 399
            resp = template.exchange(req, String.class);
            // get the json payload from the api call
            payload = resp.getBody();

            // print out the payload to check
            // System.out.println("payload: " + payload);

        } catch (Exception ex) {
            System.err.printf("Error: %s\n", ex.getMessage());
            return "";
        }

        Reader strReader = new StringReader(payload);
        JsonReader jsonReader = Json.createReader(strReader);
        JsonObject jo = jsonReader.readObject();
        JsonObject data = jo.getJsonObject("data");
        JsonArray results = data.getJsonArray("results");
        
        List<Character> characters = new LinkedList<>();

        for (int i = 0; i < results.size(); i++) {
            JsonObject joResult = results.getJsonObject(i);
            JsonObject thumbnail = joResult.getJsonObject("thumbnail");
            characters.add(Character.create(joResult, thumbnail));
        }

        JsonArrayBuilder arrBuilder = Json.createArrayBuilder();
        characters.stream() 
            .forEach(c -> {
                arrBuilder.add(c.toJson());
            });

        JsonArray jsonArrayCharacters = arrBuilder.build();
        // call service to cache into redis
        marvelRedisRepo.saveCharactersToRedis(jsonArrayCharacters);
        String jsonStringCharacters = jsonArrayCharacters.toString();

        // return ResponseEntity.ok(arrBuilder.build().toString());
        return jsonStringCharacters;

    }

    public String getCharacterById(String characterId) {

        // check if character is in redis cache
        Optional<String> opt = marvelRedisRepo.getFromRedisById(characterId);   
        String payload;

        // check if the box (optional) is empty
        if (opt.isEmpty()) {

            // get the character from marvel api
            System.out.println(">>> Getting character from Marvel API by ID");
            String url_id = "https://gateway.marvel.com:443/v1/public/characters/" + characterId;

            try {

                // construct the query string
                // marvel requires addtional param of ts (long string timestamp) and hash md5(ts+privateKey+publicKey)
                Long ts = System.currentTimeMillis();
                String signature = "%d%s%s".formatted(ts, privateKey, publicKey);
                String hash = "";
                // Message digest = md5, sha1, sha512
                // Get an instance of MD5
                MessageDigest md5 = MessageDigest.getInstance("MD5");
                // Calculate our hash
                // Update our message digest
                md5.update(signature.getBytes());
                // Get the MD5 digest
                byte[] h = md5.digest();
                // Stringify the MD5 digest
                hash = HexFormat.of().formatHex(h);

                // construct the url
                String urlId = UriComponentsBuilder.fromUriString(url_id)
                    .queryParam("ts", ts)
                    .queryParam("apikey", publicKey)
                    .queryParam("hash", hash)
                    .toUriString();

                // build the url
                RequestEntity<Void> req = RequestEntity.get(urlId).build();

                // decare template and response entity
                RestTemplate template = new RestTemplate();
                ResponseEntity<String> resp;

                // throws an exception if status code not in between 200 - 399
                resp = template.exchange(req, String.class);
                // get the json payload from the api call
                payload = resp.getBody();

                // print out the payload to check
                // System.out.println("payload: " + payload);

            } catch (Exception ex) {
                System.err.printf("Error: %s\n", ex.getMessage());
                return "";
            }

            Reader strReader = new StringReader(payload);
            JsonReader jsonReader = Json.createReader(strReader);
            JsonObject jo = jsonReader.readObject();
            JsonObject data = jo.getJsonObject("data");
            JsonArray results = data.getJsonArray("results");
            
            List<Character> characters = new LinkedList<>(); // list of character with only size 1

            for (int i = 0; i < results.size(); i++) {
                JsonObject joResult = results.getJsonObject(i);
                JsonObject thumbnail = joResult.getJsonObject("thumbnail");
                characters.add(Character.create(joResult, thumbnail));
            }

            JsonObject joCharacter = characters.get(0).toJson(); // the first object in the list is a Character object, convert it to json

            marvelRedisRepo.saveCharacterToRedis(joCharacter);

            System.out.println(joCharacter.toString());
            
            return joCharacter.toString(); // stringify the json object and return as string to controller
        
        } else {

            //retrive the value from redis cache, for when the box is not empty
            payload = opt.get();
            System.out.println(">>> Getting character from REDIS cache by ID");
            // System.out.println("payload: " + payload);

            Reader strReader = new StringReader(payload);
            JsonReader jsonReader = Json.createReader(strReader);
            JsonObject joCharacter = jsonReader.readObject();
            System.out.println(joCharacter.toString());
            return joCharacter.toString(); // stringify the json object and return as string to controller

        }       
    }

    public String insertComment(InsertedComment insertedComment) {
        
        InsertedComment insertedCommentResult = marvelMongoRepo.insertComment(insertedComment);

        JsonObject joInsertedComment = insertedCommentResult.toJson();
        
        return  joInsertedComment.toString();
    }

    public String getCommentsById(String id, int limit) {

        List<InsertedComment> insertedComments = marvelMongoRepo.getCommentsById(id, limit);

        JsonArrayBuilder arrBuilder = Json.createArrayBuilder();
        insertedComments.stream() 
            .forEach(i -> {
                arrBuilder.add(i.toJson());
            });
        
        JsonArray jsonArrayInsertedComments = arrBuilder.build();

        String jsonStringInsertedComments = jsonArrayInsertedComments.toString();

        // return a jsonArrayString
        return jsonStringInsertedComments;
    }

    public String getCommentById(String commentId) {
        
        List<InsertedComment> insertedComments = marvelMongoRepo.getCommentById(commentId);

        JsonArrayBuilder arrBuilder = Json.createArrayBuilder();
        insertedComments.stream() 
            .forEach(i -> {
                arrBuilder.add(i.toJson());
            });
        
        JsonArray jsonArrayInsertedComments = arrBuilder.build();

        String jsonStringInsertedComments = jsonArrayInsertedComments.toString();

        return jsonStringInsertedComments;
    }

    public void updateCommentToMongo(String commentId, String updatedComment) {
        marvelMongoRepo.updateCommentToMongo(commentId, updatedComment);
    }

    public void deleteCommentById(String id) {
        marvelMongoRepo.deleteCommentById(id);
    }
}
