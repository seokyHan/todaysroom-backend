package com.todaysroom.common.file.service;

import com.todaysroom.common.file.dto.UserFileDto;
import com.todaysroom.common.file.entity.UserFiles;
import com.todaysroom.common.file.exception.FailedMakeDirectoryException;
import com.todaysroom.common.file.exception.FailedStoreFileException;
import com.todaysroom.common.file.repository.UserFilesRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {

    private Path imageDirectoryPath;
    private final UserFilesRepository userFilesRepository;

    @Value("${file.directory-path}")
    private String imageDirectory;

    //생성자(일반)가 호출 되었을 때, bean은 아직 초기화 되지 않음. (예를 들어, 주입된 의존성이 없음)
    //하지만, @PostConstruct를 사용하면, 빈(bean)이 초기화 됨과 동시에 의존성을 확인 가능.
    @PostConstruct
    public void init(){
        this.imageDirectoryPath = Paths.get(imageDirectory);
    }

    @Transactional
    public void saveFiles(UserFileDto userFileDto){
        if(userFileDto.files() == null || userFileDto.files().isEmpty()){
            return;
        }

        try{
            if(!Files.exists(imageDirectoryPath)){
                Files.createDirectories(imageDirectoryPath);
            }
            // 경로 설정 -> /imageDirectoryPath/postId
            Path subPath = imageDirectoryPath.resolve(Paths.get(String.valueOf(userFileDto.postId()))).normalize().toAbsolutePath();

            if(!Files.exists(subPath)){
                Files.createDirectories(subPath);
            }

        } catch (IOException e){
            throw new FailedMakeDirectoryException();
        }

        for(MultipartFile file : userFileDto.files()){
            saveFile(file, userFileDto);
        }
    }

    @Transactional
    public void saveFile(MultipartFile file, UserFileDto userFileDto){
        try{
            String originFileName = file.getOriginalFilename();
            long fileSize = file.getSize();
            String contentType = file.getContentType();

            if(file.isEmpty() || originFileName == null){
                throw new FailedStoreFileException();
            }

            Path destinationFile = imageDirectoryPath
                    .resolve(Paths.get(String.valueOf(userFileDto.postId())))
                    .resolve(Paths.get(originFileName))
                    .normalize()
                    .toAbsolutePath();

            try(InputStream inputStream = file.getInputStream()){
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }

            UserFiles uploadFiles = userFileDto.toUserFilesEntity();
            UserFiles userFiles = UserFiles.builder()
                    .file(uploadFiles.getFile())
                    .postId(uploadFiles.getPostId())
                    .originalFilename(uploadFiles.getOriginalFilename())
                    .fileName(UUID.randomUUID().toString())
                    .filePath(destinationFile.toString())
                    .fileSize(fileSize)
                    .contentType(contentType)
                    .build();

            userFilesRepository.save(userFiles);

        } catch (IOException e){
            throw new FailedStoreFileException();
        }

    }
}
