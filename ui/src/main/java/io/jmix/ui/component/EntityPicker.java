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
package io.jmix.ui.component;

import io.jmix.core.metamodel.model.MetaClass;
import io.jmix.ui.meta.CanvasBehaviour;
import io.jmix.ui.meta.PropertiesConstraint;
import io.jmix.ui.meta.PropertiesGroup;
import io.jmix.ui.meta.PropertyType;
import io.jmix.ui.meta.StudioComponent;
import io.jmix.ui.meta.StudioProperties;
import io.jmix.ui.meta.StudioProperty;
import org.springframework.core.ParameterizedTypeReference;

import javax.annotation.Nullable;

/**
 * Generic UI component designed to select and display an entity instance.
 * Consists of the text field and the set of buttons defined by actions.
 *
 * @see EntityComboBox
 */
@StudioComponent(
        caption = "EntityPicker",
        category = "Components",
        xmlElement = "entityPicker",
        icon = "io/jmix/ui/icon/component/entityPicker.svg",
        canvasBehaviour = CanvasBehaviour.VALUE_PICKER,
        documentationURL = "https://docs.jmix.io/jmix/%VERSION%/backoffice-ui/vcl/components/entity-picker.html"
)
@StudioProperties(
        properties = {
                @StudioProperty(name = "captionProperty", type = PropertyType.PROPERTY_PATH_REF),
        },
        groups = {
                @PropertiesGroup(constraint = PropertiesConstraint.ALL_OR_NOTHING,
                        properties = {"dataContainer", "captionProperty"})
        }
)
public interface EntityPicker<V> extends ValuePicker<V>, LookupComponent<V> {

    String NAME = "entityPicker";

    static <T> ParameterizedTypeReference<EntityPicker<T>> of(Class<T> valueClass) {
        return new ParameterizedTypeReference<EntityPicker<T>>() {};
    }

    @Nullable
    MetaClass getMetaClass();

    @StudioProperty(name = "metaClass", type = PropertyType.ENTITY_NAME, typeParameter = "V")
    void setMetaClass(@Nullable MetaClass metaClass);

    interface EntityPickerAction extends ValuePickerAction {

        void setEntityPicker(@Nullable EntityPicker valuePicker);

        @Override
        default void setPicker(@Nullable ValuePicker valuePicker) {
            if (valuePicker != null && !(valuePicker instanceof EntityPicker)) {
                throw new IllegalArgumentException("Incorrect component type. Must be " +
                        "'EntityPicker' or its inheritors");
            }
            setEntityPicker(((EntityPicker) valuePicker));
        }
    }
}
