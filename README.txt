             Quick intro to building Jung2 jars with maven2


***********    Get Maven   *************

Download and install maven2 from maven.apache.org

http://maven.apache.org/download.html

At time of writing, the latest version was maven-2.0.4

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

Jung2 uses a generics-enabled version of the jakarta
commons-collections library. The name of that library is:

  collections-generic-4.01.jar

When maven fails to build jung2 because it could not find
that jar file, maven will provide you with the command
to install the file in your local repository.

First, download the collections-generic project from
sourceforge:

http://sourceforge.net/project/showfiles.php?group_id=139125

Download either the tar.gz or the zip file, and inflate the
file someplace.

The maven command to place the needed jar file in your 
local repository looks like this:

mvn install:install-file -DgroupId=collections-generic \
 -DartifactId=collections-generic -Dversion=4.01 \
 -Dpackaging=jar -Dfile=/path/to/file/collections-generic-4.01.jar

Once you have done that, the build should complete successfully.




***********  Prepare Jung2 for Eclipse  **********************

To prepare jung2 for eclipse, run the following maven
command:

    mvn eclipse:eclipse

which will generate the .classpath and .project files for eclipse.

The .classpath file will make reference to a M2_REPO variable,
which you must define in eclipse, so that M2_REPO points to
your local repository. You can do that in eclipse by bringing
up project properties and adding the variable M2_HOME, or you
can run the following command to have maven set the variable
for you:

    mvn -Declipse.workspace=<path-to-eclipse-workspace> eclipse:add-maven-repo 
    
If that does not work, you'll need to open one of the projects properties
and use the 'add variables' button in the 'libraries' tab.


To load jung2 in eclipse, you need to overcome an
eclipse limitation: Eclipse projects cannot contain
subprojects. (Jung2 contains 6 sub-projects).
The common work-around is to make eclipse think that
each sub-project is a top-level project.

The most common way to proceed is as follows:

Add each subproject (jung-api, jung-graph-impl, jung-visualization, 
jung-algorithms, jung-samples, jung-io) as a top-level project in eclipse, each 
with its own classpath dependencies. 

One approach is to use the eclipse feature for importing existing projects
AFTER mvn eclipse:eclipse has been run as shown above.
Simply point the eclipse import project file chooser to the jung2 directory,
then check off the list of subprojects that are shown. You can import
all of the subprojects at once this way.

Another approach is to manually add each subproject as follows:
In the 'New Project' dialog, select 'Java Project', then
'Create project from existing source'. Create the new project to point 
to where you downloaded jung2 and its subprojects.
For example, you would create a new project from the existing
source in '/where/it/is/jung2/jung-api'
and name that project 'jung-api'.

Because you previously ran mvn eclipse:eclipse at the jung2 directory
level, then the projects will already reference the other
projects they depend on (instead of the jar from those projects).

You do not want to use jung2 (the parent project) as the eclipse project,
as each eclipse project can have only one classpath, and you would then
have difficulty maintaining the correct dependencies between the
sub-projects.




************* Run some Jung2 demos  *******************************************

Once you have built everything (preceding instructions), here is a straightforward
way to run some demos from the command line:

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
out jung2 using eclipse, HOWEVER, because of the above stated limitation
that eclipse cannot manage nested projects, you must use the following
trick:

Create a new workspace that you will be using only to check out the
project. You will not be using this workspace to work on the project.
Let's call the new workspace $HOME/checkout_base.
From that workspace, use eclipse to check out jung2 from cvs.
Next, open a command prompt console and change directory to the
newly created $HOME/checkout_base/jung2.
Execute this command:

  mvn eclipse:eclipse

That will build the eclipse artifacts

Next, change eclipse to point to a different workspace, one that you
will actually be working in. Use the above instructions to import the
jung2 subprojects from $HOME/checkout_base/jung2 into your real
workspace.

(*** end special eclipse cvs instructions ***) 
