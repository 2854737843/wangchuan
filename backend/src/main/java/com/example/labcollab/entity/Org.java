package com.example.labcollab.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("org")
public class Org {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private Long createdBy;
    private LocalDateTime createdAt;
}
