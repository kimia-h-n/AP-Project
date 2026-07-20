package com.example.sales.exception;

import com.example.sales.picture.ImageData;

public class ImageNotFoundException extends BaseException{
    public ImageNotFoundException(){
        super("Image wasn't found", ErrorCode.IMAGE_NOT_FOUND);
    }
}
