package com.example.sales.picture;

/**
 * Immutable value object representing a downloadable image payload.
 *
 * @param data raw image bytes
 * @param contentType MIME type of the image
 */
public record ImageDownload(byte[] data, String contentType) {
}
