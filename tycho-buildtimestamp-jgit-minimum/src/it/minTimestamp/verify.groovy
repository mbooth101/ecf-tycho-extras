import java.io.*;

File feature = new File(basedir, "feature/target/site/features/buildtimestamp-jgit.feature_1.0.0.201701010000.jar"); 
if (!feature.canRead()) {
  throw new Exception( "Missing expected file " + feature );
}

File bundle = new File(basedir, "feature/target/site/plugins/buildtimestamp-jgit.bundle_1.0.0.201701010000.jar"); 
if (!bundle.canRead()) {
  throw new Exception( "Missing expected file " + bundle );
}

return true;
