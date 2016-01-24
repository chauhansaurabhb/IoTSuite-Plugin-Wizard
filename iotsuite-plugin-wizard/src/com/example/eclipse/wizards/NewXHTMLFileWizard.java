package com.example.eclipse.wizards;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

/**
 * This wizard creates a new strict XHTML file with the pretend Example.com
 * Enterprise stylesheets and javascript files.
 */
public class NewXHTMLFileWizard extends Wizard implements INewWizard {

	/*
	 * The automatically-built wizard page that will collect the user input.
	 */
	String name;
	static String contents;
	private NewXHTMLFileWizardPage page;

	private ISelection selection;

	/**
	 * Constructor for NewXHTMLFileWizard.
	 */
	public NewXHTMLFileWizard() {
		super();
		setNeedsProgressMonitor(true);
	}

	/**
	 * Adding the page to the wizard.
	 */
	public void addPages() {
		page = new NewXHTMLFileWizardPage(selection);
		addPage(page);
	}

	/**
	 * When the user clicks the "Finish" button on the wizard's UI, the wizard
	 * will call this method.
	 */
	public boolean performFinish() {
		final String containerName = page.getContainerName();
		final String fileName = page.getFileName();
		final String title = page.getTitle();

		/*
		 * Build a process that will run using the IRunnableWithProgress
		 * interface so the UI can handle showing progress bars, etc.
		 */
		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException {
				try {
					/*
					 * The method (see below) which contains the "real"
					 * implementation code.
					 */
					doFinish(containerName, fileName, title, monitor);
				} catch (CoreException e) {
					throw new InvocationTargetException(e);
				} finally {
					monitor.done();
				}
			}
		};
		try {
			/* This runs the process built above in a seperate thread */
			getContainer().run(true, false, op);
		} catch (InterruptedException e) {
			return false;
		} catch (InvocationTargetException e) {
			Throwable realException = e.getTargetException();
			MessageDialog.openError(getShell(), "Error", realException.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * The worker method. It will find the container, create the file if missing
	 * or just replace its contents, and open the editor on the newly created
	 * file.
	 */
	private void doFinish(String containerName, String fileName, String title, IProgressMonitor monitor)
			throws CoreException {

		monitor.beginTask("Creating " + fileName, 2);
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IResource resource = root.findMember(new Path(containerName));

		if (!resource.exists() || !(resource instanceof IContainer)) {
			throwCoreException("Container \"" + containerName + "\" does not exist.");
		}
		IContainer container = (IContainer) resource;

		final IFile file = container.getFile(new Path(fileName));
		try {

			InputStream stream = openContentStream(name);

			try {
				if (file.exists()) {
					file.setContents(stream, true, true, monitor);
				} else {
					file.create(stream, true, monitor);
				}
			} finally {
				stream.close();
			}

		} catch (IOException e) {
		}
		monitor.worked(1);
		monitor.setTaskName("Opening file for editing...");
		getShell().getDisplay().asyncExec(new Runnable() {
			public void run() {
				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				try {
					IDE.openEditor(page, file, true);
				} catch (PartInitException e) {
				}
			}
		});
		monitor.worked(1);
	}

	/**
	 * Initialize the file contents to contents of the given resource.
	 */
	public static InputStream openContentStream(String name) throws CoreException {
		
		
		if (name.equals("vocab")) {
			contents = null;
			contents = "structs:																					"
					+ "\n/*	TempStruct                                                                              "
					+ "\n		tempValue: double;                                                                  "
					+ "\n		unitOfMeasurement : String;   */                                "
					+ "\nresources:                                                                                 "
					+ "\n	sensors:                                                                                "
					+ "\n		periodicSensors:                                                                    "
					+ "\n		/*	TemperatureSensor                                                               "
					+ "\n				generate tempMeasurement:TempStruct;                                        "
					+ "\n				sample period 1000 for 100000;    */                               "
					+ "\n		eventDrivenSensors:                                                                 "
					+ "\n		/*	SmokeDetector                                                                   "
					+ "\n				generate smokeMeasurement:SmokeStruct;                                      "
					+ "\n				onCondition smokeValue > 650 PPM ;     */                                     "
					+ "\n		requestBasedSensors:                                                                "
					+ "\n		/*	YahooWeatherService                                                             "
					+ "\n				generate weatherMeasurement:TempStruct accessed-by locationID : String; */    "
					+ "\n		tags:	                                                                            "
					+ "\n		/*	BadgeReader                                                                     "
					+ "\n				generate badgeDetected: BadgeStruct;    */                                    "
					+ "\n		actuators:                                                                          "
					+ "\n		/*	Heater	                                                             "
					+ "\n				action Off();                                                                          "
					+ "\n				action SetTemp(setTemp:TempStruct);     */                                    "
					+ "\n		storages:                                                                           "
					+ "\n		/*	ProfileDB                                                                       "
					+ "\n				generate profile:TempStruct accessed-by badgeID:String;	 */					";

		}
		if (name.equals("arch")) {
			contents = null;
			contents = "computationalService:                                                       "
					+ "\n		Common:                                                                 "
					+ "\n		/*	AvgTemp                                                             "
					+ "\n				consume tempMeasurement from TemperatureSensor;                 "
					+ "\n				COMPUTE (AVG_BY_SAMPLE,5);                                      "
					+ "\n				generate roomAvgTempMeasurement :TempStruct;   */                 "
					+ "\n		Custom:                                                                 "
					+ "\n		  /*Proximity                                                           "
					+ "\n				consume badgeDetected from BadgeReader;                         "
					+ "\n				request profile to ProfileDB;                                   "
					+ "\n				generate tempPref: TempStruct;                                 "
					+ "\n			 DisplayController                                                  "
					+ "\n			 	consume tempMeasurement from TemperatureSensor;                 "
					+ "\n			 	consume humidityMeasurement from HumiditySensor;                "
					+ "\n			 	consume weatherMeasurement from YahooWeatherService;            "
					+ "\n			 	command DisplaySensorMeasurement(sensorMeasurement) to DashBoard; */ ";

		}

		if (name.equals("interaction")) {
			contents = null;
			contents = "/* structs:																									"
					+ "\n	VisualizationStruct                                                                                     "
					+ "\n		tempValue:double;                                                                                   "
					+ "\n		humidityValue:double;                                                                               "
					+ "\n		yahooTempValue:double; */                                                                             "
					+ "\nresources:                                                                                                 "
					+ "\n	userInteractions:                                                                                       "
					+ "\n	/*	EndUserApp                                                                                          "
					+ "\n			notify	FireNotify(fireNotify:FireStateStruct) from FireController;                             "
					+ "\n		DashBoard                                                                                           "
					+ "\n			notify DisplaySensorMeasurement(sensorMeasurement : VisualizationStruct ) from DisplayController; */";

		}

		if (name.equals("deploy")) {
			contents = null;
			contents = "devices:												"
					+ "\n /*	D1:                                                 "
					+ "\n		location:                                       "
					+ "\n			Room:1 ;                                   "
					+ "\n			platform: NodeJS;                          "
					+ "\n			resources: TemperatureSensor;              "
					+ "\n			protocol: mqtt ;                           "
					+ "\n	 D2:                                                "
					+ "\n	 	location:                                       "
					+ "\n	 		Room: 1 ;                                  "
					+ "\n	 		platform: JavaSE;                           "
					+ "\n	 		resources: ProfileDB;                      "
					+ "\n	 		protocol: mqtt  ;                          "
					+ "\n	 		database: MySQL;                           "
					+ "\n	 D3:                                                "
					+ "\n	 	location:                                       "
					+ "\n	 		Room: 1;                                    "
					+ "\n	 		platform:JavaSE ;                           "
					+ "\n	 		resources :;                                "
					+ "\n	 		protocol : mqtt;                            "
					+ "\n	 D4:	                                            "
					+ "\n	 	location:                                       "
					+ "\n	 		Room:1;                                     "
					+ "\n	 		platform:Android ;                          "
					+ "\n	 		resources : EndUserApp;                     "
					+ "\n	 		protocol: mqtt;       */                      ";

		}

		return new ByteArrayInputStream(contents.getBytes());

	}

	/**
	 * Method for consistently throwing a CoreException
	 * 
	 * @param message
	 * @throws CoreException
	 */
	private void throwCoreException(String message) throws CoreException {
		IStatus status = new Status(IStatus.ERROR, "NewFileWizard", IStatus.OK, message, null);
		throw new CoreException(status);
	}

	/**
	 * We will accept the selection in the workbench to see if we can initialize
	 * from it.
	 * 
	 * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
	}

}