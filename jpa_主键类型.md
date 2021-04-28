CREATE TABLE  tb_generator (  
  id INT(13) NOT NULL,  
  gen_name VARCHAR(255) NOT NULL,  
  gen_value INT(13) NOT NULL,  
  PRIMARY KEY(id)  
) 




INSERT INTO tb_generator(id, gen_name, gen_value) VALUES (1,'PAYABLEMOENY_PK', 1);  






https://blog.csdn.net/qq_37488040/article/details/78141114?utm_medium=distribute.pc_relevant.none-task-blog-baidujs_title-4&spm=1001.2101.3001.4242







一、JPA通用策略生成器 
通过annotation来映射hibernate实体的,基于annotation的hibernate主键标识为@Id, 
其生成规则由@GeneratedValue设定的.这里的@id和@GeneratedValue都是JPA的标准用法, 
JPA提供四种标准用法,由@GeneratedValue的源代码可以明显看出. 

Java代码   收藏代码
@Target({METHOD,FIELD})    
    @Retention(RUNTIME)    
    public @interface GeneratedValue{    
        GenerationType strategy() default AUTO;    
        String generator() default “”;    
    }   


其中GenerationType: 

Java代码   收藏代码
public enum GenerationType{    
    TABLE,    
    SEQUENCE,    
    IDENTITY,    
    AUTO   
}  


JPA提供的四种标准用法为TABLE,SEQUENCE,IDENTITY,AUTO. 
TABLE：使用一个特定的数据库表格来保存主键。 
SEQUENCE：根据底层数据库的序列来生成主键，条件是数据库支持序列。 
IDENTITY：主键由数据库自动生成（主要是自动增长型） 
AUTO：主键由程序控制。 

1、TABLE 
Java代码   收藏代码
@Id  
@GeneratedValue(strategy = GenerationType.TABLE, generator=“payablemoney_gen”)  
@TableGenerator(name = “pk_gen”,  
    table=”tb_generator”,  
    pkColumnName=”gen_name”,  
    valueColumnName=”gen_value”,  
    pkColumnValue=”PAYABLEMOENY_PK”,  
    allocationSize=1  
)  


这里应用表tb_generator，定义为 
Sql代码   收藏代码
CREATE TABLE  tb_generator (  
  id NUMBER NOT NULL,  
  gen_name VARCHAR2(255) NOT NULL,  
  gen_value NUMBER NOT NULL,  
  PRIMARY KEY(id)  
)  


插入纪录，供生成主键使用， 
Sql代码   收藏代码
INSERT INTO tb_generator(id, gen_name, gen_value) VALUES (1,PAYABLEMOENY_PK’, 1);  


在主键生成后，这条纪录的value值，按allocationSize递增。 


@TableGenerator的定义： 
Java代码   收藏代码
@Target({TYPE, METHOD, FIELD})   
@Retention(RUNTIME)  
public @interface TableGenerator {  
  String name();  
  String table() default “”;  
  String catalog() default “”;  
  String schema() default “”;  
  String pkColumnName() default “”;  
  String valueColumnName() default “”;  
  String pkColumnValue() default “”;  
  int initialValue() default 0;  
  int allocationSize() default 50;  
  UniqueConstraint[] uniqueConstraints() default {};  
}  


其中属性说明： 
name属性表示该表主键生成策略的名称，它被引用在@GeneratedValue中设置的“generator”值中。 
table属性表示表生成策略所持久化的表名，例如，这里表使用的是数据库中的“tb_generator”。 
catalog属性和schema具体指定表所在的目录名或是数据库名。 
pkColumnName属性的值表示在持久化表中，该主键生成策略所对应键值的名称。例如在“tb_generator”中将“gen_name”作为主键的键值 
valueColumnName属性的值表示在持久化表中，该主键当前所生成的值，它的值将会随着每次创建累加。例如，在“tb_generator”中将“gen_value”作为主键的值 
pkColumnValue属性的值表示在持久化表中，该生成策略所对应的主键。例如在“tb_generator”表中，将“gen_name”的值为“CUSTOMER_PK”。 
initialValue表示主键初识值，默认为0。 
allocationSize表示每次主键值增加的大小，例如设置成1，则表示每次创建新记录后自动加1，默认为50。 
UniqueConstraint与@Table标记中的用法类似。 

2、SEQUENCE 
Java代码   收藏代码
@Id  
@GeneratedValue(strategy = GenerationType.SEQUENCE,generator=“payablemoney_seq”)  
@SequenceGenerator(name=“payablemoney_seq”, sequenceName=“seq_payment”)  

@SequenceGenerator定义 
Java代码   收藏代码
@Target({TYPE, METHOD, FIELD})   
@Retention(RUNTIME)  
public @interface SequenceGenerator {  
 String name();  
 String sequenceName() default “”;  
 int initialValue() default 0;  
 int allocationSize() default 50;  
}  


name属性表示该表主键生成策略的名称，它被引用在@GeneratedValue中设置的“generator”值中。 
sequenceName属性表示生成策略用到的数据库序列名称。 
initialValue表示主键初识值，默认为0。 
allocationSize表示每次主键值增加的大小，例如设置成1，则表示每次创建新记录后自动加1，默认为50。 


3、IDENTITY 
Java代码   收藏代码
@Id  
@GeneratedValue(strategy = GenerationType.IDENTITY)  

4、AUTO 
Java代码   收藏代码
@Id  
@GeneratedValue(strategy = GenerationType.AUTO)  

在指定主键时，如果不指定主键生成策略，默认为AUTO。 
Java代码   收藏代码
@Id  


跟下面的定义是一样的。 
Java代码   收藏代码
@Id  
@GeneratedValue(strategy = GenerationType.AUTO)  

二、hibernate主键策略生成器 
hibernate提供多种主键生成策略，有点是类似于JPA，有的是hibernate特有： 
native: 对于 oracle 采用 Sequence 方式，对于MySQL 和 SQL Server 采用identity（自增主键生成机制），native就是将主键的生成工作交由数据库完成，hibernate不管（很常用）。 
uuid: 采用128位的uuid算法生成主键，uuid被编码为一个32位16进制数字的字符串。占用空间大（字符串类型）。 
hilo: 使用hilo生成策略，要在数据库中建立一张额外的表，默认表名为hibernate_unique_key,默认字段为integer类型，名称是next_hi（比较少用）。 
assigned: 在插入数据的时候主键由程序处理（很常用），这是 <generator>元素没有指定时的默认生成策略。等同于JPA中的AUTO。 
identity: 使用SQL Server 和 MySQL 的自增字段，这个方法不能放到 Oracle 中，Oracle 不支持自增字段，要设定sequence（MySQL 和 SQL Server 中很常用）。 
          等同于JPA中的INDENTITY。 
select: 使用触发器生成主键（主要用于早期的数据库主键生成机制，少用）。 
sequence: 调用底层数据库的序列来生成主键，要设定序列名，不然hibernate无法找到。 
seqhilo: 通过hilo算法实现，但是主键历史保存在Sequence中，适用于支持 Sequence 的数据库，如 Oracle（比较少用） 
increment: 插入数据的时候hibernate会给主键添加一个自增的主键，但是一个hibernate实例就维护一个计数器，所以在多个实例运行的时候不能使用这个方法。 
foreign: 使用另外一个相关联的对象的主键。通常和<one-to-one>联合起来使用。 
guid: 采用数据库底层的guid算法机制，对应MYSQL的uuid()函数，SQL Server的newid()函数，ORACLE的rawtohex(sys_guid())函数等。 
uuid.hex: 看uuid，建议用uuid替换。 
sequence-identity: sequence策略的扩展，采用立即检索策略来获取sequence值，需要JDBC3.0和JDK4以上（含1.4）版本 

hibernate提供了多种生成器供选择,基于Annotation的方式通过@GenericGenerator实现. 
hibernate每种主键生成策略提供接口org.hibernate.id.IdentifierGenerator的实现类,如果要实现自定义的主键生成策略也必须实现此接口. 

Java代码   收藏代码
public interface IdentifierGenerator {  
    /** 
     * The configuration parameter holding the entity name 
     */  
    public static final String ENTITY_NAME = “entity_name”;  
      
  /** 
   * Generate a new identifier. 
   * @param session 
   * @param object the entity or toplevel collection for which the id is being generated 
   * 
   * @return a new identifier 
   * @throws HibernateException 
   */  
  public Serializable generate(SessionImplementor session, Object object)   
    throws HibernateException;  
}  


IdentifierGenerator提供一generate方法,generate方法返回产生的主键. 


三、@GenericGenerator 
自定义主键生成策略，由@GenericGenerator实现。 
hibernate在JPA的基础上进行了扩展，可以用一下方式引入hibernate独有的主键生成策略，就是通过@GenericGenerator加入的。 

比如说，JPA标准用法 
Java代码   收藏代码
@Id  
@GeneratedValue(GenerationType.AUTO)  

就可以用hibernate特有以下用法来实现 
Java代码   收藏代码
@GeneratedValue(generator = “paymentableGenerator”)    
@GenericGenerator(name = “paymentableGenerator”, strategy = “assigned”)  


@GenericGenerator的定义: 
Java代码   收藏代码
@Target({PACKAGE, TYPE, METHOD, FIELD})  
@Retention(RUNTIME)  
public @interface GenericGenerator {  
 /** 
  * unique generator name 
  */  
 String name();  
 /** 
  * Generator strategy either a predefined Hibernate 
  * strategy or a fully qualified class name. 
  */  
 String strategy();  
 /** 
  * Optional generator parameters 
  */  
 Parameter[] parameters() default {};  
}  


name属性指定生成器名称。 
strategy属性指定具体生成器的类名。 
parameters得到strategy指定的具体生成器所用到的参数。 

对于这些hibernate主键生成策略和各自的具体生成器之间的关系,在org.hibernate.id.IdentifierGeneratorFactory中指定了, 
Java代码   收藏代码
static {  
  GENERATORS.put(”uuid”, UUIDHexGenerator.class);  
  GENERATORS.put(”hilo”, TableHiLoGenerator.class);  
  GENERATORS.put(”assigned”, Assigned.class);  
  GENERATORS.put(”identity”, IdentityGenerator.class);  
  GENERATORS.put(”select”, SelectGenerator.class);  
  GENERATORS.put(”sequence”, SequenceGenerator.class);  
  GENERATORS.put(”seqhilo”, SequenceHiLoGenerator.class);  
  GENERATORS.put(”increment”, IncrementGenerator.class);  
  GENERATORS.put(”foreign”, ForeignGenerator.class);  
  GENERATORS.put(”guid”, GUIDGenerator.class);  
  GENERATORS.put(”uuid.hex”, UUIDHexGenerator.class); //uuid.hex is deprecated  
  GENERATORS.put(”sequence-identity”, SequenceIdentityGenerator.class);  
}  

上面十二种策略，加上native，hibernate一共默认支持十三种生成策略。 

1、native 
Java代码   收藏代码
@GeneratedValue(generator = “paymentableGenerator”)    
@GenericGenerator(name = “paymentableGenerator”, strategy = “native”)   

2、uuid 
Java代码   收藏代码
@GeneratedValue(generator = “paymentableGenerator”)    
@GenericGenerator(name = “paymentableGenerator”, strategy = “uuid”)   

3、hilo 
Java代码   收藏代码
@GeneratedValue(generator = “paymentableGenerator”)    
@GenericGenerator(name = “paymentableGenerator”, strategy = “hilo”)   

4、assigned 
Java代码   收藏代码
@GeneratedValue(generator = “paymentableGenerator”)    
@GenericGenerator(name = “paymentableGenerator”, strategy = “assigned”)   

5、identity 
Java代码   收藏代码
@GeneratedValue(generator = “paymentableGenerator”)    
@GenericGenerator(name = “paymentableGenerator”, strategy = “identity”)   

6、select 
Java代码   收藏代码
@GeneratedValue(generator = “paymentableGenerator”)  
@GenericGenerator(name=“select”, strategy=“select”,  
     parameters = { @Parameter(name = “key”, value = “idstoerung”) })  

7、sequence 
Java代码   收藏代码
@GeneratedValue(generator = “paymentableGenerator”)  
@GenericGenerator(name = “paymentableGenerator”, strategy = “sequence”,   
         parameters = { @Parameter(name = “sequence”, value = “seq_payablemoney”) })  

8、seqhilo 
Java代码   收藏代码
@GeneratedValue(generator = “paymentableGenerator”)  
@GenericGenerator(name = “paymentableGenerator”, strategy = “seqhilo”,   
         parameters = { @Parameter(name = “max_lo”, value = “5”) })  

9、increment 
Java代码   收藏代码
@GeneratedValue(generator = “paymentableGenerator”)    
@GenericGenerator(name = “paymentableGenerator”, strategy = “increment”)   

10、foreign 
Java代码   收藏代码
@GeneratedValue(generator = “idGenerator”)  
@GenericGenerator(name = “idGenerator”, strategy = “foreign”,   
         parameters = { @Parameter(name = “property”, value = “employee”) })  


注意：直接使用@PrimaryKeyJoinColumn 报错（?） 
Java代码   收藏代码
@OneToOne(cascade = CascadeType.ALL)   
@PrimaryKeyJoinColumn   


例如 
Java代码   收藏代码
@Entity  
public class Employee {  
  @Id Integer id;  
      
  @OneToOne @PrimaryKeyJoinColumn  
  EmployeeInfo info;  
  …  
}  


应该为 
Java代码   收藏代码
@Entity  
public class Employee {  
  @Id   
  @GeneratedValue(generator = “idGenerator”)  
  @GenericGenerator(name = “idGenerator”, strategy = “foreign”,   
         parameters = { @Parameter(name = “property”, value = “info”) })   
  Integer id;  
      
  @OneToOne  
  EmployeeInfo info;  
  …  
}  


11、guid 
Java代码   收藏代码
@GeneratedValue(generator = “paymentableGenerator”)    
@GenericGenerator(name = “paymentableGenerator”, strategy = “guid”)   

12、uuid.hex 
Java代码   收藏代码
@GeneratedValue(generator = “paymentableGenerator”)    
@GenericGenerator(name = “paymentableGenerator”, strategy = “uuid.hex”)   

13、sequence-identity 
Java代码   收藏代码
@GeneratedValue(generator = “paymentableGenerator”)  
@GenericGenerator(name = “paymentableGenerator”, strategy = “sequence-identity”,   
         parameters = { @Parameter(name = “sequence”, value = “seq_payablemoney”) })  


四、通过@GenericGenerator自定义主键生成策略 
如果实际应用中，主键策略为程序指定了就用程序指定的主键（assigned），没有指定就从sequence中取。 
明显上面所讨论的策略都不满足，只好自己扩展了，集成assigned和sequence两种策略。 

Java代码   收藏代码
public class AssignedSequenceGenerator extends SequenceGenerator implements   
 PersistentIdentifierGenerator, Configurable {  
 private String entityName;  
    
 public void configure(Type type, Properties params, Dialect dialect) throws MappingException {  
  entityName = params.getProperty(ENTITY_NAME);  
  if (entityName==null) {  
   throw new MappingException(“no entity name”);  
  }  
    
  super.configure(type, params, dialect);    
 }  
   
 public Serializable generate(SessionImplementor session, Object obj)   
  throws HibernateException {  
    
  Serializable id = session.getEntityPersister( entityName, obj )   
    .getIdentifier( obj, session.getEntityMode() );  
    
  if (id==null) {  
   id = super.generate(session, obj);  
  }  
    
  return id;  
 }  
}  


实际应用中，定义同sequence。 
Java代码   收藏代码
@GeneratedValue(generator = “paymentableGenerator”)  
@GenericGenerator(name = “paymentableGenerator”, strategy = “AssignedSequenceGenerator”,   
     parameters = { @Parameter(name = “sequence”, value = “seq_payablemoney”) })  



四种数据库的支持情况如下：

 

数据库名称

支持的id策略

mysql

GenerationType.TABLE

GenerationType.AUTO

GenerationType.IDENTITY

不支持GenerationType.SEQUENCE



oracle

strategy=GenerationType.AUTO

GenerationType.SEQUENCE

GenerationType.TABLE

不支持GenerationType.IDENTITY



postgreSQL

GenerationType.TABLE

GenerationType.AUTO

GenerationType.IDENTITY

GenerationType.SEQUENCE

都支持



kingbase

GenerationType.TABLE

GenerationType.SEQUENCE

GenerationType.IDENTITY

GenerationType.AUTO

都支持

