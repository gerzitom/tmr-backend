package cz.cvut.fel.tmr.service;

import cz.cvut.fel.tmr.dao.ImageDao;
import cz.cvut.fel.tmr.model.Image;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

import static cz.cvut.fel.tmr.config.ConfigConstants.AVATARS_FULL_PATH;

@Service
public class ImageService {
    private final ImageDao dao;


    @Autowired
    public ImageService(ImageDao dao) {
        this.dao = dao;
    }

    @Transactional
    public Image storeImage(MultipartFile file) throws IOException {
        Objects.requireNonNull(file.getOriginalFilename());
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        Path uploadPath = Paths.get(AVATARS_FULL_PATH);
        if(!Files.exists(uploadPath )){
            Files.createDirectories(uploadPath);
        }

        InputStream inputStream = file.getInputStream();
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);

        Image image = new Image(fileName, "");
        dao.persist(image);
        return image;
    }

    @Transactional
    public byte[] getUserAvatar(String imageName){
        Path path = Paths.get(AVATARS_FULL_PATH + "/" + imageName);
        return getImage(path);

    }

    private byte[] getImage(Path path){
        byte[] image = new byte[0];
        try {
            image = Files.readAllBytes(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }
}
