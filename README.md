# Build:
Please first make sure to have Maven as well as Java 21 installed.  
Run the `package` goal through maven, for example by using the command `mvn package` in
the root directory of this project.  
You will find the executable JAR file in /target which you can run using `java -jar <Filename.jar>`.

We strongly recommend moving this file into an empty directory on your computer and running it from there!  
This is to prevent any unforeseen file collisions.

# Usage:
**Note:** For simplicity we will assume you start the program with a command `WebCrawler`  
while in reality you will likely use something more similar to `java -jar WebCrawler.jar`.

Only the URL is a required argument: `WebCrawler https://example.org/`  
This will NOT generate a report and only recursively store the page with a max depth of 10!  
For an overview of all options, please run `WebCrawler --help`.  
The option format closely follows the bash standard. These all mean the same thing:  
- --depth=10
- --depth 10
- -d 10

## Formal task requirements:
To fulfill the Moodle task requirements use the --depth option to set a maxDepth and specify --report to get a markdown report.  
You will find the crawled page as well as the report in ./crawl (unless otherwise specified by --output).