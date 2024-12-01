package ru.pgk.map.common;

import org.springframework.http.MediaType;

import java.util.List;

public class FileExtensions {

    public static List<String> imagesExtensions() {
        return List.of(".jpg", ".jpeg", ".png", ".gif", ".heic");
    }

    public static MediaType getContentType(String fileName) {
        String extension = getFileExtension(fileName).toLowerCase();

        return switch (extension) {
            case ".jpg", ".jpeg" -> MediaType.IMAGE_JPEG;
            case ".png" -> MediaType.IMAGE_PNG;
            case ".gif" -> MediaType.IMAGE_GIF;
            case ".heic" ->
                    MediaType.valueOf("image/heic");
            default ->
                    MediaType.APPLICATION_OCTET_STREAM;
        };
    }

    public static String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        return (lastDotIndex == -1) ? "" : fileName.substring(lastDotIndex);
    }
}
