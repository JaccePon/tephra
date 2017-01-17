package org.lpw.tephra.dao.orm.mybatis;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @author lpw
 */
public interface TestMapper {
    @Select("select * from t_tephra_mybatis where id=#{id}")
    TestModel findById(String id);

    @Insert("insert into t_tephra_mybatis values(#{id},#{sort},#{name},#{datecol},#{timecol})")
    int insert(TestModel model);

    @Update("update t_tephra_mybatis set name=#{name},datecol=#{datecol},timecol=#{timecol} where id=#{id}")
    int update(TestModel model);

    @Delete("delete from t_tephra_mybatis where id=#{id}")
    int delete(String id);

    @Select("select * from t_tephra_mybatis order by sort")
    List<TestModel> selectAll();
}
