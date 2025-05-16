package ru.pgk.map.features.field.services;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.pgk.map.common.exceptions.BadRequestException;
import ru.pgk.map.features.border.dto.BorderEntityDto;
import ru.pgk.map.features.border.services.BorderService;
import ru.pgk.map.features.field.entities.FieldEntity;
import ru.pgk.map.features.field.repositories.FieldRepository;
import ru.pgk.map.features.point.entities.PointEntity;
import ru.pgk.map.features.point.repositories.PointRepository;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static ru.pgk.map.common.FileExtensions.getContentType;
import static ru.pgk.map.common.FileExtensions.imagesExtensions;

@Service
@RequiredArgsConstructor
public class FieldResourceServiceImpl implements FieldResourceService {

    private final FieldRepository fieldRepository;
    private final PointRepository pointRepository;
    private final BorderService borderService;

    @Value("${field.dir}")
    private String DIR;

    @Value("${field.metadata_filename}")
    private String METADATA_FILENAME;

    @Value("${field.images_folder_name}")
    private String IMAGES_FOLDER_NAME;

    @Override
    @SneakyThrows
    @Transactional
    public long parseAndSave(String name, LocalDate date, List<BorderEntityDto> borders, MultipartFile file) {
        if (borders.size() < 3) {
            throw new BadRequestException("Список границ должен содержать как минимум 3 элемента");
        }

        if (file.isEmpty()) {
            throw new BadRequestException("Файл не выбран для загрузки");
        }

        if (!file.getOriginalFilename().endsWith(".zip")) {
            throw new BadRequestException("Файл должен быть в формате ZIP");
        }

        String folderId = UUID.randomUUID().toString();
        File uploadDir = new File(DIR + "/" + folderId);
        uploadDir.mkdirs();
//        long usableSpace = uploadDir.getUsableSpace();
//        long fileSize = file.getSize();

//        if (usableSpace < fileSize) {
//            throw new BadRequestException("Недостаточно места на диске для загрузки файла");
//        }

        File destinationFile = null;
        File xlsxFilePoints = null;
        try {
            destinationFile = new File(uploadDir, file.getOriginalFilename());

            file.transferTo(destinationFile);

            unzip(destinationFile, uploadDir);

            xlsxFilePoints = new File(uploadDir.getAbsolutePath() + "/" + METADATA_FILENAME);
            FieldEntity field = createAndSaveField(name, date, folderId);
            borderService.saveAll(field, borders);
            List<PointEntity> points = parsePointsXLSX(xlsxFilePoints, field, borders);
            savePoints(field, points);
            return field.getId();
        } finally {
            if (destinationFile != null && destinationFile.exists()) {
                destinationFile.delete();
            }
            if (xlsxFilePoints != null && xlsxFilePoints.exists()) {
                xlsxFilePoints.delete();
            }
        }
    }

    @Override
    public void deleteByFolderId(String folderId) {
        Path dir = Paths.get(DIR, folderId, IMAGES_FOLDER_NAME);

        if (Files.exists(dir)) {
            try {
                Files.walkFileTree(dir, new SimpleFileVisitor<>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        Files.delete(file);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path directory, IOException exc) throws IOException {
                        Files.delete(directory);
                        return FileVisitResult.CONTINUE;
                    }
                });
            } catch (IOException e) {
                throw new RuntimeException("Не удалось удалить папку: " + dir.toAbsolutePath(), e);
            }
        }
    }

    @Override
    public ResponseEntity<byte[]> getImage(String folderId, String name) {
        Path imagePath = Paths.get(DIR, folderId, IMAGES_FOLDER_NAME, name);

        if (!Files.exists(imagePath)) {
            throw new BadRequestException("Файл не найден: " + imagePath);
        }

        try {
            byte[] imageBytes = Files.readAllBytes(imagePath);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(getContentType(name));
            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            throw new BadRequestException("Ошибка при чтении файла: " + imagePath, e);
        }
    }

    private FieldEntity createAndSaveField(String name, LocalDate date, String folderId) {
        FieldEntity field = new FieldEntity();
        field.setName(name);
        field.setFolderId(folderId);
        field.setDate(date);
        return fieldRepository.save(field);
    }

    @Transactional
    public void savePoints(FieldEntity field, List<PointEntity> points) {
        field.setPoints(points);
        pointRepository.saveAll(points);
    }

    private void unzip(File zipFile, File destDir) throws IOException {
        boolean isXlsxFileIsEmpty = false;

        try (InputStream is = new FileInputStream(zipFile); ZipInputStream zis = new ZipInputStream(is)) {
            ZipEntry entry;
            List<String> imagesExtensions = imagesExtensions();

            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().contains("__MACOSX")) {
                    continue;
                }

                if (entry.getName().endsWith(".xlsx")) {
                    File newXlsxFile = new File(destDir, METADATA_FILENAME);
                    isXlsxFileIsEmpty = true;
                    new File(newXlsxFile.getParent()).mkdirs();
                    Files.copy(zis, newXlsxFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                } else {
                    final ZipEntry finalEntry = entry;
                    if (imagesExtensions.stream().anyMatch(e -> finalEntry.getName().toLowerCase().endsWith(e))) {
                        String entryFileName = Paths.get(entry.getName()).getFileName().toString();
                        File newFile = new File(destDir, IMAGES_FOLDER_NAME + "/" + entryFileName);
                        new File(newFile.getParent()).mkdirs();
                        Files.copy(zis, newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    }
                }

                zis.closeEntry();
            }
        }

        if (!isXlsxFileIsEmpty) {
            deleteDirectory(destDir);
            throw new BadRequestException("ZIP-архив должен содержать XLSX файл");
        }
    }

    private void deleteDirectory(File directory) {
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    deleteDirectory(file);
                }
            }
        }
        directory.delete();
    }

    @SneakyThrows
    private List<PointEntity> parsePointsXLSX(File xlsxFile, FieldEntity field, List<BorderEntityDto> borders) {
        List<PointEntity> points = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(xlsxFile);
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                try {
                    System.out.println(row.getRowNum());
                    if (row.getRowNum() == 0) {
                        continue;
                    }
                    double latitude = row.getCell(0).getNumericCellValue();
                    System.out.println(latitude);
                    double longitude = row.getCell(1).getNumericCellValue();
                    System.out.println(longitude);
                    if (!isPointInPolygon(latitude, longitude, borders)) {
                        continue;
                    }

                    double speedGPS = row.getCell(2).getNumericCellValue();
                    double rotateGPS = row.getCell(3).getNumericCellValue();
                    int date = (int) row.getCell(4).getNumericCellValue();
                    int time = (int) row.getCell(5).getNumericCellValue();
                    double altitude = row.getCell(6).getNumericCellValue();
                    double roll = row.getCell(7).getNumericCellValue();
                    double pitch = row.getCell(8).getNumericCellValue();
                    int rotate = (int) row.getCell(9).getNumericCellValue();
                    double vRef = row.getCell(10).getNumericCellValue();
                    int timeFly = (int) row.getCell(11).getNumericCellValue();
                    double altitudePVD = row.getCell(12).getNumericCellValue();
                    double speedPVD = row.getCell(13).getNumericCellValue();
                    int numFoto1 = (int) row.getCell(14).getNumericCellValue();
                    String name = row.getCell(15).getStringCellValue();

                    PointEntity point = PointEntity.builder()
                            .latitude(latitude)
                            .longitude(longitude)
                            .speedGPS(speedGPS)
                            .altitude(altitude)
                            .rotateGPS(rotateGPS)
                            .roll(roll)
                            .pitch(pitch)
                            .rotate(rotate)
                            .vRef(vRef)
                            .timeFly(timeFly)
                            .altitudePVD(altitudePVD)
                            .speedPVD(speedPVD)
                            .numFoto1(numFoto1)
                            .name(name)
                            .dateTime(convertToLocalDateTime(date, time))
                            .field(field)
                            .build();

                    points.add(point);
                }catch (Exception ignore) {
                    System.out.println("Failed");
                }
            }
        }

        return points;
    }

    private LocalDateTime convertToLocalDateTime(int dateValue, int timeValue) {
        var localDate = LocalDate.of(1900, 1, 1).plusDays(dateValue - 2);

        int hours = timeValue / 10000;
        int minutes = (timeValue % 10000) / 100;
        int seconds = timeValue % 100;

        var localTime = LocalTime.of(hours, minutes, seconds);

        return LocalDateTime.of(localDate, localTime);
    }

    // Метод для проверки, входит ли точка в многоугольник (Ray Casting)
    private boolean isPointInPolygon(double latitude, double longitude, List<BorderEntityDto> borders) {
        int n = borders.size();
        boolean inside = false;

        for (int i = 0, j = n - 1; i < n; j = i++) {
            double lat1 = borders.get(i).latitude();
            double lon1 = borders.get(i).longitude();
            double lat2 = borders.get(j).latitude();
            double lon2 = borders.get(j).longitude();

            if (((lon1 > longitude) != (lon2 > longitude)) &&
                    (latitude < (lat2 - lat1) * (longitude - lon1) / (lon2 - lon1) + lat1)) {
                inside = !inside;
            }
        }

        return inside;
    }
}
