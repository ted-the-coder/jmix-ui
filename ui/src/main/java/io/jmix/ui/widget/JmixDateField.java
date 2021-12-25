/*
 * Copyright 2019 Haulmont.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.jmix.ui.widget;

import com.vaadin.data.Result;
import com.vaadin.event.Action;
import com.vaadin.shared.ui.datefield.DateResolution;
import io.jmix.ui.widget.client.datefield.JmixDateFieldState;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class JmixDateField extends com.vaadin.ui.DateField implements Action.Container {

    protected String dateString;

    public JmixDateField() {
        setStyleName("jmix-datefield");
    }

    @Override
    protected JmixDateFieldState getState() {
        return (JmixDateFieldState) super.getState();
    }

    @Override
    protected JmixDateFieldState getState(boolean markAsDirty) {
        return (JmixDateFieldState) super.getState(markAsDirty);
    }

    @Override
    public void setDateFormat(String dateFormat) {
        super.setDateFormat(dateFormat);
        getState().dateMask = StringUtils.replaceChars(dateFormat, "dDMYy", "#####");
        markAsDirty();
    }

    @Override
    protected void updateInternal(String newDateString, Map<String, Integer> resolutions) {
        // CAUTION: copied from AbstractDateField
        Set<String> resolutionNames = getResolutions().map(Enum::name)
                .collect(Collectors.toSet());
        resolutionNames.retainAll(resolutions.keySet());
        if (!isReadOnly()
                && (!resolutionNames.isEmpty() || newDateString != null)) {

            // Old and new dates
            final LocalDate oldDate = getValue();

            LocalDate newDate;

            String mask = StringUtils.replaceChars(getState(false).dateMask, "#U", "__");
            if ("".equals(newDateString)
                    || mask.equals(newDateString)) {

                newDate = null;
            } else {
                newDate = reconstructDateFromFields(resolutions, oldDate);
            }

            boolean parseErrorWasSet = currentErrorMessage != null;
            boolean hasChanges = !Objects.equals(dateString, newDateString)
                    || !Objects.equals(oldDate, newDate)
                    || parseErrorWasSet;

            if (hasChanges) {
                dateString = newDateString;
                currentErrorMessage = null;
                if (newDateString == null || newDateString.isEmpty()) {
                    boolean valueChanged = setValue(newDate, true);
                    if (!valueChanged && parseErrorWasSet) {
                        doSetValue(newDate);
                    }
                } else {
                    // invalid date string
                    if (resolutions.isEmpty()) {
                        Result<LocalDate> parsedDate = handleUnparsableDateString(
                                dateString);
                        parsedDate.ifOk(v -> setValue(v, true));
                        if (parsedDate.isError()) {
                            dateString = null;
                            currentErrorMessage = parsedDate
                                    .getMessage().orElse("Parsing error");

                            if (!isDifferentValue(null)) {
                                doSetValue(null);
                            } else {
                                setValue(null, true);
                            }
                        }
                    } else {
                        setValue(newDate, true);
                    }
                }
            }
        }
    }

    @Override
    protected boolean isValueInRange(LocalDate value) {
        // Return true to avoid exception and set value
        return true;
    }

    @Override
    protected Result<LocalDate> handleUnparsableDateString(String dateString) {
        if (Objects.equals(dateString, StringUtils.replaceChars(getState(false).dateMask, "#U", "__"))) {
            return Result.ok(null);
        }

        return Result.error(getParseErrorMessage());
    }

    @Override
    public void setResolution(DateResolution resolution) {
        super.setResolution(resolution);
        // By default, only visual representation is updated after the resolution is changed.
        // As a result, the actual value and the visual representation are different values.
        // But we want to update the field value and fire value change event.
        if (getValue() != null) {
            setValue(reconstructDateFromFields(getState().resolutions, getValue()), true);
        }
    }

    @Override
    public void addActionHandler(Action.Handler actionHandler) {
        getActionManager().addActionHandler(actionHandler);
    }

    @Override
    public void removeActionHandler(Action.Handler actionHandler) {
        getActionManager().removeActionHandler(actionHandler);
    }

    public boolean isCaptionManagedByLayout() {
        return getState(false).captionManagedByLayout;
    }

    public void setCaptionManagedByLayout(boolean captionManagedByLayout) {
        if (isCaptionManagedByLayout() != captionManagedByLayout) {
            getState().captionManagedByLayout = captionManagedByLayout;
        }
    }

    public void setAutofill(boolean autofill) {
        if (isAutofill() != autofill) {
            getState().autofill = autofill;
        }
    }

    public boolean isAutofill() {
        return getState(false).autofill;
    }
}
