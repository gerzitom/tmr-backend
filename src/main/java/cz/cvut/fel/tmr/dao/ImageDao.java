package cz.cvut.fel.tmr.dao;

import cz.cvut.fel.tmr.model.Image;
import org.springframework.stereotype.Repository;

@Repository
public class ImageDao extends BaseDao<Image> {
    public ImageDao() {
        super(Image.class);
    }
}
