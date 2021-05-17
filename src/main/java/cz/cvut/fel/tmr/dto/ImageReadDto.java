package cz.cvut.fel.tmr.dto;

import cz.cvut.fel.tmr.model.Image;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ImageReadDto implements Serializable {
    private String url;
    private String description;

    public ImageReadDto(Image image, String imagePath) {
        this.url = imagePath + "/" + image.getFileName();
        this.description = image.getDescription();
    }
}
