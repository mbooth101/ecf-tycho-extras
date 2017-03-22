/*******************************************************************************
 * Copyright (c) 2017 Red Hat Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Red Hat Inc. - initial API and implementation
 *******************************************************************************/
package org.eclipse.ecf.tycho.extras.buildtimestamp.jgit;

import java.util.Date;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.eclipse.tycho.buildversion.BuildTimestampProvider;
import org.eclipse.tycho.extras.buildtimestamp.jgit.JGitBuildTimestampProvider;

/**
 * Same as {@link JGitBuildTimestampProvider} but allows you to specify a
 * "minimum" timestamp where if no commits are detected since the given
 * timestamp, then the given timestamp is used. This is useful to avoid versions
 * going backwards if a project that previously used build timestamps switched
 * to git timestamps, but the last commit time pre-dates the last build time.
 * 
 * <pre>
 * &lt;jgit.minimum&gt;1483228800&lt;/jgit.minimum&gt;
 * </pre>
 */
@Component(role = BuildTimestampProvider.class, hint = "jgit-minimum")
public class JGitMinBuildTimestampProvider extends JGitBuildTimestampProvider {

	@Override
	public Date getTimestamp(MavenSession session, MavenProject project, MojoExecution execution)
			throws MojoExecutionException {
		Date commitTimestamp = super.getTimestamp(session, project, execution);
		Date minTimestamp = getMinimumTimestamp(execution);
		if (commitTimestamp.after(minTimestamp)) {
			return commitTimestamp;
		} else {
			return minTimestamp;
		}
	}

	private static Date getMinimumTimestamp(MojoExecution execution) throws MojoExecutionException {
		Xpp3Dom pluginConfiguration = (Xpp3Dom) execution.getPlugin().getConfiguration();
		if (pluginConfiguration == null) {
			return new Date(0);
		}
		Xpp3Dom ignoreDom = pluginConfiguration.getChild("jgit.minimum");
		if (ignoreDom == null) {
			return new Date(0);
		}
		try {
			long stamp = Long.parseLong(ignoreDom.getValue());
			return new Date(stamp * 1000);
		} catch (NumberFormatException e) {
			throw new MojoExecutionException("jgit.minimum could not be parsed as a timestamp", e);
		}
	}
}
