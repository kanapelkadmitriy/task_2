package com.example.task_2.service;

import com.example.task_2.entity.Document;
import org.springframework.web.multipart.MultipartFile;

public interface DocumentParserService {

    Document createDocument(MultipartFile file);

}
