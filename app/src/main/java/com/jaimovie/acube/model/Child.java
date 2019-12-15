
package com.jaimovie.acube.model;

public class Child {

    private String id;
    private String name;
    private String image;
    private String pathurl;
    private String genre;
    private String type;

    public Child(String id, String name, String image, String pathurl, String genre, String type) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.pathurl = pathurl;
        this.genre = genre;
        this.type = type;
    }

    public String getPathurl() {
        return pathurl;
    }

    public void setPathurl(String pathurl) {
        this.pathurl = pathurl;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    private String link;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


}
