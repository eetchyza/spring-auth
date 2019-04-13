# Spring Auth

about project

## Motivation

why I did this

## Build Status
[![Build Status](<travis link>)](<travis link>)

## Getting Started

Import the library into your spring boot project via maven:

``` xml
TBC

```
Then you will need to make sure your spring boot application scans for 'com.eetchyza.springauth'

``` java
@SpringBootApplication(scanBasePackages = {"com.eetchyza.demo", "com.eetchyza.springauth"})
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}

```
Include this bean in your java configs

``` java
    @Bean
    public SecurityFilter securityFilter(){
        return new SecurityFilter();
    }

```

Next there are two interfaces you will need to implement both of these should then be mapped to your data store and domain objects

``` java

@Entity
public class Authority implements GrantedAuthority {

    @Column
    private String name;

    public Authority() {

    }

    public Authority(String name) {
        this.name = name;
    }

    @Override
    public String getAuthority() {
        return name;
    }
}

@Entity
public class User implements UserDetails {
    @Column(unique = true)
    private String username;
    @Column
    private String password;
    @Column(unique = true)
    private String emailAddress;
    @OneToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private List<Authority> authorities;
    @Column
    private boolean temporaryPassword;
    @Column
    private LocalDateTime expires;
    
    ...

```

Finally the library needs to be able to access users from your data store, to do this you will need implement the user details service and mark it with @Service to make sure spring picks it up.

``` java 

@Service
public class UserQueryService implements UserDetailsService {

    @Override
    public User loadUserByUsername(String username){
        // Fetch user from data store in here
    }
}

```

At this point your spring boot application should be all set up with spring auth and ready to go.

## API Reference

[Java Doc](https://eetchyza.co.uk/spring-auth/1.0.0/)

**Login**
----
  Authenticates a user and returns authentication data for a single user as json.

* **URL**

  /security/login

* **Method:**

  `POST`

* **Data Params**

  **Required:**
 
   `username=[string]`
   `password=[string]`

* **Success Response:**

  * **Code:** 200 <br />
    **Content:** `{ authenticationToken : 2-$=1#2421, refreshToken : ~'huZZ68, roles : [ 'STANDARD' ], username : Bob, id : 4, expire : '2019-03-13:17:35:00' }`
    
    
**Logout**
----
  Destorys a users token and returns no content.

* **URL**

  /security/logout

* **Method:**

  `GET`

* **Success Response:**

  * **Code:** 204 <br />
    
**Refresh**
----
  Regenerates tokens for a user and returns authentication data for a single user as json.

* **URL**

  /security/refresh

* **Method:**

  `POST`

* **Data Params**

  **Required:**
 
   `token=[string]`
   `refreshToken=[string]`

* **Success Response:**

  * **Code:** 200 <br />
    **Content:** `{ authenticationToken : 2-$=1#2421, refreshToken : ~'huZZ68, roles : [ 'STANDARD' ], username : Bob, id : 4, expire : '2019-03-13:17:35:00' }`

## Contribute

If you would be interested in contributing to this project, you can find out how by first reading our [Code of conduct](https://github.com/eetchyza/.github/blob/master/CODE_OF_CONDUCT.md) and our [Contributing guide lines](https://github.com/eetchyza/.github/blob/master/CONTRIBUTING.md)

## Need help?

link to chat channel

## License

MIT © Eetchyza
