/**
 * 
 */
package com.example.eclipse.wizards;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;

/**
 * Creates a new project containing folders and files for use with an
 * Example.com enterprise web site.
 * 
 * @author Nathan A. Good &lt;mail@nathanagood.com&gt;
 * 
 */
public class NewSpecificationProjectWizard extends Wizard implements INewWizard,
		IExecutableExtension {

	/*
	 * Use the WizardNewProjectCreationPage, which is provided by the Eclipse
	 * framework.
	 */
	private WizardNewProjectCreationPage wizardPage;

	private IConfigurationElement config;

	private IWorkbench workbench;

	private IStructuredSelection selection;

	private IProject project;
	Text activityName;
	Text templatePath;

	/**
	 * Constructor
	 */
	public NewSpecificationProjectWizard() {
		super();
	}

	public void addPages() {
		/*
		 * Unlike the custom new wizard, we just add the pre-defined one and
		 * don't necessarily define our own.
		 */
		wizardPage = new WizardNewProjectCreationPage("NewIoTSuiteProject");
		wizardPage.setDescription("Create a new IoT Suite Project.");
		wizardPage.setTitle("New IoT Suite Project");

		addPage(wizardPage);
	}

	@Override
	public boolean performFinish() {

		if (project != null) {
			return true;
		}

		final IProject projectHandle = wizardPage.getProjectHandle();

		URI projectURI = (!wizardPage.useDefaults()) ? wizardPage
				.getLocationURI() : null;

		IWorkspace workspace = ResourcesPlugin.getWorkspace();

		final IProjectDescription desc = workspace
				.newProjectDescription(projectHandle.getName());

		desc.setLocationURI(projectURI);

		/*
		 * Just like the NewFileWizard, but this time with an operation object
		 * that modifies workspaces.
		 */
		WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
			protected void execute(IProgressMonitor monitor)
					throws CoreException {
				createProject(desc, projectHandle, monitor);
			}
		};

		/*
		 * This isn't as robust as the code in the BasicNewProjectResourceWizard
		 * class. Consider beefing this up to improve error handling.
		 */
		try {
			getContainer().run(true, true, op);
		} catch (InterruptedException e) {
			return false;
		} catch (InvocationTargetException e) {
			Throwable realException = e.getTargetException();
			MessageDialog.openError(getShell(), "Error",
					realException.getMessage());
			return false;
		}

		project = projectHandle;

		if (project == null) {
			return false;
		}

		BasicNewProjectResourceWizard.updatePerspective(config);
		BasicNewProjectResourceWizard.selectAndReveal(project,
				workbench.getActiveWorkbenchWindow());

		return true;
	}

	/**
	 * This creates the project in the workspace.
	 * 
	 * @param description
	 * @param projectHandle
	 * @param monitor
	 * @throws CoreException
	 * @throws OperationCanceledException
	 */
	void createProject(IProjectDescription description, IProject proj,
			IProgressMonitor monitor) throws CoreException,
			OperationCanceledException {
		try {

			monitor.beginTask("", 2000);

			proj.create(description, new SubProgressMonitor(monitor, 1000));

			if (monitor.isCanceled()) {
				throw new OperationCanceledException();
			}

			proj.open(IResource.BACKGROUND_REFRESH, new SubProgressMonitor(
					monitor, 1000));

			/*
			 * Okay, now we have the project and we can do more things with it
			 * before updating the perspective.
			 */
			IContainer container = (IContainer) proj;

			IFolder folderSrc = proj.getFolder("src");
			folderSrc.create(false, false, monitor);

			IFile depFile = ResourcesPlugin.getWorkspace().getRoot()
					.getFolder(folderSrc.getFullPath()).getFile("deploy.mydsl");
			
			if (depFile.exists()) {
				depFile.setContents(NewXHTMLFileWizard.openContentStream("deploy"), true, true, monitor);
			} else {
				depFile.create(NewXHTMLFileWizard.openContentStream("deploy"), true, monitor);
			}

			/*IFolder folderLib = proj.getFolder("lib");
			folderLib.create(false, false, monitor);*/
			 folderSrc = proj.getFolder("src");
			//folderSrc.create(false, false, monitor);

			IFile vocFile = ResourcesPlugin.getWorkspace().getRoot()
					.getFolder(folderSrc.getFullPath()).getFile("vocab.mydsl");
			
			if (vocFile.exists()) {
				vocFile.setContents(NewXHTMLFileWizard.openContentStream("vocab"), true, true, monitor);
			} else {
				vocFile.create(NewXHTMLFileWizard.openContentStream("vocab"), true, monitor);
			}
			
			IFile arcFile = ResourcesPlugin.getWorkspace().getRoot()
					.getFolder(folderSrc.getFullPath()).getFile("arch.mydsl");
			
			if (arcFile.exists()) {
				arcFile.setContents(NewXHTMLFileWizard.openContentStream("arch"), true, true, monitor);
			} else {
				arcFile.create(NewXHTMLFileWizard.openContentStream("arch"), true, monitor);
			}

			
			IFile userinteractionFile = ResourcesPlugin.getWorkspace().getRoot()
					.getFolder(folderSrc.getFullPath()).getFile("userinteraction.mydsl");
			
			if (userinteractionFile.exists()) {
				userinteractionFile.setContents(NewXHTMLFileWizard.openContentStream("interaction"), true, true, monitor);
			} else {
				userinteractionFile.create(NewXHTMLFileWizard.openContentStream("interaction"), true, monitor);
			}
			
			
		/*	IFolder folderGen = proj.getFolder("gen");
			folderGen.create(false, false, monitor);*/
			
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IWorkspaceRoot root = workspace.getRoot();
			//IPath location = root.getLocation();

			//IoTSuitePropHelper prop = IoTSuitePropHelper.getInstance();

		} finally {
			monitor.done();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench,
	 * org.eclipse.jface.viewers.IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
		this.workbench = workbench;
	}

	/**
	 * Sets the initialization data for the wizard.
	 */
	public void setInitializationData(IConfigurationElement config,
			String propertyName, Object data) throws CoreException {
		this.config = config;
	}

}
