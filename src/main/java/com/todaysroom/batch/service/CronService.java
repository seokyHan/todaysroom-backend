package com.todaysroom.batch.service;

import com.todaysroom.file.entity.UserFiles;
import com.todaysroom.file.repository.UserFilesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class CronService {

    private final UserFilesRepository userFilesRepository;

    @Value("${file.directory-path}")
    private String imageDirectory;

    // @Scheduled(cron = "0 * * * * *") 1 분마다 실행
    @Scheduled(cron = "0 0 18 * * *") // 매일 오후 18시에 실행
    public void fileDeleteScheduled() {
        List<UserFiles> fileList = userFilesRepository.findAll();

        try{
            // 해당 경로 하위 폴더 탐색 후 디렉토리가 아닌 일반파일인지 체크
            List<Path> filePath = Files.walk(Paths.get(imageDirectory))
                    .filter(Files::isRegularFile)
                    .collect(Collectors.toList());

            for(Path path : filePath){
                String fileName = path.getFileName().toString();
                if(!isFilePresent(fileName, fileList)){
                    Files.delete(path);
                }
            }
        }catch (IOException e){
            log.info(e.getMessage());
        }
    }

    private boolean isFilePresent(String fileName, List<UserFiles> fileList){
        for(UserFiles userFile : fileList){
            if(userFile.getFileName().equals(fileName)){
                return true;
            }
        }

        return false;
    }
}
