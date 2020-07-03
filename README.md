# Malwareless Web Analytics Pollution (MWAP) Attack Tool

> eLoki has been crafted for my EECS 4480 Computer Security Project at York University. It will be maintained and extended as active research.

In this project, we evaluate the overall feasibility and effectiveness of conducting successful MWAP
attacks. To make our analysis more realistic, we used free and readily available tools traditionally
deployed for application layer testing and DDoS attacks.

This website is up and running at http://www.eloki.tk

```bash
./target        # The sample target website
./nodecode      # JS code to start working with Headless Chrome and Firefox, includes other needed JS code
./eloki         # eLoki project, a Maven project
./setup         # A guide to setting up AWStats on Ubuntu
./tools         # A brief description of analyzed tools
```

## Setting Up the Target

After setting up a server (an EC2 or any other technology), log in to the terminal and set up an apache server. Apache is not a requirement, but the Apache2 on Ubuntu flavour was chosen to make a framework for this project.

> A great place to start: https://www.digitalocean.com/community/tutorials/how-to-install-the-apache-web-server-on-ubuntu-18-04

After getting to the terminal, install apache2:
```bash
$ sudo apt update
$ sudo apt upgrade
$ sudo apt install apache2
$ sudo apt install awstats libgeo-ip-perl libgeo-ipfree-perl
$ sudo systemctl status apache2
```

Now, setup the website folder:
```bash
$ sudo mkdir /var/www/<example>
$ sudo chown -R $USER:$USER /var/www/<example>
$ sudo chmod -R 755 /var/www/<example>
$ nano /var/www/<example>/index.html               # Put a test text in this file
```

Next, let's notify Apache about this location; run the following:

```bash
$ sudo nano /etc/apache2/sites-available/<example>.conf
```

You can use the following configuration:

```
<VirtualHost *:80>
    ServerAdmin saaniaki@gmail.com
    ServerName <domain_name>
    ServerAlias www.<domain_name>
    DocumentRoot /var/www/<example>
    ErrorLog ${APACHE_LOG_DIR}/<example>_error.log
    CustomLog ${APACHE_LOG_DIR}/<example>_access.log combined
</VirtualHost>
```

Then, enable the new configuration:
```bash
$ sudo a2ensite eecs.conf
$ sudo a2dissite 000-default.conf
$ sudo apache2ctl configtest
$ sudo systemctl restart apache2
# Only on localhost:
$ sudo nano /etc/hosts              # Add the domain and point to 127.0.0.1
```

The `/var/www/<example>/index.html` should be live now. Remove it and copy everything inside the `./target` folder of this repository and put it in the `/var/www/<example>` folder on your server.

## Setting Up Google Analytics

> Good place to start: https://support.google.com/analytics/answer/1008080

After setting up a google account and enabling Google Analytics, the guide will ask you to put the following code snippet on every page of your website. In the proposal files of this repository, <GAToken> is `UA-157513426-1`.

```html
<!-- Global site tag (gtag.js) - Google Analytics -->
<script async src="https://www.googletagmanager.com/gtag/js?id=<GAToken>"></script>
<script>
    window.dataLayer = window.dataLayer || [];
    function gtag() { dataLayer.push(arguments); }
    gtag('js', new Date());
    gtag('config', '<GAToken>');
</script>
```

Make sure you change the <GAToken> and put your target GAToken.

## Setting Up AWStats

> A great resource to start: https://www.linuxbabe.com/ubuntu/install-awstats-ubuntu-18-04-apache

To install AWStatson your server, run the following and then modify the configuration file:
```bash
$ sudo apt install awstats libgeo-ip-perl libgeo-ipfree-perl
$ sudo nano /etc/apache2/sites-available/<example>.conf
```
Now, put the following configuration before the `VirtualHost` closure tag:
```
Alias /awstatsclasses "/usr/share/awstats/lib/"
Alias /awstats-icon/ "/usr/share/awstats/icon/"
Alias /awstatscss "/usr/share/doc/awstats/examples/css"
ScriptAlias /cgi-bin/ /usr/lib/cgi-bin/
ScriptAlias /awstats/ /usr/lib/cgi-bin/
Options +ExecCGI -MultiViews +SymLinksIfOwnerMatch
```

Next, enable `cgi` and restart apache:
```bash
$ sudo a2enmod cgi
$ sudo systemctl restart apache2
```

Then set up the AWStats:
```bash
$ sudo cp /etc/awstats/awstats.conf /etc/awstats/awstats.<example>.conf
$ sudo nano /etc/awstats/awstats.<example>.conf
```
Now, change the following lines to suit your own configuration:
```
LogFile="/var/log/apache2/<example>_access.log"
LogFormat=1
SiteDomain="<domain_name>"
HostAliases="<domain_name> localhost 127.0.0.1"
```
Then run the following:
```bash
$ sudo setfacl -R -m "u:www-data:rx" /var/log/apache2/
```

Now <domain_name>/cgi-bin/awstats.pl should be accessible.
Finally, restrict the access to the AWStats dashboard by running the following:

```bash
$ sudo htpasswd -c /etc/apache2/htpasswd admin
```
and adding the following configueation before the `VirtualHost` closure tag:
```
<Directory "/usr/lib/cgi-bin/">
    AuthUserFile /etc/apache2/htpasswd
    AuthName "Please Enter Your Password"
    AuthType Basic
    Require valid-user
</Directory>
```

Restart the Apache webserver and AWStats should be up and running:
```bash
$ sudo systemctl restart apache2
```

NOTE: To make update button available on your dashboard, edit the `/etc/awstats/awstats.<example>.conf` file and set `AllowToUpdateStatsFromBrowser` to `1`. Next, make you need to adjust the permissions:
```bash
$ sudo setfacl -m "u:www-data:rx" /var/log/apache2/<example>_access.log
```

NOTE: All access logs are available in the `<example>_access.log`. To manually check if your requests are getting through the firewalls and hitting the server, you can run something like `cat /var/log/apache2/eecs_access.log | grep "GET /p1.html"`.

## Building eLoki

To start working on eLoki, open it as a Maven project in your favourite IDE and download the dependencies mentioned in the `pom.xml`. 

To compile and build eLoki without an IDE you can use Maven. After installing MVN, you can run the `install` plugin and then build the project using the `package` plugin.

```bash
$ sudo apt install maven
$ mvn -version
$ mvn clean                 # To clean up the target folder completly
$ mvn install               # Installs all the dependencies
$ mvn package               # Complies and makes a fat jar
```

After seeing the **BUILD SUCCESS** you can find eLoki fat jar file in the `./eloki/tearget` folder. To run it, you can execute the following:
```bash
$ java -jar eLoki-1.0-jar-with-dependencies.jar
```
To make eLoki use Tor proxy, open the Tor browser and make sure it runs the proxy on `127.0.0.1:9150`.

NOTE: By setting the `MaxCircuitDirtiness` parameter of Tor’s browser to one minute, we can benefit the most from Loki’s request distribution feature.

## Dependencies and Configuration

eLoki has been designed to be modular, extendable and scalable and uses Spring Context under the hood to handle dependency injection.

> Please take a look at https://www.baeldung.com/spring-dependency-injection for more details about Spring framework DI and IoC.

eLoki is picking up the values provided in `eloki.properties` file located under `resources` folder to make it easier to modify the `Config` class values.

NOTE: Please take a look at the Main class as an example of the following description.

When constructing an instance of `eLoki`, a `Config` object should be passed to it. The config class is a container for the following configuration:
```
String target               // The target website full URL, default: http://www.eloki.tk
String GAToken;             // The target website GAToken, default: UA-157513426-1
int threadsNumber;          // Number of threads, default: 50
int maxRequests;            // Maximum requsts to be made, default: 1000
short initMinDelay;         // Initial minimum delay in minutes, default: 1
short initMaxDelay;         // Initial maximum delay in minutes, default: 3
short minDelay;             // Minimum delay in minutes, default: 5
short maxDelay;             // Maximum delay in minutes, default: 10
short haltDelay;            // Halt delay in seconds, default: 30
boolean useTor;             // Indicates either forcing eLoki to use Tor proxy or not
String geckoDriverPath;     // Path of the Gecko Web Driver on the system
String chromeDriverPath;    // Path of the Chrome Web Driver on the system
```

Before running eLoki, make sure that you have set the `target` and `GAToken` correctly.

After instantiating a `eLoki` instance, you can pass it to a `Thread` object and start the thread:
```java
Thread t = new Thread(loki);
t.start();
```

Each `eLoki` object should be in one single thread.

eLoki has been set to use `slf4j`, which binds with `log4j2` to collect and display the generated logs systematically. The settings of `log4j2` have been located under the `resources` folder in `log4j2.properties`.

> Please take a look at https://logging.apache.org/log4j/2.x/manual/configuration.html for detailed documentation of `log4j2` configuration.

NOTE: After changing any values in the `resources`, eLoki just needs to be re-run, and there is no need for recompiling the whole application.

## Architecture

To make eLoki extendable, two main abstractions have been put in place:

1. Client
2. Provider

#### Client

eLoki relies on a `Client` implementation to make the requests and fake the browsing behaviour. `Client` interface exposes the `browse` method will be called by eLoki threads. There are currently three client implementations available:

1. HtmlUnit
2. Chrome
3. Firefox

`HtmlUnit` implements `Client` directly and uses HTTP header, browser navigation features and JavaScript to fake the browsing behaviour.

> You can take a look at HtmlUnit webste to get more information: https://htmlunit.sourceforge.io/

Since the `HtmlUnit` does not expose an automated mouse movement API, the other two clients have been created. Both `Chrome` and `Firefox` are Selenium Web Drivers, and they need the respective web driver to be available on the system, which is running eLoki. The path of the driver should be provided in the `eloki.properties`. Since these two clients are using the same interface to browse the Internet, `SeleniumClient` has been created as an abstract class to avoid duplication and ease of extension. `SeleniumClient` uses `org.openqa.selenium.WebDriver` via composition and implements the `Client` interface.

> http://chromedriver.storage.googleapis.com/index.html
> https://github.com/mozilla/geckodriver/releases

NOTE: To mark a `Client` implementation to be used, it should be annotated with `@Component` and `@Scope("prototype")`. Only one inherited class of `Client` should have these annotations at a time, or Spring DI would not be able to determine which bean is in use at runtime.

#### Provider

`Provider<T>` is a generic class that provides random elements of type `T`. All providers should be Spring Singleton beans in the service layer that provide inputs to the clients in a random fashion. For example, one of the important providers is the `BrowserProvider`, which can be injected into a `Client` and provide a random browser agent name to be faked. All providers read and load the valid values into the memory and then provide them when the `provideRandomElement` method has been called.

Providers can read values from disk, network, or they can even calculate them. As of now, all providers read values from disk, and therefore, the abstract `FromDiskProvider<T>` class has been created, which then can be extended for more specific use cases. Any class that extends `FromDiskProvider<T>` must also be annotated with `@AsDiskProvider(String path)` and provide the path of the file or folder which contains all valid values relative to the `resources` folder. `path` can be a text file or a folder which contains other text files or folders. In the case of a folder path, `FromDiskProvider<T>` children would read values recursively.

Providers can provide any primitive type or any model class. For example, `MouseRecordingProvider` provides a `List<MouseEvent>` which `SeleniumClient` knows how to work with.

## Provider Values

- BrowserProvider

Provides an agent name from all the available agent names available in the text files located under `resources/agents`. As of now, there 9448 agents listed.

- AnchorProvider

Only available in `HtmlUnit`. It provides a random link href to be clicked on. The anchor list is located at `resources/anchors`. `SeleniumClient` is not using this provides since is uses the reply of a mouse movement to click on any of the page elements.

- KeywordProvider

Only available in `HtmlUnit`. It provides a random word to be put in the `referee` HTTP header of the request. This feature is not being supported by the `SeleniumClient` and is being prevented due to security concerns. The list of keywords is located at `resources/anchors`.


- PathProvider

It provides a random path under the main domain to distribute the requests evenly. All the available paths should be listed in `resources/paths`.

- MouseRecordingProvider

Only available in `SeleniumClient`. It provides a random previously recorded mouse movement to be replied on the targetted web page. To record a new mouse movement, open a new tab in your favourite browser and copy the `mouseCapture.js` script into the JS console. Now make sure that the size of the window is exactly set to `1920x1200`. Once you are ready, hit the `ctrl` key and start browsing. Please note that the scrolling offset is also being recorded. Once you are done with browsing the page, you can hit the `ctrl` key again and copy the content of the page to a new file and locate it under `resources/mouseRecordings` folder.

