package oep.views.workflow.table;

import org.apache.oozie.client.OEPOozieClient;
import org.apache.oozie.client.OozieClient;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

/**
 * Workflow Tabular View
 * 
 * @author Chris White
 */
public class WorkflowTableView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "oep.views.workflow.table.WorkflowTableView";

	private String oozieUrl;

	/** Table content provider (rows & columns) */
	private WorkflowContentProvider contentProvider = new WorkflowContentProvider();

	/** Table label provider */
	private WorkflowLabelProvider labelProvider = new WorkflowLabelProvider();

	/** Table Viewer */
	private TableViewer viewer;

	private Action action1;
	private Action action2;

	private Action doubleClickAction;

	/** forward page action */
	private Action pageForwardAction;

	/** backwards page action */
	private Action pageBackwardsAction;

	/** Refresh table action */
	private Action refreshAction;

	/** Table */
	private Table table;

	private Action itemCount20Action;
	private Action itemCount50Action;
	private Action itemCount100Action;

	class NameSorter extends ViewerSorter {
	}

	/**
	 * The constructor.
	 */
	public WorkflowTableView() {
		setPartName("Oozie Workflows");

		OEPOozieClient client = new OEPOozieClient(
				"http://192.168.126.50:11000/oozie");
		contentProvider.setOozieClient(client);
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {
		viewer = new TableViewer(parent, SWT.FULL_SELECTION | SWT.H_SCROLL
				| SWT.V_SCROLL);
		viewer.setContentProvider(contentProvider);
		viewer.setLabelProvider(labelProvider);
		viewer.setSorter(new NameSorter());

		table = viewer.getTable();

		// configure column headers
		for (WorkflowColumn colDef : WorkflowColumn.values()) {
			createColumn(table, colDef);
		}

		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		viewer.setInput(getViewSite());

		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();

		refresh();
	}

	private void createColumn(Table table, WorkflowColumn colDef) {
		TableColumn col = new TableColumn(table, SWT.NONE);
		col.setWidth(colDef.getWidth());
		col.setText(colDef.toString());
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				WorkflowTableView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(action1);
		manager.add(action2);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(refreshAction);
		manager.add(pageBackwardsAction);
		manager.add(pageForwardAction);
	}

	private void makeActions() {
		action1 = new Action() {
			public void run() {
				showMessage("Action 1 executed");
			}
		};
		action1.setText("Action 1");
		action1.setToolTipText("Action 1 tooltip");
		action1.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));

		action2 = new Action() {
			public void run() {
				showMessage("Action 2 executed");
			}
		};
		action2.setText("Action 2");
		action2.setToolTipText("Action 2 tooltip");
		action2.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		doubleClickAction = new Action() {
			public void run() {
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection) selection)
						.getFirstElement();
				showMessage("Double-click detected on " + obj.toString());
			}
		};

		pageForwardAction = createPageAction(true);
		pageBackwardsAction = createPageAction(false);

		refreshAction = new Action("Refresh",
				Activator.getImageDescriptor("icons/refresh_tab.gif")) {
			@Override
			public void run() {
				refresh();
			}
		};

		contentProvider.setPageLength(20);
		contentProvider.setPageOffset(1);
		createItemAction(20, true);
		createItemAction(50, false);
		createItemAction(100, false);
	}

	private void createItemAction(final int pageSize, boolean selected) {
		Action pageSizeAction = new Action(String.format("%d items", pageSize),
				Action.AS_RADIO_BUTTON) {
			@Override
			public void run() {
				if (isChecked()) {
					contentProvider.setPageLength(pageSize);
					refresh();
				}
			}
		};

		pageSizeAction.setChecked(selected);

		getViewSite().getActionBars().getMenuManager().add(pageSizeAction);
	}

	private Action createPageAction(final boolean isPageForwardAction) {
		Action pageAction = new Action() {
			@Override
			public void run() {
				contentProvider.movePage(isPageForwardAction);
				refresh();
			}
		};

		pageAction.setText((isPageForwardAction ? "Next" : "Previous")
				+ " Page");
		pageAction.setImageDescriptor(PlatformUI
				.getWorkbench()
				.getSharedImages()
				.getImageDescriptor(
						isPageForwardAction ? ISharedImages.IMG_TOOL_FORWARD
								: ISharedImages.IMG_TOOL_BACK));

		return pageAction;
	}

	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});
	}

	private void showMessage(String message) {
		MessageDialog.openInformation(viewer.getControl().getShell(),
				"Workflow Table", message);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	public String getOozieUrl() {
		return oozieUrl;
	}

	public void setOozieUrl(String oozieUrl) {
		this.oozieUrl = oozieUrl;
	}

	protected void refresh() {
		// get number of items
		int totalItems = contentProvider.getTotalItemCount();

		// update actions based upon item count and current page number
		pageBackwardsAction.setEnabled(totalItems != -1
				&& contentProvider.pageOffset > 1);
		pageForwardAction
				.setEnabled(totalItems != -1
						&& contentProvider.pageOffset
								+ contentProvider.pageLength >= totalItems);

		// update label
		if (oozieUrl == null) {
			setContentDescription("OOZIE Url not configured");
			table.setEnabled(false);
		} else if (totalItems != -1) {
			setContentDescription("Communications Error, check problems view");
			table.setEnabled(false);
		} else {
			int maxItemIdx = Math.min(totalItems, contentProvider.pageOffset
					+ contentProvider.pageLength - 1);
			setContentDescription(String.format(
					"Showing Jobs %d to %d (%d in total)",
					contentProvider.pageOffset, maxItemIdx, totalItems));

			table.setEnabled(true);
		}

		viewer.refresh();
	}
}
