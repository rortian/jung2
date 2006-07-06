Download and install maven2 from maven.apache.org

set MAVEN_HOME to point to it
add $MAVEN_HOME/bin to your PATH

Get the jung2 code from CVS:

cvs -z3 -d:ext:your-login@jung.cvs.sourceforge.net:/cvsroot/jung co -P jung2

cd jung2
mvn install

(it will fail, as you do not have the collections-generic jar, and
neither does any maven repository)

Go get collections-generic from sourceforge:
http://sourceforge.net/project/showfiles.php?group_id=139125&package_id=153011

Inflate the archive someplace, then install collections-generic-4.0.jar
in your local maven2 repository ($HOME/.m2/repository) like this one-line
command (assuming the file is in /usr/local...):

mvn install:install-file
-Dfile=/usr/local/collections-generic-4.0/collections-generic-4.0.jar
-DgroupId=collections-generic -DartifactId=collections-generic -Dpackaging=jar
-DgeneratePom=true -Dversion=4.0

cd jung2
mvn install
mvn eclipse:eclipse

the above mvn commands should succeed....

Eclipse needs to know the path to the local maven repository. 
Therefore the classpath variable M2_REPO has to be set. Execute the 
following command:

mvn -Declipse.workspace=<path-to-eclipse-workspace> eclipse:add-maven-repo 


You can now add each subproject (api, graph-impl, etc) as a top-level
project in eclipse, each with its own classpath dependencies.







