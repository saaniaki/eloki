# EECS 4480 Computer Security Project

In this project, we evaluate the overall feasibility and effectiveness of conducting successful MWAP
attacks. To make our analysis more realistic, we used free and readily available tools traditionally
deployed for application layer testing and DDoS attacks.

This website is up and running at the following link:
http://www.eloki.tk

```
./target        The sample target website
./nodecode      JS code to start working with Headless Chrome and Firefox
./eloki         eLoki project, a Maven project
./setup         A guide to setting up AWStats on Ubuntu
./tools         A brief description of analyzed tools
```

To start working on eLoki, open it as a Maven project in your favourite IDE and download the dependencies mentioned in the `pom.xml`. To make eLoki use Tor proxy, open the Tor browser and make sure that it is running the proxy on `127.0.0.1:9150`.

NOTE: By setting the “MaxCircuitDirtiness” parameter of Tor’s browser to one
minute, we can benefit the most from Loki’s request distribution feature.