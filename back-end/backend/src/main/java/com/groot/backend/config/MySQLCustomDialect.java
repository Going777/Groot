package com.groot.backend.config;

import org.hibernate.dialect.MySQL8Dialect;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.type.IntegerType;

public class MySQLCustomDialect extends MySQL8Dialect {

    public MySQLCustomDialect() {
        super();
        registerFunction("bitand", new SQLFunctionTemplate(IntegerType.INSTANCE, "(?1 &?2)"));
    }
}
