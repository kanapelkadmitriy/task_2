package com.example.task_2.mapper;

import com.example.task_2.dto.DocumentDto;
import com.example.task_2.entity.Document;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DocumentMapper implements EntityMapper<Document, DocumentDto> {
    @Override
    public DocumentDto convertToDto(Document document) {
        return DocumentDto.builder()
                .documentName(document.getDocumentName())
                .startDate(document.getStartDate())
                .endDate(document.getEndDate())
                .build();
    }

    @Override
    public Document convertToModel(DocumentDto documentDto) {
        return Document.builder()
                .documentName(documentDto.getDocumentName())
                .startDate(documentDto.getStartDate())
                .endDate(documentDto.getEndDate())
                .build();
    }

    @Override
    public List<DocumentDto> convertToDtoList(List<Document> documents) {
        return documents.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<Document> convertToModelList(List<DocumentDto> documentDtos) {
        return documentDtos.stream()
                .map(this::convertToModel)
                .collect(Collectors.toList());
    }
}
