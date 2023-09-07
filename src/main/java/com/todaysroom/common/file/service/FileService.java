package com.todaysroom.common.file.service;

import com.todaysroom.common.file.repository.UserFilesRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FileService {

    private Path imageDirectoryPath;
    private final UserFilesRepository userFilesRepository;

    @Value("${file-storage-directory}")
    private String imageDirectory;

    //생성자(일반)가 호출 되었을 때, bean은 아직 초기화 되지 않음. (예를 들어, 주입된 의존성이 없음)
    //하지만, @PostConstruct를 사용하면, 빈(bean)이 초기화 됨과 동시에 의존성을 확인 가능.
    @PostConstruct
    public void init(){
        this.imageDirectoryPath = Paths.get(imageDirectory);
    }

    @Transactional
    public void saveFiles(List<MultipartFile> files, Long postId){
        if(files == null || files.isEmpty()){
            return;
        }

        try{
            if(!Files.exists(imageDirectoryPath)){
                Files.createDirectories(imageDirectoryPath);
            }
        } catch (IOException e){

        }

    }
}
