package com.qtztlink.ejile.system.mapper.communication;

import com.qtztlink.ejile.common.utils.DateUtil;
import com.qtztlink.ejile.system.model.communication.Contact;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ContactMapper implements RowMapper<Contact> {
    @Override
    public Contact mapRow(ResultSet resultSet, int i) throws SQLException {
        Contact contact = new Contact();
        contact.setCid(resultSet.getInt("cid"));
        contact.setSid(resultSet.getInt("sid"));
        contact.setTime(DateUtil.formatTimeStamp(resultSet.getTimestamp("ctime")));
        contact.setContent(resultSet.getString("content"));
        contact.setState(resultSet.getString("state"));
        contact.setCname(resultSet.getString("cname"));
        contact.setSname(resultSet.getString("sname"));
//        contact.setIsRead(resultSet.getInt("isRead"));
        return contact;
    }
}
