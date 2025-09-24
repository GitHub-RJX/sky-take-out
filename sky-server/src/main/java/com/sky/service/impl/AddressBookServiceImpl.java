package com.sky.service.impl;

import com.sky.constant.BooleanConstant;
import com.sky.context.BaseContext;
import com.sky.entity.AddressBook;
import com.sky.mapper.AddressBookMapper;
import com.sky.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class AddressBookServiceImpl implements AddressBookService {
    @Autowired
    private AddressBookMapper addressBookMapper;

    /**
     * 条件查询
     *
     * @param addressBook 地址信息查询请求数据
     * @return 地址信息列表
     */
    public List<AddressBook> list(AddressBook addressBook) {
        return addressBookMapper.list(addressBook);
    }

    /**
     * 新增地址
     *
     * @param addressBook 地址信息新增请求数据
     */
    public void save(AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBook.setIsDefault(BooleanConstant.FALSE);
        addressBookMapper.insert(addressBook);
    }

    /**
     * 根据id查询
     *
     * @param id 地址ID
     * @return 地址信息
     */
    public AddressBook getById(Long id) {
        return addressBookMapper.getById(id);
    }

    /**
     * 根据id修改地址
     *
     * @param addressBook 地址信息修改请求数据
     */
    public void update(AddressBook addressBook) {
        addressBookMapper.update(addressBook);
    }

    /**
     * 设置默认地址
     *
     * @param addressBook 默认地址设置请求数据
     */
    @Transactional
    public void setDefault(AddressBook addressBook) {
        //先将当前用户的所有地址修改为非默认地址
        addressBook.setIsDefault(BooleanConstant.FALSE);
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBookMapper.updateIsDefaultByUserId(addressBook);
        //再将当前地址改为默认地址
        addressBook.setIsDefault(BooleanConstant.TRUE);
        addressBookMapper.update(addressBook);
    }

    /**
     * 根据id删除地址
     *
     * @param id 地址ID
     */
    public void deleteById(Long id) {
        addressBookMapper.deleteById(id);
    }

}
