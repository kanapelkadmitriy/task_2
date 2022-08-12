package com.example.task_2.mapper;

import java.util.List;

public interface EntityMapper<Model, Dto> {

    Dto convertToDto(Model model);

    Model convertToModel(Dto dto);

    List<Dto> convertToDtoList(List<Model> modelList);

    List<Model> convertToModelList(List<Dto> dtoList);
}
