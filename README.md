# Malwareless Web Analytics Pollution (MWAP) Attack

> This project has been created for my EECS 4480 Computer Security Project at York University, and it will be maintained and extended as active research.

In this project, we evaluate the overall feasibility and effectiveness of conducting successful MWAP
attacks. To make our analysis more realistic, we used free and readily available tools traditionally
deployed for application layer testing and DDoS attacks.


This website is up and running at the following link:
http://www.eloki.tk

```bash
./target        # The sample target website
./nodecode      # JS code to start working with Headless Chrome and Firefox
./eloki         # eLoki project, a Maven project
./setup         # A guide to setting up AWStats on Ubuntu
./tools         # A brief description of analyzed tools
```

## Setting Up the Target

After setting up a server (an EC2 or any other technology), login to the terminal and setup an apache server. Apache is not a requirement but to make a framework for this project, the Apache2 on Ubuntu flavour was chosen.

> A great resource to start: https://www.digitalocean.com/community/tutorials/how-to-install-the-apache-web-server-on-ubuntu-18-04

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

Next, let's notify apache about this location, run the following:

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

After setting up a google account and enabling Google Analytics, the guide will ask you to put the folling code sinnept on evey page of your website. In the progonal files of this repository, <GAToken> is `UA-157513426-1`.

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

Make sure you change the <GAToken> and put your own GAToken.

## Setting Up AWStats

> A great resource to start: https://www.linuxbabe.com/ubuntu/install-awstats-ubuntu-18-04-apache

To install AWStatson your server, run the following and then modify the configuration file:
```bash
$ sudo apt install awstats libgeo-ip-perl libgeo-ipfree-perl
$ sudo nano /etc/apache2/sites-available/<example>.conf
```
Now, put the following configueation before the `VirtualHost` closure tag:
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

Then setup the AWStats:
```bash
$ sudo cp /etc/awstats/awstats.conf /etc/awstats/awstats.<example>.conf
$ sudo nano /etc/awstats/awstats.<example>.conf
```
Now, change the following lines to suite your own configuration:
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

Restart the apache webserver and AWStats should be up and running:
```bash
$ sudo systemctl restart apache2
```

NOTE: To make update button available on your dashboard, edit the `/etc/awstats/awstats.<example>.conf` file and set `AllowToUpdateStatsFromBrowser` to `1`. Next, make you need to adjust the permissions:
```bash
$ sudo setfacl -m "u:www-data:rx" /var/log/apache2/<example>_access.log
```

NOTE: All access logs are avialble in the `<example>_access.log`. So to manually check if your requests are getting through the firewalls and hitting the server, you can run something like `cat /var/log/apache2/eecs_access.log | grep "GET /p1.html"`.

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
To make eLoki use Tor proxy, open the Tor browser and make sure that it is running the proxy on `127.0.0.1:9150`.

NOTE: By setting the `MaxCircuitDirtiness` parameter of Tor’s browser to one
minute, we can benefit the most from Loki’s request distribution feature.

## Targeting a Victim

NOTE: Please take a look at the Main class as an example of the following description.

When constructing an instance of `Loki`, a `Config` object should be passed to it. The config class is a container for the following configuration:
```
String target           // The target website full URL, default: http://www.eloki.tk
String GAToken;         // The target website GAToken, default: UA-157513426-1
int threadsNumber;      // Number of threads, default: 50
int maxRequests;        // Maximum requsts to be made, default: 1000
short initMinDelay;     // Initial minimum delay in minutes, default: 1
short initMaxDelay;     // Initial maximum delay in minutes, default: 3
short minDelay;         // Minimum delay in minutes, default: 5
short maxDelay;         // Maximum delay in minutes, default: 10
short haltDelay;        // Halt delay in seconds, default: 30
```

Before running eLoki, make sure that you have set the `target` and `GAToken` correctly.

After instantiating a `Loki` instance, you can pass it to a `Thread` object and start the thread:
```java
Thread t = new Thread(loki);
t.start();
```

Each `Loki` object should be in one single thread.

> eLoki uses HtmlUnit under the hood, you can take a look at their webste to get more information: https://htmlunit.sourceforge.io/
