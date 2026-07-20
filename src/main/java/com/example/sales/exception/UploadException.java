package com.example.sales.exception;

public class UploadException extends BaseException{
    public UploadException(){
        super("An error occurred during image upload", ErrorCode.IMAGE_UPLOAD_FAILED);
    }
}
