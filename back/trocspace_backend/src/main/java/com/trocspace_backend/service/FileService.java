package com.trocspace_backend.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileService {

    private static final String FILE_STORAGE_PATH = System.getProperty("user.dir") + "/src/main/resources/static/uploads";


    public File getFile(String imageName) throws IOException {
        // Construire le chemin complet du fichier avec un séparateur compatible OS
        String filePath = FILE_STORAGE_PATH + File.separator + imageName;
        File file = new File(filePath);

        if (!file.exists()) {
            throw new IOException("L'image " + imageName + " n'a pas été trouvée à l'adresse " + file.getAbsolutePath());
        }

        return file;
    }

    public String saveFile(MultipartFile file) throws IOException {
        String uniqueFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        String filePath = FILE_STORAGE_PATH + File.separator + uniqueFileName;
        File destinationFile = new File(filePath);

        // Créez le répertoire si nécessaire
        File directory = new File(FILE_STORAGE_PATH);
        if (!directory.exists() && !directory.mkdirs()) {
            throw new IOException("Impossible de créer le répertoire de stockage : " + FILE_STORAGE_PATH);
        }

        file.transferTo(destinationFile);

        // Retourne le chemin relatif pour l'enregistrement en base de données
        return uniqueFileName;
    }


    public boolean isValidImage(MultipartFile file) {
        try {
            BufferedImage image = ImageIO.read(file.getInputStream());
            return image != null; // Si l'image est invalide, elle retourne null
        } catch (IOException e) {
            return false; // Si une exception se produit, considérer que le fichier est invalide
        }
    }

    public void deleteFile(String filePath) throws IOException {
        // Retirer le préfixe "/uploads/" du filePath si nécessaire
        if (filePath.startsWith("/uploads/")) {
            filePath = filePath.substring(9); // Enlever "/uploads/"
        }

        // Utiliser le bon séparateur pour construire le chemin final
        File file = new File(FILE_STORAGE_PATH + File.separator + filePath);

        if (!file.exists()) {
            throw new IOException("Erreur : Le fichier n'existe pas à l'emplacement : " + file.getPath());
        }

        if (!file.delete()) {
            throw new IOException("Erreur : Échec de la suppression du fichier : " + file.getPath());
        }
    }


}
