package org.reldb.dbrowser.ui.content.rel;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.reldb.dbrowser.ui.DbConnection;
import org.reldb.dbrowser.ui.RevDatabase;
import org.reldb.rel.client.Attribute;
import org.reldb.rel.client.Heading;
import org.reldb.rel.client.NullTuples;
import org.reldb.rel.client.Tuple;
import org.reldb.rel.client.Tuples;
import org.reldb.rel.client.Value;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;

import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.DisposeEvent;

public class ExporterDialog extends Dialog {
	private Shell shlExportToFile;
	
	private FileDialog exportDialog;

	private String name;
	private Value result;
	
	private Button btnRadioButtonCSV;
	private Button btnRadioButtonXLS;
	private Button btnRadioButtonXLSX;
	
	private static String lastPath = null;
	
	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public ExporterDialog(Shell parent, String name, Value result) {
		super(parent, SWT.DIALOG_TRIM | SWT.RESIZE);
		exportDialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.SAVE);
		exportDialog.setOverwrite(true);
		this.name = name;
		this.result = result;
		setText("Export to File");
	}

	/**
	 * Open the dialog.
	 */
	public void open() {
		createContents();
		setupExportToCSV();
		if (lastPath == null)
			lastPath = System.getProperty("user.home");
		exportDialog.setFilterPath(lastPath);
		exportDialog.setFileName(name);
		shlExportToFile.open();
		shlExportToFile.layout();
		Display display = getParent().getDisplay();
		while (!shlExportToFile.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
	
	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shlExportToFile = new Shell(getParent(), getStyle());
		shlExportToFile.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				lastPath = exportDialog.getFileName();
			}
		});
		shlExportToFile.setSize(600, 200);
		shlExportToFile.setText("Export to File");
		shlExportToFile.setLayout(new FormLayout());
		
		Group group = new Group(shlExportToFile, SWT.NONE);
		FormData fd_group = new FormData();
		fd_group.top = new FormAttachment(0, 10);
		fd_group.right = new FormAttachment(100, -10);
		group.setLayoutData(fd_group);
		
		btnRadioButtonCSV = new Button(group, SWT.RADIO);
		btnRadioButtonCSV.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setupExportToCSV();
			}
		});
		btnRadioButtonCSV.setBounds(10, 10, 484, 18);
		btnRadioButtonCSV.setText("CSV text file");
		btnRadioButtonCSV.setSelection(true);
		
		btnRadioButtonXLS = new Button(group, SWT.RADIO);
		btnRadioButtonXLS.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setupExportToXLS();
			}
		});
		btnRadioButtonXLS.setBounds(10, 34, 484, 18);
		btnRadioButtonXLS.setText("Excel spreadsheet file (.XLS)");
		
		btnRadioButtonXLSX = new Button(group, SWT.RADIO);
		btnRadioButtonXLSX.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setupExportToXLSX();
			}
		});
		btnRadioButtonXLSX.setBounds(10, 58, 484, 18);
		btnRadioButtonXLSX.setText("Excel spreadsheet file (.XLSX)");
		
		Label lblExportTo = new Label(shlExportToFile, SWT.NONE);
		fd_group.left = new FormAttachment(lblExportTo, 6);
		FormData fd_lblExportTo = new FormData();
		fd_lblExportTo.top = new FormAttachment(0, 10);
		fd_lblExportTo.left = new FormAttachment(0, 10);
		lblExportTo.setLayoutData(fd_lblExportTo);
		lblExportTo.setText("Export to:");
		
		Button btnExport = new Button(shlExportToFile, SWT.NONE);
		btnExport.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				doExport();
			}
		});
		FormData fd_btnExport = new FormData();
		fd_btnExport.top = new FormAttachment(group, 6);
		fd_btnExport.right = new FormAttachment(100, -10);
		btnExport.setLayoutData(fd_btnExport);
		btnExport.setText("Export");
		
		Button btnCancel = new Button(shlExportToFile, SWT.NONE);
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				close();
			}
		});
		btnCancel.setSelection(true);
		FormData fd_btnNewButton = new FormData();
		fd_btnNewButton.top = new FormAttachment(group, 6);
		fd_btnNewButton.right = new FormAttachment(btnExport, -10);
		btnCancel.setLayoutData(fd_btnNewButton);
		btnCancel.setText("Cancel");
		
		shlExportToFile.pack();
	}

	private void setupExportToCSV() {
		exportDialog.setText("Export to CSV File");
		exportDialog.setFilterExtensions(new String[] {"*.csv", "*.*"});
		exportDialog.setFilterNames(new String[] {"CSV file", "All Files"});
	}
	
	private void setupExportToXLS() {
		exportDialog.setText("Export to Excel File");
		exportDialog.setFilterExtensions(new String[] {"*.xls", "*.*"});
		exportDialog.setFilterNames(new String[] {"Excel spreadsheet file", "All Files"});
	}
	
	private void setupExportToXLSX() {
		exportDialog.setText("Export to Excel File");
		exportDialog.setFilterExtensions(new String[] {"*.xlsx", "*.*"});
		exportDialog.setFilterNames(new String[] {"Excel spreadsheet file", "All Files"});
	}

	private void doExport() {
		if (result == null || result instanceof NullTuples) {
			MessageDialog.openError(shlExportToFile, "Export Error", "Unable to export due to error.");
			return;
		}

		if (!(result instanceof Tuples)) {
			MessageDialog.openError(shlExportToFile, "Export Error", "Unable to export due to " + result);
			return;
		}
			
		String fname = exportDialog.open();
		if (fname == null)
			return;
						
		Tuples tuples = (Tuples)result;
		
		File file = new File(fname);
		try {
			if (btnRadioButtonCSV.getSelection())
				emitCSV(file, tuples);
			else if (btnRadioButtonXLS.getSelection())
				emitXLS(file, tuples);
			else
				emitXLSX(file, tuples);
			MessageDialog.openInformation(shlExportToFile, "Export to File", "Successfully exported " + name + " to " + file + ".");
			close();
		} catch (Exception error) {
			MessageDialog.openError(shlExportToFile, "Export Error", "Unable to export due to " + error);
		}
	}

	private void emitCSV(File file, Tuples tuples) throws FileNotFoundException {
		PrintWriter writer = null;
		try {
		    writer = new PrintWriter(file);
		    writer.println(tuples.getHeading().toCSV());
			Iterator<Tuple> tupleIterator = tuples.iterator();
			while (tupleIterator.hasNext()) {
				Tuple tuple = tupleIterator.next();
				writer.println(tuple.toCSV());
			}
		} finally {
			if (writer != null)
				writer.close();
		}
	}

	private void emitXLS(File file, Tuples tuples) throws IOException {
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet(name);
		int rownum = 0;
	    Heading resultsetHeading = tuples.getHeading();
		Row row = sheet.createRow(rownum++);
		int column = 0;
	    for (Attribute attribute: resultsetHeading.toArray()) {
			Cell cell = row.createCell(column++);
			cell.setCellValue(attribute.getName());
	    }
		Iterator<Tuple> tupleIterator = tuples.iterator();
		while (tupleIterator.hasNext()) {
			Tuple tuple = tupleIterator.next();
			row = sheet.createRow(rownum++);
			for (column=0; column < tuple.getAttributeCount(); column++) {
				Value value = tuple.get(column);
				Cell cell = row.createCell(column);
				cell.setCellValue(value.toString());
			}
		}	
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(file);
			workbook.write(out);
		} finally {
			if (out != null)
				out.close();
		}
		workbook.close();
	}
	
	private void emitXLSX(File file, Tuples tuples) throws IOException {
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet(name);
		int rownum = 0;
	    Heading resultsetHeading = tuples.getHeading();
		Row row = sheet.createRow(rownum++);
		int column = 0;
	    for (Attribute attribute: resultsetHeading.toArray()) {
			Cell cell = row.createCell(column++);
			cell.setCellValue(attribute.getName());
	    }
		Iterator<Tuple> tupleIterator = tuples.iterator();
		while (tupleIterator.hasNext()) {
			Tuple tuple = tupleIterator.next();
			row = sheet.createRow(rownum++);
			for (column=0; column < tuple.getAttributeCount(); column++) {
				Value value = tuple.get(column);
				Cell cell = row.createCell(column);
				cell.setCellValue(value.toString());
			}
		}	
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(file);
			workbook.write(out);
		} finally {
			if (out != null)
				out.close();
		}
		workbook.close();
	}

	public void close() {
		shlExportToFile.dispose();
	}

	public static void runQueryToExport(Shell shell, DbConnection connection, String name, String query) {
		Value result = connection.evaluate(query);
		if (!(result instanceof NullTuples))
			(new ExporterDialog(shell, name, result)).open();
	}

	public static void runQueryToExport(Shell shell, RevDatabase database, String name, String query) {
		Value result = database.evaluate(query);
		if (!(result instanceof NullTuples))
			(new ExporterDialog(shell, name, result)).open();
	}
}
