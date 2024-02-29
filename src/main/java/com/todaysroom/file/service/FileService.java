package com.todaysroom.file.service;

import com.todaysroom.file.dto.UserFileRequestDto;
import com.todaysroom.file.entity.FilesLocation;
import com.todaysroom.file.entity.UserFiles;
import com.todaysroom.file.repository.FilesRepository;
import com.todaysroom.file.repository.UserFilesRepository;
import com.todaysroom.global.exception.CustomException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
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
import java.util.stream.Collectors;

import static com.todaysroom.global.exception.code.AuthResponseCode.USER_NOT_FOUND;
import static com.todaysroom.global.exception.code.CommonResponseCode.*;

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
            throw new CustomException(DIRECTORY_MAKE_ERROR, "디렉토리 생성 실패");
        }

        for(MultipartFile file : userFileDto.fileList()){
                saveFile(file, userFileDto);
        }
    }

    @Transactional
    public void saveFile(MultipartFile file, UserFileRequestDto userFileDto){
        try{
            String fileName = UUID.randomUUID()+"$$";
            long fileSize = file.getSize();
            String contentType = file.getContentType();

            if(file.isEmpty() || file.getOriginalFilename() == null){
                throw new CustomException(FILE_NOT_FOUND, "저장하려는 파일을 찾을 수 없습니다.");
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
            throw new CustomException(FILE_NOT_FOUND, "파일 저장에 실패 했습니다.");
        }
    }

    @Transactional
    public <T> void deleteByFileId(List<T> entities) {
        if (entities != null && !entities.isEmpty()) {
            for (T entity : entities) {
                if (entity instanceof UserFiles) {
                    userFilesRepository.deleteById(((UserFiles) entity).getId());
                } else if (entity instanceof Long) {
                    userFilesRepository.deleteById(((Long) entity));
                }
            }
        }
    }

    @Transactional
    public FilesLocation findByFileLocationId(Long id){
        FilesLocation filesLocation = filesRepository.findById(id).orElseThrow(() -> new CustomException(FILE_LOCATION_NOT_FOUND, "저장하려는 파일의 서비스를 찾을 수 없습니다."));

        return filesLocation;
    }

    @Transactional
    public List<UserFiles> findByPostIdAndFileLocationId(Long postId, Long fileLocationId){
        List<UserFiles> fileList = userFilesRepository.findByPostIdAndFileId(postId, fileLocationId);

        return fileList;
    }

}
