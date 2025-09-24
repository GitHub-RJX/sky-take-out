package com.sky.mapper;

import com.sky.entity.AddressBook;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AddressBookMapper {

    /**
     * 条件查询
     *
     * @param addressBook 地址信息查询请求数据
     * @return 地址信息列表
     */
    List<AddressBook> list(AddressBook addressBook);

    /**
     * 新增
     *
     * @param addressBook 地址信息新增请求数据
     */
    void insert(AddressBook addressBook);

    /**
     * 根据id查询
     *
     * @param id 地址ID
     * @return 地址信息
     */
    @Select("select * from address_book where id = #{id}")
    AddressBook getById(Long id);

    /**
     * 根据地址ID修改
     *
     * @param addressBook 地址信息修改请求数据
     */
    void update(AddressBook addressBook);

    /**
     * 根据用户ID修改是否默认地址
     *
     * @param addressBook 默认地址信息修改请求数据
     */
    @Update("update address_book set is_default = #{isDefault} where user_id = #{userId}")
    void updateIsDefaultByUserId(AddressBook addressBook);

    /**
     * 根据id删除地址
     *
     * @param id 地址ID
     */
    @Delete("delete from address_book where id = #{id}")
    void deleteById(Long id);

}
