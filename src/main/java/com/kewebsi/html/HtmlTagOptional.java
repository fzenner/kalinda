package com.kewebsi.html;


/**
 * DO NOT USE. In orger to make this concept really work, all methods of HtmlTag must be overwritten and delegate
 * to the embedded HtmlTag. That is too fragile in case HtmlTag gets enhanced.
 * An alternative would be to add the optionality functionality to the HtmlTag itself. But we opt for using the
 * visibility:hidden attribute, which keeps the position in layouts unlike the attribute hidden.
 * Another option would be to enhance SmartTag
 * @param <T>
 */
public abstract class HtmlTagOptional<T extends HtmlTag> extends HtmlTag {


    protected T embeddedTag;

    // protected boolean isVisible = true;
    protected boolean oldIsVisible = true;

    protected HtmlTagOptional(String id) {
        this.id = id;
    }


    public HtmlTagOptional(T embeddedTag) {
        // The ID of this tag must be the same for both visibility states, so that the exchange mechanism works.
        this.id = embeddedTag.getId();

        this.embeddedTag = embeddedTag;
    }

    public abstract boolean isVisible();


    @Override
    public boolean isContentOrGuiDefModified() {
        if (isVisible() != oldIsVisible) {
            return true;
        } else {
            if (isVisible()) {
                return embeddedTag.isContentOrGuiDefModified();
            } else {
                return false;
            }
        }
    }

    @Override
    public void setContentOrGuiDefNotModified() {
        if (embeddedTag != null) {
            embeddedTag.setContentOrGuiDefNotModified();
        }
        oldIsVisible = isVisible();
    }


    public T getEmbeddedTag() {
        return embeddedTag;
    }


    public static HtmlTagOptional showNever(String id) {
        return new HtmlTagOptional(id) {
            @Override
            public boolean isVisible() {
                return false;
            }
        };
    }





}
