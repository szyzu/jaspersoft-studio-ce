/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.editor.preview.input;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.nebula.widgets.cdatetime.CDT;
import org.eclipse.nebula.widgets.cdatetime.CDateTime;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

import com.jaspersoft.studio.editor.preview.view.control.VParameters;
import com.jaspersoft.studio.preferences.execution.InputControlsPreferencePage;
import com.jaspersoft.studio.preferences.execution.ReportExecutionPreferencePage;
import com.jaspersoft.studio.swt.widgets.DRDateTime;

import net.sf.jasperreports.eclipse.util.Misc;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.types.date.CalendarUnit;
import net.sf.jasperreports.types.date.DateRange;
import net.sf.jasperreports.types.date.DateRangeBuilder;
import net.sf.jasperreports.types.date.DateRangeExpression;
import net.sf.jasperreports.types.date.InvalidDateRangeExpressionException;
import net.sf.jasperreports.types.date.RelativeDateRange;
import net.sf.jasperreports.types.date.TimestampRange;

public class DateInput extends ADataInput {

	protected boolean supportDateRange;

	public DateInput() {
		this(false, true);
	}

	public DateInput(boolean isNumeric, boolean supportDateRange) {
		this.isNumeric = isNumeric;
		this.supportDateRange = supportDateRange;
	}

	public boolean isForType(Class<?> valueClass) {
		return Date.class.isAssignableFrom(valueClass) || DateRange.class.isAssignableFrom(valueClass);
	}

	@Override
	public void createInput(Composite parent, final IParameter param, final Map<String, Object> params) {
		super.createInput(parent, param, params);

		Class<?> valueClass = param.getValueClass();
		if (java.sql.Date.class.isAssignableFrom(valueClass)) {
			createDate(parent, param, params);
		} else if (java.sql.Time.class.isAssignableFrom(valueClass)) {
			createTime(parent, param, params);
		} else if (java.sql.Timestamp.class.isAssignableFrom(valueClass)
				|| java.util.Date.class.isAssignableFrom(valueClass)) {
			createTimestamp(parent, param, params);
		} else if (TimestampRange.class.isAssignableFrom(valueClass))
			createTimestampRange(parent, param, params);
		else if (DateRange.class.isAssignableFrom(valueClass))
			createDateRange(parent, param, params);
		date.setToolTipText(VParameters.createToolTip(param));
		if (supportDateRange) {
			StringBuilder tt = new StringBuilder("Possible values to use as parameter:\n");
			String del = "";
			for (CalendarUnit cu : CalendarUnit.values()) {
				tt.append(del);
				if (cu.equals(CalendarUnit.WEEK)) {
					DateRangeBuilder drb = new DateRangeBuilder("WEEK");
					tt.append(cu.name() + "("
							+ namesOfDays[Misc.nvl(drb.getWeekStartDay(), RelativeDateRange.DEFAULT_WEEK_START_DAY)]
							+ ")");
				} else
					tt.append(cu.name());
				del = ", ";
			}
			date.setToolTipText(tt + "\n" + date.getToolTipText());
		}
		setNullable(param, date);
		date.addFocusListener(focusListener);
		date.addTraverseListener(keyListener);
	}

	protected void handleFocusLost() {
		IStatusLineManager statusLineManager = getStatusLineManager();
		if (statusLineManager != null)
			statusLineManager.setMessage(null);
		// updateInput();
	}

	private static String[] namesOfDays = new String[] { "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday",
			"Friday", "Saturday" };

	private void setFormat(CDateTime cDateTime, String key) {
		String f = Misc.nvl(getjConfig().getProperty(key));
		if (!Misc.isNullOrEmpty(f))
			cDateTime.setPattern(f);
	}

	/**
	 * Called when the timezone changes, this force the parameter to be
	 * recalculated with the current timezone
	 */
	private void refresh() {
		if (date != null && !date.isDisposed()) {
			Class<?> valueClass = getParameter().getValueClass();
			if (TimestampRange.class.isAssignableFrom(valueClass))
				handleDateRangeChange(Timestamp.class);
			else if (DateRange.class.isAssignableFrom(valueClass)) {
				handleDateRangeChange(Date.class);
			}
		}
	}

	protected void handleDateChanged() {
		Date sdate = date.getSelection();
		Date d = sdate != null ? new java.sql.Date(sdate.getTime()) : null;
		updateModel(isNumeric && d != null ? d.getTime() : d);
	}

	protected void handleTimestampChanged() {
		Date sdate = date.getSelection();
		Timestamp d = sdate != null ? new java.sql.Timestamp(sdate.getTime()) : null;
		d.setNanos(0);
		updateModel(isNumeric && d != null ? d.getTime() : d);
	}

	protected void handleTimeChanged() {
		Date sdate = date.getSelection();
		Time d = sdate != null ? new java.sql.Time(sdate.getTime()) : null;
		updateModel(isNumeric && d != null ? d.getTime() : d);
	}

	/**
	 * Check if the flag to use the report timezone is enabled in the
	 * preferences
	 * 
	 * @return true if the flag is enabled, false otherwise
	 */
	private boolean useReportTimezone() {
		return !getjConfig().getPrefStore()
				.getBoolean(ReportExecutionPreferencePage.JSS_REPORT_FORCE_PARAMETER_TIMEZONE);
	}

	protected void handleDateRangeChange(Class<? extends Date> clazz) {
		try {
			DateRangeBuilder drb = getDateRangeBuilder();

			// if the value should be influenced by the timezone then read its
			// value from the parameter
			if (useReportTimezone()) {
				Object timeZoneObj = params.get(JRParameter.REPORT_TIME_ZONE);
				boolean timeZoneSet = false;
				if (timeZoneObj != null) {
					TimeZone timeZone = null;
					if (timeZoneObj instanceof String) {
						timeZone = TimeZone.getTimeZone((String) timeZoneObj);
					} else if (timeZoneObj instanceof TimeZone) {
						timeZone = (TimeZone) timeZoneObj;
					}
					if (timeZone != null) {
						drb.set(timeZone);
						timeZoneSet = true;
					}
				}
				// code currently not used, uncomment this to fallback to JSS
				// preferences when the parameter is not set
				if (!timeZoneSet) {
					// look in the preferences
					String prefTimeZone = getjConfig().getPrefStore()
							.getString(ReportExecutionPreferencePage.JSS_REPORT_TIMEZONE);
					if (prefTimeZone != null) {
						TimeZone timeZone = TimeZone.getTimeZone(prefTimeZone);
						if (timeZone != null) {
							drb.set(timeZone);
						}
					}
				}

			}

			updateModel(drb.set(clazz).toDateRange());
		} catch (InvalidDateRangeExpressionException dre) {
			updateModel(null);
		}
	}

	private DateRangeBuilder getDateRangeBuilder() {
		if (date.getSelection() != null)
			return new DateRangeBuilder(date.getSelection());

		String exp = Misc.nvl(date.getText()).toUpperCase();
		Matcher matcher = Pattern.compile(RelativeDateRange.DATE_RANGE_REGEXP).matcher(exp.replaceAll(" ", ""));
		if (matcher.matches())
			return new DateRangeBuilder(exp.replaceAll(" ", ""));

		if (date.getPattern() != null) {
			SimpleDateFormat sdf = new SimpleDateFormat(date.getPattern());
			try {
				return new DateRangeBuilder(sdf.parse(exp));
			} catch (ParseException e) {
				// do nothing here
			}
		}
		return new DateRangeBuilder(exp);
	}

	protected void createTimestampRange(Composite parent, final IParameter param, final Map<String, Object> params) {
		final IPropertyChangeListener preferencesTimeZoneListener = event -> {
			if (ReportExecutionPreferencePage.JSS_REPORT_TIMEZONE.equals(event.getProperty())
					|| ReportExecutionPreferencePage.JSS_REPORT_FORCE_PARAMETER_TIMEZONE.equals(event.getProperty())) {
				handleDateRangeChange(Timestamp.class);
			}
		};
		getjConfig().getPrefStore().addPropertyChangeListener(preferencesTimeZoneListener);
		date = new DRDateTime(parent, CDT.BORDER | CDT.DATE_SHORT | CDT.TIME_MEDIUM | CDT.DROP_DOWN);
		((DRDateTime) date).setSupportDateRange(supportDateRange);

		GridData gd = new GridData();
		gd.horizontalIndent = 8;
		gd.widthHint = 25 * getCharWidth(date);
		date.setLayoutData(gd);
		setFormat(date, InputControlsPreferencePage.JSS_TIMESTAMP_FORMAT);

		setMandatory(param, date);
		ModifyListener listener = e -> handleDateRangeChange(Timestamp.class);
		((DRDateTime) date).addModifyListener(listener);
		date.addDisposeListener(
				e -> getjConfig().getPrefStore().removePropertyChangeListener(preferencesTimeZoneListener));
		updateInput();
		listener.modifyText(null);
	}

	protected void createDateRange(Composite parent, final IParameter param, final Map<String, Object> params) {
		final IPropertyChangeListener preferencesTimeZoneListener = event -> {
			if (ReportExecutionPreferencePage.JSS_REPORT_TIMEZONE.equals(event.getProperty())
					|| ReportExecutionPreferencePage.JSS_REPORT_FORCE_PARAMETER_TIMEZONE.equals(event.getProperty())) {
				handleDateRangeChange(Date.class);
			}
		};
		getjConfig().getPrefStore().addPropertyChangeListener(preferencesTimeZoneListener);
		date = new DRDateTime(parent, CDT.BORDER | CDT.DATE_SHORT | CDT.DROP_DOWN);
		((DRDateTime) date).setSupportDateRange(supportDateRange);

		GridData gd = new GridData();
		gd.horizontalIndent = 8;
		gd.widthHint = 25 * getCharWidth(date);
		date.setLayoutData(gd);
		setFormat(date, InputControlsPreferencePage.JSS_DATE_FORMAT);

		setMandatory(param, date);

		ModifyListener listener = e -> handleDateRangeChange(Date.class);
		((DRDateTime) date).addModifyListener(listener);
		date.addDisposeListener(
				e -> getjConfig().getPrefStore().removePropertyChangeListener(preferencesTimeZoneListener));
		updateInput();
		listener.modifyText(null);
	}

	protected void createTimestamp(Composite parent, final IParameter param, final Map<String, Object> params) {
		date = new CDateTime(parent, CDT.BORDER | CDT.DATE_SHORT | CDT.TIME_MEDIUM | CDT.DROP_DOWN);

		GridData gd = new GridData();
		gd.horizontalIndent = 8;
		gd.widthHint = 25 * getCharWidth(date);
		date.setLayoutData(gd);
		setFormat(date, InputControlsPreferencePage.JSS_TIMESTAMP_FORMAT);

		setMandatory(param, date);
		SelectionAdapter listener = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleTimestampChanged();
			}
		};
		date.addSelectionListener(listener);
		updateInput();
		listener.widgetSelected(null);
	}

	protected void createTime(Composite parent, final IParameter param, final Map<String, Object> params) {
		date = new CDateTime(parent, CDT.BORDER | CDT.TIME_MEDIUM | CDT.DROP_DOWN);
		GridData gd = new GridData();
		gd.horizontalIndent = 8;
		gd.widthHint = 25 * getCharWidth(date);
		date.setLayoutData(gd);
		setFormat(date, InputControlsPreferencePage.JSS_TIME_FORMAT);

		setMandatory(param, date);

		SelectionAdapter listener = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleTimeChanged();
			}
		};
		date.addSelectionListener(listener);
		updateInput();
		listener.widgetSelected(null);
	}

	protected void createDate(Composite parent, final IParameter param, final Map<String, Object> params) {
		date = new CDateTime(parent, CDT.BORDER | CDT.DATE_SHORT | CDT.DROP_DOWN);
		GridData gd = new GridData();
		gd.horizontalIndent = 8;
		gd.widthHint = 25 * getCharWidth(date);
		date.setLayoutData(gd);
		setFormat(date, InputControlsPreferencePage.JSS_DATE_FORMAT);

		setMandatory(param, date);

		updateInput();
		SelectionAdapter listener = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleDateChanged();
			}
		};
		date.addSelectionListener(listener);
		listener.widgetSelected(null);
	}

	/**
	 * When the timezone changes update the parameter value
	 */
	@Override
	public void parameterChanged(java.beans.PropertyChangeEvent evt) {
		if (JRParameter.REPORT_TIME_ZONE.equals(evt.getPropertyName())) {
			refresh();
		}
	}

	public void updateInput() {
		Object d = params.get(param.getName());
		if (d != null) {
			if (d instanceof String) {
				try {
					SimpleDateFormat sdf = new SimpleDateFormat(date.getPattern());
					sdf.parse((String) d);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			if (d instanceof Date) {
				date.setSelection((Date) d);
			} else if (d instanceof Long) {
				date.setSelection(new Date((Long) d));
				isNumeric = true;
			} else if (d instanceof DateRange) {
				DateRange dr = (DateRange) d;
				if (dr instanceof DateRangeExpression) {
					String expr = ((DateRangeExpression) dr).getExpression();
					if (expr != null) {
						((DRDateTime) date).setText(expr);
						return;
					}
				}
				date.setSelection(null);
				((DRDateTime) date).setText(null);
			}
		} else {
			date.setSelection(null);
			if (date instanceof DRDateTime)
				((DRDateTime) date).setText(null);
		}
	}

	protected boolean isNumeric = false;
	protected CDateTime date;

	public boolean isSupportDateRange() {
		return supportDateRange;
	}

	public void setSupportDateRange(boolean supportDateRange) {
		this.supportDateRange = supportDateRange;
	}

}
