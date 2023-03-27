package vttp2022.workshop39marvel.model;

import jakarta.json.Json;
import jakarta.json.JsonObject;

public class Character {

    private String id;
    private String name;
    private String description;
    private String imageurl;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }


    // helper functions
    public static Character create(JsonObject joResult, JsonObject thumbnail) {
        Character c = new Character();
        c.setId(Integer.toString(joResult.getInt("id")));
        c.setName(joResult.getString("name"));
        c.setDescription(joResult.getString("description"));
        c.setImageurl(thumbnail.getString("path") + "." + thumbnail.getString("extension"));
        return c;
    }

    public JsonObject toJson() {
        return Json.createObjectBuilder()
            .add("id", id)
            .add("name", name)
            .add("description", description)
            .add("imageurl", imageurl)
            .build();
    }


    
}
