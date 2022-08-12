package com.example.task_2.service;

import com.example.task_2.dto.DocumentDto;
import com.example.task_2.entity.Document;
import com.example.task_2.mapper.DocumentMapper;
import com.example.task_2.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentParserService documentParserService;
    private final DocumentRepository documentRepository;
    private final DocumentMapper documentMapper;

    public void saveDocumentToDatabase(MultipartFile file) {
        Document document = documentParserService.createDocument(file);
        documentRepository.save(document);
    }

    public List<DocumentDto> getAllDocuments() {
        return documentMapper.convertToDtoList(documentRepository.findAll());
    }
}
