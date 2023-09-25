package com.todaysroom.common.file.service;

import com.todaysroom.common.file.dto.UserFileRequestDto;
import com.todaysroom.common.file.entity.FilesLocation;
import com.todaysroom.common.file.entity.UserFiles;
import com.todaysroom.common.file.exception.FailedMakeDirectoryException;
import com.todaysroom.common.file.exception.FailedStoreFileException;
import com.todaysroom.common.file.exception.FileLocationNotFoundException;
import com.todaysroom.common.file.repository.FilesRepository;
import com.todaysroom.common.file.repository.UserFilesRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.text.Normalizer;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {

    private Path imageDirectoryPath;
    private final UserFilesRepository userFilesRepository;
    private final FilesRepository filesRepository;

    @Value("${file.directory-path}")
    private String imageDirectory;

    //생성자(일반)가 호출 되었을 때, bean은 아직 초기화 되지 않음. (예를 들어, 주입된 의존성이 없음)
    //하지만, @PostConstruct를 사용하면, 빈(bean)이 초기화 됨과 동시에 의존성을 확인 가능.
    @PostConstruct
    public void init(){
        this.imageDirectoryPath = Paths.get(imageDirectory);
    }

    @Transactional
    public void saveFiles(UserFileRequestDto userFileDto){
        if(userFileDto.fileList() == null || userFileDto.fileList().isEmpty()){
            return;
        }

        try{
            if(!Files.exists(imageDirectoryPath)){
                Files.createDirectories(imageDirectoryPath);
            }
            // 경로 설정 -> /imageDirectoryPath/postId
            Path subPath = imageDirectoryPath.resolve(Paths.get(userFileDto.file().getFileLocation())).normalize().toAbsolutePath();

            if(!Files.exists(subPath)){
                Files.createDirectories(subPath);
            }

        } catch (IOException e){
            throw new FailedMakeDirectoryException();
        }

        for(MultipartFile file : userFileDto.fileList()){
                saveFile(file, userFileDto);
        }
    }

    @Transactional
    public FilesLocation findByFileLocationId(Long id){
        FilesLocation filesLocation = filesRepository.findById(id).orElseThrow(FileLocationNotFoundException::new);

        return filesLocation;
    }

    @Transactional
    public List<UserFiles> findByPostIdAndFileLocationId(Long postId, Long fileLocationId){
        List<UserFiles> fileList = userFilesRepository.findByPostIdAndFileId(postId, fileLocationId);

        return fileList;
    }

    @Transactional
    public void saveFile(MultipartFile file, UserFileRequestDto userFileDto){
        try{
            String fileName = UUID.randomUUID()+"$$";
            long fileSize = file.getSize();
            String contentType = file.getContentType();

            if(file.isEmpty() || file.getOriginalFilename() == null){
                throw new FailedStoreFileException();
            }

            Path destinationFile = imageDirectoryPath
                    .resolve(Paths.get(userFileDto.file().getFileLocation()))
                    .resolve(Paths.get(fileName))
                    .normalize()
                    .toAbsolutePath();

            try(InputStream inputStream = file.getInputStream()){
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }

            UserFiles uploadFiles = userFileDto.toUserFilesEntity();
            UserFiles userFiles = UserFiles.builder()
                    .file(uploadFiles.getFile())
                    .postId(uploadFiles.getPostId())
                    .originalFilename(Normalizer.normalize(file.getOriginalFilename(), Normalizer.Form.NFC))
                    .fileName(fileName)
                    .filePath(destinationFile.toString())
                    .fileSize(fileSize)
                    .contentType(contentType)
                    .build();

            userFilesRepository.save(userFiles);

        } catch (IOException e){
            e.printStackTrace();
            throw new FailedStoreFileException();
        }
    }
}
