package com.kencorp.travelbiz;

import android.os.Parcel;
import android.os.Parcelable;

public class TravelDeals implements Parcelable {

    private String id;
    private String title;
    private String price;
    private String description;
    private String imageUrl;

    public TravelDeals() {

    }

    public TravelDeals(String title, String price, String description, String imageUrl) {

        this.title = title;
        this.price = price;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    protected TravelDeals(Parcel in) {
        id = in.readString();
        title = in.readString();
        price = in.readString();
        description = in.readString();
        imageUrl = in.readString();
    }

    public static final Creator<TravelDeals> CREATOR = new Creator<TravelDeals>() {
        @Override
        public TravelDeals createFromParcel(Parcel in) {
            return new TravelDeals(in);
        }

        @Override
        public TravelDeals[] newArray(int size) {
            return new TravelDeals[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(price);
        dest.writeString(description);
        dest.writeString(imageUrl);
    }
}
