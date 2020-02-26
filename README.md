[![Release](https://jitpack.io/v/albahrani/aquacontrol-server.svg)](https://jitpack.io/#albahrani/aquacontrol-server)
[![Build](https://jitci.com/gh/albahrani/aquacontrol-server/svg)](https://jitci.com/gh/albahrani/aquacontrol-server)
[![Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=com.github.albahrani.aquacontrol%3Aaquacontrol-server&metric=alert_status)](https://sonarcloud.io/dashboard?id=com.github.albahrani.aquacontrol%3Aaquacontrol-server)
[![codebeat badge](https://codebeat.co/badges/53540457-4882-4db6-b560-6a85baee9727)](https://codebeat.co/projects/github-com-albahrani-aquacontrol-server-master)
[![Known Vulnerabilities](https://snyk.io/test/github/albahrani/aquacontrol-server/badge.svg?targetFile=pom.xml)](https://snyk.io/test/github/albahrani/aquacontrol-server?targetFile=pom.xml)
[![FOSSA Status](https://app.fossa.io/api/projects/git%2Bgithub.com%2Falbahrani%2Faquacontrol-server.svg?type=shield)](https://app.fossa.io/projects/git%2Bgithub.com%2Falbahrani%2Faquacontrol-server?ref=badge_shield)
[![Hits-of-Code](https://hitsofcode.com/github/albahrani/aquacontrol-server)](https://hitsofcode.com/view/github/albahrani/aquacontrol-server)

===============
aquacontrol-server
===============
A pi4j based aquarium management server. It uses the [Pi4J](https://github.com/Pi4J/pi4j) PCA9685GpioProvider to generate PWM signals for light control. It is accessible over REST service interfaces over the integrated [RESTExpress](https://github.com/RestExpress/RestExpress) server.

## PROJECT INFORMATION
Project website: https://github.com/albahrani/aquacontrol-server <br />
Project issues list: https://github.com/albahrani/aquacontrol-server/issues <br />
<br />
No releases yet available from Maven Central.

Snapshot builds are available via
```xml
  <dependency>
    <groupId>com.github.albahrani.aquacontrol</groupId>
    <artifactId>aquacontrol-server</artifactId>
    <version>0.0.1-SNAPSHOT</version>
  </dependency>
  ...
  <repository>
    <id>sonatype-snapshots-repo</id>
    <url>https://oss.sonatype.org/content/repositories/snapshots</url>
    <releases><enabled>false</enabled></releases>
    <snapshots><enabled>true</enabled></snapshots>
  </repository>
```

## LICENSE
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0
  
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.


[![FOSSA Status](https://app.fossa.io/api/projects/git%2Bgithub.com%2Falbahrani%2Faquacontrol-server.svg?type=large)](https://app.fossa.io/projects/git%2Bgithub.com%2Falbahrani%2Faquacontrol-server?ref=badge_large)

## USAGE
### aquacontrol-server JAR
  The AquaControl server shall be packaged in an runnable jar file which can be executed on a Raspberry Pi with an JRE8 or newer.
  For executing the software you need to provide two program arguments.
  * -c *&lt;path_to_configuration&gt;* (Read-only)
  * -p *&lt;path_to_light_plan&gt;* (Read-Write)

 An example config file:
```json
{
 "channels": [
   {"id":"coolwhite","name":"Cool White", "color" : "#ffffff", "pins": ["PWM 0","PWM 4"]},
   {"id":"red", "name":"Red", "color" : "#ff0000", "pins":["PWM 1","PWM 5"]},
   {"id":"blue", "name":"Blue", "color" : "#0000ff", "pins":["PWM 2","PWM 6"]},
   {"id":"warmwhite", "name":"Warm White", "color" : "#ffeeee", "pins":["PWM 3", "PWM 7"]}   
 ]
}
```

 An example plan:
```json
{
  "channels" : [ {
    "id" : "coolwhite",
    "timetable" : [ {
      "time" : [ 10, 0 ],
      "perc" : 100.0
    }, {
      "time" : [ 17, 0 ],
      "perc" : 0.0
    }, {
      "time" : [ 19, 0 ],
      "perc" : 100.0
    }, {
      "time" : [ 22, 0 ],
      "perc" : 0.0
    } ]    
  }, {
    "id" : "red",
    "timetable" : [ {
      "time" : [ 6, 0 ],
      "perc" : 100.0
    }, {
      "time" : [ 12, 0 ],
      "perc" : 0.0
    }, {
      "time" : [ 16, 0 ],
      "perc" : 100.0
    }, {
      "time" : [ 17, 0 ],
      "perc" : 0.0
    } ]    
  }]
}
```

### REST Services
 The server is accessible via RESTfull Webservices. All Webservices support CORS preflight requests.
 The following WebServices are currently available:
 
 * **PUT /shutdown**
 Stops the aquacontrol-server software
 * **PUT /pause**
 Pause the aquacontrol-server plan execution
 * **PUT /resume**
 Resume a paused aquacontrol-server
 * **POST /channels/*{channelId}*/force**
 Force a certain value for the light channel specified by its `channelId`. This channel will be excluded from plan execution and keep the forced (static) value.
 * **DELETE /channels/*{channelId}*/clear**
 Clear the forced value of a light channel specified by its `channelId`. This channel will be included to plan  execution and is updated with its current plan value.
 * **GET /status**
 Provides a Status report about the current light channels and their values. Additionally the most important system parameters are included.
 * **GET /plan**
 Returns the currently active light plan.
 * **POST /plan**
 Uploads a new light plan and activates it.

There are several WebServices that are only available when really running on Raspberry Pi. See [pi4j-rest-sysinfo](https://github.com/albahrani/pi4j-rest-sysinfo) for the services available. They are all registered under the baseUri `/raspberry/systeminfo`.
