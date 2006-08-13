             Quick intro to building jung2 jars with maven2



Download and install maven2 from maven.apache.org

http://maven.apache.org/download.html

At time of writing, the latest version was maven-2.0.4

Install the downloaded maven2 (there are installation instructions
here: http://maven.apache.org/download.html#Installation)

Follow the installation instructions and confirm a successful
installation by typing 'mvn --version' in a command terminal
window.


Get the jung2 code from CVS:

cvs -z3 -d:ext:your-login@jung.cvs.sourceforge.net:/cvsroot/jung co -P jung2

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
subprojects. (Jung2 contains 5 sub-projects).
The common work-around is to make eclipse think that
each sub-project is a top-level project.

The most common way to proceed is as follows:

Add each subproject (jung-api, jung-graph-impl, jung-visualization, 
jung-algorithms, jung-samples) as a top-level project in eclipse, each 
with its own classpath dependencies. 

If you previously ran mvn eclipse:eclipse at the jung2 directory
level, then the projects will already reference the other
projects they depend on (instead of the jar from those projects).

You do not want to use jung2 (the parent project) as the eclipse project,
as each eclipse project can have only one classpath, and you would then
have difficulty maintaining the correct dependencies between the
sub-projects.







