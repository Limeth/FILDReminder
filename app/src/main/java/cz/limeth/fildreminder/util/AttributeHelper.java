package cz.limeth.fildreminder.util;

import android.content.res.Resources;
import android.util.AttributeSet;

public class AttributeHelper implements AttributeSet {
    private static final String NAMESPACE_DEFAULT = "http://schemas.android.com/apk/res/android";
    private static final int RESOURCE_NOT_FOUND = -1;
    private final AttributeSet source;
    private final Resources resources;

    public AttributeHelper(AttributeSet source, Resources resources) {
        this.source = source;
        this.resources = resources;
    }

    public int resolveInteger(String namespace, String attribute, int defaultValue) {
        int resourceId = source.getAttributeResourceValue(namespace, attribute, RESOURCE_NOT_FOUND);

        if(resourceId != RESOURCE_NOT_FOUND)
            return resources.getInteger(resourceId);

        return source.getAttributeIntValue(namespace, attribute, defaultValue);
    }

    public int resolveInteger(String attribute, int defaultValue) {
        return resolveInteger(NAMESPACE_DEFAULT, attribute, defaultValue);
    }

    // Delegate functions follow

    @Override
    public int getAttributeCount() {
        return source.getAttributeCount();
    }

    @Override
    public String getAttributeName(int index) {
        return source.getAttributeName(index);
    }

    @Override
    public String getAttributeValue(int index) {
        return source.getAttributeValue(index);
    }

    @Override
    public String getAttributeValue(String namespace, String name) {
        return source.getAttributeValue(namespace, name);
    }

    @Override
    public String getPositionDescription() {
        return source.getPositionDescription();
    }

    @Override
    public int getAttributeNameResource(int index) {
        return source.getAttributeNameResource(index);
    }

    @Override
    public int getAttributeListValue(String namespace, String attribute, String[] options, int defaultValue) {
        return source.getAttributeListValue(namespace, attribute, options, defaultValue);
    }

    @Override
    public boolean getAttributeBooleanValue(String namespace, String attribute, boolean defaultValue) {
        return source.getAttributeBooleanValue(namespace, attribute, defaultValue);
    }

    @Override
    public int getAttributeResourceValue(String namespace, String attribute, int defaultValue) {
        return source.getAttributeResourceValue(namespace, attribute, defaultValue);
    }

    @Override
    public int getAttributeIntValue(String namespace, String attribute, int defaultValue) {
        return source.getAttributeIntValue(namespace, attribute, defaultValue);
    }

    @Override
    public int getAttributeUnsignedIntValue(String namespace, String attribute, int defaultValue) {
        return source.getAttributeUnsignedIntValue(namespace, attribute, defaultValue);
    }

    @Override
    public float getAttributeFloatValue(String namespace, String attribute, float defaultValue) {
        return source.getAttributeFloatValue(namespace, attribute, defaultValue);
    }

    @Override
    public int getAttributeListValue(int index, String[] options, int defaultValue) {
        return source.getAttributeListValue(index, options, defaultValue);
    }

    @Override
    public boolean getAttributeBooleanValue(int index, boolean defaultValue) {
        return source.getAttributeBooleanValue(index, defaultValue);
    }

    @Override
    public int getAttributeResourceValue(int index, int defaultValue) {
        return source.getAttributeResourceValue(index, defaultValue);
    }

    @Override
    public int getAttributeIntValue(int index, int defaultValue) {
        return source.getAttributeIntValue(index, defaultValue);
    }

    @Override
    public int getAttributeUnsignedIntValue(int index, int defaultValue) {
        return source.getAttributeUnsignedIntValue(index, defaultValue);
    }

    @Override
    public float getAttributeFloatValue(int index, float defaultValue) {
        return source.getAttributeFloatValue(index, defaultValue);
    }

    @Override
    public String getIdAttribute() {
        return source.getIdAttribute();
    }

    @Override
    public String getClassAttribute() {
        return source.getClassAttribute();
    }

    @Override
    public int getIdAttributeResourceValue(int defaultValue) {
        return source.getIdAttributeResourceValue(defaultValue);
    }

    @Override
    public int getStyleAttribute() {
        return source.getStyleAttribute();
    }
}
