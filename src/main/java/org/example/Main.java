package org.example;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import javax.imageio.ImageIO;
public class Main {

    public static void main(String[] args) throws IOException {
        BufferedImage image = getDuckImage();

        if (image == null) return;

        String[][] imageString = new String[10000][10000];

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                final int clr = image.getRGB(x, y);
                final int red = (clr & 0x00ff0000) >> 16;
                final int green = (clr & 0x0000ff00) >> 8;
                final int blue = clr & 0x000000ff;

                imageString[x][y] = "\033[38;2;" + red + ";" + green + ";" + blue + "m" + "#";

                System.out.print(imageString[x][y]);
            }
            System.out.println();
        }
    }

    static public BufferedImage getDuckImage() {
        String url = "https://random-d.uk/api/v2/random";

        // Create an HTTP client object, so we can send a request
        HttpClient client = HttpClient.newHttpClient();

        // Build an HTTP request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        try {
            // Send the request to the API, and get a response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                // here we create a JsonObject, which we'll parse through in the main()
                JsonObject jsonResponse = JsonParser.parseString(response.body()).getAsJsonObject();
                return ImageIO.read(new URL(jsonResponse.get("url").getAsString()));
            } else {
                return null;
            }

        } catch (IOException | InterruptedException e) {
            return null;
        }
    }
}