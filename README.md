# Refy

**v1.0.0**

This project, based on Java and the Spring Boot framework, is an open source self-hosted references collector and custom 
links generator

Save your references with **Refy**!

## Customize the application

To customize and create your own version of this application you need to have the <a href="https://github.com/N7ghtm4r3/Refy/tree/main/core">
core library</a> implemented in your project and published into maven local system

### Clone the core library and publish to maven local

- Clone the repository or download the zip file of the current version available

- Open the folder file in your development environment and publish to maven local with the
  **publishMavenPublicationToMavenLocal** gradle task, take a look <a href="https://docs.gradle.org/current/userguide/publishing_maven.html">here</a>
  for a help

### Implement the core library to your application

- #### Gradle (Short)

```gradle
repositories {
  ...
  mavenLocal()
}

dependencies {
  implementation 'com.tecknobit.refycore:refycore:1.0.0'
}
```

#### Gradle (Kotlin)

```gradle
repositories {
  ...
  mavenLocal()
}

dependencies {
  implementation("com.tecknobit.refycore:refycore:1.0.0")
}
```

## 🛠 Skills

- Java
- Kotlin

## Roadmap

This project will be constantly developed to reach different platforms to work on, following the platforms releases
steps:

- Mobile
    - <a href="https://github.com/N7ghtm4r3/Refy-Android#readme">Android</a>
    - iOS -> planned
- <a href="https://github.com/N7ghtm4r3/Refy-Desktop#readme">Desktop version</a>
- <a href="https://github.com/N7ghtm4r3/Refy/releases/tag/1.0.0">Backend service "out-of-the-box"</a>

## Usages

### Backend configuration

> [!WARNING]  
> Note: the service will run using the *HTTP* protocol as default, it is recommended to implement an *SSL* or *TLS* certificate to secure communication on your infrastructure.
>
> **Wikis**
>
> To create a self-signed certificate you can look <a href="https://tecadmin.net/step-by-step-guide-to-creating-self-signed-ssl-certificates/">here</a>
>
> To implement a certificate in Spring you can look <a href="https://www.thomasvitale.com/https-spring-boot-ssl-certificate/">here</a>
>
> If you encounter any problems with the creation of the keystore you can look <a href="https://stackoverflow.com/questions/906402/how-to-import-an-existing-x-509-certificate-and-private-key-in-java-keystore-to">here</a> to get more information, or
> use the following command to add the **private key** to the keystore:
> ```xml
> openssl pkcs12 -export -in your_certificate_file.crt -inkey your_private_key.key -out your_out_pkcs12_file.p12 -name your_alias_name -CAfile your_certificate_file.crt -caname root
> ```


#### Default configuration

The default properties to launch the backend service as designed are the following:

``` properties
# The properties considered critical could alter the flow of how the backend service was designed, so we do not recommend
# to change them

spring.datasource.url=jdbc:mysql://localhost:3306/refy?createDatabaseIfNotExist=true
server.port=1989
spring.datasource.username=root
spring.jpa.generate-ddl=true 
spring.jpa.hibernate.ddl.auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
spring.mvc.dispatch-options-request=true
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
```

| Property                                  | Default value                                                  |    Not-Critical    | Recommended to change |
|-------------------------------------------|----------------------------------------------------------------|:------------------:|:---------------------:|
| spring.datasource.url                     | jdbc:mysql://localhost:3306/refy?createDatabaseIfNotExist=true | :white_check_mark: |          :x:          | 
| server.port                               | 1989                                                           | :white_check_mark: |           /           |
| spring.datasource.username                | root                                                           | :white_check_mark: |  :white_check_mark:   |
| spring.jpa.generate-ddl                   | update                                                         |        :x:         |          :x:          |
| spring.jpa.hibernate.ddl.auto             | auto                                                           |        :x:         |          :x:          |           
| spring.jpa.properties.hibernate.dialect   | org.hibernate.dialect.MySQL8Dialect                            |        :x:         |          :x:          |           
| spring.mvc.dispatch-options-request       | true                                                           |        :x:         |          :x:          |           
| spring.servlet.multipart.max-file-size    | 10MB                                                           | :white_check_mark: |           /           |           
| spring.servlet.multipart.max-request-size | 10MB                                                           | :white_check_mark: |           /           |

The **spring.datasource.username** if is not set is used the default password of the MySQL environment

#### Custom configuration

To customize the properties to launch the backend service you must create a file **in the same folder where you placed
the server file (.jar)** and call it **"custom.properties"** as below:

``` bash
  folderOfWhereYouPlacedTheServerFile
   |-- custom.properties
   |-- refy.jar
  ```

If your custom properties do not contain the properties of the default configuration will be used these default
properties instead,
so if you need to change some default properties you have to overwrite them.

Take a look to the official page of **Spring** for a high
customization <a href="https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html">
here</a>

### Run the service

To start the **Refy** service on your own infrastructure you have to follow these steps

#### Requirements

- At least **Java 18 JDK** installed on your machine
- An SQL environment installed, it is recommended to use **MySQL**
- Before run the backend service, **for the first time only**, you need to store in the **profiles** subfolder **(created automatically by
  the server)** the default profile pic as below:
  ``` bash
  folderOfWhereYouPlacedTheServerFile
   |-- resources
   |   |-- profiles
   |       |-- defProfilePic.png
   |-- refy.jar
  ```
  This action is required to provide the **defProfilePic** as logo or profile for the users.
  You can take a look directly <a href="https://github.com/N7ghtm4r3/Refy/tree/main/images">here</a>
  to get more information and to get the **defProfilePic** file to store in the **resources** folder
- The SQL service running on "localhost:3306/refy" by default, or if has been customized, with the custom data to
  format correctly the connection URL

#### Launch the service

When you have to start the service you will have different scenarios:

- At the first launch the server will be interrupted and will be thrown the
  **SaveData** exception to store the server secret to manage the user accesses to
  the server, share it **only to the users that you retains allowed to access to your server**
  ``` java
  Exception in thread "main" com.tecknobit.apimanager.exceptions.SaveData: Note: is not an error, but is an alert!
  Please you should safely save: the_server_secret_generated to correctly register a new user in the Refy system
  ```
- If is not the first launch the service will start directly
- If you need to recreate the server secret you need to launch the service with the **rss** command like this:
  ``` java
  java -jar Refy.jar rss // this will generate a new server secret overwriting the current server secret
  ```
- If you need to delete the server secret, just note that when the service will be launched again will be generated a
  new
  server secret to work correctly, you need to launch the service with the **dss** or **dssi** command like this:
  ``` java
  // dss command
  java -jar Refy.jar dss // this will delete the current server secret
  
   // dssi command
  java -jar Refy.jar dssi // this will delete the current server secret and interrupts the server workflow right next
  ```

## Authors

- [@N7ghtm4r3](https://www.github.com/N7ghtm4r3)

## Support

If you need help using the library or encounter any problems or bugs, please contact us via the following links:

- Support via <a href="mailto:infotecknobitcompany@gmail.com">email</a>
- Support via <a href="https://github.com/N7ghtm4r3/Refy/issues/new">GitHub</a>

Thank you for your help!

## Badges

[![](https://img.shields.io/badge/Google_Play-414141?style=for-the-badge&logo=google-play&logoColor=white)](https://play.google.com/store/apps/developer?id=Tecknobit)
[![Twitter](https://img.shields.io/badge/Twitter-1DA1F2?style=for-the-badge&logo=twitter&logoColor=white)](https://twitter.com/tecknobit)

[![](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)](https://www.oracle.com/java/)
[![](https://img.shields.io/badge/Kotlin-0095D5?&style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org/)

[![](https://img.shields.io/badge/Spring_Boot-F2F4F9?style=for-the-badge&logo=spring-boot)](https://spring.io/projects/spring-boot)

## Donations

If you want support project and developer

| Crypto                                                                                              | Address                                        | Network  |
|-----------------------------------------------------------------------------------------------------|------------------------------------------------|----------|
| ![](https://img.shields.io/badge/Bitcoin-000000?style=for-the-badge&logo=bitcoin&logoColor=white)   | **3H3jyCzcRmnxroHthuXh22GXXSmizin2yp**         | Bitcoin  |
| ![](https://img.shields.io/badge/Ethereum-3C3C3D?style=for-the-badge&logo=Ethereum&logoColor=white) | **0x1b45bc41efeb3ed655b078f95086f25fc83345c4** | Ethereum |

If you want support project and developer
with <a href="https://www.paypal.com/donate/?hosted_button_id=5QMN5UQH7LDT4">PayPal</a>

Copyright © 2024 Tecknobit
