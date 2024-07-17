package com.rocky.coordinatorservice.mapper;

import com.rocky.coordinatorservice.model.Session;
import com.rocky.coordinatorservice.model.User;
import org.apache.ibatis.annotations.*;
import org.springframework.web.bind.annotation.RequestMapping;

@Mapper
public interface UserSessionMapper {

    @Select("select * from user where name = #{name}")
    public User findUserByName(String name);

    @Select("select * from user where id = #{id}")
    public User getUserById(@Param("id") Integer id);

    @Insert("INSERT INTO user(name, password) VALUES (#{name}, #{password})")
    public void saveUser(User user);

    @Insert("INSERT INTO session(name, user_id) VALUES (#{name}, ${userId})")
    public void saveSession(Session session);

    @Select("select u.id as uid, u.name as uname, s.id as sid, s.name as sname " +
            "from user u, session s where s.user_id = u.id and u.id = #{userId}")
    @Results({
            @Result(column = "uid", property = "id"),
            @Result(column = "uname", property = "name"),
            @Result(property = "sessions", many = @Many(resultMap = "sessionMap", columnPrefix = "s"))
    })
    public User findUserSession(@Param("userId") Integer userId);

    @Select("select id, name from session where id = #{id}")
    @Results(id = "sessionMap", value = {
            @Result(column = "id", property = "id"),
            @Result(column = "name", property = "name")
    })
    public Session getSessionById(@Param("id") Integer id);

    @Select("select * from session where name = #{sessionName} and user_id = #{userId}")
    public Session getSessionByName(@Param("sessionName") String sessionName, @Param("userId") Integer userId);
}
