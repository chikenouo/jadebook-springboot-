package com.example.jadebook.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.jadebook.entity.Comments;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommentMapper extends BaseMapper<Comments> {
}