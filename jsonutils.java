@Slf4j
public class JsonUtils {
    private static ObjectMapper mapper = new ObjectMapper();

    public static ObjectMapper getMapper() {
        return mapper;
    }

    static {
        // 在JDK8项目中使用jackson 注册Module
        mapper.registerModule(new ParameterNamesModule())
                .registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule());

//		mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    /**
     * JSON 转 集合(集合元素可以为:基本数据类型, Object)
     *
     * @param data
     * @param cls
     * @return
     * @throws IOException
     */
    public static List<?> transformCollectionsfromJson(String data, Class<?> cls, Class<? extends Collection> collection) {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, false);
        mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);

        CollectionType collectionType = mapper.getTypeFactory().constructCollectionType(collection, cls);
        try {
            return mapper.readValue(data, collectionType);
        } catch (IOException e) {
            log.error("--->>> String:{} To Collection:{} error, reason: {}", data, collection, e.getMessage(), e);
            return null;
        }
    }

    /**
     * JSON 转 集合(集合元素可以为:Map)
     *
     * @param data
     * @param cls
     * @return
     * @throws IOException
     */
    public static List<?> transformCollectionsfromJson(String data, Class<? extends Map> cls) {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, false);
        mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        CollectionType collectionType = mapper.getTypeFactory().constructCollectionType(ArrayList.class, cls);
        try {
            return mapper.readValue(data, collectionType);
        } catch (IOException e) {
            log.error("--->>> String:{} To Map:{} error, reason: {}", data, cls, e.getMessage(), e);
            return null;
        }
    }

    /**
     * JSON 转 对象
     *
     * @param data
     * @param cls
     * @param <T>
     * @return
     * @throws IOException
     */
    public static <T> T fromJson(String data, Class<T> cls) {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, false);
        mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        try {
            return mapper.readValue(data, cls);
        } catch (IOException e) {
            log.error("--->>> String:{} To Object:{} error, reason: {}", data, cls, e.getMessage(), e);
            return null;
        }
    }

    /**
     * 将Map转成指定的Bean
     *
     * @param map
     * @param clazz
     * @return
     * @throws Exception
     */
    public static Object mapToBean(Map map, Class clazz) {
        try {
            return mapper.readValue(objectToString(map), clazz);
        } catch (IOException e) {
            log.error("--->>> Map:{} To Bean:{} error, reason: {}", map, clazz, e.getMessage(), e);
            return null;
        }
    }

    /**
     * 将对象转成字符串
     *
     * @param obj
     * @return
     * @throws Exception
     */
    public static String objectToString(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("--->>> Object:{} To String error, reason: {}", obj, e.getMessage(), e);
            return null;
        }
    }

    /**
     * 对象 转 JSON
     *
     * @param protocol
     * @return
     */
    public static String toJosn(Object protocol) {
        try {
            // JavaTimeModule timeModule = new JavaTimeModule();
            // timeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer());
            // mapper.registerModule(timeModule);
            return mapper.writeValueAsString(protocol);
        } catch (JsonProcessingException e) {
            log.error("--->>> Object:{} To Json error, reason: {}", protocol, e.getMessage(), e);
            return null;
        }
    }

    /**
     * 将字符串转换为对应的map集合
     * @param json
     * @return
     */
    public static Map<String, Object> strJson2Map(String json) {
        JSONObject jsonObject = JSONObject.parseObject(json);
        Map<String, Object> resMap = new HashMap<String, Object>();
        Iterator<Map.Entry<String, Object>> it = jsonObject.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Object> param = (Map.Entry<String, Object>) it.next();
            if (param.getValue() instanceof JSONObject) {
                resMap.put(param.getKey(), strJson2Map(param.getValue().toString()));
            } else if (param.getValue() instanceof JSONArray) {
                resMap.put(param.getKey(), json2List(param.getValue()));
            } else {
                resMap.put(param.getKey(), JSONObject.toJSONString(param.getValue(), SerializerFeature.WriteClassName));
            }
        }
        return resMap;
    }

    private static List<Map<String, Object>> json2List(Object json) {
        JSONArray jsonArr = (JSONArray) json;
        List<Map<String, Object>> arrList = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < jsonArr.size(); ++i) {
            arrList.add(strJson2Map(jsonArr.getString(i)));
        }
        return arrList;
    }

}
