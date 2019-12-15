
package com.jaimovie.acube.model;

public class MovieList {

    private String id;
    private String name;
    private String image;
    private String pathurl;
    private String genre;

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


}
