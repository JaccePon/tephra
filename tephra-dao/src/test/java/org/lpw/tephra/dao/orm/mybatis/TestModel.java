package org.lpw.tephra.dao.orm.mybatis;

import org.lpw.tephra.dao.model.ModelSupport;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * @author lpw
 */
@Component("tephra.dao.orm.mybatis.model")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class TestModel extends ModelSupport {
    private int sort;
    private String name;
    private Date datecol;
    private Timestamp timecol;

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDatecol() {
        return datecol;
    }

    public void setDatecol(Date datecol) {
        this.datecol = datecol;
    }

    public Timestamp getTimecol() {
        return timecol;
    }

    public void setTimecol(Timestamp timecol) {
        this.timecol = timecol;
    }
}
