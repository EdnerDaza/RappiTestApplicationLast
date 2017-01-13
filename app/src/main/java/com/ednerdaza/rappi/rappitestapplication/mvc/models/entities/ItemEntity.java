package com.ednerdaza.rappi.rappitestapplication.mvc.models.entities;

/**
 * Created by administrador on 8/01/17.
 */
public class ItemEntity {

    String Title;
    String Summary;
    String ImageUrl;


    public ItemEntity(String Title, String Summary, String ImageUrl) {
        this.Title = Title;
        this.Summary = Summary;
        this.ImageUrl = ImageUrl;
    }

    public String getTitle() {
        return Title;
    }

    public String getSummary() {
        return Summary;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public void setSummary(String summary) {
        Summary = summary;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

}
