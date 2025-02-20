
## Prep

> macOS

#### Tools

> With the help of
>
>> *Docker Port-mapping* (`.. run -p`<u>`3306:`</u>`3306 ..`) <br/>
>> *Vagrantfile* (`config.vm.network "forwarded_port", guest:`<u>`3306,`</u>***`host: 3306`***)
>
>
> You can connect to them using `localhost:3306` like they are in the host machine
>> If you also have an app which uses port `3336`, just change the `host: PORT` in *Vagrantfile*
>
> To make the *MySQL* and *Redis* to run when you boot up the VM, add
>> `docker update mysql5dot7 --restart=always` <br/>
>> `docker update redis6dot16 --restart=always`

1. The Foundation *Vagrant*

    > Almost all the tools were installed to the VM, including *Docker*

    - Get started

    ```bash
    brew install vagrant    # use the GUI way if you prefer

    vagrant --version       # check if installed correctly

    vagrant init centos/7   # config file

    # cd the where the Vagrantfile file lies in
    vagrant up              # download, config and boot
    vagrant ssh             # ssh into the virtual machine
    ```

    - Networking

        > To make the VM like just another physical PC in your LAN

    ```ini
    # Edit the Vagrantfile
    config.vm.network "public_network"

    # Run `vagrant up`
    # You shall choose the one you used to connect to the Internet
    ```

2. Docker

    > Run under the user `root`

    ```bash
    # 1. Remove existing docker just in case any problems arise
    # https://docs.docker.com/engine/install/centos/#uninstall-old-versions
    yum remove docker-engine \
        docker-common \
        docker docker-latest \
        docker-client docker-client-latest \
        docker-logrotate docker-latest-logrotate  \


    # 2. Let yum know where to find Docker to install
    # https://docs.docker.com/engine/install/centos/#install-using-the-repository

    yum install -y yum-utils
    yum-config-manager \
        --add-repo \
        "https://download.docker.com/linux/centos/docker-ce.repo"

    yum install docker-ce docker-ce-cli containerd.io docker-compose-plugin


    # 3. Configure mirrors Docker images for faster fetching
    # https://cr.console.aliyun.com/cn-hangzhou/instances/mirrors
    mkdir -p /etc/docker
    touch    /etc/docker/daemon.json
    echo '{"registry-mirrors": ["https://22diassb.mirror.aliyuncs.com"]}' > /etc/docker/daemon.json


    # 4. Configure Docker to run when the OS boots
    systemctl enable docker    # add to 'run when OS boots' list


    # 5. Run
    systemctl start docker
    ```

3. MySQL

    > Run under the user `root`

    - Get started

    ```bash
    # 1. Get MySQL
    docker pull mysql:5.7
    docker images | grep mysql

    docker container ls
    docker container stop mysql5dot7
    docker container rm   mysql5dot7


    # 2. Run MySQL
    docker run -p 3306:3306 --name mysql5dot7 \
        -v /mydata/mysql/log:/var/log/mysql \
        -v /mydata/mysql/data:/var/lib/mysql \
        -v /mydata/mysql/conf:/etc/mysql \
        -e MYSQL_ROOT_PASSWORD=root \
        -d mysql:5.7
    ```

    - Configuration

        - Ready to edit the config for MySQL 5.7 in VM (<- Docker container)

        ```bash
        # [VIRTUAL MACHINE]     [DOCKER CONTAINER]
        # /mydata/mysql/log     /var/log/mysql
        # /mydata/mysql/data    /var/lib/mysql
        # /mydata/mysql/conf    /etc/mysql
        ```

        - Edit MySQL config ( `sudo vi /mydata/mysql/conf/my.cnf` )

        ```ini
        [client]
        default-character-set=utf8

        [mysql]
        default-character-set=utf8

        [mysqld]
        init_connect='SET NAMES utf8'
        init_connect='SET collation_connection = utf8_unicode_ci'

        character-set-server=utf8
        collation-server=utf8_unicode_ci

        skip-character-set-client-handshake
        skip-name-resolve
        ```

        - Reload new configuration

        ```bash
        docker restart mysql5dot7
        ```

        - Check new configuration

        ```bash
        docker exec -it mysql5dot7 /bin/bash     # VM
        cat /etc/mysql/my.cnf                    # MySQL container
        ```

4. Redis

    > Run under the user `root`

    - Get started

    ```bash
    # 1. Get Redis
    docker pull redis:6.0.16
    docker images | grep redis

    docker container ls
    docker container stop redis6dot16
    docker container rm   redis6dot16


    # 2. Initialization before running
    # Mapping the config in VM into the Redis container
    mkdir -p /mydata/redis/conf
    touch /mydata/redis/conf/redis.conf


    # 3. Run Redis
    docker run -p 6379:6379 --name redis6dot16 \
        -v /mydata/redis/data:/data \
        -v /mydata/redis/conf/redis.conf:/etc/redis/redis.conf \
        -d redis:6.0.16 \
        redis-server /etc/redis/redis.conf
    ```

    - Configuration

    ```bash
    # 1. Edit configuration (Redis persistence)
    echo 'appendonly yes' > /mydata/redis/conf/redis.conf


    # 2. Check configuration
    docker exec -it redis6dot16 /bin/bash    # VM
    cat /etc/redis/redis.conf                # Redis container
    ```

5. Maven

    > AliYun mirror (for faster download) and use JDK 1.8 to compile

6. Editors

    > Plugins to install

    - IDEA: *Lombok*, *MyBatisX*
    - VS Code: whatever suits you

#### Data

> I'm using [mycli](https://github.com/dbcli/mycli/) <small>(`mycli -u root -h localhost`)</small> and [BeeKeeper](https://www.beekeeperstudio.io/get) to manage the databases

- Create the database

    > Reference for the [command](https://dba.stackexchange.com/questions/76788/create-a-mysql-database-with-charset-utf-8/76789#76789), the *why* for [`utf8mb4`](https://stackoverflow.com/questions/30074492/what-is-the-difference-between-utf8mb4-and-utf8-charsets-in-mysql/30074553#30074553) and [`utf8mb4_unicode_ci`](https://stackoverflow.com/questions/1036454/what-are-the-differences-between-utf8-general-ci-and-utf8-unicode-ci/1036459#1036459)

    ```sql
    CREATE DATABASE IF NOT EXISTS gulimall_pms_product
        DEFAULT CHARACTER SET utf8mb4
        DEFAULT COLLATE utf8mb4_unicode_ci ;

    CREATE DATABASE IF NOT EXISTS gulimall_oms_order
        DEFAULT CHARACTER SET utf8mb4
        DEFAULT COLLATE utf8mb4_unicode_ci ;

    CREATE DATABASE IF NOT EXISTS gulimall_sms_salepromo
        DEFAULT CHARACTER SET utf8mb4
        DEFAULT COLLATE utf8mb4_unicode_ci ;

    CREATE DATABASE IF NOT EXISTS gulimall_ums_user
        DEFAULT CHARACTER SET utf8mb4
        DEFAULT COLLATE utf8mb4_unicode_ci ;

    CREATE DATABASE IF NOT EXISTS gulimall_wms_logistics
        DEFAULT CHARACTER SET utf8mb4
        DEFAULT COLLATE utf8mb4_unicode_ci ;
    ```

- Load the schema

    >  `cd` to where the `.sql` files are and get `mycli` installed (reference to [`source`](https://github.com/dbcli/mycli/issues/108#issuecomment-131845477))

    ```sql
    USE    gulimall_pms_product       ;
    source gulimall_pms_product.sql   ;

    USE    gulimall_oms_order         ;
    source gulimall_oms_order.sql     ;

    USE    gulimall_sms_salepromo     ;
    source gulimall_sms_salepromo.sql ;

    USE    gulimall_ums_user          ;
    source gulimall_ums_user.sql      ;

    USE    gulimall_wms_logistics     ;
    source gulimall_wms_logistics.sql ;
    ```

#### Scaffold

> Remove the `.git` folder before you include or start using these

###### Backend

> Backend: [*renren-fast*](https://gitee.com/renrenio/renren-fast) <br/>

- Create the database

    ```sql
    CREATE DATABASE IF NOT EXISTS gulimall_admin_renrenfast
        DEFAULT CHARACTER SET utf8mb4
        DEFAULT COLLATE utf8mb4_unicode_ci ;
    ```

- Load the data from the `.sql` included in the [repository](https://gitee.com/renrenio/renren-fast/tree/master/db)

    >  `cd` to where the `.sql` files are and get `mycli` installed (reference to [`source`](https://github.com/dbcli/mycli/issues/108#issuecomment-131845477))

    ```sql
    use    gulimall_admin_renrenfast     ;
    source gulimall_admin_renrenfast.sql ;
    ```

- Run

    ```bash
    mvn clean
    mvn install

    # Now you can run the start up the frontend Vue.js project
    mvn spring-boot:run
    ```

###### Frontend

> Frontend: [*renren-fast-vue*](https://gitee.com/renrenio/renren-fast-vue)

- Make sure you already have the backend server running

    ```bash
    yarn install

    # 1. The name and the password are both 'admin'
    # 2. The captcha was generated by the backend server
    yarn run dev
    ```

#### Code Generation

> Get [renren-generator](https://gitee.com/renrenio/renren-generator) and fix basic dep issues

###### Snippet

- Component 👐 Database

    ```java
    /*
    gulimall-coupon     gulimall_sms_salepromo
    gulimall-member     gulimall_ums_user
    gulimall-order      gulimall_oms_order
    gulimall-product    gulimall_pms_product
    gulimall-ware       gulimall_wms_logistics
    */
    ```

###### Procedure

1. Make copies based on the template

    ```bash
    ORIG_CONF_APP='template.application.yml'
    ORIG_CONF_GEN='template.generator.properties'

    # Then edit the details by your own
    for f in application-{coupon,member,order,product,ware}.yml;
        do cp ${ORIG_CONF_APP} $f;
    done

    # Then edit the details by your own
    for f in generator-{coupon,member,order,product,ware}.properties;
        do cp ${ORIG_CONF_GEN} $f;
    done
    ```

2. Run the generation service

    > Modify the directory in correspondence with yours

    ```bash
    COMP="coupon"
    PROJ_ROOT="/Users/mac/dev/ytb-projects-gulimall"

    cd "${PROJ_ROOT}/renren-generator/src/main/resources/"
    cp -fv application-${COMP}.yml application.yml ;
    cp -fv generator-${COMP}.properties generator.properties ;

    cd "${PROJ_ROOT}/renren-generator"
    mvn clean install && mvn spring-boot:run

    cd "${PROJ_ROOT}"
    ```

3. Start the generation

    ```bash
    PORT=80
    open "http://localhost:${PORT}#generator.html"
    ```

4. Add the generated code to our components

    > Make sure you haven't written anything new to the components!

    ```bash
    cd "/Users/mac/dev/ytb-projects-gulimall"

    for comp in {coupon,member,order,product,ware}.zip;
        do
        unzip \
            "dev_generatedcode/srcmain-gulimall-${comp}.zip" \
            -d "gulimall-${comp}/src/" &> /dev/null
    done
    ```

5. Fix dependencies

    - Create dedicated package to hold common dependencies

    > The main theme is copying files around and `mvn clean install`

    ```bash
    cd "/Users/mac/dev/ytb-projects-gulimall"

    mkdir -p \
        gulimall-common \
        gulimall-common/com/elliot/common/{utils,xss}/
    ```

    - `pom.xml` for *gulimall-common*

    ```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <project .. >

        <parent>
            <artifactId>gulimall</artifactId>
            <groupId>com.elliot.gulimall</groupId>
            <version>0.0.1-SNAPSHOT</version>
        </parent>

        <modelVersion>4.0.0</modelVersion>

        <groupId>com.elliot.gulimall</groupId>
        <artifactId>gulimall-common</artifactId>
        <version>0.0.1-SNAPSHOT</version>
        <name>gulimall-common</name>
        <description>项目共用依赖</description>

        <dependencies>
            ...
        </dependencies>

    </project>
    ```

    - Modify `pom.xml` in other components for them to use *common dependencies*

    ```xml
    <!-- Add right after the starting section of <dependencies> -->
    ..
        <dependencies>
            <dependency>
                <groupId>com.elliot.gulimall</groupId>
                <artifactId>gulimall-common</artifactId>
                <version>0.0.1-SNAPSHOT</version>
            </dependency>
        </dependencies>
    ..
    ```

-----

## Microservice in Practice

> For non-microservices, only three parts were required, the frondend, the database and the backend. But when the business <u>grows larger</u> and <u>more complex</u>, you would need an efficient and effective way to manage the *scale* and *complexity*.
>
> And the backend is where we targeting on, to make it not just scalable, but also manageable. Technologies which were provided by *Spring Cloud* or *Spring Cloud Alibaba* is the set of tools which helps us to to do that <small>(scalable and manageable)</small>.
>
> Don't be scared by the terminologies
>> Names like *Service Registration*: the name of the solution for the problems we would face <small>(in this context: letting different services find each other)</small> <br/>
>> Names like *Nacos*: the name of a tool which delivers the solutions for at least one of the problems you would face

### *Nacos*

- Solution
  - [x] Service Registration
  - [x] Service Discovery
  - [x] Load Balancing <small>(just the setup in term of progress)</small>
  - Configuration Management

- Usage Overview

    > A server running at the background plus a few registered services

##### Service Discovery & Registration

- Setup for the Server

  - Download the [*Nacos* server](https://github.com/alibaba/nacos/releases)

    > **Do not** put these under your version control, which would exceed the `100MB` limit, as you need to handle the Git-LFS related issues

    ```bash
    # Download
    wget \
        -O nacos-server-2.1.1.zip \
        https://github.com/alibaba/nacos/releases/download/2.1.1/nacos-server-2.1.1.zip


    # Extract
    unzip nacos-server-2.1.1.zip -d .
    ```

  - Run

    ```bash
    # Start
    bash ./nacos/bin/startup.sh -m standalone


    # Check if it started correctly
    cat ./nacos/logs/start.out
    ```

- Setup for the Client

  - Download the dependency

    > Add this to the `pom.xml` inside the *gulimall-common* package

    ```xml
    <!-- Right after where the level 'mysql-connector-java' is in -->
    <!-- Other components would be able to use this as well! -->
    ..
        <dependency>
            <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
        </dependency>
    ..
    ```

  - Configuration

    > Add this to whichever components you wanna test on

    ```yml
    # application.yml
    spring:
      application:
        name: gulimall-COMPONENT
      ..
        cloud:
          nacos:
            discovery:
              server-addr=127.0.0.1:8848
    ```

  - Annotation

    > Add this to whichever components you wanna test on

    ```java
    // src/main/java -> PACKAGE -> Gulimall 👉COMPONENT👈 Application.java
    import .. ;
    import .. ;
    import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

    @ ..
    @ ..
    @EnableDiscoveryClient
    public class GulimallCouponApplication { .. }
    ```

- Get the clients running (registering)

    ```bash
    # Go to the root folder of whichever components you wanna test on
    ./mvnw spring-boot:run


    # Now you could go to localhost:8848 to check if they registered correctly
    # Both the username and the password are 'nacos' (lowercase)
    ```

##### Load Balancing

> Basically we are choosing the one included in [*Nacos* instead of *Netflix Ribbon*](https://spring-cloud-alibaba-group.github.io/github-pages/2021/en-us/index.html#_spring_cloud_loadbalancer)

- Add this to the `pom.xml` in the `gulimall-common` so all the components could use it

```xml
..
    <dependency>
        <!-- Already added this, showing you where to add '<exclusion>' tag -->
        <groupId>com.alibaba.cloud</groupId>
        <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>

        <!-- Excluding this Load Balancer, we'll use the one down below -->
        <exclusions>
            <exclusion>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-netflix-ribbon</artifactId>
            </exclusion>
        </exclusions>
    </dependency>

    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-loadbalancer</artifactId>
        <version>2.2.9.RELEASE</version>
    </dependency>
..
```

##### Configuration Management

> Basically we'll let components be able to send a <small>(HTTP/RPC)</small> request to upload the configurations

###### Configuration

> Followed these steps from the [Nacos documentation](https://nacos.io/en-us/docs/quick-start-spring-cloud.html)

1. Add this to the `pom.xml` in the `gulimall-common` so all the components are able to use them

```xml
..
    <!-- Nacos: Configuration Management -->
    <dependency>
        <groupId>com.alibaba.cloud</groupId>
        <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
    </dependency>


    <!-- Dedicated for bootstrap.properties which Nacos use it to configure -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-bootstrap</artifactId>
        <version>3.1.4</version>
    </dependency>
```

2. Config file *bootstrap.properties* for *Nacos* configuration management

    > *Nacos* requires this to configure the config management ([their doc](https://nacos.io/en-us/docs/quick-start-spring-cloud.html) references it)

    ```bash
    touch \
        gulimall-{coupon,member,order,product,ware}/src/main/resources/bootstrap.yml
    ```

3. Configuration for/with `bootstrap.yml`

    > Two files were your concern: `application.yml` and the `bootstrap.yml`

    - [Enable](https://docs.spring.io/spring-cloud-config/docs/current/reference/html/#config-first-bootstrap) `bootstrap`

    ```yml
    # application.yml

    spring:
      cloud:
        bootstrap:
          enabled: true
    ```

    - [Configure](https://nacos.io/en-us/docs/quick-start-spring-cloud.html) `bootstrap`

    > Modify the `name` and the `prefix` for each components as needed

    ```yaml
    # bootstrap.yml

    spring:
      application:
        # Should be the same as the one you defined in application.yml
        name: nacos-gulimall-coupon
      cloud:
        nacos:
          config:
            server-addr: 127.0.0.1:8848

            # Combining these two, you get the data ID for you to add to the
            # Nacos configuration center for hot-reloading config, eventually,
            # you can put 'nacosconfig-coupon.yaml' in the configuration server
            # for future update
            prefix: nacosconfig-coupon
            file-extension: yaml
    ```

###### Setup for *Hot-reload*

> Without this, the procedure for updating the configuration is `edit config`, `deploy` then `send request` to see the changes being made. BUT! If you have **multiple machines** <small>(each with their own different config)</small>, you would have to do the same process **over and over**!

- Code which reads from the config file

    > The `@Value` here is used either for reading config keys `${}` or templates `#{}`

    ```java
    import .. ;
    import .. ;
    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.cloud.context.config.annotation.RefreshScope;

    @ ..
    @ ..
    @RefreshScope
    public class CouponController {

        // We gotta set a default value for the keys here, since some of the
        // config would only be available when the configuration server is
        // online (at least that's my experience: errors being raised by
        // Spring Boot when I'm not setting up the default value).

        @Value("${coupon.user.name:nobody}")  // default for name: 'nobody'
        private String name;

        @Value("${coupon.user.age:1}")        // default for age: 1
        private String age;

        // Test if it would read the contents from the config file
        @RequestMapping("/test")
        public R test() {
            return R.ok()
                    .put("name", name)
                    .put("age", age);
        }
    }
    ```

### *OpenFeign*

- Solution
  - A way for components calling each other (HTTP requests, but enhanced)

- Usage Overview
    > N/A

##### Configuration

> Add this to the `pom.xml` under your individual components' folder

```xml
..
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-openfeign</artifactId>
    </dependency>
..
```

##### Components to be called

> `gulimall-coupon` (as for `gulimallcoupon`, it's the lowest level package's name)

- A new method inside `CouponController.java`

    ```java
    @ ..
    @RequestMapping("gulimallcoupon/coupon")
    public class CouponController {
        @Autowired
        private CouponService couponService;

        // This is what we added, any other code exist already
        @RequestMapping("/member/list")
        public R membercoupons() {
            CouponEntity couponEntity = new CouponEntity();
            couponEntity.setCouponName("30% off");

            return R.ok().put("coupons", Arrays.asList(couponEntity));
        }
    }
    ```

##### Components who do the calling

> `gulimall-member`

- A central place to put available *calling* methods for the caller

    ```bash
    cd './gulimall-member/ .. /com/elliot/gulimall/gulimallmember/'

    mkdir -p feign
    ```

- Where to find the callers

    ```java
    import org.springframework.cloud.openfeign.EnableFeignClients;

    @ ..
    @ ..
    @EnableFeignClients(basePackages = "com.elliot.gulimall.gulimallmember.feign")
    public class GulimallMemberApplication { .. }
    ```

- Write an *interface*

    ```java
    import com.elliot.common.utils.R;
    import org.springframework.cloud.openfeign.FeignClient;
    import org.springframework.web.bind.annotation.RequestMapping;

    @FeignClient("gulimallcoupon")
    public interface CouponFeignService {

        @RequestMapping("gulimallcoupon/coupon/member/list")
        public R membercoupons();
    }
    ```

##### `gulimallmember` Calls `gulimallcoupon`

- Test code

    ```java
    @ ..
    @RequestMapping("gulimallmember/member")
    public class MemberController {
        ..

        @Autowired
        private CouponFeignService couponFeignService;

        @RequestMapping("/coupons")
        public R test() {
            MemberEntity memberEntity = new MemberEntity();
            memberEntity.setNickname("Dickson");

            R membercoupons = couponFeignService.membercoupons();

            return R.ok()
                    .put("member", memberEntity)
                    .put("coupons", membercoupons.get("coupons"));
        }
    ```

- Check the result

    ```bash
    PORT_MEMBER=8000
    ENDPOINT="http://localhost:${PORT_MEMBER}/gulimallmember/member/coupons"

    curl "${ENDPOINT}" | jq '.coupons [] .couponName'    # "30% off"
    curl "${ENDPOINT}" | jq '.member .nickname'          # "Dickson"
    ```

-----

## References

#### Tools

1. Vagrant

    ```bash
    vagrant status

    vagrant suspend     # sleep
    vagrant up          # boot

    vagrant halt        # shutdown
    vagrant reload      # reboot
    ```

2. Docker

    ```bash
    docker container ps -a
    docker container restart mysql5dot7

    docker exec -it mysql5dot7 /bin/bash
    ```

3. MySQL

    ```sql
    -- Just in case you wanna start over
    DROP DATABASE gulimall_pms_product   ;
    DROP DATABASE gulimall_oms_order     ;
    DROP DATABASE gulimall_sms_salepromo ;
    DROP DATABASE gulimall_ums_user      ;
    DROP DATABASE gulimall_wms_logistics ;
    ```
