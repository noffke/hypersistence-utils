package io.hypersistence.utils.hibernate.type.array.internal;

import org.hibernate.type.descriptor.WrapperOptions;

import java.util.Properties;

/**
 * @author Nazir El-Kayssi
 * @author Vlad Mihalcea
 */
public class EnumArrayTypeDescriptor
        extends AbstractArrayTypeDescriptor<Enum[]> {

    private String sqlArrayType;

    public EnumArrayTypeDescriptor() {
        super(Enum[].class);
    }

    public EnumArrayTypeDescriptor(Class enumClass) {
        super(enumClass);
    }

    @Override
    protected String getSqlArrayType() {
        return sqlArrayType;
    }

    @Override
    public void setParameterValues(Properties parameters) {
        sqlArrayType = parameters.getProperty(AbstractArrayType.SQL_ARRAY_TYPE);
        super.setParameterValues(parameters);
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public <X> X unwrap(Enum[] value, Class<X> type, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        String[] names = new String[value.length];
        for (int i = 0; i < value.length; i++) {
            names[i] = value[i] == null ? null : value[i].name();
        }
        return (X) names;
    }
}
