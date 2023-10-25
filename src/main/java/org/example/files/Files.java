package org.example.files;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Base64;

public class Files {

    public String encodeFile(String fileName) throws Exception {
        File image = new File(fileName);
        InputStream is = new FileInputStream(image);
        byte[] byteArray = is.readAllBytes();
        String base64String = Base64.getEncoder().encodeToString(byteArray);

        System.out.println("image in base64 format ===>> " + base64String);
        return base64String;
    }

    public void decodeFile(String newFileName, String base64String) throws Exception {
        byte[] byteArrayDecode = Base64.getDecoder().decode(base64String);
        try (FileOutputStream fos = new FileOutputStream(newFileName)) {
            fos.write(byteArrayDecode);
        }
    }
}
