package com.color.FileProcessing.model;

import lombok.Data;

import java.util.List;

@Data
public class ResourceListModel {

    private Integer total;

    private List<ResourceModel> resourceModelList;
}
