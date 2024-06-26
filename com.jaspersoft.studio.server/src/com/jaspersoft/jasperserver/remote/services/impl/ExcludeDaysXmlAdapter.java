/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/

package com.jaspersoft.jasperserver.remote.services.impl;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author Yaroslav.Kovalchyk
 * @version $Id: ExcludeDaysXmlAdapter.java 23844 2012-05-22 06:23:41Z ykovalchyk $
 */
public class ExcludeDaysXmlAdapter extends XmlAdapter<ExcludeDaysWrapper, ArrayList<Calendar>> {
    private DateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public ArrayList<Calendar> unmarshal(ExcludeDaysWrapper v) throws Exception {
        ArrayList<Calendar> result = null;
        if(v != null && v.getExcludeDays() != null && !v.getExcludeDays().isEmpty()){
            result = new ArrayList<Calendar>();
            for (String currentCalendarString : v.getExcludeDays()){
                final Date date = format.parse(currentCalendarString);
                Calendar currentCalendar = Calendar.getInstance();
                currentCalendar.setTime(date);
                result.add(currentCalendar);
            }
        }
        return result;
    }

    @Override
    public ExcludeDaysWrapper marshal(ArrayList<Calendar> v) throws Exception {
        ExcludeDaysWrapper result = null;
        if(v != null && !v.isEmpty()){
            List<String> dayStrings = new ArrayList<String>();
            for (Calendar currentCalendar : v){
                dayStrings.add(format.format(currentCalendar.getTime()));
            }
            result = new ExcludeDaysWrapper(dayStrings);
        }
        return result;
    }
}
