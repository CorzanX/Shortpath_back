package com.example.mpdemo.mapper;

import com.example.mpdemo.entity.Literature;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface LiteratureMapper {
    @Select("select * from literature")
    @Results(
            {
                    @Result(column = "id",property = "id"),
                    @Result(column = "title",property = "title"),
                    @Result(column = "type",property = "type"),
                    @Result(column = "url",property = "url"),
                    @Result(column = "source",property = "source"),
            }
    )
    List<Literature> selectAllLiterature();
}
