package com.adda.service;

import com.adda.domain.PhotoEntity;
import com.adda.service.photoService.UploadClient;
import com.adda.service.photoService.parameters.ExpirationTime;
import com.adda.service.photoService.parameters.UploadParameters;
import com.adda.service.photoService.responses.OptionalResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
public class PhotoService {
    private static final String API_KEY = "3bcf090f1603553d4218e2cea8b30549";

    public static PhotoEntity uploadPhotoToAdvertisement(List<MultipartFile> fileList) throws IOException {
        // check if the current user has the advertisement

        String[] arrayOfPath = new String[8];
        String[] fileNames = new String[8];

        String uniqueFileName = null;
        String[] imagesBase64 = new String[8];
        for (int i = 0; i < fileList.size(); i++) {
            uniqueFileName = UUID.randomUUID() + fileList.get(i).getOriginalFilename();
            fileNames[i] = uniqueFileName;

            imagesBase64[i] = Base64.getEncoder().encodeToString(fileList.get(i).getBytes());
        }

        PhotoEntity photoEntity = new PhotoEntity();
        OptionalResponse[] optionalResponse = PhotoService.uploadPhotoToServer(imagesBase64, fileNames);
        for (int i = 0; i < optionalResponse.length; i++) {
            if (optionalResponse[i] != null) {
                arrayOfPath[i] = optionalResponse[i].get().getResponseData().getImageUrl();
            }
        }

        photoEntity.setPhotos(arrayOfPath);
        return photoEntity;
    }

    public static OptionalResponse[] uploadPhotoToServer(String[] imagesInBase64, String[] fileNames) {
        OptionalResponse[] uploadedImages = new OptionalResponse[imagesInBase64.length];
        for (int i = 0; i < imagesInBase64.length; i++) {
            if (imagesInBase64[i] != null) {
                UploadParameters uploadParameters = new UploadParameters(API_KEY, imagesInBase64[i], fileNames[i], ExpirationTime.fromLong(5530000));
                uploadedImages[i] = UploadClient.upload(uploadParameters);
            }
        }
        return uploadedImages;
    }

    public static String uploadPhotoOfQRcodeToAdvertisement(String qrCodeInBase64, String url) {

        OptionalResponse optionalResponse = PhotoService.uploadPhotoOfQRcodeToServer(qrCodeInBase64, url);
        System.out.println(optionalResponse.get().getResponseData().getImageUrl());
        return optionalResponse.get().getResponseData().getImageUrl();
    }
    public static OptionalResponse uploadPhotoOfQRcodeToServer(String qrCodeInBase64, String fileNames) {
        UploadParameters uploadParameters = new UploadParameters(API_KEY, qrCodeInBase64, fileNames, ExpirationTime.fromLong(5530000));
        return UploadClient.upload(uploadParameters);
    }
}
