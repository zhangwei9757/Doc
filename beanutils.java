public class BeanUtils extends org.springframework.beans.BeanUtils {


    /**
     * 把map对象 转换为 javabean
     *
     * @param map
     * @param beantype
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T> T mapToBean(Map<String, Object> map, Class<T> beantype) throws Exception {
        T object = beantype.newInstance();
        BeanInfo beaninfo = Introspector.getBeanInfo(beantype, Object.class);
        PropertyDescriptor[] pro = beaninfo.getPropertyDescriptors();
        for (PropertyDescriptor property : pro) {
            String name = property.getName();
            Object value = map.get(name);
            Method set = property.getWriteMethod();
            set.invoke(object, value);
        }
        return object;
    }

    /**
     * 将javabean 转换为 map
     *
     * @param bean
     * @return
     * @throws Exception
     */
    public static Map<String, Object> beanToMap(Object bean) throws Exception {
        BeanInfo beaninfo = Introspector.getBeanInfo(bean.getClass(), Object.class);
        PropertyDescriptor[] pro = beaninfo.getPropertyDescriptors();
        Map<String, Object> map = new HashMap<>(pro.length);
        for (PropertyDescriptor property : pro) {
            String key = property.getName();
            Method get = property.getReadMethod();
            Object value = get.invoke(bean);
            if (null != value && StringUtils.isNotBlank(value.toString().trim())) {
                map.put(key, value);
            }
        }
        return map;
    }

    /**
     * DTO 属性全为空
     *
     * @param clazz
     * @param object
     * @return
     * @throws Exception
     */
    public static boolean validateFullNull(Class clazz, Object object, Object... exclude) throws Exception {
        int var1 = 0;
        int var2 = 0;
        BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        for (PropertyDescriptor property : propertyDescriptors) {
            String key = property.getName();
            if (null != exclude && exclude.length > 0 && ArrayUtils.contains(exclude, key)) {
                continue;
            }
            if (!StringUtils.contains(key, "class")) {
                Method readMethod = property.getReadMethod();
                Object invoke = readMethod.invoke(object);
                ++var1;
                if (Objects.isNull(invoke) || Objects.equals(invoke, StringUtils.EMPTY) || StringUtils.isBlank(invoke.toString().trim())) {
                    ++var2;
                }
            }
        }
        return var1 == var2;
    }

    /**
     * DTO 存在属性为空
     *
     * @param clazz
     * @param object
     * @return
     * @throws Exception
     */
    public static boolean validateExistsNull(Class clazz, Object object, Object... exclude) throws Exception {
        int var1 = 0;
        BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        for (PropertyDescriptor property : propertyDescriptors) {
            String key = property.getName();
            if (null != exclude && exclude.length > 0 && ArrayUtils.contains(exclude, key)) {
                continue;
            }
            if (!StringUtils.contains(key, "class")) {
                Method readMethod = property.getReadMethod();
                Object invoke = readMethod.invoke(object);
                if (Objects.isNull(invoke) || Objects.equals(invoke, StringUtils.EMPTY) || StringUtils.isBlank(invoke.toString().trim())) {
                    ++var1;
                }
            }
        }
        return var1 > 0;
    }

    /**
     * 拓展Beanutils类的类复制，完成列表集合的复制功能。
     *
     * @param obj      被复制的源列表
     * @param list2    复制的列表
     * @param classObj 复制的类型
     */
    public static <T> void copyList(Object obj, List<T> list2, Class<T> classObj) {
        if ((!Objects.isNull(obj)) && (!Objects.isNull(list2))) {
            List list1 = (List) obj;
            list1.forEach(item -> {
                try {
                    T data = classObj.newInstance();
                    org.springframework.beans.BeanUtils.copyProperties(item, data);
                    list2.add(data);
                } catch (InstantiationException e) {
                } catch (IllegalAccessException e) {
                }
            });
        }
    }

    /**
     * 通过属性名获取指定属性值
     *
     * @param object    目标对象
     * @param clazz     对象类型
     * @param fieldName 属性名
     */
    public static Object findTargetField(Object object, Class clazz, String fieldName) throws Exception {
        if (null == object || null == clazz || StringUtils.isBlank(fieldName)) {
            return null;
        }
        BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        for (PropertyDescriptor property : propertyDescriptors) {
            String key = property.getName();
            if (fieldName.equals(key)) {
                Method readMethod = property.getReadMethod();
                Object invoke = readMethod.invoke(object, null);
                return invoke;
            }
        }
        return null;
    }
