/**
 * Copyright 2013-2014 Guoqiang Chen, Shanghai, China. All rights reserved.
 *
 *   Author: Guoqiang Chen
 *    Email: subchen@gmail.com
 *   WebURL: https://github.com/subchen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jetbrick.dao.orm.mapper;

import java.sql.*;
import jetbrick.bean.KlassInfo;
import jetbrick.bean.PropertyInfo;
import jetbrick.dao.orm.RowMapper;
import jetbrick.typecast.TypeCastUtils;
import jetbrick.util.IdentifiedNameUtils;

public class BeanRowMapper<T> implements RowMapper<T> {
    private KlassInfo klass;

    public BeanRowMapper(Class<T> beanClass) {
        this.klass = KlassInfo.create(beanClass);
    }

    @Override
    public T handle(ResultSet rs) throws SQLException {
        try {
            @SuppressWarnings("unchecked")
            T bean = (T) klass.newInstance();

            ResultSetMetaData rsmd = rs.getMetaData();
            int cols = rsmd.getColumnCount();
            for (int i = 1; i <= cols; i++) {
                String columnName = rsmd.getColumnLabel(i);
                if (columnName == null || columnName.length() == 0) {
                    columnName = rsmd.getColumnName(i);
                }

                String propertyName = IdentifiedNameUtils.toCamelCase(columnName);
                Object value = rs.getObject(i);

                PropertyInfo property = klass.getProperty(propertyName);
                value = TypeCastUtils.convert(value, property.getType());
                property.set(bean, value);
            }
            return bean;

        } catch (Exception e) {
            throw new SQLException("Can't set bean property.", e);
        }
    }

}
