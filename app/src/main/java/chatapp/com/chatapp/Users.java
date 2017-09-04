package chatapp.com.chatapp;

/**
 * Created by bhuvanes on 26/8/17.
 */

public class Users {
    public String name;
    public String status;
    public String image;
    public String thumb;
    public String device_token;


    public Users() {

    }

    public Users(String name, String status, String image, String thumb, String device_token) {
        this.name = name;
        this.status = status;
        this.image = image;
        this.thumb = thumb;
        this.device_token = device_token;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getDevice_token() {
        return device_token;
    }

    public void setDevice_token(String device_token) {
        this.device_token = device_token;
    }
}
