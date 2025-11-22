package io.sf.utils.crud;

import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.*;

import java.lang.reflect.Field;
import java.util.*;

public class EnhancedDynamicDSL<T> {
    public enum Operator {
        EQ, NE, GT, GTE, LT, LTE, LIKE
    }

    private final Class<T> entityClass;
    private final Map<String, String> params;

    public EnhancedDynamicDSL(Class<T> entityClass, Map<String, String> params) {
        this.entityClass = entityClass;
        this.params = params;
    }

    @SuppressWarnings("unchecked")
    public Specification<T> build() {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            Map<String, Join<?, ?>> joinCache = new HashMap<>();

            for (Map.Entry<String, String> entry : params.entrySet()) {
                String param = entry.getKey();
                String valueStr = entry.getValue();
                if (valueStr == null || valueStr.isEmpty()) continue;

                // 解析字段 + 操作符
                Operator op = Operator.EQ;
                String fullField = param;

                if (param.contains("_")) {
                    int idx = param.lastIndexOf("_");
                    fullField = param.substring(0, idx);
                    String opStr = param.substring(idx + 1).toUpperCase();
                    try {
                        op = Operator.valueOf(opStr);
                    } catch (Exception ignore) {}
                }

                // 支持 user.username、items.productName 等级联字段
                String[] parts = fullField.split("\\.");

                Path<?> path;
                Class<?> fieldType;

                if (parts.length == 1) {
                    // 无 JOIN 的普通字段
                    String fieldName = parts[0];

                    try {
                        Field field = entityClass.getDeclaredField(fieldName);
                        fieldType = field.getType();
                        path = root.get(fieldName);
                    } catch (NoSuchFieldException e) {
                        continue;
                    }

                } else {
                    // 有 JOIN
                    Join<?, ?> join = null;
                    From<?, ?> from = root;
                    Class<?> currentClass = entityClass;

                    String lastField = parts[parts.length - 1];

                    // 遍历中间字段
                    for (int i = 0; i < parts.length - 1; i++) {
                        String joinField = parts[i];

                        // join 缓存，避免重复 join
                        String joinKey = String.join(".", Arrays.copyOfRange(parts, 0, i + 1));
                        if (joinCache.containsKey(joinKey)) {
                            join = joinCache.get(joinKey);
                            from = join;
                            continue;
                        }

                        try {
                            Field f = currentClass.getDeclaredField(joinField);
                            Class<?> targetClass = f.getType();

                            // 如果是 java.util.Collection 代表 hasMany
                            if (Collection.class.isAssignableFrom(targetClass)) {
                                // 需要获取泛型类型
                                Class<?> elementType = getCollectionGenericType(f);
                                join = from.join(joinField, JoinType.LEFT);
                                joinCache.put(joinKey, join);
                                from = join;
                                currentClass = elementType;
                            } else {
                                // hasOne
                                join = from.join(joinField, JoinType.LEFT);
                                joinCache.put(joinKey, join);
                                from = join;
                                currentClass = targetClass;
                            }

                        } catch (Exception e) {
                            join = null;
                            break;
                        }
                    }

                    if (join == null) continue;

                    try {
                        Field last = currentClass.getDeclaredField(lastField);
                        fieldType = last.getType();
                        path = join.get(lastField);
                    } catch (NoSuchFieldException e) {
                        continue;
                    }
                }

                Object value = convertValue(valueStr, fieldType);
                Predicate predicate = buildPredicate(cb, op, path, value);

                if (predicate != null) predicates.add(predicate);
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private Predicate buildPredicate(CriteriaBuilder cb, Operator op, Path<?> path, Object value) {
        switch (op) {
            case EQ: return cb.equal(path, value);
            case NE: return cb.notEqual(path, value);
            case GT: return cb.greaterThan((Path<Comparable>) path, (Comparable) value);
            case GTE: return cb.greaterThanOrEqualTo((Path<Comparable>) path, (Comparable) value);
            case LT: return cb.lessThan((Path<Comparable>) path, (Comparable) value);
            case LTE: return cb.lessThanOrEqualTo((Path<Comparable>) path, (Comparable) value);
            case LIKE: return cb.like(path.as(String.class), "%" + value + "%");
            default: return null;
        }
    }

    private Object convertValue(String v, Class<?> type) {
        if (type == String.class) return v;
        if (type == Integer.class || type == int.class) return Integer.valueOf(v);
        if (type == Long.class || type == long.class) return Long.valueOf(v);
        if (type == Boolean.class || type == boolean.class) return Boolean.valueOf(v);
        if (type == Double.class || type == double.class) return Double.valueOf(v);
        return v;
    }

    private Class<?> getCollectionGenericType(Field field) {
        var genericType = field.getGenericType();
        var param = (java.lang.reflect.ParameterizedType) genericType;
        return (Class<?>) param.getActualTypeArguments()[0];
    }
}
