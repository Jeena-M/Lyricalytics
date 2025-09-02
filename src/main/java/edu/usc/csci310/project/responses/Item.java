package edu.usc.csci310.project.responses;

import java.util.List;

public class Item {
    private String id;
    private List<Image> images;
    private String name;

    public String getId() { return id; }
    public void setId(String i) { id = i; }

    public List<Image> getImages() { return images; }
    public void setImages(List<Image> i) { images = i; }

    public String getName() { return name; }
    public void setName(String n) { name = n; }
}
