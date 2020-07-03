package ru.psu.pro_it_test.repositories.conditions;

import org.jooq.Condition;
import org.jooq.Field;

public abstract class AbstractConditionFactory {

    Condition filterByName(Field<?> nameField, String name, boolean startsWith) {
        String nameValue = startsWith ? name + "%" : name;
        return nameField.likeIgnoreCase(nameValue);
    }

    Condition filterByParent(Field<?> parentField) {
        return parentField.isNull();
    }

    Condition filterByParent(Field<Long> parentField, long parentId) {
        return parentField.eq(parentId);
    }
}
