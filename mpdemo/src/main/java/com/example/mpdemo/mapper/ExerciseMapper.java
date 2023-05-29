package com.example.mpdemo.mapper;


import com.example.mpdemo.entity.Exercise;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ExerciseMapper {
    @Select("select * from exercise")
    @Results(
            {
                    @Result(column = "id",property = "id"),
                    @Result(column = "title",property = "title"),
                    @Result(column = "difficulty",property = "difficulty"),
                    @Result(column = "url",property = "url"),
                    @Result(column = "source",property = "source"),
            }
    )
    List<Exercise> selectAllExercise();
}
