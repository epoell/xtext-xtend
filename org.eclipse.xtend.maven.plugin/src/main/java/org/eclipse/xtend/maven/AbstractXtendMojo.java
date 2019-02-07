package org.eclipse.xtend.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import com.google.inject.Inject;

public abstract class AbstractXtendMojo extends AbstractMojo {

	private static final Object lock = new Object();

	@Inject
	protected MavenLog4JConfigurator log4jConfigurator;

	/**
	 * The project itself. This parameter is set by maven.
	 */
	@Parameter(property="project", required=true)
	protected MavenProject project;

	/**
	 * Set this to true to skip compiling Xtend sources.
	 */
	@Parameter(property="skipXtend", defaultValue="false")
	protected boolean skipXtend;

	public AbstractXtendMojo() {
		injectMembers();
	}

	public void execute() throws MojoExecutionException, MojoFailureException {
		if (isSkipped()) {
			getLog().info("skipped.");
		} else {
			synchronized(lock) {
				log4jConfigurator.configureLog4j(getLog());
				internalExecute();
			}
		}
	}

	protected void injectMembers() {
		new XtendMavenStandaloneSetup().createInjectorAndDoEMFRegistration().injectMembers(this);
	}

	protected abstract void internalExecute() throws MojoExecutionException, MojoFailureException;

	protected boolean isSkipped() {
		return skipXtend;
	}

}
