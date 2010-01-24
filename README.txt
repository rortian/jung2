             Quick intro to building Jung2 jars with maven2

Ensure that you have a JDK of at least version 1.5. Ensure that your
JAVA_HOME variable is set to the location of the JDK. On a Windows
platform, you may have a separate JRE (Java Runtime Environment) and
JDK (Java Development Kit). The JRE has no capability to compile
Java source files, so you must have a JDK installed. If your
JAVA_HOME variable is set to the location of the JRE, and not the
location of the JDK, you will be unable to compile.



***********    Get Maven   *************

Download and install maven2 from maven.apache.org

http://maven.apache.org/download.html

At time of writing, the latest version was maven-2.2.1

Install the downloaded maven2 (there are installation instructions
here: http://maven.apache.org/download.html#Installation)

Follow the installation instructions and confirm a successful
installation by typing 'mvn --version' in a command terminal
window.




**********  Get Jung2   *******************

Get the jung2 code from CVS:

if you are a developer, do this:

  export CVS_RSH=ssh
  cvs -z3 -d:ext:your-login@jung.cvs.sourceforge.net:/cvsroot/jung co -P jung2

if you are a user, do this:

  cvs -z3 -d:pserver:anonymous@jung.cvs.sourceforge.net:/cvsroot/jung co -P jung2


(*** see separate instructions at the end of this file 
  for those unable to use CVS from a command-line console **)



********** Build Jung2 **********************

cd jung2
mvn install

This should build the sub-projects and run unit tests.
During the build process, maven downloads code it needs
from maven repositories. The code is cached in your
local repository that maven creates in your home
directory ($HOME/.m2/repository). If the download
of something is interrupted, the build may fail.
If so, just run it again (and again) and it should
eventually succeed.
Once all the files are cached in your local maven
repository, the build process will be faster.


***********  Prepare Jung2 for Eclipse  **********************

To prepare jung2 for eclipse, run the following maven
command:

    mvn eclipse:m2eclipse

which will generate the .classpath and .project files for eclipse.


To load jung2 in eclipse, use File -> Import -> General -> Maven Projects
and set the Root Directory to your jung2 directory. You should be able to
import all of the sub-projects, plus there sill be a jung2 project 
containing the root pom.xml file



Because you previously ran mvn eclipse:m2eclipse at the jung2 directory
level, then the projects will already reference the other
projects they depend on (instead of the jar from those projects).




************* Run some Jung2 demos  *******************************************

Once you have built everything (preceding instructions), here is a straightforward
way to run some demos from the command line:

(NOTE: you may need to change the version part of the jar names
below. It could be jung-samples-2.0-alpha2-SHAPSHOT.jar for 
example. Look at the actual jar file names to see.)

cd jung2/jung-samples/target
tar xvf jung-samples-2.0-dependencies.tar

java -cp jung-samples-2.0.jar samples.graph.VertexImageShaperDemo
java -cp jung-samples-2.0.jar samples.graph.SatelliteViewDemo
java -cp jung-samples-2.0.jar samples.graph.ShowLayouts


The jung-samples-dependencies.tar file contains all of the jar
dependencies for the jung-samples project. It was created as part
of the maven build process.




(***  Special cvs checkout instructions using eclipse  ***)

If you are unable to use cvs from the command prompt, you may check
out jung2 using eclipse.
Create a new workspace that you will be using only to check out the
project. You will not be using this workspace to work on the project.
Let's call the new workspace $HOME/checkout_base.
From that workspace, use eclipse to check out jung2 from cvs.
Next, open a command prompt console and change directory to the
newly created $HOME/checkout_base/jung2.
Execute this command:

  mvn eclipse:m2eclipse

That will build the eclipse artifacts

Next, change eclipse to point to a different workspace, one that you
will actually be working in. Use the above instructions to import the
jung2 subprojects from $HOME/checkout_base/jung2 into your real
workspace.

(*** end special eclipse cvs instructions ***) 
