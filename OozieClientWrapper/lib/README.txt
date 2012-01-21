Copy the 3 jar files from your Oozie installations client directory (version numbers may be different):
 * commons-cli-1.2.jar
 * json-simple-1.1.jar
 * oozie-client-3.0.2.jar
 
If your version numbers differ, then you'll need to update the Plugin dependencies in Eclipse:
 * Open the MANIFEST.MF in Eclipse
 * Select the Runtime tab
 * In the Classpath group, remove the current jars and replace with the new versioned jars (using the Add button)