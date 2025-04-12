package com.example.jadebook.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.jadebook.entity.Post;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PostMapper extends BaseMapper<Post> {
}